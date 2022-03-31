/*
 * BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 * Copyright (C) 2019-2022 BackwardsNode/BossWasHere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames.game;

import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.VaultConnector;
import com.backwardsnode.survivalgames.api.event.*;
import com.backwardsnode.survivalgames.config.DeathmatchConfiguration;
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.config.RewardConfiguration;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.controller.BossBarController;
import com.backwardsnode.survivalgames.controller.ScoreboardController;
import com.backwardsnode.survivalgames.controller.ScoreboardElement;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.item.ChestObject;
import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.message.PluginMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameInstance {

	private final GameManager MANAGER;
	private final GameConfiguration CONFIG;
	private final PlayerCacheSettings CACHE_SETTINGS;
	private DeathmatchConfiguration deathmatchConfig;
	
	private ScoreboardController scoreboard;
	private BossBarController bossbar;
	private BorderController border;
	
	private Map<UUID, PlayerState> ingamePlayers;
	
	private BukkitTask timerTask;
	private TimerCountdown timerInstance;
	
	private boolean flagIsActive;
	private boolean flagDisableMovement;
	private boolean flagPVPEnabled;
	private boolean flagDeathmatchStarted;
	private List<String> openedChests;
	
	protected GameInstance(GameManager manager, GameConfiguration config, PlayerCacheSettings cacheSettings) throws GameConfigurationException {
		MANAGER = manager;
		CONFIG = config;
		CACHE_SETTINGS = cacheSettings;

		if (config.spawnLocs.size() < 2) {
			throw new GameConfigurationException("Not enough spawn locations");
		}

		ingamePlayers = new HashMap<>();
		openedChests = new ArrayList<>();
	}
	
	protected GameStatus begin(Player initiator, List<Player> players, boolean ignoreIngamePlayers, PlayerSelectionMethod selectorMode) {
		GameStatus returnStatus = GameStatus.START_SUCCESS;
		int playerCount = players.size();

		final boolean acceptsSpectators = selectorMode == PlayerSelectionMethod.ARBITRARY_WITH_SPECTATORS || selectorMode == PlayerSelectionMethod.SHUFFLED_WITH_SPECTATORS;
		players.removeIf(MANAGER::isPlayerIngame);
		if (!ignoreIngamePlayers) {
			if (players.size() != playerCount) {
				return GameStatus.START_ERR_PLAYER_IN_GAME;
			}
		}

		playerCount = players.size();

		if (playerCount < 2) {
			return GameStatus.START_ERR_FEW_PLAYERS;
		}

		if (playerCount > CONFIG.spawnLocs.size()) {
			if (acceptsSpectators) {
				returnStatus = GameStatus.START_SUCCESS_WITH_SPECTATORS;
			} else {
				returnStatus = GameStatus.START_SUCCESS_WITHOUT_SPECTATORS;
			}
		}

		flagDisableMovement = true;
		flagIsActive = true;
		flagDeathmatchStarted = false;

		int spawnLocationCount = CONFIG.spawnLocs.size();
		Player pl;
		if (selectorMode == PlayerSelectionMethod.SHUFFLED || selectorMode == PlayerSelectionMethod.SHUFFLED_WITH_SPECTATORS) {
			Collections.shuffle(players);
		}

		for (int i = 0; i < players.size(); i++) {
			pl = players.get(i);
			if (i < spawnLocationCount) {
				PlayerState ps = new PlayerState(pl, false);
				ps.cache.cacheCurrent(CACHE_SETTINGS);
				ingamePlayers.put(pl.getUniqueId(), ps);
			} else if (acceptsSpectators) {
				PlayerState ps = new PlayerState(pl, true);
				ps.cache.cacheCurrentGamemode();
				ingamePlayers.put(pl.getUniqueId(), ps);
			}
		}

		setupBorder(initiator.getWorld());
		setupScoreboard(Math.min(spawnLocationCount, playerCount));
		setupBossbar();
		deathmatchConfig = CONFIG.selectDeathmatch();

		if (CONFIG.preFillChests) {
			fillChests(initiator);
		}

		preparePlayers();
		startCountdownTimer(CONFIG.waitTime, GameStatus.RELEASE_PLAYERS);

		GameStartedEvent gameStartedEvent = new GameStartedEvent(this);
		Bukkit.getPluginManager().callEvent(gameStartedEvent);

		return returnStatus;
	}

	private void setupScoreboard(int playersAlive) {
		scoreboard = new ScoreboardController(ChatColor.DARK_AQUA + "[Survival Games]");
		scoreboard.initialize();
		scoreboard.updateScoreboardElement(ScoreboardElement.MAP_NAME, (CONFIG.mapName == null ? "unknown" : CONFIG.mapName));
		scoreboard.updateScoreboardElement(ScoreboardElement.PLAYERS_LEFT, String.valueOf(playersAlive));
		String dim = String.valueOf(Math.floor(border.getRadius()));
		scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, dim + "," + dim + " blocks");
		scoreboard.setVisibleTo(ingamePlayers.values());
	}

	private void setupBossbar() {
		bossbar = new BossBarController("Game Starting...", BarColor.BLUE, CONFIG.waitTime);
		bossbar.setVisibleTo(ingamePlayers.values());
	}

	private void setupBorder(World defaultWorld) {
		border = new BorderController(MANAGER.getPlugin().getDependencyManager().getProtocolConnector(), defaultWorld);
		border.setTarget(CONFIG.border.borderStartRadius, 0);
		border.setVisibleTo(ingamePlayers.values());
	}
	
	private boolean startCountdownTimer(int seconds, GameStatus nextPhase) {
		if (timerTask == null) {
			timerInstance = new TimerCountdown(this);
			timerInstance.setOperation(nextPhase, seconds);
			timerTask = Bukkit.getScheduler().runTaskTimer(MANAGER.getPlugin(), timerInstance, 20, 20);
			return true;
		}
		return false;
	}
	
	protected void triggerEvent(GameStatus status) {
		GameStatusChangeEvent statusChangeEvent = new GameStatusChangeEvent(this, status);
		Bukkit.getServer().getPluginManager().callEvent(statusChangeEvent);

		if (statusChangeEvent.isCancelled()) {
			return;
		}

		switch (status) {
		case RELEASE_PLAYERS:
			releasePlayers();
			break;
		case ENABLE_PVP:
			enablePVP(true);
			break;
		case SHRINK_PLAY_AREA:
			shrinkPlayArea();
			break;
		case START_DEATHMATCH:
			startDeathmatch();
			break;
		case CLOSE_PLAY_AREA:
			closeBorder();
			break;
		case FINISH_GAME:
			break;
		default:
			break;
		}
	}
	
	protected void tick(long remaining, GameStatus nextOperation) {
		scoreboard.updateScoreboardElement(ScoreboardElement.TIME_LEFT, Utils.secondsToString(remaining));
		bossbar.setHealth(remaining);

		if (nextOperation == GameStatus.CLOSE_PLAY_AREA && remaining % 2 == 0) {
			String dim = String.valueOf(Math.floor(border.getRadius()));
			scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, dim + "," + dim + " blocks");
		}
	}
	
	private void preparePlayers() {
		int i = 0;
		for (PlayerState player : ingamePlayers.values()) {
			Player p = player.cache.getPlayer();
			MANAGER.getPlugin().getMessageProvider().sendMessage(p, Messages.GAME.PLAYING_ON, CONFIG.mapName);

			if (player.spectating) {
				p.teleport(CONFIG.spawnLocs.get(0).add(0.5, 0, 0.5));
				p.setGameMode(GameMode.SPECTATOR);
				MANAGER.getPlugin().getMessageProvider().sendMessage(p, Messages.GAME.AS_SPECTATOR);
			} else {
				p.teleport(CONFIG.spawnLocs.get(i).add(0.5, 0, 0.5));
				p.setGameMode(GameMode.ADVENTURE);
				i++;
			}
		}
	}
	
	private void releasePlayers() {
		flagDisableMovement = false;
		playGlobalPingSound();
		if (CONFIG.gracePeriod < 1) {
			enablePVP(false);
		} else {
			scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "PvP Off");
			announce(Messages.GAME.PVP_OFF_TIME, CONFIG.gracePeriod);
			bossbar.resetHealth(CONFIG.gracePeriod);
			bossbar.setOptions("PvP Disabled", BarColor.YELLOW);
			timerInstance.setOperation(GameStatus.ENABLE_PVP, CONFIG.gracePeriod);
		}
	}
	
	private void enablePVP(boolean shouldAnnounce) {
		flagPVPEnabled = true;
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "PvP On");
		if (shouldAnnounce) {
			playGlobalPingSound();
			announce(Messages.GAME.PVP_ENABLED);
		}
		bossbar.resetHealth(CONFIG.borderCollapseDelay);
		bossbar.setOptions("PvP Enabled", BarColor.PURPLE);
		timerInstance.setOperation(GameStatus.SHRINK_PLAY_AREA, CONFIG.borderCollapseDelay);
	}
	
	private void shrinkPlayArea() {
		playGlobalPingSound();
		if (deathmatchConfig != null) {
			announce(Messages.GAME.BORDER_SHRINKING);
			scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Border Shrinking");
			border.setTarget(deathmatchConfig.centerX, deathmatchConfig.centerZ, deathmatchConfig.borderRadius, deathmatchConfig.shrinkTime);
			bossbar.resetHealth(deathmatchConfig.shrinkTime);
			bossbar.setOptions("Stay in the zone!", BarColor.PINK);
			timerInstance.setOperation(GameStatus.START_DEATHMATCH, deathmatchConfig.shrinkTime);
		} else {
			// TODO bad
			//announce(ChatColor.AQUA + "[!] Game ended, as there is no deathmatch arena");
			terminate();
		}
	}
	
	public void forceDeathmatch() {
		if (!flagDeathmatchStarted && deathmatchConfig != null) {
			GameStatusChangeEvent statusChangeEvent = new GameStatusChangeEvent(this, GameStatus.START_DEATHMATCH);
			Bukkit.getServer().getPluginManager().callEvent(statusChangeEvent);

			startDeathmatch();
		}
	}
	
	private void startDeathmatch() {
		playGlobalPingSound();
		flagDeathmatchStarted = true;
		announce(Messages.GAME.DEATHMATCH);
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Deathmatch");
		bossbar.resetHealth(deathmatchConfig.deathmatchDuration);
		bossbar.setOptions("Deathmatch", BarColor.RED);
		timerInstance.setOperation(GameStatus.CLOSE_PLAY_AREA, deathmatchConfig.deathmatchDuration);
	}
	
	private void closeBorder() {
		playGlobalPingSound();
		announce(Messages.GAME.BORDER_SHRINKING);
		border.setTarget(1, deathmatchConfig.collapseTime);
		timerInstance.setOperation(GameStatus.FINISH_GAME, deathmatchConfig.collapseTime);
	}
	
	private void fillChests(Player initiator) {
		int missingChests = 0;
		boolean foundBadItem = false;
		for (ChestObject co : CONFIG.chestLocations) {
			Block b = co.location.getBlock();
			if (b.getType() != Material.CHEST || !(b.getState() instanceof Chest chest)) {
				if (missingChests < 5) {
					MANAGER.getPlugin().getMessageProvider().sendMessage(initiator, Messages.CONFIG.CHEST_IGNORE_MISSING, co.loc);
				}
				missingChests++;
				continue;
			}

			if (!co.fill(chest, CONFIG.itemSets)) {
				foundBadItem = true;
			}
		}
		if (missingChests > 0) {
			MANAGER.getPlugin().getMessageProvider().sendMessage(initiator, Messages.CONFIG.CHEST_MISSING_SIMPLE, missingChests);
		}
		if (foundBadItem) {
			MANAGER.getPlugin().getMessageProvider().sendMessage(initiator, Messages.CONFIG.CHEST_BAD_ITEMS);
		}
	}
	
	public void updateMostKills() {
		List<PlayerState> states = ingamePlayers.values().stream().filter(x -> !x.spectating).sorted((p1, p2) -> Integer.compare(p2.kills, p1.kills)).toList();
		int i = 0;
		for (PlayerState state : states) {
			switch (i) {
				case 0 -> scoreboard.updateScoreboardElement(ScoreboardElement.MOST_KILLS_1, state.cache.getPlayer().getDisplayName() + ": " + state.kills);
				case 1 -> scoreboard.updateScoreboardElement(ScoreboardElement.MOST_KILLS_2, state.cache.getPlayer().getDisplayName() + ": " + state.kills);
				case 2 -> scoreboard.updateScoreboardElement(ScoreboardElement.MOST_KILLS_3, state.cache.getPlayer().getDisplayName() + ": " + state.kills);
			}
			i++;
		}
	}
	
	private void playGlobalPingSound() {
		for (PlayerState ps : ingamePlayers.values()) {
			Player p = ps.cache.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1f, 1f);
		}
	}
	
	public int checkAlive() {
		Player w = null;
		int count = 0;
		for (PlayerState ps : ingamePlayers.values()) {
			if (ps.alive) {
				if (w == null) {
					w = ps.cache.getPlayer();
				}
				count++;
			}
		}
		if (count < 2) {
			if (count == 1) {
				endGame(w.getDisplayName());
			} else {
				endGame(null);
			}
			return -1;
		}
		return count;
	}
	
	protected boolean processDeath(Player player, Player killer) {
		if (removePlayer(player, false)) {
			if (killer == null) {
				announce(Messages.GAME.DEATH_GENERIC, player.getDisplayName());
				if (CONFIG.spawnFireworkOnDeath) {
					Utils.spawnRandomFirework(player.getLocation());
				}
			} else {
				announce(Messages.GAME.DEATH_KILLED, player.getDisplayName(), killer.getDisplayName());
				if (CONFIG.spawnFireworkOnKill) {
					Utils.spawnRandomFirework(player.getLocation());
				}
				PlayerState ps = getPlayerState(killer);
				if (ps != null) {
					ps.kills += 1;
					updateMostKills();
				}
			}

			GameDeathEvent gameDeathEvent = new GameDeathEvent(this, player, killer);
			Bukkit.getPluginManager().callEvent(gameDeathEvent);
			return true;
		}
		return false;
	}

	protected boolean processDeathByEntity(Player player, String entity) {
		if (removePlayer(player, false)) {
			announce(Messages.GAME.DEATH_KILLED, player.getDisplayName(), entity);
			if (CONFIG.spawnFireworkOnDeath) {
				Utils.spawnRandomFirework(player.getLocation());
			}

			GameDeathEvent gameDeathEvent = new GameDeathEvent(this, player, null);
			Bukkit.getPluginManager().callEvent(gameDeathEvent);
			return true;
		}
		return false;
	}
	
	public void endGame(String victor) {
		openedChests.clear();
		if (victor != null) {
			announce(Messages.GAME.WON, victor);
		}
		ArrayList<PlayerState> listPlayerState = new ArrayList<>(ingamePlayers.values());
		listPlayerState.sort((p1, p2) -> Integer.compare(p2.kills, p1.kills));
		announce(Messages.GAME.KILLSTATS);

		int i = 0;
		for (PlayerState state : listPlayerState) {
			switch (i) {
				case 0 -> announce(Messages.GAME.KILLSTAT_1, state.cache.getPlayer().getDisplayName(), state.kills);
				case 1 -> announce(Messages.GAME.KILLSTAT_2, state.cache.getPlayer().getDisplayName(), state.kills);
				case 2 -> announce(Messages.GAME.KILLSTAT_3, state.cache.getPlayer().getDisplayName(), state.kills);
			}

			i++;
			state.placement = i;
		}
		terminate(listPlayerState);
	}

	public void rewardPlayer(PlayerState state) {
		RewardConfiguration rewards = CONFIG.rewards.get(String.valueOf(state.placement));

		if (rewards == null) {
			rewards = new RewardConfiguration();
		} else {
			rewards = rewards.copy();
		}

		Player player = state.cache.getPlayer();
		GameRewardEvent gameRewardEvent = new GameRewardEvent(this, player, state.placement, rewards);
		Bukkit.getPluginManager().callEvent(gameRewardEvent);

		if (!gameRewardEvent.isCancelled()) {
			Location location = player.getLocation();
			VaultConnector vault = MANAGER.getPlugin().getDependencyManager().getVaultConnector();
			if (vault != null) {
				vault.depositPlayer(player, rewards.cash);
			}

			Inventory inventory = player.getInventory();
			boolean hadOverflow = false;
			for (ItemModel items : rewards.items) {
				ItemStack item = items.getEquivalent();
				Map<Integer, ItemStack> overflow = inventory.addItem(item);

				for (ItemStack overflowStack : overflow.values()) {
					hadOverflow = true;
					location.getWorld().dropItemNaturally(location, overflowStack);
				}
			}

			if (hadOverflow) {
				MANAGER.getPlugin().getMessageProvider().sendMessage(player, Messages.GAME.REWARDS_OVERFLOW);
			}
		}
	}
	
	public boolean removePlayer(Player player, boolean droppedFromSession) {
		UUID uuid = player.getUniqueId();
		PlayerState ps = ingamePlayers.get(uuid);
		boolean returning = ps.alive;
		if (returning) {
			if (droppedFromSession) {
				for (ItemStack item : player.getInventory().getContents()) {
					player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
				}
			} else {
				player.teleport(CONFIG.spawnLocs.get(0));
				ps.alive = false;
			}
		}
		if (droppedFromSession) {
			restoreState(ps);
			ingamePlayers.remove(uuid);
			bossbar.unsetVisibleTo(player);
			border.unsetVisibleTo(player);
		} else {
			ps.cache.setBaseHealth();
			player.setGameMode(GameMode.SPECTATOR);
		}
		int alive = checkAlive();
		if (alive > 1) {
			scoreboard.updateScoreboardElement(ScoreboardElement.PLAYERS_LEFT, "" + alive);
		}
		return returning;
	}
	
	public void terminate() {
		terminate(ingamePlayers.values());
	}
	
	private void terminate(Collection<PlayerState> playerStates) {
		if (!flagIsActive) return;
		flagIsActive = false;

		GameEndedEvent gameEndedEvent = new GameEndedEvent(this);
		Bukkit.getPluginManager().callEvent(gameEndedEvent);

		for (PlayerState ps : playerStates) {
			restoreState(ps);
			rewardPlayer(ps);
		}
		if (timerTask != null) timerTask.cancel();

		scoreboard.close();
		border.close();
		bossbar.close();
		MANAGER.onGameFinished(this);
	}
	
	private void restoreState(PlayerState ps) {		
		if (!ps.spectating) {
			ps.cache.restore(CACHE_SETTINGS);
		} else if (CACHE_SETTINGS.cacheGamemode) {
			ps.cache.restoreGamemode();
		}

		MANAGER.getPlugin().getDefaultListener().cleanupPlayer(ps.cache.getPlayer());
	}
	
	public void announce(PluginMessage message) {
		for (PlayerState ps : ingamePlayers.values()) {
			Player p = ps.cache.getPlayer();
			MANAGER.getPlugin().getMessageProvider().sendMessage(p, message);
		}
	}
	
	public void announce(PluginMessage message, Object... formatVars) {
		for (PlayerState ps : ingamePlayers.values()) {
			Player p = ps.cache.getPlayer();
			MANAGER.getPlugin().getMessageProvider().sendMessage(p, message, formatVars);
		}
	}
	
	public PlayerState getPlayerState(Player player) {
		return ingamePlayers.get(player.getUniqueId());
	}

	public ChestObject getChestData(Location location) {
		Optional<ChestObject> oco = CONFIG.chestLocations.stream().filter(co -> co.location.equals(location)).findFirst();
		return oco.orElse(null);
	}
	
	public List<ItemSet> getItemSets() {
		return CONFIG.itemSets;
	}

	public boolean isActive() {
		return flagIsActive;
	}

	public boolean isMovementDisabled() {
		return flagDisableMovement;
	}

	public boolean isPVPEnabled() {
		return flagPVPEnabled;
	}

	public boolean hasDeathmatchStarted() {
		return flagDeathmatchStarted;
	}

	public boolean doChestPrefill() {
		return CONFIG.preFillChests;
	}

	public BorderController getBorderController() {
		if (border != null && border.isProtocol()) {
			return border;
		}
		return null;
	}

	public String getFileName() {
		return CONFIG.getFileName();
	}

	public List<String> getOpenedChests() {
		return openedChests;
	}
}

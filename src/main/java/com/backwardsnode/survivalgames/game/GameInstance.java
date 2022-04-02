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
import com.backwardsnode.survivalgames.config.*;
import com.backwardsnode.survivalgames.dependency.plugin.VaultConnector;
import com.backwardsnode.survivalgames.api.event.*;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.controller.BossBarController;
import com.backwardsnode.survivalgames.controller.ScoreboardController;
import com.backwardsnode.survivalgames.controller.ScoreboardElement;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.message.PluginMessage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class GameInstance {

	private final GameManager MANAGER;
	private final GameConfiguration CONFIG;
	private final PlayerCacheSettings CACHE_SETTINGS;
	private final Map<UUID, PlayerState> INGAME_PLAYERS;
	private final HashSet<String> OPENED_CHESTS;

	private ScoreboardController scoreboard;
	private BossBarController bossbar;
	private BorderController border;
	private DeathmatchConfiguration deathmatchConfig;

	private GameStatus currentStatus;
	private Date startedAt;
	private int initialPlayers = -1;
	private int playersAtDeathmatch = -1;
	private long duration = -1;

	private BukkitTask timerTask;
	private TimerCountdown timerInstance;
	
	private boolean flagIsActive;
	private boolean flagDisableMovement;
	private boolean flagPVPEnabled;
	private boolean flagDeathmatchStarted;
	
	protected GameInstance(GameManager manager, GameConfiguration config, PlayerCacheSettings cacheSettings) throws GameConfigurationException {
		MANAGER = manager;
		CONFIG = config;
		CACHE_SETTINGS = cacheSettings;

		if (config.spawnLocs.size() < 2) {
			throw new GameConfigurationException("Not enough spawn locations");
		}

		INGAME_PLAYERS = new HashMap<>();
		OPENED_CHESTS = new HashSet<>();

		currentStatus = GameStatus.WAITING;
	}
	
	protected void begin(Player initiator, List<Player> players, boolean ignoreIngamePlayers, PlayerSelectionMethod selectorMode) {
		initialPlayers = players.size();

		// filter players already in-game
		boolean acceptsSpectators = selectorMode.allowSpectators();
		players.removeIf(MANAGER::isPlayerIngame);

		// halt if this is "bad"
		if (!ignoreIngamePlayers) {
			if (players.size() != initialPlayers) {
				currentStatus = GameStatus.START_ERR_PLAYER_IN_GAME;
				return;
			}
		}

		// check at least 2 candidates
		if (players.size() < 2) {
			currentStatus = GameStatus.START_ERR_FEW_PLAYERS;
			return;
		}

		// check sufficient player funds
		initialPlayers = 0;
		VaultConnector vault = MANAGER.getPlugin().getDependencyManager().getVaultConnector();
		if (vault != null && CONFIG.entryFee > 0) {
			for (Player pl : players) {
				if (pl.hasPermission("survivalgames.freeentry") || vault.getBalance(pl) >= CONFIG.entryFee){
					initialPlayers++;
				}
			}
		}

		// check at least 2 candidates again
		if (initialPlayers < 2) {
			currentStatus = GameStatus.START_ERR_FEW_PLAYERS;
			return;
		}

		// check if we have too many candidates
		if (initialPlayers > CONFIG.spawnLocs.size()) {
			if (acceptsSpectators) {
				currentStatus = GameStatus.START_SUCCESS_WITH_SPECTATORS;
			} else {
				currentStatus = GameStatus.START_SUCCESS_WITHOUT_SPECTATORS;
			}
		} else {
			currentStatus = GameStatus.START_SUCCESS;
		}

		flagDisableMovement = true;
		flagIsActive = true;
		flagDeathmatchStarted = false;

		int spawnLocationCount = CONFIG.spawnLocs.size();
		if (selectorMode.shouldShuffle()) {
			Collections.shuffle(players);
		}

		boolean allowSpectateWithoutFunds = PluginConfigKeys.PERMIT_SPECTATE_WITHOUT_FUNDS.get(MANAGER.getPlugin().getConfig());

		// process players
		int addedPlayers = 0;
		for (Player pl : players) {
			if (addedPlayers < spawnLocationCount) {
				boolean gotEntry = true;

				if (vault != null && CONFIG.entryFee > 0) {
					if (pl.hasPermission("survivalgames.freeentry")){
						MANAGER.getPlugin().getMessageProvider().sendMessage(pl, Messages.Game.FREE_ENTRY);
					} else if (!vault.withdrawPlayer(pl, CONFIG.entryFee)) {
						if (allowSpectateWithoutFunds && acceptsSpectators) {
							MANAGER.getPlugin().getMessageProvider().sendMessage(pl, Messages.Game.INSUFFICIENT_FUNDS_SPECTATING_INSTEAD);
							gotEntry = false;
						} else {
							MANAGER.getPlugin().getMessageProvider().sendMessage(pl, Messages.Game.INSUFFICIENT_FUNDS);
							continue;
						}
					}
				}

				if (gotEntry) {
					PlayerState ps = new PlayerState(pl, false);
					ps.cache.cacheCurrent(CACHE_SETTINGS);
					INGAME_PLAYERS.put(pl.getUniqueId(), ps);
					updatePlayerTime(pl, false);
					addedPlayers++;

					continue;
				}
			}
			if (acceptsSpectators) {
				PlayerState ps = new PlayerState(pl, true);
				ps.cache.cacheCurrentGamemode();
				INGAME_PLAYERS.put(pl.getUniqueId(), ps);
				updatePlayerTime(pl, false);
			}
		}

		// game init
		initialPlayers = Math.min(spawnLocationCount, initialPlayers);
		preparePlayers();

		setupBorder(players.get(0).getWorld());
		setupScoreboard(initialPlayers);
		setupBossbar();
		deathmatchConfig = CONFIG.selectDeathmatch();

		if (CONFIG.preFillChests) {
			fillChests(initiator);
		}

		// game start
		startCountdownTimer(CONFIG.waitTime, GameStatus.RELEASE_PLAYERS);

		startedAt = new Date();

		GameStartedEvent gameStartedEvent = new GameStartedEvent(this);
		Bukkit.getPluginManager().callEvent(gameStartedEvent);

	}

	private void setupScoreboard(int playersAlive) {
		scoreboard = new ScoreboardController(ChatColor.DARK_AQUA + "[Survival Games]");
		scoreboard.initialize();
		scoreboard.updateScoreboardElement(ScoreboardElement.MAP_NAME, (CONFIG.mapName == null ? "unknown" : CONFIG.mapName));
		scoreboard.updateScoreboardElement(ScoreboardElement.PLAYERS_LEFT, String.valueOf(playersAlive));
		String dim = String.valueOf(Math.floor(border.getRadius()));
		scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, dim + "," + dim + " blocks");
		scoreboard.setVisibleTo(INGAME_PLAYERS.values());
	}

	private void setupBossbar() {
		bossbar = new BossBarController("Game Starting...", BarColor.BLUE, CONFIG.waitTime);
		bossbar.setVisibleTo(INGAME_PLAYERS.values());
	}

	private void setupBorder(World defaultWorld) {
		border = new BorderController(MANAGER.getPlugin().getDependencyManager().getProtocolConnector(), defaultWorld);
		border.setTarget(CONFIG.border.borderStartRadius, 0);
		border.setVisibleTo(INGAME_PLAYERS.values());
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

		currentStatus = status;

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
		for (PlayerState player : INGAME_PLAYERS.values()) {
			Player p = player.cache.getPlayer();
			MANAGER.getPlugin().getMessageProvider().sendMessage(p, Messages.Game.PLAYING_ON, CONFIG.mapName);

			if (player.spectating) {
				p.teleport(CONFIG.spawnLocs.get(0).add(0.5, 0, 0.5));
				p.setGameMode(GameMode.SPECTATOR);
				MANAGER.getPlugin().getMessageProvider().sendMessage(p, Messages.Game.AS_SPECTATOR);
			} else {
				p.teleport(CONFIG.spawnLocs.get(i).add(0.5, 0, 0.5));
				p.setGameMode(GameMode.ADVENTURE);
				i++;
			}
		}
	}

	private void updatePlayerTime(Player player, boolean reset) {
		if (MANAGER.timeControlEnabled()) {
			if (reset) {
				player.resetPlayerTime();
			} else {
				int time = CONFIG.startingDaytime;
				if (time >= 0) {
					player.setPlayerTime(time, CONFIG.daylightCycle);
				}
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
			announce(Messages.Game.PVP_OFF_TIME, CONFIG.gracePeriod);
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
			announce(Messages.Game.PVP_ENABLED);
		}
		bossbar.resetHealth(CONFIG.borderCollapseDelay);
		bossbar.setOptions("PvP Enabled", BarColor.PURPLE);
		timerInstance.setOperation(GameStatus.SHRINK_PLAY_AREA, CONFIG.borderCollapseDelay);
	}
	
	private void shrinkPlayArea() {
		playGlobalPingSound();
		if (deathmatchConfig != null) {
			announce(Messages.Game.BORDER_SHRINKING);
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

			currentStatus = GameStatus.START_DEATHMATCH;

			startDeathmatch();
		}
	}
	
	private void startDeathmatch() {
		playGlobalPingSound();
		flagDeathmatchStarted = true;
		announce(Messages.Game.DEATHMATCH);
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Deathmatch");
		bossbar.resetHealth(deathmatchConfig.deathmatchDuration);
		bossbar.setOptions("Deathmatch", BarColor.RED);
		timerInstance.setOperation(GameStatus.CLOSE_PLAY_AREA, deathmatchConfig.deathmatchDuration);

		playersAtDeathmatch = checkAlive();
	}
	
	private void closeBorder() {
		playGlobalPingSound();
		announce(Messages.Game.BORDER_SHRINKING);
		border.setTarget(1, deathmatchConfig.collapseTime);
		timerInstance.setOperation(GameStatus.FINISH_GAME, deathmatchConfig.collapseTime);
	}
	
	private void fillChests(Player initiator) {
		int missingChests = 0;
		boolean foundBadItem = false;
		for (ChestConfiguration co : CONFIG.chestLocations) {
			Block b = co.location.getBlock();
			if (b.getType() != Material.CHEST || !(b.getState() instanceof Chest chest)) {
				if (missingChests < 5) {
					MANAGER.getPlugin().getMessageProvider().sendMessage(initiator, Messages.Config.CHEST_IGNORE_MISSING, co.loc);
				}
				missingChests++;
				continue;
			}

			if (!co.fill(chest, CONFIG.itemSets)) {
				foundBadItem = true;
			}
		}
		if (missingChests > 0) {
			MANAGER.getPlugin().getMessageProvider().sendMessage(initiator, Messages.Config.CHEST_MISSING_SIMPLE, missingChests);
		}
		if (foundBadItem) {
			MANAGER.getPlugin().getMessageProvider().sendMessage(initiator, Messages.Config.CHEST_BAD_ITEMS);
		}
	}
	
	public void updateMostKills() {
		List<PlayerState> states = INGAME_PLAYERS.values().stream().filter(x -> !x.spectating).sorted((p1, p2) -> Integer.compare(p2.kills, p1.kills)).toList();
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
		for (PlayerState ps : INGAME_PLAYERS.values()) {
			Player p = ps.cache.getPlayer();
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.PLAYERS, 1f, 1f);
		}
	}
	
	public int checkAlive() {
		Player w = null;
		int count = 0;
		for (PlayerState ps : INGAME_PLAYERS.values()) {
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
				announce(Messages.Game.DEATH_GENERIC, player.getDisplayName());
				if (CONFIG.spawnFireworkOnDeath) {
					Utils.spawnRandomFirework(player.getLocation());
				}
			} else {
				announce(Messages.Game.DEATH_KILLED, player.getDisplayName(), killer.getDisplayName());
				if (CONFIG.spawnFireworkOnKill) {
					Utils.spawnRandomFirework(player.getLocation());
				}
				PlayerState ps = getPlayerState(killer);
				if (ps != null) {
					ps.kills += 1;
					updateMostKills();
				}
			}

			if (CONFIG.lightningOnDeath) {
				player.getWorld().strikeLightningEffect(player.getLocation());
			}

			GameDeathEvent gameDeathEvent = new GameDeathEvent(this, player, killer);
			Bukkit.getPluginManager().callEvent(gameDeathEvent);
			return true;
		}
		return false;
	}

	protected boolean processDeathByEntity(Player player, String entity) {
		if (removePlayer(player, false)) {
			announce(Messages.Game.DEATH_KILLED, player.getDisplayName(), entity);
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
		OPENED_CHESTS.clear();
		if (victor != null) {
			announce(Messages.Game.WON, victor);
		}
		ArrayList<PlayerState> listPlayerState = new ArrayList<>(INGAME_PLAYERS.values());
		listPlayerState.sort((p1, p2) -> Integer.compare(p2.kills, p1.kills));
		announce(Messages.Game.KILLSTATS);

		int i = 0;
		for (PlayerState state : listPlayerState) {
			switch (i) {
				case 0 -> announce(Messages.Game.KILLSTAT_1, state.cache.getPlayer().getDisplayName(), state.kills);
				case 1 -> announce(Messages.Game.KILLSTAT_2, state.cache.getPlayer().getDisplayName(), state.kills);
				case 2 -> announce(Messages.Game.KILLSTAT_3, state.cache.getPlayer().getDisplayName(), state.kills);
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
			if (vault != null && rewards.cash != 0) {
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
				MANAGER.getPlugin().getMessageProvider().sendMessage(player, Messages.Game.REWARDS_OVERFLOW);
			}
		}
	}
	
	public boolean removePlayer(Player player, boolean droppedFromSession) {
		UUID uuid = player.getUniqueId();
		PlayerState ps = INGAME_PLAYERS.get(uuid);
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
			INGAME_PLAYERS.remove(uuid);
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
		terminate(INGAME_PLAYERS.values());
	}
	
	private void terminate(Collection<PlayerState> playerStates) {
		if (!flagIsActive) return;
		flagIsActive = false;

		duration = (new Date().getTime() - startedAt.getTime()) / 1000;

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

		updatePlayerTime(ps.cache.getPlayer(), true);

		MANAGER.getPlugin().getDefaultListener().cleanupPlayer(ps.cache.getPlayer());
	}
	
	public void announce(PluginMessage message) {
		for (PlayerState ps : INGAME_PLAYERS.values()) {
			Player p = ps.cache.getPlayer();
			MANAGER.getPlugin().getMessageProvider().sendMessage(p, message);
		}
	}
	
	public void announce(PluginMessage message, Object... formatVars) {
		for (PlayerState ps : INGAME_PLAYERS.values()) {
			Player p = ps.cache.getPlayer();
			MANAGER.getPlugin().getMessageProvider().sendMessage(p, message, formatVars);
		}
	}
	
	public PlayerState getPlayerState(Player player) {
		return INGAME_PLAYERS.get(player.getUniqueId());
	}

	public ChestConfiguration getChestData(Location location) {
		Optional<ChestConfiguration> oco = CONFIG.chestLocations.stream().filter(co -> co.location.equals(location)).findFirst();
		return oco.orElse(null);
	}

	public void tryUnpackLootDrop(Block clicked) {
		// TODO summon loot drops
	}

	public int getInitialPlayerCount() {
		return initialPlayers;
	}

	public Date getStartedAt() {
		return startedAt;
	}

	public long getDuration() {
		return duration;
	}

	public int getPlayersAtDeathmatch() {
		return playersAtDeathmatch;
	}

	public DeathmatchConfiguration getDeathmatchConfig() {
		return deathmatchConfig;
	}

	public List<ItemSet> getItemSets() {
		return CONFIG.itemSets;
	}

	public GameStatus getStatus() {
		return currentStatus;
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

	public HashSet<String> getOpenedChests() {
		return OPENED_CHESTS;
	}

	public GameConfiguration getGameConfiguration() {
		return CONFIG;
	}

	public Iterable<PlayerState> getPlayerStates() {
		return INGAME_PLAYERS.values();
	}

	public int getTeamSize() {
		return 1;
	}
}

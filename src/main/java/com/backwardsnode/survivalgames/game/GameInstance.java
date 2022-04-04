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
import com.backwardsnode.survivalgames.api.event.*;
import com.backwardsnode.survivalgames.config.*;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.controller.BossBarController;
import com.backwardsnode.survivalgames.controller.ScoreboardController;
import com.backwardsnode.survivalgames.controller.ScoreboardElement;
import com.backwardsnode.survivalgames.dependency.plugin.VaultConnector;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.message.PluginMessage;
import com.backwardsnode.survivalgames.world.BlockLocation;
import com.backwardsnode.survivalgames.world.LootDrop;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.IntStream;

public class GameInstance {

	private final GameManager manager;
	private final GameConfigurationWrapper gameConfiguration;
	private final PlayerCacheSettings cacheSettings;
	private final Map<UUID, PlayerState> ingamePlayers;
	private final Set<BlockLocation> openedChests;
	private final Map<BlockLocation, LootDrop> dropsInProgress;
	private final Random random;

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

	private int timeToNextLootDrop;
	private float nextLootDropProbability;
	
	private boolean flagIsActive;
	private boolean flagDisableMovement;
	private boolean flagPVPEnabled;
	private boolean flagDeathmatchStarted;
	
	protected GameInstance(GameManager manager, GameConfigurationWrapper gcw, PlayerCacheSettings cacheSettings) throws GameConfigurationException {
		if (gcw.getSpawnLocations().size() < 2) {
			throw new GameConfigurationException("Not enough spawn locations");
		}

		this.manager = manager;
		gameConfiguration = gcw;
		this.cacheSettings = cacheSettings;

		ingamePlayers = new HashMap<>();
		openedChests = new HashSet<>();
		dropsInProgress = new HashMap<>();
		random = new Random();

		currentStatus = GameStatus.WAITING;
	}
	
	protected void begin(Player initiator, List<Player> players, boolean ignoreIngamePlayers, PlayerSelectionMethod selectorMode) {
		initialPlayers = players.size();

		// filter players already in-game
		boolean acceptsSpectators = selectorMode.allowSpectators();
		players.removeIf(manager::isPlayerIngame);

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
		float entryFee = gameConfiguration.getEntryFee();
		VaultConnector vault = manager.getPlugin().getDependencyManager().getVaultConnector();
		if (vault != null && entryFee > 0) {
			for (Player pl : players) {
				if (pl.hasPermission("survivalgames.freeentry") || vault.getBalance(pl) >= entryFee){
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
		int spawnLocationCount = gameConfiguration.getSpawnLocations().size();
		if (initialPlayers > spawnLocationCount) {
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

		if (selectorMode.shouldShuffle()) {
			Collections.shuffle(players);
		}

		boolean allowSpectateWithoutFunds = PluginConfigKeys.PERMIT_SPECTATE_WITHOUT_FUNDS.get(manager.getPlugin().getConfig());

		// process players
		int addedPlayers = 0;
		for (Player pl : players) {
			if (addedPlayers < spawnLocationCount) {
				boolean gotEntry = true;

				if (vault != null && entryFee > 0) {
					if (pl.hasPermission("survivalgames.freeentry")){
						manager.getPlugin().getMessageProvider().sendMessage(pl, Messages.Game.FREE_ENTRY);
					} else if (!vault.withdrawPlayer(pl, entryFee)) {
						if (allowSpectateWithoutFunds && acceptsSpectators) {
							manager.getPlugin().getMessageProvider().sendMessage(pl, Messages.Game.INSUFFICIENT_FUNDS_SPECTATING_INSTEAD);
							gotEntry = false;
						} else {
							manager.getPlugin().getMessageProvider().sendMessage(pl, Messages.Game.INSUFFICIENT_FUNDS);
							continue;
						}
					}
				}

				if (gotEntry) {
					PlayerState ps = new PlayerState(pl, false);
					ps.cache.cacheCurrent(cacheSettings);
					ingamePlayers.put(pl.getUniqueId(), ps);
					updatePlayerTime(pl, false);
					addedPlayers++;

					continue;
				}
			}
			if (acceptsSpectators) {
				PlayerState ps = new PlayerState(pl, true);
				ps.cache.cacheCurrentGamemode();
				ingamePlayers.put(pl.getUniqueId(), ps);
				updatePlayerTime(pl, false);
			}
		}

		// game init
		initialPlayers = Math.min(spawnLocationCount, initialPlayers);
		preparePlayers();

		setupBorder(players.get(0).getWorld());
		setupScoreboard(initialPlayers);
		setupBossbar();
		deathmatchConfig = gameConfiguration.selectDeathmatch();

		if (gameConfiguration.getDoChestPrefilling()) {
			fillChests(initiator);
		}

		// game start
		startCountdownTimer(gameConfiguration.getWaitPeriod());

		startedAt = new Date();

		GameStartedEvent gameStartedEvent = new GameStartedEvent(this);
		Bukkit.getPluginManager().callEvent(gameStartedEvent);

	}

	private void setupScoreboard(int playersAlive) {
		scoreboard = new ScoreboardController(ChatColor.DARK_AQUA + "[Survival Games]");
		scoreboard.initialize();
		scoreboard.updateScoreboardElement(ScoreboardElement.MAP_NAME, (gameConfiguration.getMapName()));
		scoreboard.updateScoreboardElement(ScoreboardElement.PLAYERS_LEFT, String.valueOf(playersAlive));
		String dim = String.valueOf(Math.floor(border.getDiameter()));
		scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, dim + "," + dim + " blocks");
		scoreboard.setVisibleTo(ingamePlayers.values());
	}

	private void setupBossbar() {
		bossbar = new BossBarController("Game Starting...", BarColor.BLUE, gameConfiguration.getWaitPeriod());
		bossbar.setVisibleTo(ingamePlayers.values());
	}

	private void setupBorder(World defaultWorld) {
		border = new BorderController(manager.getPlugin().getDependencyManager().getProtocolConnector(), defaultWorld);
		border.setTarget(gameConfiguration.getBorderStartDiameter(), 0);
		border.setVisibleTo(ingamePlayers.values());
	}
	
	private void startCountdownTimer(int seconds) {
		if (timerTask == null) {
			timerInstance = new TimerCountdown(this);
			timerInstance.setOperation(GameStatus.RELEASE_PLAYERS, seconds);
			timerTask = Bukkit.getScheduler().runTaskTimer(manager.getPlugin(), timerInstance, 20, 20);
		}
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
		// Update time left on tick
		if (remaining >= 0) {
			scoreboard.updateScoreboardElement(ScoreboardElement.TIME_LEFT, String.format("%02d:%02d", remaining / 60, remaining % 60));
			bossbar.setHealth(remaining);
		}

		double diameter = border.getDiameter();

		// Update zone size on two-second tick
		if (nextOperation == GameStatus.CLOSE_PLAY_AREA && remaining % 2 == 0) {
			String dim = String.valueOf(Math.floor(diameter));
			scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, dim + "," + dim + " blocks");
		}

		// Damage on tick
		if (deathmatchConfig != null) {
			double radius = diameter / 2;
			double dps = getGameConfiguration().getBorderDPS();
			double centerX = deathmatchConfig.getCenterX();
			double centerZ = deathmatchConfig.getCenterZ();

			for (PlayerState playerState : ingamePlayers.values()) {
				if (playerState.alive) {
					Player player = playerState.cache.getPlayer();
					Location location = player.getLocation();

					if (Math.abs(centerX - location.getX()) > radius || Math.abs(centerZ - location.getZ()) > radius) {
						player.damage(dps);
					}
				}
			}
		}

		// Decrement loot drop timer on tick
		if (--timeToNextLootDrop == 0) {
			if (nextLootDropProbability > 0 && nextLootDropProbability > random.nextFloat()) {
				if (summonLootDrop(true)) {
					nextLootDropProbability = getGameConfiguration().getLootDropTriggerProbability();
				} else {
					nextLootDropProbability += getGameConfiguration().getLootDropTriggerProbabilityIncrement();
				}
			}
		}
	}

	public boolean summonLootDrop(boolean changeTimer) {
		boolean dropped = false;

		List<LootDropConfiguration> lootDrops = getGameConfiguration().getLootDropLocations();
		int selectionSize = lootDrops.size();
		if (selectionSize > 0) {
			List<Integer> selectionOrder = IntStream.range(0, selectionSize).boxed().toList();
			Collections.shuffle(selectionOrder, random);

			for (int i : selectionOrder) {
				LootDropConfiguration lootDropConfiguration = lootDrops.get(i);
				if (!dropsInProgress.containsKey(lootDropConfiguration.location)) {
					announce(Messages.Game.LOOT_DROPPING);

					LootDrop lootDrop = manager.getPlugin().getHost().getLootDropManager().summonLootDrop(lootDropConfiguration, true);
					dropsInProgress.put(lootDropConfiguration.location, lootDrop);

					dropped = true;
					break;
				}
			}
		}

		int window = getGameConfiguration().getLootDropTriggerWindow();
		if (changeTimer) {
			if (dropped) {
				timeToNextLootDrop = getGameConfiguration().getLootDropDelay();
				if (timeToNextLootDrop > 0 && window > 0) {
					timeToNextLootDrop += random.nextInt(window);
				}
			} else {
				timeToNextLootDrop = window == 0 ? 1 : random.nextInt(window);
			}
		}
		return dropped;
	}

	private void preparePlayers() {
		int i = 0;
		for (PlayerState player : ingamePlayers.values()) {
			Player p = player.cache.getPlayer();
			manager.getPlugin().getMessageProvider().sendMessage(p, Messages.Game.PLAYING_ON, gameConfiguration.getMapName());

			List<BlockLocation> spawnLocations = gameConfiguration.getSpawnLocations();
			Location commonSpawnLocation = spawnLocations.get(0).toBukkitLocation().add(0.5, 0, 0.5);
			if (player.spectating) {
				p.teleport(commonSpawnLocation);
				p.setGameMode(GameMode.SPECTATOR);
				manager.getPlugin().getMessageProvider().sendMessage(p, Messages.Game.AS_SPECTATOR);
			} else {
				p.teleport(spawnLocations.get(i).toBukkitLocation().add(0.5, 0, 0.5));
				p.setGameMode(GameMode.ADVENTURE);
				i++;
			}
		}
	}

	private void updatePlayerTime(Player player, boolean reset) {
		if (manager.timeControlEnabled()) {
			if (reset) {
				player.resetPlayerTime();
			} else {
				int time = gameConfiguration.getStartingDaytime();
				if (time >= 0) {
					player.setPlayerTime(time, gameConfiguration.getDoDaylightCycle());
				}
			}
		}
	}
	
	private void releasePlayers() {
		flagDisableMovement = false;
		playGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING);

		int gracePeriod = gameConfiguration.getGracePeriod();
		if (gracePeriod <= 0) {
			enablePVP(false);
		} else {
			scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "PvP Off");
			announce(Messages.Game.PVP_OFF_TIME, gracePeriod);
			bossbar.resetHealth(gracePeriod);
			bossbar.setOptions("PvP Disabled", BarColor.YELLOW);
			timerInstance.setOperation(GameStatus.ENABLE_PVP, gracePeriod);
		}
	}
	
	private void enablePVP(boolean shouldAnnounce) {
		flagPVPEnabled = true;
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "PvP On");
		if (shouldAnnounce) {
			playGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING);
			announce(Messages.Game.PVP_ENABLED);
		}
		bossbar.resetHealth(gameConfiguration.getPreShrinkPeriod());
		bossbar.setOptions("PvP Enabled", BarColor.PURPLE);
		timerInstance.setOperation(GameStatus.SHRINK_PLAY_AREA, gameConfiguration.getPreShrinkPeriod());
	}
	
	private void shrinkPlayArea() {
		playGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING);
		if (deathmatchConfig != null) {
			announce(Messages.Game.BORDER_SHRINKING);
			scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Border Shrinking");
			border.setTarget(deathmatchConfig.getCenterX(), deathmatchConfig.getCenterZ(), deathmatchConfig.borderDiameter, deathmatchConfig.shrinkTime);
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
		playGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING);
		flagDeathmatchStarted = true;
		announce(Messages.Game.DEATHMATCH);
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Deathmatch");
		bossbar.resetHealth(deathmatchConfig.deathmatchDuration);
		bossbar.setOptions("Deathmatch", BarColor.RED);
		timerInstance.setOperation(GameStatus.CLOSE_PLAY_AREA, deathmatchConfig.deathmatchDuration);

		playersAtDeathmatch = checkAlive();
	}
	
	private void closeBorder() {
		playGlobalSound(Sound.BLOCK_NOTE_BLOCK_PLING);
		announce(Messages.Game.BORDER_SHRINKING);
		border.setTarget(1, deathmatchConfig.collapseTime);
		timerInstance.setOperation(GameStatus.FINISH_GAME, deathmatchConfig.collapseTime);
	}
	
	private void fillChests(Player initiator) {
		int missingChests = 0;
		boolean foundBadItem = false;
		for (ChestConfiguration co : gameConfiguration.getChests()) {
			Block b = co.location.getBlock();
			if (b.getType() != Material.CHEST || !(b.getState() instanceof Chest chest)) {
				if (missingChests < 5) {
					manager.getPlugin().getMessageProvider().sendMessage(initiator, Messages.Config.CHEST_IGNORE_MISSING, co.location.toString());
				}
				missingChests++;
				continue;
			}

			if (!Utils.fillChest(chest, gameConfiguration.getItemSets(), co.itemSets)) {
				foundBadItem = true;
			}
		}
		if (missingChests > 0) {
			manager.getPlugin().getMessageProvider().sendMessage(initiator, Messages.Config.CHEST_MISSING_SIMPLE, missingChests);
		}
		if (foundBadItem) {
			manager.getPlugin().getMessageProvider().sendMessage(initiator, Messages.Config.CHEST_BAD_ITEMS);
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
				default -> { return; }
			}
			i++;
		}
	}
	
	public void playGlobalSound(Sound sound) {
		for (PlayerState ps : ingamePlayers.values()) {
			Player p = ps.cache.getPlayer();
			p.playSound(p.getLocation(), sound, SoundCategory.PLAYERS, 1f, 1f);
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
			endGame(count == 1 ? w.getDisplayName() : null);
			return -1;
		}
		return count;
	}
	
	protected boolean processDeath(Player player, Player killer) {
		return processDeathInternal(player, killer, null);
	}

	protected boolean processDeathByEntity(Player player, String entity) {
		return processDeathInternal(player, null, entity);
	}

	private boolean processDeathInternal(Player player, Player killer, String otherEntity) {
		if (removePlayer(player, false)) {
			boolean firework;
			if (killer == null) {
				announce(Messages.Game.DEATH_GENERIC, player.getDisplayName());
				firework = gameConfiguration.getSpawnFireworkOnDeath();
			} else if (otherEntity != null) {
				announce(Messages.Game.DEATH_KILLED, player.getDisplayName(), otherEntity);
				firework = gameConfiguration.getSpawnFireworkOnDeath();
			} else {
				announce(Messages.Game.DEATH_KILLED, player.getDisplayName(), killer.getDisplayName());
				firework = gameConfiguration.getSpawnFireworkOnKill();
				PlayerState ps = getPlayerState(killer);
				if (ps != null) {
					ps.kills += 1;
					updateMostKills();
				}
			}
			if (firework) {
				Utils.spawnRandomFirework(player.getLocation());
			}

			if (gameConfiguration.getLightningOnDeath()) {
				player.getWorld().strikeLightningEffect(player.getLocation());
			}

			GameDeathEvent gameDeathEvent = new GameDeathEvent(this, player, killer);
			Bukkit.getPluginManager().callEvent(gameDeathEvent);
			return true;
		}
		return false;
	}
	
	public void endGame(String victor) {
		openedChests.clear();
		if (victor != null) {
			announce(Messages.Game.WON, victor);
		}
		ArrayList<PlayerState> listPlayerState = new ArrayList<>(ingamePlayers.values());
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
		terminate();
	}

	public void rewardPlayer(PlayerState state) {
		RewardConfiguration rewards = gameConfiguration.getReward(state.placement);

		if (rewards == null) {
			rewards = new RewardConfiguration();
		} else {
			rewards = rewards.deepCopy();
		}

		Player player = state.cache.getPlayer();
		GameRewardEvent gameRewardEvent = new GameRewardEvent(this, player, state.placement, rewards);
		Bukkit.getPluginManager().callEvent(gameRewardEvent);

		if (!gameRewardEvent.isCancelled()) {
			Location location = player.getLocation();
			VaultConnector vault = manager.getPlugin().getDependencyManager().getVaultConnector();
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
				manager.getPlugin().getMessageProvider().sendMessage(player, Messages.Game.REWARDS_OVERFLOW);
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
				player.teleport(gameConfiguration.getSpawnLocations().get(0).toBukkitLocation());
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
		Collection<PlayerState> playerStates = ingamePlayers.values();

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
		manager.onGameFinished(this);
	}
	
	private void restoreState(PlayerState ps) {
		if (!ps.spectating) {
			ps.cache.restore(cacheSettings);
		} else if (cacheSettings.cacheGamemode) {
			ps.cache.restoreGamemode();
		}

		updatePlayerTime(ps.cache.getPlayer(), true);

		manager.getPlugin().getDefaultListener().cleanupPlayer(ps.cache.getPlayer());
	}
	
	public void announce(PluginMessage message, Object... formatVars) {
		for (PlayerState ps : ingamePlayers.values()) {
			Player p = ps.cache.getPlayer();
			manager.getPlugin().getMessageProvider().sendMessage(p, message, formatVars);
		}
	}
	
	public PlayerState getPlayerState(Player player) {
		return ingamePlayers.get(player.getUniqueId());
	}

	public void tryUnpackLootDrop(Block clicked) {
		// TODO check this works
		LootDrop lootDrop = dropsInProgress.remove(new BlockLocation(clicked.getLocation()));
		if (lootDrop != null) {
			lootDrop.popAndClose(getGameConfiguration().getItemSets());
		}
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

	public BorderController getBorderController() {
		return border;
	}

	public Set<BlockLocation> getOpenedChests() {
		return openedChests;
	}

	public GameConfigurationWrapper getGameConfiguration() {
		return gameConfiguration;
	}

	public Iterable<PlayerState> getPlayerStates() {
		return ingamePlayers.values();
	}

	public int getTeamSize() {
		return 1;
	}

    public void confirmationResolution(Player player, boolean confirm) {
		// TODO action confirmation
    }
}

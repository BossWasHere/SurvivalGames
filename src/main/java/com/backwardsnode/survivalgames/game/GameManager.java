/*
 *  BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 *  Copyright (C) 2019 BackwardsNode/BossWasHere
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.exception.GameRunningException;
import com.backwardsnode.survivalgames.game.GameManager.PlayerState;
import com.backwardsnode.survivalgames.game.ScoreboardController.ScoreboardElement;
import com.backwardsnode.survivalgames.util.WorldUtil;
import com.google.common.base.Preconditions;

public class GameManager {

	private final Plugin plugin;
	protected final GameConfiguration config;
	private ScoreboardController scoreboard;
	private BossBarController bossbar;
	Map<String, PlayerState> players;
	
	private WorldBorder border;
	private double pre_borderRadius;
	private Location pre_borderCentre;
	private int borderStartRadius;
	private DeathmatchProfile deathmatchSettings;
	
	private static GameManagerListener listener;
	
	public boolean isActive, noMovement, pvpEnabled, preFillChests;
	public final String mapName;
	protected List<String> openedChests;
	
	private GameManager(Plugin plugin, GameConfiguration config) {
		this.plugin = plugin;
		listener = new GameManagerListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
		this.config = config;
		plugin.gameManager = this;
		this.mapName = config.mapName;
		this.openedChests = new ArrayList<String>();
		border = config.spawnLocs.get(0).getWorld().getWorldBorder();
		pre_borderRadius = border.getSize();
		pre_borderCentre = border.getCenter();
		int endLocations = config.border.deathmatchLocations.size();
		if (endLocations > 0) {
			deathmatchSettings = config.border.deathmatchLocations.get(new Random().nextInt(endLocations));
		}
		borderStartRadius = config.border.borderStartRadius < 10 ? 500 : config.border.borderStartRadius;
		border.setDamageBuffer(0);
		border.setDamageAmount(config.border.damagePerSecond);
	}
	
	public static GameManager createInstance(Plugin plugin, GameConfiguration config) throws GameRunningException, GameConfigurationException {
		if (plugin.gameManager != null) {
			if (plugin.gameManager.isActive) {
				throw new GameRunningException("Game on map " + plugin.gameManager.mapName + " is already running!");
			}
		}
		if (config.spawnLocs.size() < 2) {
			throw new GameConfigurationException("There are too few spawn locations");
		}
		return new GameManager(plugin, config);
	}
	
	public void begin(Player instigator) {
		begin(instigator, new ArrayList<Player>(Bukkit.getOnlinePlayers()));
	}
	
	public void begin(Player instigator, List<Player> players) {
		if (players.size() < 2) {
			instigator.sendMessage(ChatColor.RED + "You cannot start a game with less than 2 people!");
			return;
		}
		if (players.size() > config.spawnLocs.size()) {
			instigator.sendMessage(ChatColor.RED + "There are more players than there are spawn locations!\nSome players will be spectators");
		}
		noMovement = false;
		isActive = true;
		this.players = new HashMap<String, PlayerState>();
		Collections.shuffle(players);
		for (int i = 0; i < players.size(); i++) {
			if (i < config.spawnLocs.size()) {
				this.players.put(players.get(i).getName(), new PlayerState(players.get(i).getName()));
				players.get(i).getInventory().clear();
			} else {
				Player spec = players.get(i);
				spec.sendMessage(ChatColor.GREEN + "You will be a spectator for this round");
				spec.setGameMode(GameMode.SPECTATOR);
				spec.teleport(config.spawnLocs.get(0));
			}
		}
		setupScoreboard();
		fillChests(instigator, config.preFillChests);
		teleportPlayers();
		bossbar = BossBarController.initBossBar("Game Starting...", BarColor.BLUE, config.waitTime);
		bossbar.addPlayers(Bukkit.getOnlinePlayers());
		border.setSize(borderStartRadius * 2);
		noMovement = true;
		scoreboard.setTimer(plugin, config.waitTime, new Runnable() {
			@Override
			public void run() {
				bossbar.setHealth(bossbar.getHealth() - 1);
			}
		}, new Runnable() {
			public void run() {
				releasePlayers();
			}
		});
	}
	
	protected void releasePlayers() {
		pvpEnabled = false;
		noMovement = false;
		if (config.gracePeriod < 1) {
			countdownDeathmatch();
		} else {
			playGlobalPingSound();
			scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "PvP Off");
			announce(ChatColor.DARK_AQUA + "[!] PvP is disabled for " + config.gracePeriod + " seconds!");
			bossbar.resetHealth(config.gracePeriod);
			bossbar.setOptions("PvP Disabled", BarColor.YELLOW);
			scoreboard.setTimer(plugin, config.gracePeriod, new Runnable() {
				@Override
				public void run() {
					bossbar.setHealth(bossbar.getHealth() - 1);
				}
			}, new Runnable() {
				@Override
				public void run() {
					countdownDeathmatch();
				}
			});
		}
	}
	
	protected void countdownDeathmatch() {
		playGlobalPingSound();
		announce(ChatColor.DARK_AQUA + "[!] PvP enabled!");
		pvpEnabled = true;
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Active");
		bossbar.resetHealth(config.border.deathmatchTime);
		bossbar.setOptions("PvP Enabled", BarColor.PURPLE);
		scoreboard.setTimer(plugin, config.border.deathmatchTime, new Runnable() {
			@Override
			public void run() {
				bossbar.setHealth(bossbar.getHealth() - 1);
			}
		}, new Runnable() {
			@Override
			public void run() {
				shrinkPlayArea();
			}
		});
	}
	
	protected void shrinkPlayArea() {
		if (deathmatchSettings != null) {
			playGlobalPingSound();
			announce(ChatColor.DARK_AQUA + "[!] The border is now shrinking!");
			scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Border Shrinking");
			border.setCenter(WorldUtil.locationFromString(deathmatchSettings.loc, true));
			border.setSize(deathmatchSettings.borderRadius * 2, deathmatchSettings.shrinkTime);
			bossbar.resetHealth(deathmatchSettings.shrinkTime);
			bossbar.setOptions("Stay in the zone!", BarColor.PINK);
			scoreboard.setTimer(plugin, deathmatchSettings.shrinkTime, new Runnable() {
				@Override
				public void run() {
					String borderSize = border.getSize() + "";
					bossbar.setHealth(bossbar.getHealth() - 1);
					scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, ("" + border.getSize()).substring(0, borderSize.indexOf('.')) + " blocks");
				}
				
			}, new Runnable() {
				@Override
				public void run() {
					startDeathmatch();
				}
			});
		} else {
			announce(ChatColor.AQUA + "[!] Game ended, as there is no deathmatch arena");
			terminate();
		}
	}
	
	public void forceDeathmatch() {
		scoreboard.detachTimer();
		shrinkPlayArea();
	}
	
	protected void startDeathmatch() {
		playGlobalPingSound();
		announce(ChatColor.DARK_AQUA + "[!] Deathmatch has started!");
		scoreboard.updateScoreboardElement(ScoreboardElement.STATUS, "Deathmatch");
		bossbar.resetHealth(deathmatchSettings.deathmatchTime);
		bossbar.setOptions("Deathmatch", BarColor.RED);
		scoreboard.setTimer(plugin, deathmatchSettings.deathmatchTime, new Runnable() {
			@Override
			public void run() {
				bossbar.setHealth(bossbar.getHealth() - 1);
			}
		}, new Runnable() {
			@Override
			public void run() {
				closeBorder();
			}
		});
	}
	
	protected void closeBorder() {
		playGlobalPingSound();
		announce(ChatColor.DARK_AQUA + "[!] The border is now shrinking!");
		border.setSize(1, 60);
		scoreboard.setTimer(plugin, 60,  new Runnable() {
			
			@Override
			public void run() {
				String borderSize = border.getSize() + "";
				scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, ("" + border.getSize()).substring(0, borderSize.indexOf('.')) + " blocks");
			}
			
		}, null);
	}
	
	private void fillChests(Player instigator, boolean prefill) {
		if (prefill) {
			preFillChests = true;
			for (ChestObject co : config.chestLocations) {
				Block b = co.location.getBlock();
				if (b.getType() != Material.CHEST || !(b.getState() instanceof Chest)) {
					instigator.sendMessage(ChatColor.RED + "[!] Ignoring missing chest @ " + co.loc);
					continue;
				}
				Chest chest = (Chest) b.getState();
				List<ItemSet> selector = new ArrayList<ItemSet>();
				for (ItemSet set : config.itemSets) {
					if (co.itemSets.contains(set.name)) {
						selector.add(set);
					}
				}
				List<ItemModel> shuffler = new ArrayList<ItemModel>();
				for (ItemSet select : selector) {
					select.items.forEach(item -> shuffler.add(item));
				}
				Collections.shuffle(shuffler);
				Random r = new Random();
				int m = r.nextInt(4) + 2;
				int[] slots = getRandomSlots(m, 27);
				chest.setCustomName("Loot Chest");
				chest.update(true);
				Inventory blockInv = chest.getBlockInventory();
				blockInv.clear();
				for (int i = 0; i < shuffler.size() && i < m; i++) {
					ItemStack item = shuffler.get(i).getEquivalent();
					if (item == null) {
						Bukkit.getLogger().warning("Unknown item [" + shuffler.get(i).id + "], you should check the config file");
						continue;
					}
					item.setAmount(r.nextInt(shuffler.get(i).max) + 1);
					blockInv.setItem(slots[i], item);
				}
			}
		}
	}
	
	/**
	 * Gets an array of UNIQUE randomly generated numbers below the maximum value
	 * @param count the number of values to return
	 * @param max exclusive, the maximum value which can be randomised
	 * @return an array of values
	 * @throws IllegalArgumentException if max < count
	 */
	static int[] getRandomSlots(int count, int max) throws IllegalArgumentException {
		if (max < count) {
			throw new IllegalArgumentException("Cannot create enough unique values");
		}
		int[] a = new int[count];
		Random r = new Random();
		int next;
		for (int b = 0; b < count; b++) {
			do {
				next = r.nextInt(max);
			} while (arrayContains(a, next));
			a[b] = next;
		}
		return a;
	}
	
	private static boolean arrayContains(int[] arr, int check) {
		return Arrays.stream(arr).anyMatch(a -> a==check);
	}
	
	private void setupScoreboard() {
		
		scoreboard = new ScoreboardController(ChatColor.DARK_AQUA + "[Survival Games]", getPlayersByName(players.keySet()));
		scoreboard.initialize();
		scoreboard.updateScoreboardElement(ScoreboardElement.MAP_NAME, (config.mapName == null ? "unknown" : config.mapName));
		scoreboard.updateScoreboardElement(ScoreboardElement.PLAYERS_LEFT, "" + players.values().stream().filter(p -> p.alive).count());
		scoreboard.updateScoreboardElement(ScoreboardElement.ZONE_SIZE, border.getSize() + " blocks");
	}
	
	public void updateMostKills() {
		List<PlayerState> states = new ArrayList<PlayerState>(players.values());
		Collections.sort(states, (p1, p2) -> Integer.compare(p2.kills, p1.kills));
		int i = 0;
		for (PlayerState state : states) {
			switch (i) {
			case 0:
				scoreboard.updateScoreboardElement(ScoreboardElement.MOST_KILLS_1, state.name + ": " + state.kills);
				break;
			case 1:
				scoreboard.updateScoreboardElement(ScoreboardElement.MOST_KILLS_2, state.name + ": " + state.kills);
				break;
			case 2:
				scoreboard.updateScoreboardElement(ScoreboardElement.MOST_KILLS_3, state.name + ": " + state.kills);
				break;
			default:
				return;
			}
			i++;
		}
	}
	
	public List<Player> getPlayersByName(Set<String> playerNames) {
		List<Player> p = new ArrayList<Player>();
		for (String name : playerNames) {
			Player pl = Bukkit.getPlayer(name);
			if (pl != null) {
				p.add(pl);
			}
		}
		return p;
	}
	
	private void playGlobalPingSound() {
		Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1f, 1f));
	}
	
	private void teleportPlayers() {
		List<Player> alive = getPlayersByName(players.keySet());
		for (int i = 0; i < players.size(); i++) {
			alive.get(i).setGameMode(GameMode.ADVENTURE);
			alive.get(i).teleport(config.spawnLocs.get(i).add(0.5, 0, 0.5));
		}
		
	}
	
	public boolean isAlive(Player player) {
		PlayerState s = players.get(player.getName());
		if (s != null) {
			return s.alive;
		}
		return false;
	}
	
	public int checkAlive() {
		int count = (int)players.values().stream().filter(p -> p.alive).count();
		if (count < 2) {
			if (count == 1) {
				players.values().stream().filter(p -> p.alive).forEach(p -> endGame(p.name));
			} else {
				endGame(null);
			}
			return -1;
		}
		return count;
	}
	
	public boolean processDeath(Player player, Player damager) {
		if (removePlayer(player, false)) {
			WorldUtil.spawnRandomFirework(player.getLocation());
			if (damager == null) {
				announce(ChatColor.DARK_AQUA + "[Survival Games] " + player.getName() + " has died!");
			} else {
				announce(ChatColor.DARK_AQUA + "[Survival Games] " + player.getName() + " was killed by " + damager.getName() + "!");
				PlayerState ps = players.get(damager.getName());
				if (ps != null) {
					ps.kills += 1;
					updateMostKills();
				}
			}
			player.setGameMode(GameMode.SPECTATOR);
			int alive = checkAlive();
			if (alive > 1) {
				scoreboard.updateScoreboardElement(ScoreboardElement.PLAYERS_LEFT, "" + alive);
			}
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(Player player, boolean force) {
		if (isAlive(player)) {
			if (force) {
				for (ItemStack item : player.getInventory().getContents()) {
					player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
				}
			}
			player.getInventory().clear();
			players.get(player.getName()).alive = false;
			return true;
		}
		return false;
	}
	
	public void announce(String message) {
		Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
	}
	
	public void endGame(String victor) {
		openedChests.clear();
		if (victor != null) {
			announce(ChatColor.GREEN + "[Survival Games] Player " + victor + " won the game!");
		}
		List<PlayerState> states = new ArrayList<PlayerState>(players.values());
		for (String player : players.keySet()) {
			Player p = Bukkit.getPlayer(player);
			if (p != null) {
				p.getInventory().clear();
			}
		}
		Collections.sort(states, (p1, p2) -> Integer.compare(p2.kills, p1.kills));
		int i = 0;
		announce(ChatColor.DARK_AQUA + "Kill leaders last game:");
		for (PlayerState state : states) {
			switch (i) {
			case 0:
				announce(ChatColor.BLUE + "1st - " + state.name + ": " + state.kills + " kills");
				break;
			case 1:
				announce(ChatColor.BLUE + "2nd - " + state.name + ": " + state.kills + " kills");
				break;
			case 2:
				announce(ChatColor.BLUE + "3rd - " + state.name + ": " + state.kills + " kills");
				break;
			default:
				break;
			}
			i++;
		}
		terminate();
	}
	
	public void terminate() {
		terminate(false);
	}
	
	public void terminate(boolean onDisable) {
		scoreboard.detachTimer();
		if (!onDisable) {
			scoreboard.deleteScoreboard();
		}
		border.setCenter(pre_borderCentre);
		border.setSize(pre_borderRadius);
		bossbar.release();
		HandlerList.unregisterAll(listener);
		isActive = false;
	}
	
	class PlayerState {
		final String name;
		boolean alive;
		int kills;
		
		public PlayerState(String name) {
			this.name = name;
			alive = true;
			kills = 0;
		}
	}
}

class GameManagerListener implements Listener {
	
	private final GameManager handler;
	
	public GameManagerListener(GameManager handler) {
		Preconditions.checkNotNull(handler);
		this.handler = handler;
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (handler.isActive && !handler.config.preFillChests) {
			if (e.getClickedBlock() == null) {
				return;
			}
			for (ChestObject co : handler.config.chestLocations) {
				Block b = e.getClickedBlock();
				if (co.location.equals(b.getLocation()) && b.getType() == Material.CHEST) {
					if (!handler.openedChests.contains(b.toString())) {
						handler.openedChests.add(b.toString());
						Chest chest = (Chest) b.getState();
						List<ItemSet> selector = new ArrayList<ItemSet>();
						for (ItemSet set : handler.config.itemSets) {
							if (co.itemSets.contains(set.name)) {
								selector.add(set);
							}
						}
						List<ItemModel> shuffler = new ArrayList<ItemModel>();
						for (ItemSet select : selector) {
							select.items.forEach(item -> shuffler.add(item));
						}
						Collections.shuffle(shuffler);
						Random r = new Random();
						int m = r.nextInt(4) + 2;
						int[] slots = GameManager.getRandomSlots(m, 27);
						chest.setCustomName("Loot Chest");
						chest.update(true);
						Inventory blockInv = chest.getBlockInventory();
						blockInv.clear();
						for (int i = 0; i < shuffler.size() && i < m; i++) {
							ItemStack item = shuffler.get(i).getEquivalent();
							if (item == null) {
								Bukkit.getLogger().warning("Unknown item [" + shuffler.get(i).id + "], you should check the config file");
								continue;
							}
							item.setAmount(r.nextInt(shuffler.get(i).max) + 1);
							blockInv.setItem(slots[i], item);
						}
						return;
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		if (handler.isActive && handler.noMovement) {
			PlayerState state = handler.players.get(e.getPlayer().getName());
			if (state != null) {
				double x1 = e.getTo().getX();
				double z1 = e.getTo().getZ();
				double x2 = e.getFrom().getX();
				double z2 = e.getFrom().getZ();
				if (x1 != x2 || z1 != z2) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (handler.isActive) {
			if (e.getEntity() instanceof Player) {
				if (e.getDamager().getType() == EntityType.FIREWORK) {
					e.setCancelled(true);
					return;
				}
				Player p = (Player) e.getEntity();
				if (handler.players.get(p.getName()) != null && !handler.pvpEnabled) {
					e.setCancelled(true);
					return;
				}
				if (p.getHealth() - e.getFinalDamage() <= 0) {
					if (e.getDamager() instanceof Player) {
						handler.processDeath(p, (Player)e.getDamager());
					} else {
						handler.processDeath(p, null);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamageOtherEvent(EntityDamageByBlockEvent e) {
		if (handler.isActive) {
			if (e.getEntity() instanceof Player) {
				Player p = (Player) e.getEntity();
				if (p.getHealth() - e.getFinalDamage() <= 0) {
					handler.processDeath(p, null);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		if (handler.isActive) {
			handler.removePlayer(e.getPlayer(), true);
		}
	}
}
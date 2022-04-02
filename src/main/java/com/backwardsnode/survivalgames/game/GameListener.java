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
import com.backwardsnode.survivalgames.config.PluginConfigKeys;
import com.backwardsnode.survivalgames.config.ChestConfiguration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class GameListener implements Listener {

	private final GameManager manager;
	
	private HashMap<UUID, Player> damagingEffects;

	private final boolean preventSpectatorInventoryViewing;
	
	public GameListener(GameManager manager) {
		this.manager = manager;
		damagingEffects = new HashMap<>();
		preventSpectatorInventoryViewing = !PluginConfigKeys.ALLOW_SPECTATORS_SEE_INVENTORY.get(manager.getPlugin().getConfig());
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Block clicked = e.getClickedBlock();
		if (clicked == null) {
			return;
		}
		GameInstance instance = manager.getGame(e.getPlayer());
		if (instance == null) {
			return;
		}
		if (instance.isActive()) {
			if (clicked.getType() == Material.ENDER_CHEST) {
				instance.tryUnpackLootDrop(clicked);
				e.setCancelled(true);

			} else if (!instance.doChestPrefill() && clicked.getState() instanceof Chest) {
				ChestConfiguration co = instance.getChestData(clicked.getLocation());
				if (co != null) {
					HashSet<String> openedChests = instance.getOpenedChests();
					if (!openedChests.contains(co.loc)) {
						openedChests.add(co.loc);
						Chest chest = (Chest) clicked.getState();
						co.fill(chest, instance.getItemSets());
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent e) {
		if (preventSpectatorInventoryViewing) {
			return;
		}
		Entity rightClicked = e.getRightClicked();
		if (!(rightClicked instanceof Player clicked)) {
			return;
		}

		Player player = e.getPlayer();
		GameInstance instance = manager.getGame(player);
		if (instance == null) {
			return;
		}

		PlayerState playerState = instance.getPlayerState(player);
		if (playerState == null || playerState.alive) {
			return;
		}
		PlayerState clickedState = instance.getPlayerState(clicked);
		if (clickedState == null) {
			return;
		}

		// TODO safer to copy inventory?
		playerState.spectatingInventory = true;
		player.openInventory(clicked.getInventory());
	}

	@EventHandler
	public void onInventoryInteractEvent(InventoryInteractEvent e) {
		if (!(e.getWhoClicked() instanceof Player player)) {
			return;
		}
		GameInstance instance = manager.getGame(player);
		if (instance == null) {
			return;
		}

		PlayerState playerState = instance.getPlayerState(player);
		if (playerState == null) {
			return;
		}

		if (playerState.spectatingInventory) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClosedEvent(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player player)) {
			return;
		}
		GameInstance instance = manager.getGame(player);
		if (instance == null) {
			return;
		}

		PlayerState playerState = instance.getPlayerState(player);
		if (playerState == null) {
			return;
		}

		playerState.spectatingInventory = false;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMoveEvent(PlayerMoveEvent e) {
		GameInstance instance = manager.getGame(e.getPlayer());
		if (instance == null) {
			return;
		}
		if (instance.isActive() && instance.isMovementDisabled()) {
			double x1 = e.getTo().getX();
			double z1 = e.getTo().getZ();
			double x2 = e.getFrom().getX();
			double z2 = e.getFrom().getZ();
			if (x1 != x2 || z1 != z2) {
				e.setCancelled(true);
			}
		}
	}
	
	/*
	 * When damaged by another player or an entity
	 */
	@EventHandler
	public void onPlayerDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof Player damaged)) {
			return;
		}
		GameInstance instance = manager.getGame(damaged);
		if (instance == null) {
			return;
		}
		if (instance.isActive()) {
			if (e.getDamager().getType().equals(EntityType.FIREWORK)) {
				e.setCancelled(true);
				return;
			}
			if (e.getDamager() instanceof Player damager) {
				if (instance.isPVPEnabled()) {
					if (damaged.getHealth() - e.getFinalDamage() <= 0) {
						instance.processDeath(damaged, damager);
					}
				} else {
					e.setCancelled(true);
				}
				return;
			}
			if (damaged.getHealth() - e.getFinalDamage() <= 0) {
				instance.processDeathByEntity(damaged, Utils.capitalizeEnumName(e.getDamager().getType()));
			}
		}
	}
	
	/*
	 * When damaged by a block
	 */
	@EventHandler
	public void onPlayerBlockDamageEvent(EntityDamageByBlockEvent e) {
		if (!(e.getEntity() instanceof Player damaged)) {
			return;
		}
		GameInstance instance = manager.getGame(damaged);
		if (instance == null) {
			return;
		}
		if (instance.isActive()) {
			if (damaged.getHealth() - e.getFinalDamage() <= 0) {
				instance.processDeath(damaged, null);
			}
		}
	}
	
	/*
	 * When damaged by potion effect previously applied
	 */
	@EventHandler
	public void onPlayerDamageEvent(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player damaged)) {
			return;
		}
		GameInstance instance = manager.getGame(damaged);
		if (instance == null) {
			return;
		}
		if (instance.isActive() && e.getCause().equals(DamageCause.MAGIC)) {
			PlayerState ps = instance.getPlayerState(damaged);
			if (ps.alive) {
				Player potionPlayer = damagingEffects.remove(damaged.getUniqueId());
				if (potionPlayer != null) {
					instance.processDeath(damaged, potionPlayer);
					return;
				}
				instance.processDeath(damaged, null);
			}
		}
	}
	
	/*
	 * Any other cause of death
	 */
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		// TODO is this needed?
	}
	
	/*
	 * When a potion effect is applied
	 */
	@EventHandler
	public void onPlayerSplashPotionEvent(PotionSplashEvent e) {
		Collection<LivingEntity> affected = e.getAffectedEntities();
		if (affected.size() < 1) {
			return;
		}
		Collection<PotionEffect> effects = e.getPotion().getEffects();
		boolean isHarmfulEffect = false;
		for (PotionEffect effect : effects) {
			if (effect.getType().equals(PotionEffectType.HARM) || effect.getType().equals(PotionEffectType.WITHER)) {
				isHarmfulEffect = true;
			}
		}
		if (!isHarmfulEffect) {
			return;
		}
		ProjectileSource shooter = e.getEntity().getShooter();
		if (shooter instanceof Player player) {
			GameInstance instance = manager.getGame(player);
			if (instance == null) {
				return;
			}
			if (instance.isActive()) {
				if (instance.isPVPEnabled()) {
					for (LivingEntity le : affected) {
						if (le instanceof Player) {
							PlayerState state = instance.getPlayerState((Player)le);
							if (state.alive) {
								damagingEffects.put(le.getUniqueId(), player);
							}
						}
					}
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		GameInstance instance = manager.getGame(player);
		if (instance == null) {
			return;
		}
		if (instance.isActive()) {
			instance.removePlayer(player, true);
		}
	}
}

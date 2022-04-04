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
package com.backwardsnode.survivalgames;

import com.backwardsnode.survivalgames.world.AnvilState;
import com.backwardsnode.survivalgames.world.LootDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class PluginListener implements Listener {

	private final Plugin PLUGIN;

	private final HashMap<UUID, Location> playersUsingAnvils;
	private final HashMap<Location, AnvilState> anvilsInUse;
	
	public PluginListener(Plugin plugin) {
		PLUGIN = plugin;

		playersUsingAnvils = new HashMap<>();
		anvilsInUse = new HashMap<>();

		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void reset() {
		playersUsingAnvils.clear();
		anvilsInUse.clear();
	}

	public void cleanupPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		Location lastAnvil = playersUsingAnvils.remove(uuid);

		if (lastAnvil != null) {
			AnvilState state = anvilsInUse.get(lastAnvil);
			if (state != null) {
				state.removeViewer(uuid);

				if (state.hasNoViewers()) {
					anvilsInUse.remove(lastAnvil);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PLUGIN.getHost().cancelInvitations(e.getPlayer(), true);

		cleanupPlayer(e.getPlayer());
	}

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e) {

		Block block = e.getBlock();
		Location location = block.getLocation();
		Material changedType = block.getType();

		switch (changedType) {
			case ANVIL:
			case CHIPPED_ANVIL:
			case DAMAGED_ANVIL:

				AnvilState anvil = anvilsInUse.get(location);

				if (anvil != null) {
					anvil.replaceBlock(location);
					e.setCancelled(true);
				}
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Block clickedBlock = e.getClickedBlock();

		if (e.getAction() != Action.RIGHT_CLICK_BLOCK || clickedBlock == null) {
			return;
		}

		Player player = e.getPlayer();
		Material currentType = clickedBlock.getType();

		switch (currentType) {
			case ANVIL:
			case CHIPPED_ANVIL:
			case DAMAGED_ANVIL:
				Location location = clickedBlock.getLocation();

				if (PLUGIN.getHost().hasWorldProtection(player)) {
					AnvilState anvil = anvilsInUse.get(location);
					if (anvil == null) {
						anvil = new AnvilState(currentType, ((Directional)clickedBlock.getBlockData()).getFacing());
						anvilsInUse.put(location, anvil);
					}

					UUID uuid = player.getUniqueId();
					anvil.addViewer(uuid);
					playersUsingAnvils.put(uuid, location);
				}
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryClosed(InventoryCloseEvent e) {
		Inventory i = e.getInventory();

		if (i.getType() == InventoryType.ANVIL) {
			Location location = i.getLocation();
			UUID uuid = e.getPlayer().getUniqueId();

			if (location != null) {
				Material type = location.getBlock().getType();
				AnvilState anvil = anvilsInUse.get(location);

				if (anvil == null) {
					return;
				}

				if (anvil.removeViewer(uuid) && type.isAir()) {
					anvil.replaceBlock(location);
				}

				if (anvil.hasNoViewers()) {
					anvilsInUse.remove(location);
				}

			}

			playersUsingAnvils.remove(uuid);
		} else if (i.getType().equals(InventoryType.CHEST) && i.getSize() == 27) {
			ItemStack item = i.getItem( 26);

			if (item != null && item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if (meta.hasDisplayName()) {
					String name = item.getItemMeta().getDisplayName();

					if (name.startsWith("id:confirm")) {
						PLUGIN.getHost().passConfirmationResolution((Player)e.getPlayer(), false);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		Inventory i = e.getClickedInventory();

		if (i == null) {
			return;
		}

		if (i.getType().equals(InventoryType.CHEST) && i.getSize() == 27 && e.getWhoClicked() instanceof Player player) {
			ItemStack item = i.getItem( 26);

			if (item != null && item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if (meta.hasDisplayName()) {
					String name = item.getItemMeta().getDisplayName();

					if (name.equals("id:confirm")) {
						ItemStack clicked = e.getCurrentItem();
						if (clicked != null) {
							Material type = clicked.getType();
							if (type == Material.RED_CONCRETE) {
								PLUGIN.getHost().passConfirmationResolution(player, false);
							} else if (type == Material.LIME_CONCRETE) {
								PLUGIN.getHost().passConfirmationResolution(player, true);
							}

							e.getWhoClicked().closeInventory();
						}

						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e) {
		if (e.getEntity() instanceof FallingBlock fallingBlock) {

			LootDrop drop = PLUGIN.getHost().getLootDropManager().getAndRemoveAssociatedDrop(fallingBlock.getUniqueId());

			if (drop != null) {
				e.setCancelled(true);
				fallingBlock.remove();
				if (!drop.isClosed()) {
					drop.placeChest(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDropItem(EntityDropItemEvent e) {
		if (e.getEntity() instanceof FallingBlock fallingBlock) {

			LootDrop drop = PLUGIN.getHost().getLootDropManager().getAndRemoveAssociatedDrop(fallingBlock.getUniqueId());

			if (drop != null) {
				e.setCancelled(true);
				fallingBlock.remove();
				if (!drop.isClosed()) {
					drop.placeChest(true);
				}
			}
		}
	}
}

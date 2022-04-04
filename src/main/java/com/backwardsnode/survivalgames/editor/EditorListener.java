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
package com.backwardsnode.survivalgames.editor;

import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.world.BlockLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EditorListener implements Listener {

	private final EditorManager handler;

	public EditorListener(EditorManager handler) {
		this.handler = handler;
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		Scene scene = handler.getEditor(e.getPlayer());
		if (scene == null) {
			return;
		}

		ItemStack item = e.getItemInHand();
		String locale = e.getPlayer().getLocale();

		if (EditorItems.LOOT_CHEST.isSimilar(handler.getHandler(), locale, item)) {
			scene.addItemChest(e.getBlock().getLocation());
		} else if (EditorItems.SPAWN_PLATE.isSimilar(handler.getHandler(), locale, item)) {
			scene.addSpawnPlate(e.getBlock().getLocation());
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		
		Scene scene = handler.getEditor(player);
		if (scene == null || e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		Block target = e.getClickedBlock();
		ItemStack item = e.getItem();
		String locale = player.getLocale();

		Action action = e.getAction();
		if (target != null) {
			Material type = target.getType();

			if (type == Material.CHEST) {
				if (action == Action.RIGHT_CLICK_BLOCK) {
					if (scene.isItemChest(target.getLocation())) {
						if (item == null) {
							scene.removeItemChest(target.getLocation());
						} else if (EditorItems.CHEST_SETS_BOOK.isSimilar(handler.getHandler(), locale, item)) {
							handler.openChestItemSetInventory(player, target.getLocation());
						}
					} else {
						if (item == null) {
							scene.addItemChest(target.getLocation());
						} else if (EditorItems.CHEST_SETS_BOOK.isSimilar(handler.getHandler(), locale, item)) {
							handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.ADD_CHEST_FIRST);
						}
					}
					e.setCancelled(true);
				} else {
					scene.removeItemChest(target.getLocation());
				}
			} else if (type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
				if (action == Action.RIGHT_CLICK_BLOCK) {
					if (scene.isSpawnPlate(target.getLocation())) {
						if (item == null) {
							scene.removeSpawnPlate(target.getLocation());
						}
					} else {
						if (item == null) {
							scene.addSpawnPlate(target.getLocation());
						}
					}
					e.setCancelled(true);
				} else {
					scene.removeSpawnPlate(target.getLocation());
				}
			}

		}
		if (EditorItems.ITEMSET_SWORD.isSimilar(handler.getHandler(), locale, item)) {
			handler.openItemSetInventory(player);
			e.setCancelled(true);

		} else if (EditorItems.SETTINGS_COMPARATOR.isSimilar(handler.getHandler(), locale, item)) {
			handler.openSettingInventory(player);
			e.setCancelled(true);

		} else if (EditorItems.WORLDBORDER_FENCE.isSimilar(handler.getHandler(), locale, item)) {
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				scene.toggleWorldBorder();
			} else {
				handler.openWorldBorderInventory(player);
			}
			e.setCancelled(true);

		} else if (EditorItems.INVSWITCH_BOOKSHELF.isSimilar(handler.getHandler(), locale, item)) {
			if (scene.isToolkitOpen()) {
				scene.hideToolkit();
			} else {
				scene.showToolkit();
			}

			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInventoryEvent(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player player)) {
			return;
		}
		Scene scene = handler.getEditor(player);
		if (scene == null) {
			return;
		}

		String locale = player.getLocale();
		Inventory i = e.getClickedInventory();
		ItemStack item = e.getCurrentItem();
		String title = i == null ? "" : e.getView().getTitle();

		if (i == null || item == null) {
			return;
		}

		if (title.equals(handler.getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.ALL_ITEM_SETS_TITLE, locale))) {
			if (EditorItems.NEW_ITEMSET_CONCRETE.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.NEW_ITEMSET_NAME);
			} else {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasDisplayName()) {
						handler.openItemSetCustomiseInventory(player, item.getItemMeta().getDisplayName());
					}
				}
			}
			e.setCancelled(true);
		} else if (title.equals(handler.getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.CHOOSE_SET_TITLE, locale))) {
			ItemStack chestBlockData = i.getItem(i.getSize() - 1);
			if (chestBlockData != null) {
				Location loc = new BlockLocation(chestBlockData.getItemMeta().getLore().get(0)).toBukkitLocation();
				ItemStack[] items = i.getContents();
				List<String> selected = new ArrayList<>();
				for (ItemStack itemStack : items) {
					if (itemStack != null) {
						if (itemStack.equals(item)) {
							switch (itemStack.getType()) {
								case LIME_STAINED_GLASS:
									itemStack.setType(Material.RED_STAINED_GLASS);
									break;
								case LIME_CONCRETE:
									itemStack.setType(Material.RED_CONCRETE);
									break;
								case RED_STAINED_GLASS:
									itemStack.setType(Material.LIME_STAINED_GLASS);
									break;
								case RED_CONCRETE:
									itemStack.setType(Material.LIME_CONCRETE);
									break;
								default:
									continue;
							}
						}
						switch (itemStack.getType()) {
							case LIME_STAINED_GLASS:
							case LIME_CONCRETE:
								selected.add(itemStack.getItemMeta().getDisplayName());
							default:
								break;
						}
					}
				}
				if (scene.setChestItemSets(loc, selected)) {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.MODIFIED_ITEMS);
				} else {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.MODIFY_ITEMS_ERR);
					player.closeInventory();
				}
			}
			e.setCancelled(true);
		} else if (title.equals(handler.getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.SETTINGS_TITLE, locale))) {
			if (EditorItems.RENAME_MAP_NAMETAG.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.MAP_NAME);
			} else if (EditorItems.BORDER_DPS_CACTUS.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.BORDER_DPS);
			} else if (EditorItems.DEATHMATCH_CONFIG_FISHINGROD.isSimilar(handler.getHandler(), locale, item)) {
				handler.openDeathmatchInventory(player);
			} else if (EditorItems.SHRINK_TIME_AXE.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.TIME_TO_SHRINK);
			} else if (EditorItems.BORDER_START_MAP.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.BORDER_START_RADIUS);
			} else if (EditorItems.WAIT_PERIOD_CLOCK.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.WAIT_PERIOD);
			} else if (EditorItems.GRACE_PERIOD_POPPY.isSimilar(handler.getHandler(), locale, item)) {
				player.closeInventory();
				scene.queryInput(EditorQueries.GRACE_PERIOD);
			} else if (EditorItems.PREFILL_CHESTMINECART.isSimilar(handler.getHandler(), locale, item)) {
				boolean doChestPrefilling = !scene.getGameConfiguration().getDoChestPrefilling();
				scene.getGameConfiguration().setDoChestPrefilling(doChestPrefilling);
				if (doChestPrefilling) {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.ENABLED_AUTOFILL);
				} else {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.DISABLED_AUTOFILL);
				}
			} else if (EditorItems.DEATH_FIREWORK.isSimilar(handler.getHandler(), locale, item)) {
				boolean deathFirework = !scene.getGameConfiguration().getSpawnFireworkOnDeath();
				scene.getGameConfiguration().setSpawnFireworkOnDeath(deathFirework);
				if (deathFirework) {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.ENABLED_DEATH_FIREWORK);
				} else {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.DISABLED_DEATH_FIREWORK);
				}
			} else if (EditorItems.KILL_FIREWORK.isSimilar(handler.getHandler(), locale, item)) {
				boolean killFirework = !scene.getGameConfiguration().getSpawnFireworkOnKill();
				scene.getGameConfiguration().setSpawnFireworkOnKill(killFirework);
				if (killFirework) {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.ENABLED_KILL_FIREWORK);
				} else {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.DISABLED_KILL_FIREWORK);
				}
			} else if (EditorItems.LIGHTNING_ROD.isSimilar(handler.getHandler(), locale, item)) {
				boolean deathLightning = !scene.getGameConfiguration().getLightningOnDeath();
				scene.getGameConfiguration().setLightningOnDeath(deathLightning);
				if (deathLightning) {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.ENABLED_DEATH_LIGHTNING);
				} else {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.DISABLED_DEATH_LIGHTNING);
				}
			} else if (EditorItems.ISWIP_BRICKS.isSimilar(handler.getHandler(), locale, item)) {
				boolean wip = !scene.getGameConfiguration().getIsWIP();
				scene.getGameConfiguration().setIsWIP(wip);
				if (wip) {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.ENABLED_WIP);
				} else {
					handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.DISABLED_WIP);
				}
			}
			e.setCancelled(true);
		} else if (title.equals(handler.getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.BORDER_TITLE, locale))) {
			if (EditorItems.INITIAL_BORDER_MAP.isSimilar(handler.getHandler(), locale, item)) {
				scene.setBorderTargetType(false);
			} else if (EditorItems.DEATHMATCH_BORDER_SWORD.isSimilar(handler.getHandler(), locale, item)) {
				scene.setBorderTargetType(true);
			} else if (EditorItems.HIDE_BORDER_BARRIER.isSimilar(handler.getHandler(), locale, item)) {
				scene.hideBorder();
			} else {
				if (item.hasItemMeta()) {
					if (item.getItemMeta().hasDisplayName()) {
						scene.setBorderDeathmatchTarget(item.getItemMeta().getDisplayName());
					}
				}
			}
			e.setCancelled(true);
			player.closeInventory();
		}
	}
	
	@EventHandler
	public void onPlayerInventoryCloseEvent(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player player)) {
			return;
		}
		Scene scene = handler.getEditor(player);
		if (scene == null) {
			return;
		}

		Inventory i = e.getInventory();
		String expectedTitleSubstr = handler.getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.ITEM_SET_TITLE, player.getLocale(), "");
		if (e.getView().getTitle().startsWith(expectedTitleSubstr)) {
			String set = e.getView().getTitle().substring(expectedTitleSubstr.length());
			if (scene.replaceItemSet(i.getContents(), set)) {
				handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.UPDATED_ITEM_SET, set);
			} else {
				handler.getHandler().getMessageProvider().sendMessage(player, Messages.Editor.UPDATE_ITEM_SET_ERR, set);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		Scene scene = handler.getEditor(e.getPlayer());
		if (scene == null) {
			return;
		}

		if (scene.passChat(e.getMessage())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		handler.closeEditor(e.getPlayer(), true);
	}
}
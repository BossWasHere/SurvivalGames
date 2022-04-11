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

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.PluginWrapper;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.world.BlockLocation;
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

public class EditorListener extends PluginWrapper implements Listener {

	private final EditorManager handler;

	public EditorListener(Plugin plugin, EditorManager handler) {
		super(plugin);
		this.handler = handler;
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		Scene scene = handler.getEditor(player);
		if (scene == null) {
			return;
		}

		ItemStack item = e.getItemInHand();

		EditorItems model = EditorItems.getRepresentingItem(plugin, item);
		if (model == null) {
			return;
		}
		
		switch (model) {
			case LOOT_CHEST -> {
				scene.getGameConfiguration().addItemChest(new BlockLocation(e.getBlock().getLocation()), true);
				getMessageProvider().sendMessage(player, Messages.Editor.ADDED_CHEST);
			}
			case SPAWN_PLATE -> {
				scene.getGameConfiguration().addSpawnLocation(new BlockLocation(e.getBlock().getLocation()));
				getMessageProvider().sendMessage(player, Messages.Editor.ADDED_SPAWN_POS);
			}
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
		BlockLocation blockLocation = new BlockLocation(target.getLocation());
		ItemStack item = e.getItem();

		Action action = e.getAction();
		Material type = target.getType();

		if (type == Material.CHEST) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (scene.getGameConfiguration().getChestAt(blockLocation).isPresent()) {
					if (item == null) {
						scene.getGameConfiguration().removeChestAt(blockLocation);
						getMessageProvider().sendMessage(player, Messages.Editor.REMOVED_CHEST);
					} else if (EditorItems.CHEST_ITEMSET_MANAGER.isSimilar(plugin, item)) {
						handler.openChestItemSetInventory(player, target.getLocation());
					}
				} else {
					if (item == null) {
						scene.getGameConfiguration().addItemChest(blockLocation, true);
						getMessageProvider().sendMessage(player, Messages.Editor.ADDED_CHEST);
					} else if (EditorItems.CHEST_ITEMSET_MANAGER.isSimilar(plugin, item)) {
						getMessageProvider().sendMessage(player, Messages.Editor.ADD_CHEST_FIRST);
					}
				}
				e.setCancelled(true);
			} else {
				if (scene.getGameConfiguration().removeChestAt(blockLocation)) {
					getMessageProvider().sendMessage(player, Messages.Editor.REMOVED_CHEST);
				}
			}
			return;
		} else if (type == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (item == null) {
					if (scene.getGameConfiguration().removeSpawnLocation(blockLocation)) {
						getMessageProvider().sendMessage(player, Messages.Editor.REMOVED_SPAWN_POS);
					} else {
						scene.getGameConfiguration().addSpawnLocation(blockLocation);
						getMessageProvider().sendMessage(player, Messages.Editor.ADDED_SPAWN_POS);
					}
					e.setCancelled(true);
				}
			} else if (scene.getGameConfiguration().removeSpawnLocation(blockLocation)) {
				getMessageProvider().sendMessage(player, Messages.Editor.REMOVED_SPAWN_POS);
			}
			return;
		}

		if (item != null) {
			EditorItems model = EditorItems.getRepresentingItem(plugin, item);

			if (model == null) {
				return;
			}

			switch (model) {
				case ITEMSET_VIEWER -> handler.openItemSetInventory(player);
				case SETTINGS -> handler.openSettingInventory(player);
				case WORLDBORDER -> {
					if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
						scene.toggleWorldBorder();
					} else {
						handler.openWorldBorderInventory(player);
					}
				}
				case INVSWITCH -> {
					if (scene.isToolkitOpen()) {
						scene.hideToolkit();
					} else {
						scene.showToolkit();
					}
				}
				case LOOT_CHEST, SPAWN_PLATE -> {
					return;
				}
			}
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInventoryEvent(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player player)) {
			return;
		}

		Inventory i = e.getClickedInventory();
		ItemStack item = e.getCurrentItem();
		if (i == null || item == null) {
			return;
		}

		Scene scene = handler.getEditor(player);
		if (scene == null) {
			return;
		}

		EditorItems model = EditorItems.getRepresentingItem(plugin, item);
		if (model == null) {
			return;
		}
		switch (model) {
			case RENAME_MAP -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.MAP_NAME);
			}
			case BORDER_DPS -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.BORDER_DPS);
			}
			case DEATHMATCH_CONFIG -> handler.openDeathmatchInventory(player);
			case SHRINK_TIME -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.TIME_TO_SHRINK);
			}
			case BORDER_START -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.BORDER_START_RADIUS);
			}
			case WAIT_PERIOD -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.WAIT_PERIOD);
			}
			case GRACE_PERIOD -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.GRACE_PERIOD);
			}
			case ADD_ITEMSET -> {
				player.closeInventory();
				scene.queryInput(EditorQueries.NEW_ITEMSET_NAME);
			}
			case PREFILL_CHEST -> {
				boolean doChestPrefilling = !scene.getGameConfiguration().getDoChestPrefilling();
				scene.getGameConfiguration().setDoChestPrefilling(doChestPrefilling);
				getMessageProvider().sendMessage(player, doChestPrefilling ? Messages.Editor.ENABLED_AUTOFILL : Messages.Editor.DISABLED_AUTOFILL);
			}
			case DEATH_FIREWORK -> {
				boolean deathFirework = !scene.getGameConfiguration().getSpawnFireworkOnDeath();
				scene.getGameConfiguration().setSpawnFireworkOnDeath(deathFirework);
				getMessageProvider().sendMessage(player, deathFirework ? Messages.Editor.ENABLED_DEATH_FIREWORK : Messages.Editor.DISABLED_DEATH_FIREWORK);
			}
			case KILL_FIREWORK -> {
				boolean killFirework = !scene.getGameConfiguration().getSpawnFireworkOnKill();
				scene.getGameConfiguration().setSpawnFireworkOnKill(killFirework);
				getMessageProvider().sendMessage(player, killFirework ? Messages.Editor.ENABLED_KILL_FIREWORK : Messages.Editor.DISABLED_KILL_FIREWORK);
			}
			case DEATH_LIGHTNING -> {
				boolean deathLightning = !scene.getGameConfiguration().getLightningOnDeath();
				scene.getGameConfiguration().setLightningOnDeath(deathLightning);
				getMessageProvider().sendMessage(player, deathLightning ? Messages.Editor.ENABLED_DEATH_LIGHTNING : Messages.Editor.DISABLED_DEATH_LIGHTNING);
			}
			case IS_WIP -> {
				boolean wip = !scene.getGameConfiguration().getIsWIP();
				scene.getGameConfiguration().setIsWIP(wip);
				getMessageProvider().sendMessage(player, wip ? Messages.Editor.ENABLED_WIP : Messages.Editor.DISABLED_WIP);
			}
			case SET_INITIAL_BORDER -> scene.setBorderTargetType(false);
			case SET_DEATHMATCH_BORDER -> scene.setBorderTargetType(true);
			case SET_HIDE_BORDER -> scene.hideBorder();
			case ENABLED_DEFAULT_ITEMSET, DISABLED_DEFAULT_ITEMSET, ENABLED_ITEMSET, DISABLED_ITEMSET -> {
				String blockLocationString = EditorItems.getTemplateId(plugin, item);
				if (blockLocationString != null) {
					BlockLocation location = new BlockLocation(blockLocationString);

					String name = item.getItemMeta().getDisplayName();
					String locale = player.getLocale();

					switch (model) {
						case ENABLED_DEFAULT_ITEMSET -> {
							i.setItem(e.getSlot(), EditorItems.DISABLED_DEFAULT_ITEMSET.getTemplatedItem(plugin, blockLocationString, name, locale));
							scene.getGameConfiguration().removeChestItemSet(location, name);
						}
						case DISABLED_DEFAULT_ITEMSET -> {
							i.setItem(e.getSlot(), EditorItems.ENABLED_DEFAULT_ITEMSET.getTemplatedItem(plugin, blockLocationString, name, locale));
							scene.getGameConfiguration().addChestItemSet(location, name);
						}
						case ENABLED_ITEMSET -> {
							i.setItem(e.getSlot(), EditorItems.DISABLED_ITEMSET.getTemplatedItem(plugin, blockLocationString, name, locale));
							scene.getGameConfiguration().removeChestItemSet(location, name);
						}
						case DISABLED_ITEMSET -> {
							i.setItem(e.getSlot(), EditorItems.ENABLED_ITEMSET.getTemplatedItem(plugin, blockLocationString, name, locale));
							scene.getGameConfiguration().addChestItemSet(location, name);
						}
					}
					getMessageProvider().sendMessage(player, Messages.Editor.MODIFIED_ITEMS);
				}
			}
			case SELECT_BORDER, SELECTED_BORDER -> {
				String location = EditorItems.getTemplateId(plugin, item);
				scene.setBorderDeathmatchTarget(location);
				player.closeInventory();
			}
			case ITEMSET -> handler.openItemSetCustomiseInventory(player, EditorItems.getTemplateId(plugin, item));
		}
		e.setCancelled(true);
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
		String expectedTitleSubstr = getMessageProvider().compileMessage(Messages.Editor.Inventory.ITEM_SET_TITLE, player.getLocale(), "");
		if (e.getView().getTitle().startsWith(expectedTitleSubstr)) {
			String set = e.getView().getTitle().substring(expectedTitleSubstr.length());
			if (scene.replaceItemSet(i.getContents(), set)) {
				getMessageProvider().sendMessage(player, Messages.Editor.UPDATED_ITEM_SET, set);
			} else {
				getMessageProvider().sendMessage(player, Messages.Editor.UPDATE_ITEM_SET_ERR, set);
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
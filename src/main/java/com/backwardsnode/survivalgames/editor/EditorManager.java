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
import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.world.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EditorManager {
	
	private final Plugin PLUGIN;
	private EditorListener listener;

	private Map<UUID, Scene> editorsActive;

	public EditorManager(Plugin plugin) {
		PLUGIN = plugin;
		editorsActive = new HashMap<>();

		listener = new EditorListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	public Plugin getHandler() {
		return PLUGIN;
	}
	
	public boolean addEditor(Player player, GameConfigurationWrapper config) {
		if (editorsActive.containsKey(player.getUniqueId())) {
			return false;
		}
		Scene scene = new Scene(player, this, config);
		scene.showToolkit();
		editorsActive.put(player.getUniqueId(), scene);

		return true;
	}
	
	public boolean closeEditor(Player player, boolean save) {
		Scene scene = editorsActive.remove(player.getUniqueId());

		if (scene == null) {
			return false;
		}

		if (save) {
			scene.getGameConfiguration().save();
		}
		scene.restoreInventory();
		scene.disposeBorder();
		return true;
	}
	
	public void closeAllEditors(boolean save) {
		for (Scene scene : editorsActive.values()) {
			if (save) {
				scene.getGameConfiguration().save();
			}
			scene.restoreInventory();
			scene.disposeBorder();
		}
		editorsActive.clear();
	}
	
	public boolean isEditor(Player player) {
		return editorsActive.containsKey(player.getUniqueId());
	}

	public Scene getEditor(Player player) {
		return editorsActive.get(player.getUniqueId());
	}

	public boolean openItemSetInventory(Player player) {
		Scene scene = getEditor(player);
		if (scene == null) {
			return false;
		}

		String locale = player.getLocale();
		String itemSetTitle = PLUGIN.getMessageProvider().compileMessage(Messages.Editor.Inventory.ALL_ITEM_SETS_TITLE, locale);

		List<ItemSet> itemSets = scene.getGameConfiguration().getItemSets();
		int itemSetCount = itemSets.size();

		int invSize = getAugmentedInventorySize(itemSetCount);

		Inventory i = Bukkit.createInventory(null, invSize, itemSetTitle);
		for (int j = 0; j < itemSetCount; j++) {
			i.addItem(Utils.addNameAndLore(new ItemStack(Material.IRON_SWORD), itemSets.get(j).name, "ItemSet #" + j));
		}
		i.setItem(invSize - 1, EditorItems.NEW_ITEMSET_CONCRETE.getItem(PLUGIN, locale));
		player.openInventory(i);
		return true;
	}
	
	public boolean openItemSetCustomiseInventory(Player player, String itemSet) {
		Scene scene = getEditor(player);
		if (scene == null) {
			return false;
		}

		ItemSet target = scene.getItemSet(itemSet);
		if (target == null) {
			return false;
		}

		Inventory inv = Bukkit.createInventory(player, 54, PLUGIN.getMessageProvider().compileMessage(Messages.Editor.Inventory.ITEM_SET_TITLE, player.getLocale(), itemSet));
		if (target.items != null) {
			List<ItemModel> items = target.items;
			for (int j = 0; j < items.size() && j < 54; j++) {
				ItemStack p = items.get(j).getEquivalent();
				if (p == null) {
					Bukkit.getLogger().warning("[SGEdit] Cannot instantiate ItemStack (id=" + items.get(j).id + ")");
					return false;
				}
				inv.addItem(p);
			}
		}
		player.openInventory(inv);
		return true;
	}
	
	public boolean openChestItemSetInventory(Player player, Location location) {
		Scene scene = getEditor(player);

		List<ItemSet> itemSets = scene.getGameConfiguration().getItemSets();
		int invSize = getAugmentedInventorySize(itemSets.size());

		Inventory inv = Bukkit.createInventory(null, invSize, getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.CHOOSE_SET_TITLE, player.getLocale()));
		List<String> enabledItemSets = scene.getChestItemSets(location);
		for (ItemSet set : itemSets) {
			if (enabledItemSets.stream().anyMatch(str -> str != null && str.equals(set.name))) {
				if (set.isDefault) {
					inv.addItem(Utils.addNameAndLore(new ItemStack(Material.LIME_STAINED_GLASS), set.name, "Default itemset", "Click me to disable", "this item set for chest"));
				} else {
					inv.addItem(Utils.addNameAndLore(new ItemStack(Material.LIME_CONCRETE), set.name, "Click me to disable", "this item set for chest"));
				}
			} else {
				if (set.isDefault) {
					inv.addItem(Utils.addNameAndLore(new ItemStack(Material.RED_STAINED_GLASS), set.name, "Default itemset", "Click me to enable", "this item set for chest"));
				} else {
					inv.addItem(Utils.addNameAndLore(new ItemStack(Material.RED_CONCRETE), set.name, "Click me to enable", "this item set for chest"));
				}
			}
		}
		inv.setItem(invSize - 1, Utils.addNameAndLore(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE), "Inventory-Info", new BlockLocation(location).toString()));
		player.openInventory(inv);		
		return true;
	}
	
	public boolean openSettingInventory(Player player) {
		String locale = player.getLocale();
		Inventory i = Bukkit.createInventory(null, 18, getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.SETTINGS_TITLE, locale));
		i.addItem(EditorItems.RENAME_MAP_NAMETAG.getItem(PLUGIN, locale), EditorItems.BORDER_DPS_CACTUS.getItem(PLUGIN, locale),
				EditorItems.DEATHMATCH_CONFIG_FISHINGROD.getItem(PLUGIN, locale), EditorItems.SHRINK_TIME_AXE.getItem(PLUGIN, locale),
				EditorItems.BORDER_START_MAP.getItem(PLUGIN, locale), EditorItems.WAIT_PERIOD_CLOCK.getItem(PLUGIN, locale),
				EditorItems.GRACE_PERIOD_POPPY.getItem(PLUGIN, locale), EditorItems.PREFILL_CHESTMINECART.getItem(PLUGIN, locale),
				EditorItems.DEATH_FIREWORK.getItem(PLUGIN, locale), EditorItems.KILL_FIREWORK.getItem(PLUGIN, locale),
				EditorItems.LIGHTNING_ROD.getItem(PLUGIN, locale), EditorItems.ISWIP_BRICKS.getItem(PLUGIN, locale));
		player.openInventory(i);
		return true;
	}
	
	public boolean openDeathmatchInventory(Player player) {
		Inventory i = Bukkit.createInventory(null, 18, getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.DEATHMATCH_TITLE, player.getLocale()));
		player.openInventory(i);
		return true;
	}
	
	public boolean openWorldBorderInventory(Player player) {
		Scene scene = getEditor(player);
		List<String> locations = scene.getDeathmatchLocations();
		int size = Utils.getPreferredSize(locations.size() + 9);
		String locale = player.getLocale();
		Inventory i = Bukkit.createInventory(null, size, getHandler().getMessageProvider().compileMessage(Messages.Editor.Inventory.BORDER_TITLE, locale));
		String[] itemLore = PLUGIN.getMessageProvider().compileMessage(Messages.Misc.BORDER_SELECT_LORE, locale).split("\\\\n");
		for (String location : locations) {
			if (scene.getTargetedBorder() != null) {
				if (location.equals(scene.getTargetedBorder())) {
					i.addItem(Utils.addNameAndLore(new ItemStack(Material.NETHER_BRICK_FENCE), location, itemLore));
					continue;
				}
			}
			i.addItem(Utils.addNameAndLore(new ItemStack(Material.OAK_FENCE), location, itemLore));
		}
		i.setItem(size - 9, EditorItems.INITIAL_BORDER_MAP.getItem(PLUGIN, locale));
		i.setItem(size - 8, EditorItems.DEATHMATCH_BORDER_SWORD.getItem(PLUGIN, locale));
		i.setItem(size - 7, EditorItems.HIDE_BORDER_BARRIER.getItem(PLUGIN, locale));
		player.openInventory(i);
		return true;
	}

	public BorderController getBorderFor(Player player) {
		Scene scene = getEditor(player);
		if (scene != null) {
			return scene.getBorderController();
		}
		return null;
	}

	private static int getAugmentedInventorySize(int itemCount) {
		if (itemCount > 0) {
			itemCount = Utils.getPreferredSize(itemCount);
			if (itemCount <= 45) {
				itemCount += 9;
			}
			return itemCount;
		}
		return 9;
	}
}
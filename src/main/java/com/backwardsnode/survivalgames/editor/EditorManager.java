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

public class EditorManager extends PluginWrapper {

	private final Map<UUID, Scene> editorsActive;

	public EditorManager(Plugin plugin) {
		super(plugin);
		editorsActive = new HashMap<>();

		EditorListener listener = new EditorListener(plugin, this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	public boolean addEditor(Player player, GameConfigurationWrapper config) {
		if (editorsActive.containsKey(player.getUniqueId())) {
			return false;
		}
		Scene scene = new Scene(plugin, player, this, config);
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
		String itemSetTitle = getMessageProvider().compileMessage(Messages.Editor.Inventory.ALL_ITEM_SETS_TITLE, locale);

		List<ItemSet> itemSets = scene.getGameConfiguration().getItemSets();
		int itemSetCount = itemSets.size();

		int invSize = getAugmentedInventorySize(itemSetCount);

		Inventory i = Bukkit.createInventory(null, invSize, itemSetTitle);
		for (ItemSet itemSet : itemSets) {
			i.addItem(EditorItems.ITEMSET.getTemplatedItem(plugin, itemSet.name, "Items in " + itemSet.name, locale));
		}
		i.setItem(invSize - 1, EditorItems.ADD_ITEMSET.getItem(plugin, locale));
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

		Inventory inv = Bukkit.createInventory(player, 54, getMessageProvider().compileMessage(Messages.Editor.Inventory.ITEM_SET_TITLE, player.getLocale(), itemSet));
		if (target.items != null) {
			List<ItemModel> items = target.items;
			for (int j = 0; j < items.size() && j < 54; j++) {
				ItemStack p = items.get(j).getEquivalent();
				if (p == null) {
					Bukkit.getLogger().warning("[SGEdit] Cannot instantiate ItemStack (id=" + items.get(j).id + ")");
					continue;
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

		Inventory inv = Bukkit.createInventory(null, invSize, getMessageProvider().compileMessage(Messages.Editor.Inventory.CHOOSE_SET_TITLE, player.getLocale()));
		List<String> enabledItemSets = scene.getGameConfiguration().getChestItemSetNames(new BlockLocation(location));

		String locale = player.getLocale();
		String blockLocationString = new BlockLocation(location).toString();
		for (ItemSet set : itemSets) {
			if (enabledItemSets != null && enabledItemSets.contains(set.name)) {
				if (set.isDefault) {
					inv.addItem(EditorItems.ENABLED_DEFAULT_ITEMSET.getTemplatedItem(plugin, blockLocationString, set.name, locale));
				} else {
					inv.addItem(EditorItems.ENABLED_ITEMSET.getTemplatedItem(plugin, blockLocationString, set.name, locale));
				}
			} else {
				if (set.isDefault) {
					inv.addItem(EditorItems.DISABLED_DEFAULT_ITEMSET.getTemplatedItem(plugin, blockLocationString, set.name, locale));
				} else {
					inv.addItem(EditorItems.DISABLED_ITEMSET.getTemplatedItem(plugin, blockLocationString, set.name, locale));
				}
			}
		}
		player.openInventory(inv);		
		return true;
	}
	
	public boolean openSettingInventory(Player player) {
		String locale = player.getLocale();
		Inventory i = Bukkit.createInventory(null, 18, getMessageProvider().compileMessage(Messages.Editor.Inventory.SETTINGS_TITLE, locale));
		i.addItem(EditorItems.RENAME_MAP.getItem(plugin, locale), EditorItems.BORDER_DPS.getItem(plugin, locale),
				EditorItems.DEATHMATCH_CONFIG.getItem(plugin, locale), EditorItems.SHRINK_TIME.getItem(plugin, locale),
				EditorItems.BORDER_START.getItem(plugin, locale), EditorItems.WAIT_PERIOD.getItem(plugin, locale),
				EditorItems.GRACE_PERIOD.getItem(plugin, locale), EditorItems.PREFILL_CHEST.getItem(plugin, locale),
				EditorItems.DEATH_FIREWORK.getItem(plugin, locale), EditorItems.KILL_FIREWORK.getItem(plugin, locale),
				EditorItems.DEATH_LIGHTNING.getItem(plugin, locale), EditorItems.IS_WIP.getItem(plugin, locale));
		player.openInventory(i);
		return true;
	}
	
	public boolean openDeathmatchInventory(Player player) {
		Inventory i = Bukkit.createInventory(null, 18, getMessageProvider().compileMessage(Messages.Editor.Inventory.DEATHMATCH_TITLE, player.getLocale()));

		//TODO ??

		player.openInventory(i);
		return true;
	}
	
	public boolean openWorldBorderInventory(Player player) {
		Scene scene = getEditor(player);
		List<String> locations = scene.getDeathmatchLocations();
		int size = getAugmentedInventorySize(locations.size());
		String locale = player.getLocale();

		Inventory i = Bukkit.createInventory(null, size, getMessageProvider().compileMessage(Messages.Editor.Inventory.BORDER_TITLE, locale));
		for (String location : locations) {
			if (scene.getTargetedBorder() != null && location.equals(scene.getTargetedBorder())) {
				i.addItem(EditorItems.SELECTED_BORDER.getTemplatedItem(plugin, location, location, locale));
				continue;
			}
			i.addItem(EditorItems.SELECT_BORDER.getTemplatedItem(plugin, location, location, locale));
		}
		i.setItem(size - 9, EditorItems.SET_INITIAL_BORDER.getItem(plugin, locale));
		i.setItem(size - 8, EditorItems.SET_DEATHMATCH_BORDER.getItem(plugin, locale));
		i.setItem(size - 7, EditorItems.SET_HIDE_BORDER.getItem(plugin, locale));
		player.openInventory(i);
		return true;
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
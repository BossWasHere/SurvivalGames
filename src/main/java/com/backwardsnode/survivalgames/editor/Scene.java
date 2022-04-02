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

import com.backwardsnode.survivalgames.config.ChestConfiguration;
import com.backwardsnode.survivalgames.config.DeathmatchConfiguration;
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.dependency.plugin.ProtocolConnector;
import com.backwardsnode.survivalgames.game.PlayerStorageCache;
import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.backwardsnode.survivalgames.message.Messages;
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class Scene {

	private final Player editor;
	private final EditorManager manager;
	private final GameConfigurationWrapper gameConfig;
	private final PlayerStorageCache playerCache;
	private final PlayerStorageCache togglingCache;
	
	private BorderController borderController;
	private DeathmatchConfiguration selectedData;
	
	private boolean loadedToolkit;
	private boolean isToolkitOpen;
	private boolean isBorderVisible;
	private boolean isBorderTargetDeathmatch;
	
	private String associatedKey = null;
	private EditorQueries currentQuery;

	public Scene(Player editor, EditorManager manager, GameConfigurationWrapper gameConfig) {
		checkNotNull(editor);
		checkNotNull(manager);
		checkNotNull(gameConfig);
		this.editor = editor;
		this.manager = manager;
		this.gameConfig = gameConfig;
		playerCache = new PlayerStorageCache(editor, true);
		playerCache.cacheCurrentInventory();
		togglingCache = new PlayerStorageCache(editor, true);
		setDefaults();

		if (gameConfig.hasNoItemSets()) {
			createItemSet("common", true);
		}
	}
	
	public void restoreInventory() {
		playerCache.restoreInventory();
	}

	public void setDefaults() {
		if (gameConfig.hasMapName()) {
			gameConfig.setMapName("Untitled");
		}
		// TODO these shouldn't be true?
//		if (gameConfig.getSpawnLocations() == null) {
//			gameConfig.spawnLocs = new ArrayList<>();
//		}
//		if (gameConfig.getChests() == null) {
//			gameConfig.chestLocations = new ArrayList<>();
//		}
//		if (gameConfig.getItemSets() == null) {
//			gameConfig.itemSets = new ArrayList<>();
//		}
//		if (gameConfig.getBorder() == null) {
//			gameConfig.border = new BorderConfiguration();
//		}
//		if (gameConfig.getDeathmatchConfigs() == null) {
//			gameConfig.border.deathmatchLocations = new ArrayList<>();
//		}
	}
	
	public void showToolkit() {
		if (!isToolkitOpen) {
			if (loadedToolkit) {
				togglingCache.swapInventory();
			} else {
				String locale = editor.getLocale();
				replaceHotbar(editor, EditorItems.LOOT_CHEST.getItem(manager.getHandler(), locale), EditorItems.CHEST_SETS_BOOK.getItem(manager.getHandler(), locale), EditorItems.WORLDBORDER_FENCE.getItem(manager.getHandler(), locale), null,
						EditorItems.SPAWN_PLATE.getItem(manager.getHandler(), locale), null, EditorItems.ITEMSET_SWORD.getItem(manager.getHandler(), locale), EditorItems.SETTINGS_COMPARATOR.getItem(manager.getHandler(), locale), EditorItems.INVSWITCH_BOOKSHELF.getItem(manager.getHandler(), locale));
				loadedToolkit = true;
			}
			isToolkitOpen = true;
		}
	}
	
	public void hideToolkit() {
		if (isToolkitOpen) {
			togglingCache.swapInventory();
			isToolkitOpen = false;
			editor.getInventory().setItem(8, EditorItems.INVSWITCH_BOOKSHELF.getItem(manager.getHandler(), editor.getLocale()));
		}
	}
	
	public boolean isToolkitOpen() {
		return isToolkitOpen;
	}
	
	public void replaceHotbar(Player player, ItemStack... items) {
		Preconditions.checkNotNull(items);
		PlayerInventory inv = player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (items.length > i) {
				if (items[i] == null) {
					inv.clear(i);
				} else {
					inv.setItem(i, items[i]);
				}
			} else {
				inv.clear(i);
			}
		}
	}
	
	public void toggleWorldBorder() {
		if (borderController != null) {
			if (isBorderVisible) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.DISABLED_BORDER);
				borderController.unsetVisibleTo(editor);
				isBorderVisible = false;
			} else {
				borderController.setVisibleTo(editor);
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.ENABLED_BORDER);
				isBorderVisible = true;
			}
		} else {
			if (gameConfig.getDeathmatchConfigs().size() < 1) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.NO_DEATHMATCH_LOCS);
				return;
			}
			selectedData = gameConfig.getDeathmatchConfigs().get(0);
			if (selectedData == null) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.NO_DEATHMATCH_LOCS);
				return;
			}
			ProtocolConnector connector = manager.getHandler().getDependencyManager().getProtocolConnector();
			if (connector != null) {
				borderController = new BorderController(connector, editor.getWorld());
				borderController.setDefaultWarning(1, 1);
				borderController.setTarget(selectedData.getCenterX(), selectedData.getCenterZ(), gameConfig.getBorderStartDiameter(), 0);
				borderController.setVisibleTo(editor);
				borderController.updatePlayers();
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.ENABLED_BORDER);
				isBorderVisible = true;
			} else {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.BORDER_UNAVAILABLE);
			}
		}
	}
	
	public String getTargetedBorder() {
		if (selectedData == null) {
			return null;
		}
		return selectedData.getLocationAsString();
	}
	
	public void setBorderTargetType(boolean toDeathmatch) {
		if (selectedData == null) {
			return;
		}
		if (borderController != null) {
			if (!isBorderVisible) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.ENABLED_BORDER);
				borderController.setVisibleTo(editor);
				isBorderVisible = true;
			}
			if (toDeathmatch) {
				borderController.setTarget(selectedData.getCenterX(), selectedData.getCenterZ(), selectedData.borderDiameter, 0);
				isBorderTargetDeathmatch = true;
			} else {
				borderController.setTarget(selectedData.getCenterX(), selectedData.getCenterZ(), gameConfig.getBorderStartDiameter(), 0);
				isBorderTargetDeathmatch = false;
			}
		}
	}
	
	public void hideBorder() {
		if (isBorderVisible) {
			toggleWorldBorder();
		}
	}

	public BorderController getBorderController() {
		return borderController;
	}
	
	public void setBorderDeathmatchTarget(String target) {
		for (DeathmatchConfiguration dc : gameConfig.getDeathmatchConfigs()) {
			if (dc.getLocationAsString().equals(target)) {
				selectedData = dc;
				setBorderTargetType(isBorderTargetDeathmatch);
				break;
			}
		}
	}
	
	public void disposeBorder() {
		if (borderController != null) {
			borderController.close();
		}
	}

	public ItemSet getItemSet(String name) {
		// TODO extract to HashMap?

		for (ItemSet is : gameConfig.getItemSets()) {
			if (is.name.contentEquals(name)) {
				return is;
			}
		}
		return null;
	}
	
	public boolean isItemChest(Location l) {
		// TODO check
		//return gameConfig.chestLocations.stream().anyMatch(co -> co.loc.contentEquals(Utils.stringFromLocation(l, false, true)));
		return gameConfig.getChests().stream().anyMatch(co -> co.location == l);
	}

	public boolean addItemChest(Location l) {
		ChestConfiguration co = new ChestConfiguration();
		co.location = l;
		co.itemSets = new ArrayList<>();
		co.itemSets.add(gameConfig.getDefaultItemSet().name);

		return gameConfig.getChests().add(co);
	}
	
	public boolean removeItemChest(Location l) {
		return gameConfig.getChests().removeIf(co -> l.equals(co.location));
	}
	
	public boolean addChestItemSet(Location l, String item) {
		for (ChestConfiguration co : gameConfig.getChests()) {
			if (l.equals(co.location)) {
				co.itemSets.add(item);
				return true;
			}
		}
		return false;
	}
	
	public boolean updateAllChestItemSets(Location l, List<String> itemSets) {
		for (ChestConfiguration co : gameConfig.getChests()) {
			if (l.equals(co.location)) {
				co.itemSets.clear();
				co.itemSets.addAll(itemSets);
				return true;
			}
		}
		return false;
	}
	
	public List<String> getChestItemSets(Location l) {
		for (ChestConfiguration co : gameConfig.getChests()) {
			if (l.equals(co.location)) {
				return co.itemSets;
			}
		}
		if (l.getBlock().getType() == Material.CHEST) {
			return new ArrayList<>();
		}
		return null;
	}
	
	public boolean isSpawnPlate(Location l) {
		return gameConfig.getSpawnLocations().stream().anyMatch(loc -> loc.equals(l));
	}
	
	public boolean addSpawnPlate(Location l) {
		return gameConfig.getSpawnLocations().add(l);
	}
	
	public boolean removeSpawnPlate(Location l) {
		return gameConfig.getSpawnLocations().removeIf(loc -> loc.equals(l));
	}
	
	public boolean createItemSet(String name, boolean makeDefault) {
		if (gameConfig.getItemSetByName(name) != null) {
			return false;
		}

		ItemSet set = new ItemSet();
		set.name = name;
		set.isDefault = makeDefault;

		gameConfig.addItemSet(set, makeDefault);
		return true;
	}
	
	public boolean renameItemSet(String itemSet, String newName) {
		for (ItemSet set : gameConfig.getItemSets()) {
			if (set.name.equals(itemSet)) {
				set.name = newName;
				return true;
			}
		}
		return false;
	}
	
	public boolean replaceItemSet(ItemStack[] items, String itemSet) {
		for (ItemSet set : gameConfig.getItemSets()) {
			if (set.name.equals(itemSet)) {
				if (set.items == null) {
					set.items = new ArrayList<>();
				}
				set.items.clear();
				for (ItemStack item : items) {
					if (item != null) {
						set.items.add(ItemModel.fromItemStack(item));
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public List<String> getDeathmatchLocations() {
		return gameConfig.getDeathmatchConfigs().stream().map(x -> x.getLocationAsString()).collect(Collectors.toList());
	}
	
	public void queryInput(EditorQueries query) {
		manager.getHandler().getMessageProvider().sendMessage(editor, query.getPluginMessage());
		currentQuery = query;
	}
	
	public boolean passChat(String message) {
		if (currentQuery == null) {
			return false;
		}

		if (message.toLowerCase().contentEquals("cancel")) {
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.CANCEL_INPUT);
			currentQuery = null;
			return true;
		}
		PredicateResult<String, ?> pr = currentQuery.getPredicate().validate(message);
		if (!pr.SUCCESSFUL) {
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.BAD_VALUE);
			return true;
		}
		String strOutput = pr.INPUT;
		switch (currentQuery) {
		case NEW_ITEMSET_NAME:
			createItemSet(strOutput, false);
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.ADDED_ITEM_SET);

			Bukkit.getServer().getScheduler().callSyncMethod(manager.getHandler(), () ->
				manager.openItemSetCustomiseInventory(editor, strOutput)
			);

			break;
		case RENAME_ITEMSET_NAME:
			renameItemSet(associatedKey, strOutput);
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.RENAMED_ITEM_SET);
			break;
		case MAP_NAME:
			gameConfig.setMapName(strOutput);
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.RENAMED_MAP);
			break;
		case TIME_TO_SHRINK:
			gameConfig.setPreShrinkPeriod(pr.wrapOutput(Integer.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.UPDATED_PRE_SHRINK_DURATION);
			break;
		case BORDER_DPS:
			gameConfig.setBorderDPS(pr.wrapOutput(Double.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.UPDATED_BORDER_DPS);
			break;
		case BORDER_START_RADIUS:
			gameConfig.setBorderStartDiameter(pr.wrapOutput(Double.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.UPDATED_BORDER_RADIUS);
			break;
		case GRACE_PERIOD:
			gameConfig.setGracePeriod(pr.wrapOutput(Integer.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.UPDATED_PVP_OFF_TIME);
			break;
		case WAIT_PERIOD:
			gameConfig.setWaitPeriod(pr.wrapOutput(Integer.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.Editor.UPDATED_WAIT_PERIOD);
		default:
			break;
		}
		currentQuery = null;
		return true;
	}

	public GameConfigurationWrapper getGameConfiguration() {
		return gameConfig;
	}
}

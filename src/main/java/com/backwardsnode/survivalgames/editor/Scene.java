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

import com.backwardsnode.survivalgames.ProtocolConnector;
import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.config.BorderConfiguration;
import com.backwardsnode.survivalgames.config.DeathmatchConfiguration;
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.config.IConfigurable;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.game.PlayerStorageCache;
import com.backwardsnode.survivalgames.item.ChestObject;
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

public class Scene implements IConfigurable {

	private final Player editor;
	private final EditorManager manager;
	private final GameConfiguration gameConfig;
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

	public Scene(Player editor, EditorManager manager, GameConfiguration gameConfig) {
		checkNotNull(editor);
		checkNotNull(manager);
		checkNotNull(gameConfig);
		this.editor = editor;
		this.manager = manager;
		this.gameConfig = gameConfig;
		playerCache = new PlayerStorageCache(editor, true);
		playerCache.cacheCurrentInventory();
		togglingCache = new PlayerStorageCache(editor, true);
		configure();
		if (gameConfig.itemSets.size() < 1) {
			createItemSet("common", true);
		}
	}
	
	public void restoreInventory() {
		playerCache.restoreInventory();
	}
	
	@Override
	public void configure() {
		if (gameConfig.mapName == null) {
			gameConfig.mapName = "Untitled";
		}
		if (gameConfig.implementList == null) {
			gameConfig.implementList = new ArrayList<>();
		}
		if (gameConfig.spawnLocs == null) {
			gameConfig.spawnLocs = new ArrayList<>();
		}
		if (gameConfig.chestLocations == null) {
			gameConfig.chestLocations = new ArrayList<>();
		}
		if (gameConfig.itemSets == null) {
			gameConfig.itemSets = new ArrayList<>();
		}
		if (gameConfig.border == null) {
			gameConfig.border = new BorderConfiguration();
		}
		if (gameConfig.border.deathmatchLocations == null) {
			gameConfig.border.deathmatchLocations = new ArrayList<>();
		}
	}
	
	public boolean save() {
		gameConfig.strSpawns = new ArrayList<>();
		for (Location spawnpoint : gameConfig.spawnLocs) {
			gameConfig.strSpawns.add(Utils.stringFromLocation(spawnpoint, false, true));
		}
		for (ChestObject co : gameConfig.chestLocations) {
			co.loc = Utils.stringFromLocation(co.location, false, true);
		}
		return gameConfig.saveConfiguration();
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
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.DISABLED_BORDER);
				borderController.unsetVisibleTo(editor);
				isBorderVisible = false;
			} else {
				borderController.setVisibleTo(editor);
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.ENABLED_BORDER);
				isBorderVisible = true;
			}
		} else {
			if (gameConfig.getDeathmatchConfigs().size() < 1) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.NO_DEATHMATCH_LOCS);
				return;
			}
			selectedData = gameConfig.getDeathmatchConfigs().get(0);
			if (selectedData == null) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.NO_DEATHMATCH_LOCS);
				return;
			}
			ProtocolConnector connector = manager.getHandler().getDependencyManager().getProtocolConnector();
			if (connector != null) {
				borderController = new BorderController(connector, editor.getWorld());
				borderController.setDefaultWarning(1, 1);
				borderController.setTarget(selectedData.centerX, selectedData.centerZ, gameConfig.border.borderStartRadius, 0);
				borderController.setVisibleTo(editor);
				borderController.updatePlayers();
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.ENABLED_BORDER);
				isBorderVisible = true;
			} else {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.BORDER_UNAVAILABLE);
			}
		}
	}
	
	public String getTargetedBorder() {
		if (selectedData == null) {
			return null;
		}
		return selectedData.loc;
	}
	
	public void setBorderTargetType(boolean toDeathmatch) {
		if (selectedData == null) {
			return;
		}
		if (borderController != null) {
			if (!isBorderVisible) {
				manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.ENABLED_BORDER);
				borderController.setVisibleTo(editor);
				isBorderVisible = true;
			}
			if (toDeathmatch) {
				borderController.setTarget(selectedData.centerX, selectedData.centerZ, selectedData.borderRadius, 0);
				isBorderTargetDeathmatch = true;
			} else {
				borderController.setTarget(selectedData.centerX, selectedData.centerZ, gameConfig.border.borderStartRadius, 0);
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
		for (DeathmatchConfiguration dc : gameConfig.border.deathmatchLocations) {
			if (dc.loc.contentEquals(target)) {
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
	
	public void setMapName(String name) {
		gameConfig.mapName = name;
	}
	
	public void setWaitPeriod(int period) {
		gameConfig.waitTime = period;
	}
	
	public void setGracePeriod(int period) {
		gameConfig.gracePeriod = period;
	}
	
	public void setTimeToShrink(int period) {
		gameConfig.borderCollapseDelay = period;
	}
	
	public void setBorderDps(double damage) {
		gameConfig.border.damagePerSecond = damage;
	}
	
	public void setBorderStartDiameter(double diameter) {
		gameConfig.border.borderStartRadius = diameter;
	}
	
	public void setPreFillChest(boolean prefill) {
		gameConfig.preFillChests = prefill;
	}
	
	public void setDeathFirework(boolean spawn) {
		gameConfig.spawnFireworkOnDeath = spawn;
	}
	
	public void setKillFirework(boolean spawn) {
		gameConfig.spawnFireworkOnKill = spawn;
	}
	
	public void setWIP(boolean wip) {
		gameConfig.isWIP = wip;
	}

	public String getMapName() {
		return gameConfig.mapName;
	}
	
	public int getWaitPeriod() {
		return gameConfig.waitTime;
	}
	
	public int getGracePeriod() {
		return gameConfig.gracePeriod;
	}
	
	public int getTimeToDeathmatch() {
		return gameConfig.borderCollapseDelay;
	}
	
	public double getBorderDps() {
		return gameConfig.border.damagePerSecond;
	}
	
	public double borderStartDiameter() {
		return gameConfig.border.borderStartRadius;
	}

	public ItemSet getItemSet(String name) {
		// TODO extract to HashMap?

		for (ItemSet is : gameConfig.itemSets) {
			if (is.name.contentEquals(name)) {
				return is;
			}
		}
		return null;
	}

	public List<ItemSet> getItemSets() {
		return gameConfig.itemSets;
	}
	
	public boolean getPreFillChests() {
		return gameConfig.preFillChests;
	}
	
	public boolean getDeathFirework() {
		return gameConfig.spawnFireworkOnDeath;
	}
	
	public boolean getKillFirework() {
		return gameConfig.spawnFireworkOnKill;
	}

	public boolean isWIP() {
		return gameConfig.isWIP;
	}
	
	public boolean isItemChest(Location l) {
		return gameConfig.chestLocations.stream().anyMatch(co -> co.loc.contentEquals(Utils.stringFromLocation(l, false, true)));
	}

	public boolean addItemChest(Location l) {
		ChestObject co = new ChestObject();
		co.loc = Utils.stringFromLocation(l, false, true);
		co.location = l;
		co.itemSets = new ArrayList<>();
		co.itemSets.add(gameConfig.itemSets.get(gameConfig.defaultSetIndex).name);
		return gameConfig.chestLocations.add(co);
	}
	
	public boolean removeItemChest(Location l) {
		return gameConfig.chestLocations.removeIf(co -> l.equals(co.location));
	}
	
	public boolean addChestItemSet(Location l, String item) {
		for (ChestObject co : gameConfig.chestLocations) {
			if (l.equals(co.location)) {
				co.itemSets.add(item);
				return true;
			}
		}
		return false;
	}
	
	public boolean updateAllChestItemSets(Location l, List<String> itemSets) {
		for (ChestObject co : gameConfig.chestLocations) {
			if (l.equals(co.location)) {
				co.itemSets.clear();
				itemSets.forEach(itemSet -> co.itemSets.add(itemSet));
				return true;
			}
		}
		return false;
	}
	
	public List<String> getChestItemSets(Location l) {
		for (ChestObject co : gameConfig.chestLocations) {
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
		return gameConfig.spawnLocs.stream().anyMatch(loc -> loc.equals(l));
	}
	
	public boolean addSpawnPlate(Location l) {
		return gameConfig.spawnLocs.add(l);
	}
	
	public boolean removeSpawnPlate(Location l) {
		return gameConfig.spawnLocs.removeIf(loc -> loc.equals(l));
	}
	
	public boolean createItemSet(String name, boolean isDefault) {
		for (ItemSet set : gameConfig.itemSets) {
			if (set.name.equals(name)) {
				return false;
			}
		}
		ItemSet set = new ItemSet();
		set.name = name;
		set.isDefault = isDefault;
		gameConfig.itemSets.add(set);
		if (isDefault) {
			gameConfig.defaultSetIndex = gameConfig.itemSets.indexOf(set);
		}
		return true;
	}
	
	public boolean renameItemSet(String itemSet, String newName) {
		for (ItemSet set : gameConfig.itemSets) {
			if (set.name.equals(itemSet)) {
				set.name = newName;
				return true;
			}
		}
		return false;
	}
	
	public boolean replaceItemSet(ItemStack[] items, String itemSet) {
		for (ItemSet set : gameConfig.itemSets) {
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
		return gameConfig.getDeathmatchConfigs().stream().map(x -> x.loc).collect(Collectors.toList());
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
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.CANCEL_INPUT);
			currentQuery = null;
			return true;
		}
		PredicateResult<String, ?> pr = currentQuery.getPredicate().validate(message);
		if (!pr.SUCCESSFUL) {
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.BAD_VALUE);
			return true;
		}
		String strOutput = pr.INPUT;
		switch (currentQuery) {
		case NEW_ITEMSET_NAME:
			createItemSet(strOutput, false);
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.ADDED_ITEM_SET);

			Bukkit.getServer().getScheduler().callSyncMethod(manager.getHandler(), () ->
				manager.openItemSetCustomiseInventory(editor, strOutput)
			);

			break;
		case RENAME_ITEMSET_NAME:
			renameItemSet(associatedKey, strOutput);
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.RENAMED_ITEM_SET);
			break;
		case MAP_NAME:
			setMapName(strOutput);
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.RENAMED_MAP);
			break;
		case TIME_TO_SHRINK:
			setTimeToShrink(pr.wrapOutput(Integer.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.UPDATED_PRE_SHRINK_DURATION);
			break;
		case BORDER_DPS:
			setBorderDps(pr.wrapOutput(Double.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.UPDATED_BORDER_DPS);
			break;
		case BORDER_START_RADIUS:
			setBorderStartDiameter(pr.wrapOutput(Double.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.UPDATED_BORDER_RADIUS);
			break;
		case GRACE_PERIOD:
			setGracePeriod(pr.wrapOutput(Integer.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.UPDATED_PVP_OFF_TIME);
			break;
		case WAIT_PERIOD:
			setWaitPeriod(pr.wrapOutput(Integer.class));
			manager.getHandler().getMessageProvider().sendMessage(editor, Messages.EDITOR.UPDATED_WAIT_PERIOD);
		default:
			break;
		}
		currentQuery = null;
		return true;
	}
}

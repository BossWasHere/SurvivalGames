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
package com.backwardsnode.survivalgames.editor;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.backwardsnode.survivalgames.game.BorderControl;
import com.backwardsnode.survivalgames.game.ChestObject;
import com.backwardsnode.survivalgames.game.DeathmatchProfile;
import com.backwardsnode.survivalgames.game.GameConfiguration;
import com.backwardsnode.survivalgames.game.ItemModel;
import com.backwardsnode.survivalgames.game.ItemSet;
import com.backwardsnode.survivalgames.util.WorldUtil;

public class Scene {

	private final GameConfiguration gameConfig;

	public Scene(GameConfiguration gameConfig) {
		checkNotNull(gameConfig);
		this.gameConfig = gameConfig;
		createNulls();
		if (gameConfig.itemSets.size() < 1) {
			createItemSet("common", true);
		}
	}
	
	protected void createNulls() {
		if (gameConfig.mapName == null) {
			gameConfig.mapName = "Untitled";
		}
		if (gameConfig.implementOther == null) {
			gameConfig.implementOther = new ArrayList<String>();
		}
		if (gameConfig.spawnLocs == null) {
			gameConfig.spawnLocs = new ArrayList<Location>();
		}
		if (gameConfig.chestLocations == null) {
			gameConfig.chestLocations = new ArrayList<ChestObject>();
		}
		if (gameConfig.itemSets == null) {
			gameConfig.itemSets = new ArrayList<ItemSet>();
		}
		if (gameConfig.border == null) {
			gameConfig.border = new BorderControl();
		}
		if (gameConfig.border.deathmatchLocations == null) {
			gameConfig.border.deathmatchLocations = new ArrayList<DeathmatchProfile>();
		}
	}
	
	public boolean save() {
		gameConfig.str_spawns = new ArrayList<String>();
		for (Location spawnpoint : gameConfig.spawnLocs) {
			gameConfig.str_spawns.add(WorldUtil.stringFromLocation(spawnpoint, true));
		}
		for (ChestObject co : gameConfig.chestLocations) {
			co.loc = WorldUtil.stringFromLocation(co.location, true);
		}
		return gameConfig.saveConfiguration();
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
	
	public void setTimeToDeathmatch(int period) {
		gameConfig.border.deathmatchTime = period;
	}
	
	public void setBorderDps(double damage) {
		gameConfig.border.damagePerSecond = damage;
	}
	
	public void setBorderStartRadius(int radius) {
		gameConfig.border.borderStartRadius = radius;
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
		return gameConfig.border.deathmatchTime;
	}
	
	public double getBorderDps() {
		return gameConfig.border.damagePerSecond;
	}
	
	public int getBorderStartRadius() {
		return gameConfig.border.borderStartRadius;
	}
	
	public List<ItemSet> getItemSets() {
		return gameConfig.itemSets;
	}
	
	public boolean isItemChest(Location l) {
		return gameConfig.chestLocations.stream().anyMatch(co -> co.loc.contentEquals(WorldUtil.stringFromLocation(l, true)));
	}

	public boolean addItemChest(Location l) {
		ChestObject co = new ChestObject();
		co.loc = WorldUtil.stringFromLocation(l, true);
		co.location = l;
		co.itemSets = new ArrayList<String>();
		co.itemSets.add(gameConfig.itemSets.get(gameConfig.defaultSetIndex).name);
		return gameConfig.chestLocations.add(co);
	}
	
	public boolean removeItemChest(Location l) {
		return gameConfig.chestLocations.removeIf(co -> WorldUtil.isSameLocation(l, co.location));
	}
	
	public boolean addChestItemSet(Location l, String item) {
		for (ChestObject co : gameConfig.chestLocations) {
			if (WorldUtil.isSameLocation(l, co.location)) {
				co.itemSets.add(item);
				return true;
			}
		}
		return false;
	}
	
	public boolean updateAllChestItemSets(Location l, List<String> itemSets) {
		for (ChestObject co : gameConfig.chestLocations) {
			if (WorldUtil.isSameLocation(l, co.location)) {
				co.itemSets.clear();
				itemSets.forEach(itemSet -> co.itemSets.add(itemSet));
				return true;
			}
		}
		return false;
	}
	
	public List<String> getChestItemSets(Location l) {
		for (ChestObject co : gameConfig.chestLocations) {
			if (WorldUtil.isSameLocation(l, co.location)) {
				return co.itemSets;
			}
		}
		if (l.getBlock().getType() == Material.CHEST) {
			return new ArrayList<String>();
		}
		return null;
	}
	
	public boolean isSpawnPlate(Location l) {
		return gameConfig.spawnLocs.stream().anyMatch(loc -> WorldUtil.isSameLocation(l, loc));
	}
	
	public boolean addSpawnPlate(Location l) {
		return gameConfig.spawnLocs.add(l);
	}
	
	public boolean removeSpawnPlate(Location l) {
		return gameConfig.spawnLocs.removeIf(loc -> WorldUtil.isSameLocation(l, loc));
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
					set.items = new ArrayList<ItemModel>();
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
}

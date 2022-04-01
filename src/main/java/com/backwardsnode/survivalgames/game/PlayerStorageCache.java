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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerStorageCache {

	private final Player player;
	private final boolean storeHotbarOnly;
    private ItemStack[] storedInventory;
	private Location playerLocation;
	private int playerExperienceLevel;
	private float playerExperience;
	private double playerHealth;
	private int playerFood;
	private float playerSaturation;
	private GameMode playerGamemode;
	
	public PlayerStorageCache(Player player) {
		this.player = player;
		storeHotbarOnly = false;
	}
	
	public PlayerStorageCache(Player player, boolean storeHotbarOnly) {
		this.player = player;
		this.storeHotbarOnly = storeHotbarOnly;
	}

	public void cacheCurrent(PlayerCacheSettings cacheSettings) {
		if (cacheSettings.cacheInventory) {
			cacheCurrentInventory();
		}
		if (cacheSettings.cacheLocation) {
			cacheCurrentLocation();
		}
		if (cacheSettings.autoClearInventory) {
			clearInventory();
		}
		if (cacheSettings.cacheGamemode) {
			cacheCurrentGamemode();
		}
		if (cacheSettings.cacheExperience) {
			cacheCurrentExperience();
		}
		if (cacheSettings.autoZeroExperience) {
			resetExperience();
		}
		if (cacheSettings.cacheHealth) {
			cacheCurrentHealth();
		}
		if (cacheSettings.autoFullHealth) {
			setBaseHealth();
		}
		if (cacheSettings.cacheFood) {
			cacheCurrentFood();
		}
		if (cacheSettings.autoFullFood) {
			setBaseFood();
		}
	}
	
	public void cacheCurrentInventory() {
		if (storeHotbarOnly) {
			storedInventory = new ItemStack[9];
			PlayerInventory ci = player.getInventory();
			for (int i = 0; i < 9; i++) {
				storedInventory[i] = ci.getItem(i);
			}
		} else {
			storedInventory = player.getInventory().getContents();
		}
	}

	public void cacheCurrentLocation() {
		playerLocation = player.getLocation();
	}
	
	public void cacheCurrentExperience() {
		playerExperienceLevel = player.getLevel();
		playerExperience = player.getExp();
	}
	
	public void cacheCurrentGamemode() {
		playerGamemode = player.getGameMode();
	}
	
	public void cacheCurrentHealth() {
		playerHealth = player.getHealth();
	}
	
	public void cacheCurrentFood() {
		playerFood = player.getFoodLevel();
		playerSaturation = player.getSaturation();
	}
	
	public void restoreInventory() {
		if (storedInventory != null) {
			if (storeHotbarOnly) {
				PlayerInventory ci = player.getInventory();
				for (int i = 0; i < 9; i++) {
					ci.setItem(i, storedInventory[i]);
				}
			} else {
				player.getInventory().setContents(storedInventory);
			}
		}
	}
	
	public void swapInventory() {
		if (storedInventory != null) {
			ItemStack[] items;
			if (storeHotbarOnly) {
				items = new ItemStack[9];
				PlayerInventory ci = player.getInventory();
				for (int i = 0; i < 9; i++) {
					items[i] = ci.getItem(i);
					ci.setItem(i, storedInventory[i]);
				}
			} else {
				items = player.getInventory().getContents();
				player.getInventory().setContents(storedInventory);
			}
			storedInventory = items;
		} else {
			cacheCurrentInventory();
			player.getInventory().clear();
		}
	}

	public void restore(PlayerCacheSettings cacheSettings) {
		if (cacheSettings.cacheInventory) {
			restoreInventory();
		}
		if (cacheSettings.cacheLocation) {
			restoreLocation();
		}
		if (cacheSettings.cacheGamemode) {
			restoreGamemode();
		}
		if (cacheSettings.cacheExperience) {
			restoreExperience();
		}
		if (cacheSettings.cacheHealth) {
			restoreHealth();
		}
		if (cacheSettings.cacheFood) {
			restoreFood();
		}
	}

	public void restoreLocation() {
		player.teleport(playerLocation);
	}
	
	public void restoreExperience() {
		player.setExp(playerExperience);
		player.setLevel(playerExperienceLevel);
	}
	
	public void restoreGamemode() {
		player.setGameMode(playerGamemode);
	}
	
	public void restoreHealth() {
		player.setHealth(playerHealth);
	}
	
	public void restoreFood() {
		player.setFoodLevel(playerFood);
		player.setSaturation(playerSaturation);
	}
	
	public void clearInventory() {
		player.getInventory().clear();
	}
	
	public void resetExperience() {
		player.setExp(0);
		player.setLevel(0);
	}
	
	public void setBaseHealth() {
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
	}
	
	public void setBaseFood() {
		player.setFoodLevel(20);
		player.setSaturation(5);
	}

	public Player getPlayer() {
		return player;
	}
}

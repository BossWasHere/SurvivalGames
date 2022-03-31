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

public class PlayerCacheSettings {

	public boolean cacheInventory = true;
	public boolean cacheLocation = true;
	public boolean cacheExperience = true;
	public boolean cacheGamemode = true;
	public boolean cacheHealth = true;
	public boolean cacheFood = true;
	
	public boolean autoClearInventory = true;
	public boolean autoZeroExperience = true;
	public boolean autoFullHealth = true;
	public boolean autoFullFood = true;
	
	public PlayerCacheSettings() { }

	public PlayerCacheSettings(boolean cacheInventory, boolean cacheLocation, boolean cacheExperience) {
		this.cacheInventory = cacheInventory;
		this.cacheLocation = cacheLocation;
		this.cacheExperience = cacheExperience;
	}

	public PlayerCacheSettings(boolean cacheInventory, boolean cacheLocation, boolean cacheExperience, boolean cacheGamemode, boolean cacheHealth, boolean cacheFood) {
		this.cacheInventory = cacheInventory;
		this.cacheLocation = cacheLocation;
		this.cacheExperience = cacheExperience;
		this.cacheGamemode = cacheGamemode;
		this.cacheHealth = cacheHealth;
		this.cacheFood = cacheFood;
	}

	public PlayerCacheSettings(boolean cacheInventory, boolean cacheLocation, boolean cacheExperience, boolean cacheGamemode, boolean cacheHealth, boolean cacheFood, boolean autoClearInventory, boolean autoZeroExperience, boolean autoFullHealth, boolean autoFullFood) {
		this.cacheInventory = cacheInventory;
		this.cacheLocation = cacheLocation;
		this.cacheExperience = cacheExperience;
		this.cacheGamemode = cacheGamemode;
		this.cacheHealth = cacheHealth;
		this.cacheFood = cacheFood;
		this.autoClearInventory = autoClearInventory;
		this.autoZeroExperience = autoZeroExperience;
		this.autoFullHealth = autoFullHealth;
		this.autoFullFood = autoFullFood;
	}
}

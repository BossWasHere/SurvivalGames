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
package com.backwardsnode.survivalgames.config;

import com.backwardsnode.survivalgames.item.ItemSet;

import java.util.List;
import java.util.Map;

public class GameConfiguration {
	
	public int configVersion;
	public String mapName;
	public float entryFee;
	public Map<String, RewardConfiguration> rewards;
	public List<String> spawnLocations;
	public BorderConfiguration border;

	public int startingDaytime;
	public int gracePeriod;
	public int preShrinkPeriod;
	public int waitPeriod;
	public int lootDropDelay;
	public int lootDropTriggerWithin;
	public float lootDropTriggerProbability;
	public float lootDropTriggerProbabilityIncrement;
	public boolean daylightCycle;
	public boolean preFillChests;
	public boolean spawnFireworkOnKill;
	public boolean spawnFireworkOnDeath;
	public boolean lightningOnDeath;
	public boolean isWIP;
	
	public List<ChestConfiguration> chestLocations;
	public List<LootDropConfiguration> lootDropLocations;
	public List<ItemSet> itemSets;

}

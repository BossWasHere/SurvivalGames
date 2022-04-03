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

package com.backwardsnode.survivalgames.config.migration;

import com.backwardsnode.survivalgames.config.*;
import com.backwardsnode.survivalgames.world.BlockLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Version0Model implements Migration {

	public String mapName;
	public List<String> spawnLocations;
	public Version0Border border;
	
	public int waitTime;
	public int gracePeriod;
	public boolean preFillChests;
	
	public List<ChestConfiguration> chestLocations;
	public List<Version1Model.Version1ItemSet> itemSets;
	
	public static class Version0Border {
		public double damagePerSecond;
		public int deathmatchTime;
		public int borderStartRadius;
		public List<Version0Deathmatch> deathmatchLocations;
	}
	
	public static class Version0Deathmatch {
		public String loc;
		public int borderRadius;
		public int shrinkTime;
		public int deathmatchTime;
	}

	@Override
	public GameConfiguration migrate() {
		GameConfiguration gc = new GameConfiguration();
		gc.configVersion = GameConfigurationWrapper.SUPPORTED_CONFIG;
		gc.mapName = mapName;
		gc.entryFee = 0;
		gc.rewards = new HashMap<>(0);
		gc.spawnLocations = spawnLocations;
		
		BorderConfiguration bc = new BorderConfiguration();
		bc.borderStartDiameter = border.borderStartRadius;
		bc.damagePerSecond = border.damagePerSecond;
		bc.deathmatchLocations = border.deathmatchLocations.stream().map(x -> {
			DeathmatchConfiguration dc = new DeathmatchConfiguration();
			dc.location = new BlockLocation(x.loc);
			dc.borderDiameter = x.borderRadius;
			dc.shrinkTime = x.shrinkTime;
			dc.deathmatchDuration = x.deathmatchTime;
			dc.collapseTime = 60;
			return dc;
		}).collect(Collectors.toList());
		gc.border = bc;

		gc.startingDaytime = -1;
		gc.waitPeriod = waitTime;
		gc.gracePeriod = gracePeriod;
		gc.preFillChests = preFillChests;
		gc.preShrinkPeriod = border.deathmatchTime;
		gc.lootDropDelay = -1;
		gc.lootDropTriggerWithin = -1;
		gc.lootDropTriggerProbability = 0;
		gc.lootDropTriggerProbabilityIncrement = 0;
		gc.daylightCycle = true;
		gc.spawnFireworkOnDeath = true;
		gc.spawnFireworkOnKill = true;
		gc.lightningOnDeath = true;
		gc.isWIP = false;
		gc.chestLocations = chestLocations;

		gc.lootDropLocations = new ArrayList<>(0);
		gc.itemSets = new ArrayList<>(itemSets.size());
		for (Version1Model.Version1ItemSet is : itemSets) {
			gc.itemSets.add(is.migrate());
		}
		
		return gc;
	}
}

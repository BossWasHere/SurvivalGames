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

import com.backwardsnode.survivalgames.config.BorderConfiguration;
import com.backwardsnode.survivalgames.config.DeathmatchConfiguration;
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.config.ChestConfiguration;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Version0Model {

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
	
	public GameConfiguration migrate() {
		GameConfiguration gc = new GameConfiguration();
		gc.configVersion = GameConfiguration.SUPPORTED_CONFIG;
		gc.mapName = mapName;
		gc.isWIP = false;
		gc.entryFee = 0;
		gc.rewards = new HashMap<>();
		gc.strSpawns = spawnLocations;
		
		BorderConfiguration bc = new BorderConfiguration();
		bc.borderStartRadius = border.borderStartRadius;
		bc.damagePerSecond = border.damagePerSecond;
		bc.deathmatchLocations = border.deathmatchLocations.stream().map(x -> {
			DeathmatchConfiguration dc = new DeathmatchConfiguration();
			dc.loc = x.loc;
			dc.borderRadius = x.borderRadius;
			dc.shrinkTime = x.shrinkTime;
			dc.deathmatchDuration = x.deathmatchTime;
			dc.collapseTime = 60;
			return dc;
		}).collect(Collectors.toList());
		gc.border = bc;
		
		gc.waitTime = waitTime;
		gc.gracePeriod = gracePeriod;
		gc.preFillChests = preFillChests;
		gc.borderCollapseDelay = border.deathmatchTime;
		gc.spawnFireworkOnDeath = true;
		gc.spawnFireworkOnKill = true;
		gc.lightningOnDeath = true;
		gc.chestLocations = chestLocations;

		gc.itemSets = new ArrayList<>(itemSets.size());
		for (Version1Model.Version1ItemSet is : itemSets) {
			gc.itemSets.add(is.migrate());
		}
		
		return gc;
	}
}

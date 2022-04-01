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

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.item.ChestObject;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class GameConfiguration implements IConfigurable {

	public transient static final int SUPPORTED_CONFIG = 2;

    private transient File _gameConfigFile;
	public transient List<Location> spawnLocs;
	public transient int defaultSetIndex = 0;
	
	public int configVersion;
	public String mapName;
	public float entryFee;
	public Map<String, RewardConfiguration> rewards;
	@SerializedName("spawnLocations")
	public List<String> strSpawns;
	@SerializedName("implements")
	public List<String> implementList;
	public BorderConfiguration border;
	
	public int waitTime;
	public int gracePeriod;
	public int borderCollapseDelay;
	public boolean preFillChests;
	public boolean spawnFireworkOnKill;
	public boolean spawnFireworkOnDeath;
	public boolean lightningOnDeath;
	public boolean isWIP;
	
	public List<ChestObject> chestLocations;
	public List<ItemSet> itemSets;
	
	public static GameConfiguration createEmptyConfiguration(File configFile) {
		checkNotNull(configFile);
		GameConfiguration gc = new GameConfiguration();
		gc.configVersion = SUPPORTED_CONFIG;
		gc._gameConfigFile = configFile;
		return gc;
	}
	
	public boolean copyConfiguration(File target) {
		try {
			GsonBuilder builder = new GsonBuilder().serializeNulls().setPrettyPrinting();
			Gson gson = builder.create();
			Utils.writeFile(target, gson.toJson(this));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean saveConfiguration() {
		return copyConfiguration(_gameConfigFile);
	}
	
	public static GameConfiguration loadGameConfiguration(File configFile) throws FileNotFoundException, GameConfigurationException, JsonIOException, JsonSyntaxException {
		checkNotNull(configFile);
		Object gameConfig;
		FileReader fileReader = new FileReader(configFile);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		gameConfig = gson.fromJson(new JsonReader(fileReader), GameConfiguration.class);
		try {
			fileReader.close();
		} catch (IOException ignored) {}

		if (gameConfig instanceof GameConfiguration gc) {
			if (gc.configVersion != SUPPORTED_CONFIG) {
				throw new GameConfigurationException("Configuration is outdated: " + gc.configVersion + " < " + SUPPORTED_CONFIG);
			}
			gc._gameConfigFile = configFile;
			if (!Plugin.TEST) {
				gc.configure();
			}
			return gc;
		}
		return null;
	}
	
	@Override
	public void configure() {
		if (chestLocations == null || itemSets == null || strSpawns == null || rewards == null) {
			throw new GameConfigurationException("Missing configuration entries");
		}
		chestLocations.forEach(ChestObject::configure);
		spawnLocs = new ArrayList<>();
		for (String strSpawn : strSpawns) {
			spawnLocs.add(Utils.locationFromString(strSpawn, false));
		}
		for (int i = 0; i < itemSets.size(); i++) {
			if (itemSets.get(i).isDefault) {
				defaultSetIndex = i;
				break;
			}
		}
		border.configure();
	}

	public List<Location> checkChests() {
		List<Location> invalidLocations = new ArrayList<>();
		for (ChestObject co : chestLocations) {
			if (co.location.getBlock().getType() != Material.CHEST) {
				invalidLocations.add(co.location);
			}
		}
		return invalidLocations;
	}
	
	public String getFileName() {
		return _gameConfigFile.getName();
	}

	public DeathmatchConfiguration selectDeathmatch() {
		if (border.deathmatchLocations == null) {
			return null;
		}
		int opts = border.deathmatchLocations.size();
		if (opts == 0) {
			return null;
		}
		return border.deathmatchLocations.get(new Random().nextInt(opts));
	}

	public List<DeathmatchConfiguration> getDeathmatchConfigs() {
		return border.deathmatchLocations;
	}
}

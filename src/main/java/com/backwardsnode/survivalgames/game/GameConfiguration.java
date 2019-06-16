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
package com.backwardsnode.survivalgames.game;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import com.backwardsnode.survivalgames.editor.Scene;
import com.backwardsnode.survivalgames.util.FileUtil;
import com.backwardsnode.survivalgames.util.WorldUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

public class GameConfiguration {

	private transient File _gameConfigFile;
	
	public String mapName;
	@SerializedName("spawnLocations")
	public List<String> str_spawns;
	@SerializedName("implements")
	public List<String> implementOther;
	public BorderControl border;
	public int waitTime;
	public int gracePeriod;
	public boolean preFillChests;
	public List<ChestObject> chestLocations;
	public List<ItemSet> itemSets;
	
	public transient List<Location> spawnLocs;
	public transient int defaultSetIndex = 0;
	
	public static GameConfiguration createEmptyConfiguration(File configDirectory, File configFile) {
		GameConfiguration gc = new GameConfiguration();
		gc._gameConfigFile = configFile;
		return gc;
	}
	
	public Scene createScene() {
		Scene scene = new Scene(this);
		return scene;
	}
	
	public boolean saveConfiguration() {
		try {
			GsonBuilder builder = new GsonBuilder().serializeNulls().setPrettyPrinting();
			Gson gson = builder.create();
			FileUtil.writeFile(_gameConfigFile.getAbsolutePath(), gson.toJson(this));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static GameConfiguration loadGameConfigurationOrCreateEmpty(File configDirectory, File configFile) {
		GameConfiguration configToReturn = null;
		try {
			configToReturn = loadGameConfiguration(configDirectory, configFile);
		} catch (Exception e) {}
		if (configToReturn == null) {
			return createEmptyConfiguration(configDirectory, configFile);
		}
		return configToReturn;
	}
	
	public static GameConfiguration loadGameConfiguration(File configDirectory, File configFile) throws FileNotFoundException, JsonIOException, JsonSyntaxException {
		checkNotNull(configFile);
		Object gameConfig = null;
		FileReader fileReader = new FileReader(configFile);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		gameConfig = gson.fromJson(new JsonReader(fileReader), GameConfiguration.class);
		try {
			fileReader.close();
		} catch (IOException e) {}
		if (gameConfig instanceof GameConfiguration) {
			GameConfiguration gc = (GameConfiguration) gameConfig;
			gc._gameConfigFile = configFile;
			gc.buildObjects();
			return gc;
		}
		return null;
	}
	
	public void buildObjects() {
		checkNotNull(chestLocations);
		checkNotNull(itemSets);
		checkNotNull(str_spawns);
		for (ChestObject co : chestLocations) {
			co.buildObject();
		}
		spawnLocs = new ArrayList<Location>();
		for (int i = 0; i < str_spawns.size(); i++) {
			spawnLocs.add(WorldUtil.locationFromString(str_spawns.get(i)));
		}
		for (int i = 0; i < itemSets.size(); i++) {
			if (itemSets.get(i).isDefault) {
				defaultSetIndex = i;
				break;
			}
		}
	}

	public List<Location> checkChests() {
		List<Location> invalidLocations = new ArrayList<Location>();
		for (ChestObject co : chestLocations) {
			if (co.location.getBlock().getType() != Material.CHEST) {
				invalidLocations.add(co.location);
			}
		}
		return invalidLocations;
	}
}

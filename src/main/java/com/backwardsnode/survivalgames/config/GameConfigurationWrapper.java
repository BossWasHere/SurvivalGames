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

import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.backwardsnode.survivalgames.world.BlockLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class GameConfigurationWrapper {

    public static final int SUPPORTED_CONFIG = 2;

    private final GameConfiguration GAME_CONFIGURATION;

    private List<BlockLocation> spawnLocs;
    private int defaultSetIndex = 0;
    private File mainConfigFile;

    public GameConfigurationWrapper() {
        GAME_CONFIGURATION = new GameConfiguration();
        GAME_CONFIGURATION.configVersion = SUPPORTED_CONFIG;
        initialize();
    }

    public GameConfigurationWrapper(@NotNull GameConfiguration gameConfiguration) throws GameConfigurationException {
        if (gameConfiguration.configVersion != SUPPORTED_CONFIG) {
            throw new GameConfigurationException("Configuration is outdated: " + gameConfiguration.configVersion + " < " + SUPPORTED_CONFIG);
        }

        GAME_CONFIGURATION = gameConfiguration;
        initialize();
    }

    public GameConfigurationWrapper(File configFile, boolean load) throws FileNotFoundException, GameConfigurationException, JsonIOException, JsonSyntaxException {
        if (load) {
            FileReader fileReader = new FileReader(configFile);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            GAME_CONFIGURATION = gson.fromJson(new JsonReader(fileReader), GameConfiguration.class);

            try {
                fileReader.close();
            } catch (IOException ignored) {}

            if (GAME_CONFIGURATION.configVersion != SUPPORTED_CONFIG) {
                throw new GameConfigurationException("Configuration is outdated: " + GAME_CONFIGURATION.configVersion + " < " + SUPPORTED_CONFIG);
            }

        } else {
            GAME_CONFIGURATION = new GameConfiguration();
            GAME_CONFIGURATION.configVersion = SUPPORTED_CONFIG;
        }


        mainConfigFile = configFile;
        initialize();
    }

    private void initialize() {
        if (GAME_CONFIGURATION.chestLocations == null) {
            GAME_CONFIGURATION.chestLocations = new ArrayList<>();
        }
        if (GAME_CONFIGURATION.itemSets == null) {
            GAME_CONFIGURATION.itemSets = new ArrayList<>();
        }
        if (GAME_CONFIGURATION.spawnLocations == null) {
            GAME_CONFIGURATION.spawnLocations = new ArrayList<>();
        }
        if (GAME_CONFIGURATION.rewards == null) {
            GAME_CONFIGURATION.rewards = new HashMap<>();
        }
        if (GAME_CONFIGURATION.lootDropLocations == null) {
            GAME_CONFIGURATION.lootDropLocations = new ArrayList<>();
        }

        spawnLocs = new ArrayList<>();
        for (String strSpawn : GAME_CONFIGURATION.spawnLocations) {
            spawnLocs.add(new BlockLocation(strSpawn));
        }

        for (int i = 0; i < GAME_CONFIGURATION.itemSets.size(); i++) {
            if (GAME_CONFIGURATION.itemSets.get(i).isDefault) {
                defaultSetIndex = i;
                break;
            }
        }
    }

    public Set<BlockLocation> checkChests() {
        Set<BlockLocation> invalidLocations = new HashSet<>();
        for (ChestConfiguration co : GAME_CONFIGURATION.chestLocations) {
            if (co.location.getBlock().getType() != Material.CHEST) {
                invalidLocations.add(co.location);
            }
        }
        return invalidLocations;
    }

    public String getFileName() {
        return getMainConfigFile().getName();
    }

    public void setMainConfigFile(File file) {
        mainConfigFile = file;
    }

    public File getMainConfigFile() {
        return mainConfigFile;
    }

    public DeathmatchConfiguration selectDeathmatch() {
        if (GAME_CONFIGURATION.border.deathmatchLocations == null) {
            return null;
        }
        int opts = GAME_CONFIGURATION.border.deathmatchLocations.size();
        if (opts == 0) {
            return null;
        }
        return GAME_CONFIGURATION.border.deathmatchLocations.get(new Random().nextInt(opts));
    }

    public List<DeathmatchConfiguration> getDeathmatchConfigs() {
        return GAME_CONFIGURATION.border.deathmatchLocations;
    }

    public boolean save() {
        return mainConfigFile != null && saveCopyTo(mainConfigFile);
    }

    public boolean saveCopyTo(@NotNull File file) {
        try {
            GsonBuilder builder = new GsonBuilder().serializeNulls().setPrettyPrinting();
            Gson gson = builder.create();

            if (GAME_CONFIGURATION.spawnLocations == null) {
                GAME_CONFIGURATION.spawnLocations = new ArrayList<>();
            } else {
                GAME_CONFIGURATION.spawnLocations.clear();
            }

            for (BlockLocation spawnLocation : spawnLocs) {
                GAME_CONFIGURATION.spawnLocations.add(spawnLocation.toString());
            }

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(GAME_CONFIGURATION, writer);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasMapName() {
        return GAME_CONFIGURATION.mapName != null;
    }

    public String getMapName() {
        return GAME_CONFIGURATION.mapName == null ? "unknown" : GAME_CONFIGURATION.mapName;
    }

    public void setMapName(String mapName) {
        GAME_CONFIGURATION.mapName = mapName;
    }

    public List<BlockLocation> getSpawnLocations() {
        return spawnLocs;
    }

    public boolean getDoChestPrefilling() {
        return GAME_CONFIGURATION.preFillChests;
    }

    public void setDoChestPrefilling(boolean doChestPrefilling) {
        GAME_CONFIGURATION.preFillChests = doChestPrefilling;
    }

    public float getEntryFee() {
        return GAME_CONFIGURATION.entryFee;
    }

    public void setEntryFee(float entryFee) {
        GAME_CONFIGURATION.entryFee = entryFee;
    }

    public int getWaitPeriod() {
        return GAME_CONFIGURATION.waitPeriod;
    }

    public void setWaitPeriod(int waitPeriod) {
        GAME_CONFIGURATION.waitPeriod = waitPeriod;
    }

    public int getGracePeriod() {
        return GAME_CONFIGURATION.gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        GAME_CONFIGURATION.gracePeriod = gracePeriod;
    }

    public int getPreShrinkPeriod() {
        return GAME_CONFIGURATION.preShrinkPeriod;
    }

    public void setPreShrinkPeriod(int preShrinkPeriod) {
        GAME_CONFIGURATION.preShrinkPeriod = preShrinkPeriod;
    }

    public int getLootDropDelay() {
        return GAME_CONFIGURATION.lootDropDelay;
    }

    public void setLootDropDelay(int lootDropDelay) {
        GAME_CONFIGURATION.lootDropDelay = Math.max(-1, lootDropDelay);
    }

    public int getLootDropTriggerWindow() {
        return Math.max(0, GAME_CONFIGURATION.lootDropTriggerWithin);
    }

    public void setLootDropTriggerWindow(int lootDropTriggerWindow) {
        GAME_CONFIGURATION.lootDropTriggerWithin = Math.max(0, lootDropTriggerWindow);
    }

    public float getLootDropTriggerProbability() {
        return GAME_CONFIGURATION.lootDropTriggerProbability;
    }

    public void setLootDropTriggerProbability(float lootDropTriggerProbability) {
        GAME_CONFIGURATION.lootDropTriggerProbability = lootDropTriggerProbability;
    }

    public float getLootDropTriggerProbabilityIncrement() {
        return GAME_CONFIGURATION.lootDropTriggerProbabilityIncrement;
    }

    public void setLootDropTriggerProbabilityIncrement(float lootDropTriggerProbabilityIncrement) {
        GAME_CONFIGURATION.lootDropTriggerProbabilityIncrement = lootDropTriggerProbabilityIncrement;
    }

    public double getBorderStartDiameter() {
        return GAME_CONFIGURATION.border.borderStartDiameter;
    }

    public void setBorderStartDiameter(double borderStartDiameter) {
        GAME_CONFIGURATION.border.borderStartDiameter = borderStartDiameter;
    }

    public double getBorderDPS() {
        return GAME_CONFIGURATION.border.damagePerSecond;
    }

    public void setBorderDPS(double borderDps) {
        GAME_CONFIGURATION.border.damagePerSecond = borderDps;
    }

    public int getStartingDaytime() {
        return GAME_CONFIGURATION.startingDaytime;
    }

    public void setStartingDaytime(int startingDaytime) {
        GAME_CONFIGURATION.startingDaytime = startingDaytime;
    }

    public boolean getDoDaylightCycle() {
        return GAME_CONFIGURATION.daylightCycle;
    }

    public void setDoDaylightCycle(boolean doDaylightCycle) {
        GAME_CONFIGURATION.daylightCycle = doDaylightCycle;
    }

    public boolean getSpawnFireworkOnDeath() {
        return GAME_CONFIGURATION.spawnFireworkOnDeath;
    }

    public void setSpawnFireworkOnDeath(boolean spawnFireworkOnDeath) {
        GAME_CONFIGURATION.spawnFireworkOnDeath = spawnFireworkOnDeath;
    }

    public boolean getSpawnFireworkOnKill() {
        return GAME_CONFIGURATION.spawnFireworkOnKill;
    }

    public void setSpawnFireworkOnKill(boolean spawnFireworkOnKill) {
        GAME_CONFIGURATION.spawnFireworkOnKill = spawnFireworkOnKill;
    }

    public boolean getLightningOnDeath() {
        return GAME_CONFIGURATION.lightningOnDeath;
    }

    public void setLightningOnDeath(boolean lightningOnDeath) {
        GAME_CONFIGURATION.lightningOnDeath = lightningOnDeath;
    }

    public boolean getIsWIP() {
        return GAME_CONFIGURATION.isWIP;
    }

    public void setIsWIP(boolean isWIP) {
        GAME_CONFIGURATION.isWIP = isWIP;
    }

    public Collection<ChestConfiguration> getChests() {
        return GAME_CONFIGURATION.chestLocations;
    }

    public Optional<ChestConfiguration> getChestAt(Location location) {
        return getChests().stream().filter(co -> co.location.compareTo(location)).findFirst();
    }

    public List<ItemSet> getItemSets() {
        return Collections.unmodifiableList(GAME_CONFIGURATION.itemSets);
    }

    public boolean hasNoItemSets() {
        return GAME_CONFIGURATION.itemSets.isEmpty();
    }

    public ItemSet getDefaultItemSet() {
        return GAME_CONFIGURATION.itemSets.get(defaultSetIndex);
    }

    public void addItemSet(ItemSet itemSet, boolean makeDefault) {
        GAME_CONFIGURATION.itemSets.add(itemSet);

        if (makeDefault) {
            defaultSetIndex = GAME_CONFIGURATION.itemSets.size() - 1;
        }
    }

    public ItemSet getItemSetByName(String name) {
        return GAME_CONFIGURATION.itemSets.stream().filter(x -> x.name.equals(name)).findFirst().orElse(null);
    }

    public List<LootDropConfiguration> getLootDropLocations() {
        return GAME_CONFIGURATION.lootDropLocations;
    }

    public Map<String, RewardConfiguration> getRewards() {
        return GAME_CONFIGURATION.rewards;
    }

    public RewardConfiguration getReward(int placement) {
        return getRewards().get(String.valueOf(placement));
    }
}

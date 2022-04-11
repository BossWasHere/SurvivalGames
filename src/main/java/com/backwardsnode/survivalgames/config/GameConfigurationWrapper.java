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
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class GameConfigurationWrapper {

    public static final int SUPPORTED_CONFIG = 2;

    private final GameConfiguration gameConfiguration;

    private List<BlockLocation> spawnLocs;
    private int defaultSetIndex = 0;
    private File mainConfigFile;

    public GameConfigurationWrapper() {
        gameConfiguration = new GameConfiguration();
        gameConfiguration.configVersion = SUPPORTED_CONFIG;
        initialize();
    }

    public GameConfigurationWrapper(@NotNull GameConfiguration gameConfiguration) throws GameConfigurationException {
        if (gameConfiguration.configVersion != SUPPORTED_CONFIG) {
            throw new GameConfigurationException("Configuration is outdated: " + gameConfiguration.configVersion + " < " + SUPPORTED_CONFIG);
        }

        this.gameConfiguration = gameConfiguration;
        initialize();
    }

    public GameConfigurationWrapper(File configFile, boolean load) throws FileNotFoundException, GameConfigurationException, JsonIOException, JsonSyntaxException {
        if (load) {
            FileReader fileReader = new FileReader(configFile);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            gameConfiguration = gson.fromJson(new JsonReader(fileReader), GameConfiguration.class);

            try {
                fileReader.close();
            } catch (IOException ignored) {}

            if (gameConfiguration.configVersion != SUPPORTED_CONFIG) {
                throw new GameConfigurationException("Configuration is outdated: " + gameConfiguration.configVersion + " < " + SUPPORTED_CONFIG);
            }

        } else {
            gameConfiguration = new GameConfiguration();
            gameConfiguration.configVersion = SUPPORTED_CONFIG;
        }


        mainConfigFile = configFile;
        initialize();
    }

    private void initialize() {
        if (gameConfiguration.chestLocations == null) {
            gameConfiguration.chestLocations = new ArrayList<>();
        }
        if (gameConfiguration.itemSets == null) {
            gameConfiguration.itemSets = new ArrayList<>();
        }
        if (gameConfiguration.spawnLocations == null) {
            gameConfiguration.spawnLocations = new ArrayList<>();
        }
        if (gameConfiguration.rewards == null) {
            gameConfiguration.rewards = new HashMap<>();
        }
        if (gameConfiguration.lootDropLocations == null) {
            gameConfiguration.lootDropLocations = new ArrayList<>();
        }

        spawnLocs = new ArrayList<>();
        for (String strSpawn : gameConfiguration.spawnLocations) {
            spawnLocs.add(new BlockLocation(strSpawn));
        }

        for (int i = 0; i < gameConfiguration.itemSets.size(); i++) {
            if (gameConfiguration.itemSets.get(i).isDefault) {
                defaultSetIndex = i;
                break;
            }
        }
    }

    public Set<BlockLocation> checkChests() {
        Set<BlockLocation> invalidLocations = new HashSet<>();
        for (ChestConfiguration co : gameConfiguration.chestLocations) {
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
        if (gameConfiguration.border.deathmatchLocations == null) {
            return null;
        }
        int opts = gameConfiguration.border.deathmatchLocations.size();
        if (opts == 0) {
            return null;
        }
        return gameConfiguration.border.deathmatchLocations.get(new Random().nextInt(opts));
    }

    public List<DeathmatchConfiguration> getDeathmatchConfigs() {
        return gameConfiguration.border.deathmatchLocations;
    }

    public boolean save() {
        return mainConfigFile != null && saveCopyTo(mainConfigFile);
    }

    public boolean saveCopyTo(@NotNull File file) {
        try {
            GsonBuilder builder = new GsonBuilder().serializeNulls().setPrettyPrinting();
            Gson gson = builder.create();

            if (gameConfiguration.spawnLocations == null) {
                gameConfiguration.spawnLocations = new ArrayList<>();
            } else {
                gameConfiguration.spawnLocations.clear();
            }

            for (BlockLocation spawnLocation : spawnLocs) {
                gameConfiguration.spawnLocations.add(spawnLocation.toString());
            }

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(gameConfiguration, writer);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasMapName() {
        return gameConfiguration.mapName != null;
    }

    public String getMapName() {
        return gameConfiguration.mapName == null ? "unknown" : gameConfiguration.mapName;
    }

    public void setMapName(String mapName) {
        gameConfiguration.mapName = mapName;
    }

    public List<BlockLocation> getSpawnLocations() {
        return spawnLocs;
    }

    public boolean isSpawnLocation(BlockLocation location) {
        //return spawnLocs.stream().anyMatch(l -> l.equals(location));
        return spawnLocs.contains(location);
    }

    public boolean removeSpawnLocation(BlockLocation location) {
        return spawnLocs.removeIf(l -> l.equals(location));
    }

    public void addSpawnLocation(BlockLocation location) {
        spawnLocs.add(location);
    }

    public boolean getDoChestPrefilling() {
        return gameConfiguration.preFillChests;
    }

    public void setDoChestPrefilling(boolean doChestPrefilling) {
        gameConfiguration.preFillChests = doChestPrefilling;
    }

    public float getEntryFee() {
        return gameConfiguration.entryFee;
    }

    public void setEntryFee(float entryFee) {
        gameConfiguration.entryFee = entryFee;
    }

    public int getWaitPeriod() {
        return gameConfiguration.waitPeriod;
    }

    public void setWaitPeriod(int waitPeriod) {
        gameConfiguration.waitPeriod = waitPeriod;
    }

    public int getGracePeriod() {
        return gameConfiguration.gracePeriod;
    }

    public void setGracePeriod(int gracePeriod) {
        gameConfiguration.gracePeriod = gracePeriod;
    }

    public int getPreShrinkPeriod() {
        return gameConfiguration.preShrinkPeriod;
    }

    public void setPreShrinkPeriod(int preShrinkPeriod) {
        gameConfiguration.preShrinkPeriod = preShrinkPeriod;
    }

    public int getLootDropDelay() {
        return gameConfiguration.lootDropDelay;
    }

    public void setLootDropDelay(int lootDropDelay) {
        gameConfiguration.lootDropDelay = Math.max(-1, lootDropDelay);
    }

    public int getLootDropTriggerWindow() {
        return Math.max(0, gameConfiguration.lootDropTriggerWithin);
    }

    public void setLootDropTriggerWindow(int lootDropTriggerWindow) {
        gameConfiguration.lootDropTriggerWithin = Math.max(0, lootDropTriggerWindow);
    }

    public float getLootDropTriggerProbability() {
        return gameConfiguration.lootDropTriggerProbability;
    }

    public void setLootDropTriggerProbability(float lootDropTriggerProbability) {
        gameConfiguration.lootDropTriggerProbability = lootDropTriggerProbability;
    }

    public float getLootDropTriggerProbabilityIncrement() {
        return gameConfiguration.lootDropTriggerProbabilityIncrement;
    }

    public void setLootDropTriggerProbabilityIncrement(float lootDropTriggerProbabilityIncrement) {
        gameConfiguration.lootDropTriggerProbabilityIncrement = lootDropTriggerProbabilityIncrement;
    }

    public double getBorderStartDiameter() {
        return gameConfiguration.border.borderStartDiameter;
    }

    public void setBorderStartDiameter(double borderStartDiameter) {
        gameConfiguration.border.borderStartDiameter = borderStartDiameter;
    }

    public double getBorderDPS() {
        return gameConfiguration.border.damagePerSecond;
    }

    public void setBorderDPS(double borderDps) {
        gameConfiguration.border.damagePerSecond = borderDps;
    }

    public int getStartingDaytime() {
        return gameConfiguration.startingDaytime;
    }

    public void setStartingDaytime(int startingDaytime) {
        gameConfiguration.startingDaytime = startingDaytime;
    }

    public boolean getDoDaylightCycle() {
        return gameConfiguration.daylightCycle;
    }

    public void setDoDaylightCycle(boolean doDaylightCycle) {
        gameConfiguration.daylightCycle = doDaylightCycle;
    }

    public boolean getSpawnFireworkOnDeath() {
        return gameConfiguration.spawnFireworkOnDeath;
    }

    public void setSpawnFireworkOnDeath(boolean spawnFireworkOnDeath) {
        gameConfiguration.spawnFireworkOnDeath = spawnFireworkOnDeath;
    }

    public boolean getSpawnFireworkOnKill() {
        return gameConfiguration.spawnFireworkOnKill;
    }

    public void setSpawnFireworkOnKill(boolean spawnFireworkOnKill) {
        gameConfiguration.spawnFireworkOnKill = spawnFireworkOnKill;
    }

    public boolean getLightningOnDeath() {
        return gameConfiguration.lightningOnDeath;
    }

    public void setLightningOnDeath(boolean lightningOnDeath) {
        gameConfiguration.lightningOnDeath = lightningOnDeath;
    }

    public boolean getIsWIP() {
        return gameConfiguration.isWIP;
    }

    public void setIsWIP(boolean isWIP) {
        gameConfiguration.isWIP = isWIP;
    }

    public Collection<ChestConfiguration> getChests() {
        return gameConfiguration.chestLocations;
    }

    public Optional<ChestConfiguration> getChestAt(BlockLocation location) {
        return gameConfiguration.chestLocations.stream().filter(co -> co.location.equals(location)).findFirst();
    }

    public void addItemChest(BlockLocation location, boolean addDefaultItemSet) {
        ChestConfiguration co = new ChestConfiguration();
        co.location = location;
        co.itemSets = new ArrayList<>();
        if (addDefaultItemSet) {
            co.itemSets.add(getDefaultItemSet().name);
        }

        gameConfiguration.chestLocations.add(co);
    }

    public boolean removeChestAt(BlockLocation location) {
        return gameConfiguration.chestLocations.removeIf(co -> co.location.equals(location));
    }

    public List<String> getChestItemSetNames(BlockLocation location) {
        return getChestAt(location).map(co -> co.itemSets).orElse(null);
    }

    public Collection<ItemSet> getChestItemSets(BlockLocation location) {
        List<String> itemSetNames = getChestItemSetNames(location);
        if (itemSetNames == null) {
            return null;
        }

        return getItemSets().stream().filter(itemSet -> itemSetNames.contains(itemSet.name)).collect(Collectors.toSet());
    }

    public boolean addChestItemSet(BlockLocation location, String itemSet) {
        Optional<ChestConfiguration> co = getChestAt(location);
        if (co.isPresent()) {
            co.get().itemSets.add(itemSet);
            return true;
        }
        return false;
    }

    public boolean removeChestItemSet(BlockLocation location, String itemSet) {
        Optional<ChestConfiguration> co = getChestAt(location);
        if (co.isPresent()) {
            co.get().itemSets.remove(itemSet);
            return true;
        }
        return false;
    }

    public List<ItemSet> getItemSets() {
        return Collections.unmodifiableList(gameConfiguration.itemSets);
    }

    public boolean hasNoItemSets() {
        return gameConfiguration.itemSets.isEmpty();
    }

    public ItemSet getDefaultItemSet() {
        return gameConfiguration.itemSets.get(defaultSetIndex);
    }

    public boolean addItemSet(ItemSet itemSet, boolean makeDefault) {
        if (itemSet.name == null || getItemSetByName(itemSet.name) != null) {
            return false;
        }

        gameConfiguration.itemSets.add(itemSet);

        if (makeDefault) {
            defaultSetIndex = gameConfiguration.itemSets.size() - 1;
        }
        return true;
    }

    public boolean renameItemSet(String from, String to) {
        ItemSet src = getItemSetByName(from);
        ItemSet dst = getItemSetByName(to);
        if (src == null || dst != null) {
            return false;
        }
        src.name = to;
        for (ChestConfiguration co : gameConfiguration.chestLocations) {
            if (co.itemSets.remove(from)) {
                co.itemSets.add(to);
            }
        }
        for (LootDropConfiguration ldc : gameConfiguration.lootDropLocations) {
            if (ldc.itemSets.remove(from)) {
                ldc.itemSets.add(to);
            }
        }

        return true;
    }

    public ItemSet getItemSetByName(String name) {
        return gameConfiguration.itemSets.stream().filter(x -> x.name.equals(name)).findFirst().orElse(null);
    }

    public List<LootDropConfiguration> getLootDropLocations() {
        return gameConfiguration.lootDropLocations;
    }

    public Map<String, RewardConfiguration> getRewards() {
        return gameConfiguration.rewards;
    }

    public RewardConfiguration getReward(int placement) {
        return getRewards().get(String.valueOf(placement));
    }
}

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
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.config.ChestConfiguration;
import com.backwardsnode.survivalgames.item.EnchantmentModel;
import com.backwardsnode.survivalgames.item.ItemModel;
import com.backwardsnode.survivalgames.item.ItemSet;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Version1Model {

    public String mapName;
    @SerializedName("spawnLocations")
    public List<String> strSpawns;
    public BorderConfiguration border;

    public int waitTime;
    public int gracePeriod;
    public int borderCollapseDelay;
    public boolean preFillChests;
    public boolean spawnFireworkOnKill;
    public boolean spawnFireworkOnDeath;
    public boolean isWIP;

    public List<ChestConfiguration> chestLocations;
    public List<Version1ItemSet> itemSets;

    public static class Version1ItemSet {
        @SerializedName("default")
        public boolean isDefault;
        public String name;
        public List<Version1ItemModel> items;

        public ItemSet migrate() {
            ItemSet updatedSet = new ItemSet();

            updatedSet.name = name;
            updatedSet.isDefault = isDefault;
            updatedSet.items = new ArrayList<>(items.size());

            for (Version1ItemModel im : items) {
                updatedSet.items.add(im.migrate());
            }

            return updatedSet;
        }
    }

    public static class Version1ItemModel {
        public String id;
        public int max;
        public String name;
        public List<String> lore;
        public List<EnchantmentModel> enchantments;

        public ItemModel migrate() {
            ItemModel updatedModel = new ItemModel();

            updatedModel.id = id;
            updatedModel.count = max;
            updatedModel.name = name;
            updatedModel.lore = lore;
            updatedModel.enchantments = enchantments;

            return updatedModel;
        }
    }

    public GameConfiguration migrate() {
        GameConfiguration gc = new GameConfiguration();
        gc.configVersion = GameConfiguration.SUPPORTED_CONFIG;
        gc.mapName = mapName;
        gc.isWIP = isWIP;
        gc.entryFee = 0;
        gc.rewards = new HashMap<>();
        gc.strSpawns = strSpawns;
        gc.border = border;
        gc.waitTime = waitTime;
        gc.gracePeriod = gracePeriod;
        gc.preFillChests = preFillChests;
        gc.borderCollapseDelay = borderCollapseDelay;
        gc.spawnFireworkOnDeath = spawnFireworkOnDeath;
        gc.spawnFireworkOnKill = spawnFireworkOnKill;
        gc.lightningOnDeath = spawnFireworkOnDeath || spawnFireworkOnKill;
        gc.chestLocations = chestLocations;

        gc.itemSets = new ArrayList<>(itemSets.size());
        for (Version1ItemSet is : itemSets) {
            gc.itemSets.add(is.migrate());
        }

        return gc;
    }
}

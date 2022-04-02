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

package com.backwardsnode.survivalgames.world;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.config.LootDropConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.*;

public class LootDropManager {

    private final Plugin PLUGIN;

    private final Map<String, LootDrop> DROPS;
    private final Map<UUID, LootDrop> FALLING_BLOCKS;

    public LootDropManager(Plugin plugin) {
        PLUGIN = plugin;
        DROPS = new HashMap<>();
        FALLING_BLOCKS = new HashMap<>();
    }

    public LootDrop summonLootDrop(LootDropConfiguration configuration, boolean placeBeacon) {
        LootDrop lootDrop = new LootDrop(this, configuration);
        String locationString = Utils.stringFromLocation(configuration.location, false, true);

        LootDrop existing = DROPS.remove(locationString);
        if (existing != null) {
            existing.close();
        }

        if (placeBeacon) {
            lootDrop.placeBeacon();
        }

        lootDrop.summonDropEntity();

        DROPS.put(locationString, lootDrop);

        return lootDrop;
    }

    public LootDrop getDropAtLocation(Location location) {
        return DROPS.get(Utils.stringFromLocation(location, false, true));
    }

    public LootDrop getAndRemoveAssociatedDrop(UUID entityId) {
        return FALLING_BLOCKS.remove(entityId);
    }

    protected void rememberFallingEntity(UUID entityId, LootDrop lootDrop) {
        FALLING_BLOCKS.put(entityId, lootDrop);
    }

    public void clearAll() {
        for (UUID key : FALLING_BLOCKS.keySet()) {
            Entity e = Bukkit.getEntity(key);
            if (e != null) {
                e.remove();
            }
        }
        for (LootDrop drop : DROPS.values()) {
            drop.close();
        }
        FALLING_BLOCKS.clear();
        DROPS.clear();
    }
}

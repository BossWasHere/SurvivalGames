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

import com.backwardsnode.survivalgames.config.LootDropConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LootDropManager {

    private final Map<BlockLocation, LootDrop> drops;
    private final Map<UUID, LootDrop> fallingBlocks;

    public LootDropManager() {
        drops = new HashMap<>();
        fallingBlocks = new HashMap<>();
    }

    public LootDrop summonLootDrop(LootDropConfiguration configuration, boolean placeBeacon) {
        LootDrop lootDrop = new LootDrop(this, configuration);

        LootDrop existing = drops.remove(configuration.location);
        if (existing != null) {
            existing.close();
        }

        if (placeBeacon) {
            lootDrop.placeBeacon();
        }

        lootDrop.summonDropEntity();

        drops.put(configuration.location, lootDrop);

        return lootDrop;
    }

    public LootDrop getDropAtLocation(BlockLocation location) {
        return drops.get(location);
    }

    public LootDrop getAndRemoveAssociatedDrop(UUID entityId) {
        return fallingBlocks.remove(entityId);
    }

    protected void rememberFallingEntity(UUID entityId, LootDrop lootDrop) {
        fallingBlocks.put(entityId, lootDrop);
    }

    public void clearAll() {
        for (UUID key : fallingBlocks.keySet()) {
            Entity e = Bukkit.getEntity(key);
            if (e != null) {
                e.remove();
            }
        }
        for (LootDrop drop : drops.values()) {
            drop.close();
        }
        fallingBlocks.clear();
        drops.clear();
    }
}

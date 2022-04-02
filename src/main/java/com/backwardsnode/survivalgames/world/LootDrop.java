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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import java.util.UUID;

public class LootDrop {

    public static final int SPAWN_HEIGHT = 120;

    private final LootDropManager MANAGER;
    private final LootDropConfiguration LOOT_DROP_CONFIGURATION;

    private BlockState[] beaconReplacementStates;
    private boolean chestPlaced = false;
    private boolean closed = false;

    protected LootDrop(LootDropManager manager, LootDropConfiguration lootDropConfiguration) {
        MANAGER = manager;
        LOOT_DROP_CONFIGURATION = lootDropConfiguration;
    }

    public void summonDropEntity() {
        if (closed) {
            return;
        }

        Location location = LOOT_DROP_CONFIGURATION.location;

        World world = location.getWorld();

        Location top = world.getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation().add(0.5, SPAWN_HEIGHT, 0.5);

        Entity entity = world.spawnEntity(top, EntityType.FIREBALL);
        if (entity instanceof Fireball fireball) {
            fireball.setIsIncendiary(false);
            fireball.setYield(0);
            fireball.setDirection(new Vector(0, -0.03, 0));

            FallingBlock fallingBlock = world.spawnFallingBlock(top, Material.OBSIDIAN.createBlockData());

            fireball.addPassenger(fallingBlock);

            UUID fallingBlockUUID = fallingBlock.getUniqueId();
            MANAGER.rememberFallingEntity(fallingBlockUUID, this);
        } else {
            Bukkit.getLogger().warning("Tried to spawn fireball but ended up with " + entity.getType() + " instead.");
        }
    }

    public void placeChest() {
        if (closed || LOOT_DROP_CONFIGURATION == null || chestPlaced) {
            return;
        }

        LOOT_DROP_CONFIGURATION.location.getBlock().setType(Material.ENDER_CHEST);
        chestPlaced = true;
    }

    public void removeChest() {
        if (chestPlaced) {
            LOOT_DROP_CONFIGURATION.location.getBlock().setType(Material.AIR);

            chestPlaced = false;
        }
    }

    public boolean placeBeacon() {
        if (closed || beaconReplacementStates != null) {
            return false;
        }

        Location location = LOOT_DROP_CONFIGURATION.location.clone().add(0, -1, 0);
        World world = location.getWorld();

        int blockY = location.getBlockY();

        // not enough space to place it
        if (world.getMinHeight() >= blockY || blockY >= world.getMaxHeight()) {
            return false;
        }

        beaconReplacementStates = new BlockState[10];
        Block beaconTop = location.getBlock();
        beaconReplacementStates[0] = beaconTop.getState();

        Location cloned = location.clone();
        Block nextBlock = cloned.add(-1, -1, -1).getBlock();
        beaconReplacementStates[1] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(1, 0, 0).getBlock();
        beaconReplacementStates[2] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(1, 0, 0).getBlock();
        beaconReplacementStates[3] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(-2, 0, 1).getBlock();
        beaconReplacementStates[4] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(1, 0, 0).getBlock();
        beaconReplacementStates[5] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(1, 0, 0).getBlock();
        beaconReplacementStates[6] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(-2, 0, 1).getBlock();
        beaconReplacementStates[7] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(1, 0, 0).getBlock();
        beaconReplacementStates[8] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);
        nextBlock = cloned.add(1, 0, 0).getBlock();
        beaconReplacementStates[9] = nextBlock.getState();
        nextBlock.setType(Material.DIAMOND_BLOCK);

        beaconTop.setType(Material.BEACON);

        return true;
    }

    public boolean isBeaconPlaced() {
        return beaconReplacementStates != null;
    }

    public void removeBeacon() {
        if (beaconReplacementStates != null) {
            for (BlockState state : beaconReplacementStates) {
                state.update(true, false);
            }

            beaconReplacementStates = null;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        removeChest();
        removeBeacon();

        closed = true;
    }

}

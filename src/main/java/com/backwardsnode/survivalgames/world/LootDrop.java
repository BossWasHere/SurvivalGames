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

import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.config.LootDropConfiguration;
import com.backwardsnode.survivalgames.item.ItemSet;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.UUID;

public class LootDrop {

    public static final int SPAWN_HEIGHT = 120;

    private final LootDropManager manager;
    private final LootDropConfiguration lootDropConfiguration;

    private BlockState[] beaconReplacementStates;
    private boolean chestPlaced = false;
    private boolean closed = false;

    protected LootDrop(LootDropManager manager, LootDropConfiguration lootDropConfiguration) {
        this.manager = manager;
        this.lootDropConfiguration = lootDropConfiguration;
    }

    public void summonDropEntity() {
        if (closed) {
            return;
        }

        Location location = lootDropConfiguration.location.toBukkitLocation();

        World world = location.getWorld();
        assert world != null;

        Location top = world.getHighestBlockAt(location.getBlockX(), location.getBlockZ()).getLocation().add(0.5, SPAWN_HEIGHT, 0.5);

        Entity entity = world.spawnEntity(top, EntityType.FIREBALL);
        if (entity instanceof Fireball fireball) {
            fireball.setIsIncendiary(false);
            fireball.setYield(0);
            fireball.setDirection(new Vector(0, -0.03, 0));

            FallingBlock fallingBlock = world.spawnFallingBlock(top, Material.OBSIDIAN.createBlockData());

            fireball.addPassenger(fallingBlock);

            UUID fallingBlockUUID = fallingBlock.getUniqueId();
            manager.rememberFallingEntity(fallingBlockUUID, this);
        } else {
            Bukkit.getLogger().warning("Tried to spawn fireball but ended up with " + entity.getType() + " instead.");
        }
    }

    public void placeChest(boolean playEffects) {
        if (closed || lootDropConfiguration == null || chestPlaced) {
            return;
        }

        Location location = lootDropConfiguration.location.toBukkitLocation();
        if (playEffects) {
            World world = location.getWorld();
            assert world != null;
            world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.1f, 1f);
            world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_FALL, SoundCategory.PLAYERS, 1f, 1f);
            world.spawnParticle(Particle.DRAGON_BREATH, location, 50, 2d, 0, 2d, 0.03d);
        }

        lootDropConfiguration.location.getBlock().setType(Material.ENDER_CHEST);
        chestPlaced = true;
    }

    public void removeChest() {
        if (chestPlaced) {
            lootDropConfiguration.location.getBlock().setType(Material.AIR);

            chestPlaced = false;
        }
    }

    public boolean placeBeacon() {
        if (closed || beaconReplacementStates != null) {
            return false;
        }

        Location location = lootDropConfiguration.location.copyAndAdd(0, -1, 0).toBukkitLocation();
        World world = location.getWorld();

        int blockY = location.getBlockY();

        // not enough space to place it
        assert world != null;
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

    public void popAndClose(Collection<ItemSet> itemSets) {
        if (closed) return;
        Location location = lootDropConfiguration.location.toBukkitLocationCentered();
        World world = location.getWorld();

        assert world != null;
        world.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.6f, 1f);
        world.playSound(location, Sound.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 1f, 1f);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5f, 1f);
        world.spawnParticle(Particle.EXPLOSION_NORMAL, location, 1);
        Utils.dropSomeItems(location, itemSets, lootDropConfiguration.itemSets, lootDropConfiguration.itemCount);

        close();
    }
}

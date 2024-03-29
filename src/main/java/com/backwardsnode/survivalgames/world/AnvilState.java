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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import java.util.HashSet;
import java.util.UUID;

public class AnvilState {

    private final HashSet<UUID> viewers;
    private final Material originalType;
    private final BlockFace rotation;

    public AnvilState(Material originalType, BlockFace rotation) {
        viewers = new HashSet<>();
        this.originalType = originalType;
        this.rotation = rotation;
    }

    public boolean addViewer(UUID uuid) {
        return viewers.add(uuid);
    }

    public boolean removeViewer(UUID uuid) {
        return viewers.remove(uuid);
    }

    public boolean isViewer(UUID uuid) {
        return viewers.contains(uuid);
    }

    public boolean hasNoViewers() {
        return viewers.isEmpty();
    }

    public void replaceBlock(Location location) {
        Block block = location.getBlock();
        block.setType(originalType);
        BlockData data = block.getBlockData();

        if (data instanceof Directional rotatable) {
            rotatable.setFacing(rotation);
            block.setBlockData(rotatable);
        }
    }
}

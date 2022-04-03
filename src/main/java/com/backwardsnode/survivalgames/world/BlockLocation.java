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

import com.backwardsnode.survivalgames.config.Copyable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public final class BlockLocation implements Copyable<BlockLocation> {

    private UUID worldUUID;
    private int x, y, z;

    private boolean serializeY;

    private BlockLocation(UUID worldUUID, int x, int y, int z, boolean serializeY) {
        this.worldUUID = worldUUID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.serializeY = serializeY;
    }

    public BlockLocation(@Nullable World world, int x, int y, int z) {
        this(world == null ? null : world.getUID(), x, y, z, true);
    }

    public BlockLocation(@NotNull Location location) {
        World world = location.getWorld();
        worldUUID = world == null ? null : world.getUID();
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        serializeY = true;
    }

    public BlockLocation(@NotNull String serialized) throws IllegalArgumentException {
        String[] spl = serialized.split(",");
        int args = spl.length;

        String name;

        try {
            if (args == 3) {
                x = Integer.parseInt(spl[0]);
                z = Integer.parseInt(spl[1]);
                name = spl[2];
                serializeY = false;
            } else if (args == 4) {
                x = Integer.parseInt(spl[0]);
                y = Integer.parseInt(spl[1]);
                z = Integer.parseInt(spl[2]);
                name = spl[3];
                serializeY = true;
            } else {
                throw new IllegalArgumentException("Location string has " + args + " components, should have 3-4");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Location has invalid coordintes", e);
        }

        if (name.length() > 0) {
            World world = Bukkit.getWorld(name);
            worldUUID = world == null ? null : world.getUID();
        }
    }

    public @Nullable World getWorld() {
        return worldUUID == null ? null : Bukkit.getWorld(worldUUID);
    }

    public void setWorld(@Nullable World world) {
        worldUUID = world == null ? null : world.getUID();
    }

    public int getX() {
        return x;
    }

    public BlockLocation setX(int x) {
        this.x = x;

        return this;
    }

    public int getY() {
        return y;
    }

    public BlockLocation setY(int y) {
        this.y = y;

        return this;
    }

    public int getZ() {
        return z;
    }

    public BlockLocation setZ(int z) {
        this.z = z;

        return this;
    }

    public boolean getRememberYValue() {
        return serializeY;
    }

    public void setRememberYValue(boolean rememberYValue) {
        serializeY = rememberYValue;
    }

    public BlockLocation add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;

        return this;
    }

    public BlockLocation copyAndAdd(int x, int y, int z) {
        return deepCopy().add(x, y, z);
    }

    public @NotNull Location toBukkitLocation() {
        return new Location(getWorld(), x, y, z);
    }

    public @NotNull Location toBukkitLocationCentered() {
        return new Location(getWorld(), x + 0.5d, y + 0.5d, z + 0.5d);
    }

    public Block getBlock() {
        return toBukkitLocation().getBlock();
    }

    @Override
    public String toString() {
        World world = getWorld();

        String output = x + ",";
        if (serializeY) output += y + ",";
        output += z + "," + (world == null ? "" : world.getName());

        return output;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof BlockLocation that) {
            return x == that.x && y == that.y && z == that.z && serializeY == that.serializeY && Objects.equals(worldUUID, that.worldUUID);
        }
        return false;
    }

    public boolean compareTo(Location location) {
        if (location == null) return false;
        World thatWorld = location.getWorld();
        UUID thatWorldUUID = thatWorld == null ? null : thatWorld.getUID();
        return x == location.getBlockX() && y == location.getBlockY() && z == location.getBlockZ() && Objects.equals(worldUUID, thatWorldUUID);
    }

    public boolean compareTo(String serialized) {
        return this.equals(new BlockLocation(serialized));
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldUUID, x, y, z);
    }

    @Override
    public BlockLocation deepCopy() {
        return new BlockLocation(worldUUID, x, y, z, serializeY);
    }
}

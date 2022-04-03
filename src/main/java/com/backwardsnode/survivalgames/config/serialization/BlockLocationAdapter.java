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

package com.backwardsnode.survivalgames.config.serialization;

import com.backwardsnode.survivalgames.world.BlockLocation;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.logging.Level;

public class BlockLocationAdapter extends TypeAdapter<BlockLocation> {

    @Override
    public void write(JsonWriter jsonWriter, BlockLocation location) throws IOException {
        if (location != null) {
            jsonWriter.value(location.toString());
        } else {
            jsonWriter.jsonValue(null);
        }

    }

    @Override
    public BlockLocation read(JsonReader jsonReader) throws IOException {
        String locationAsString = jsonReader.nextString();

        try {
            return new BlockLocation(locationAsString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.WARNING, "Invalid location string " + locationAsString, e);
        }

        return null;
    }

}

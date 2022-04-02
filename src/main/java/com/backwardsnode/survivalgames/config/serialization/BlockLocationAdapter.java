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

import com.backwardsnode.survivalgames.Utils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class BlockLocationAdapter extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter jsonWriter, Location location) throws IOException {
        if (location != null) {
            jsonWriter.value(Utils.stringFromLocation(location, false, true));
        } else {
            jsonWriter.jsonValue(null);
        }


    }

    @Override
    public Location read(JsonReader jsonReader) throws IOException {
        String locationAsString = jsonReader.nextString();

        Location location = Utils.locationFromString(locationAsString, false);

        if (location == null) {
            Bukkit.getLogger().warning("Invalid location string " + locationAsString);
        }

        return location;
    }

}

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

import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.config.serialization.BlockLocationAdapter;
import com.backwardsnode.survivalgames.config.serialization.SerializableLocation;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;

import java.util.List;

public class LootDropConfiguration implements SerializableLocation {

    @JsonAdapter(BlockLocationAdapter.class)
    @SerializedName("loc")
    public Location location;
    public List<String> itemSets;
    public boolean canDropAutomatically;
    public boolean canDropOnDemand;

    @Override
    public String getLocationAsString() {
        return Utils.stringFromLocation(location, false, true);
    }
}

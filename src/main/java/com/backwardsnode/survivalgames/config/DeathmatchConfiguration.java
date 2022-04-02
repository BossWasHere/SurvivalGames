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
import com.backwardsnode.survivalgames.config.serialization.ChunkLocationAdapter;
import com.backwardsnode.survivalgames.config.serialization.SerializableLocation;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;

public class DeathmatchConfiguration implements Copyable<DeathmatchConfiguration>, SerializableLocation {

	@JsonAdapter(ChunkLocationAdapter.class)
	@SerializedName("loc")
	public Location location;
	public double borderDiameter;
	public long shrinkTime;
	public int deathmatchDuration;
	public int collapseTime;

	@Override
	public String getLocationAsString() {
		return Utils.stringFromLocation(location, true, true);
	}

	public double getCenterX() {
		return location.getX();
	}

	public double getCenterZ() {
		return location.getZ();
	}

	@Override
	public DeathmatchConfiguration deepCopy() {
		DeathmatchConfiguration deathmatchConfiguration = new DeathmatchConfiguration();

		deathmatchConfiguration.location = location == null ? null : location.clone();
		deathmatchConfiguration.borderDiameter = borderDiameter;
		deathmatchConfiguration.shrinkTime = shrinkTime;
		deathmatchConfiguration.deathmatchDuration = deathmatchDuration;
		deathmatchConfiguration.collapseTime = collapseTime;

		return deathmatchConfiguration;
	}
}

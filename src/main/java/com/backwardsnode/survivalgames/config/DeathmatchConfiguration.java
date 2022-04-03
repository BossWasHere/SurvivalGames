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

import com.backwardsnode.survivalgames.config.serialization.BlockLocationAdapter;
import com.backwardsnode.survivalgames.world.BlockLocation;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

public class DeathmatchConfiguration implements Copyable<DeathmatchConfiguration> {

	@JsonAdapter(BlockLocationAdapter.class)
	@SerializedName("loc")
	public BlockLocation location;
	public double borderDiameter;
	public long shrinkTime;
	public int deathmatchDuration;
	public int collapseTime;

	public double getCenterX() {
		return location.getX();
	}

	public double getCenterZ() {
		return location.getZ();
	}

	@Override
	public DeathmatchConfiguration deepCopy() {
		DeathmatchConfiguration deathmatchConfiguration = new DeathmatchConfiguration();

		deathmatchConfiguration.location = location == null ? null : location.deepCopy();
		deathmatchConfiguration.borderDiameter = borderDiameter;
		deathmatchConfiguration.shrinkTime = shrinkTime;
		deathmatchConfiguration.deathmatchDuration = deathmatchDuration;
		deathmatchConfiguration.collapseTime = collapseTime;

		return deathmatchConfiguration;
	}
}

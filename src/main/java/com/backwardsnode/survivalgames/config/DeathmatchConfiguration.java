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
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class DeathmatchConfiguration implements IConfigurable {

	public String loc;
	public double borderRadius;
	public long shrinkTime;
	public int deathmatchDuration;
	public int collapseTime;
	
	public transient double centerX, centerZ;

	@Override
	public void configure() {
		Location location = Utils.locationFromString(loc, true);
		if (location == null) {
			// TODO bring inside plugin logger
			Bukkit.getLogger().warning("Invalid deathmatch location configuration @ " + loc);
			return;
		}
		centerX = location.getX();
		centerZ = location.getZ();
	}
}

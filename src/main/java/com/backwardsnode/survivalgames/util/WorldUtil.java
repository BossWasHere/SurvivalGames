/*
 *  BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 *  Copyright (C) 2019 BackwardsNode/BossWasHere
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class WorldUtil {

	public static String stringFromLocation(Location location, boolean toBlock) {
		if (toBlock) {
			return String.format("%d,%d,%d,%s", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
		}
		return String.format("%e,%e,%e,%s", location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
	}

	public static Location locationFromString(String location) {
		double x,y,z;
		String name;
		String[] spl = location.split(",");
		try {
			x = Double.parseDouble(spl[0]);
			y = Double.parseDouble(spl[1]);
			z = Double.parseDouble(spl[2]);
			name = spl[3];
		} catch (NumberFormatException e) {
			return null;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		Location l = new Location(Bukkit.getWorld(name), x, y, z);
		return l;
	}
	
	public static Location locationFromString(String location, boolean ignoreY) {
		if (ignoreY) {
			double x,z;
			String name;
			String[] spl = location.split(",");
			try {
				x = Double.parseDouble(spl[0]);
				z = Double.parseDouble(spl[1]);
				name = spl[2];
			} catch (NumberFormatException e) {
				return null;
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
			Location l = new Location(Bukkit.getWorld(name), x, 1, z);
			return l;
		}
		return locationFromString(location);
	}
	
	public static boolean isSameLocation(Location primary, Location reference) {
		if (primary.getBlockX() == reference.getBlockX()) {
			if (primary.getBlockY() == reference.getBlockY()) {
				if (primary.getBlockZ() == reference.getBlockZ()) {
					if (primary.getWorld().getName().equals(reference.getWorld().getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static void spawnRandomFirework(Location l) {
		Firework f = (Firework) l.getWorld().spawnEntity(l, EntityType.FIREWORK);
		FireworkMeta fm = f.getFireworkMeta();
		
		Random r = new Random();
		
		FireworkEffect fe = FireworkEffect.builder().flicker(r.nextBoolean()).trail(r.nextBoolean()).withColor(Color.fromRGB(r.nextInt(16777216))).withFade(Color.fromRGB(r.nextInt(16777216))).with(EnumUtil.getRandomConstant(Type.class)).build();
		fm.addEffect(fe);
		f.setFireworkMeta(fm);
		
	}
}

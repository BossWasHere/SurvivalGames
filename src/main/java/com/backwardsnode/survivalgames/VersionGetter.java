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
package com.backwardsnode.survivalgames;

import org.bukkit.Bukkit;

public class VersionGetter {

	private static VersionGetter current;
	
	private String packageHeader;
	private String version;
	
	public static void init() {
		current = new VersionGetter();
		current.packageHeader = Bukkit.getServer().getClass().getPackage().getName();
		current.version = current.packageHeader.substring(current.packageHeader.lastIndexOf(".") + 1);
	}
	
	public static String getBukkitPackageHeader() {
		return current.packageHeader;
	}
	
	public static String getVersion() {
		return current.version;
	}
	
	public static String getPackageHeader() {
		return "net.minecraft.server." + current.version;
	}
}

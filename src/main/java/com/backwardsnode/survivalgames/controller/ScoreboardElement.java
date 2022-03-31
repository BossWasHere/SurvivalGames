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

package com.backwardsnode.survivalgames.controller;

import org.bukkit.ChatColor;

public enum ScoreboardElement {
	
	STATUS(ChatColor.DARK_PURPLE + "[%]", "Waiting", 8),
	MAP_NAME(ChatColor.GREEN + "[Map] " + ChatColor.WHITE + "- %", "unknown", 7),
	PLAYERS_LEFT(ChatColor.GREEN + "[Players Left] " + ChatColor.WHITE + "- %", "unknown", 6),
	ZONE_SIZE(ChatColor.GREEN + "[Play Zone] " + ChatColor.WHITE + "- %", "no zone", 5),
	MOST_KILLS_HEADER(ChatColor.GREEN + "[Most Kills] " + ChatColor.WHITE + "-", 4),
	MOST_KILLS_1(ChatColor.GRAY + "  1. %", "No data", 3),
	MOST_KILLS_2(ChatColor.GRAY + "  2. %", "No data", 2),
	MOST_KILLS_3(ChatColor.GRAY + "  3. %", "No data", 1),
	TIME_LEFT(ChatColor.GREEN + "[Time Remaining] " + ChatColor.WHITE + "- %", "00:00", 0);
	
	private final String wrap, def;
	private final int pos;
	
	
	private ScoreboardElement(String def, int pos) {
		wrap = null;
		this.def = def;
		this.pos = pos;
	}
	
	private ScoreboardElement(String wrap, String def, int pos) {
		this.wrap = wrap;
		this.def = def;
		this.pos = pos;
	}

	public String getTag() {
		return getTag(def);
	}

	public String getTag(String placeholder) {
		return wrap == null ? placeholder : wrap.replace("%", placeholder);
	}

	public int getPos() {
		return pos;
	}
}

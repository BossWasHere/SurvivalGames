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
package com.backwardsnode.survivalgames.editor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

public enum EditorInventories {

	ITEM_SETS(ChatColor.DARK_AQUA + "[SGEdit] Item Sets"),
	ITEM_SET_MODIFY(ChatColor.DARK_AQUA + "[SGEdit] Item Set: "),
	CHEST_CHOOSE_ITEM_SETS(ChatColor.DARK_AQUA + "[SGEdit] Choose Item Sets"),
	SETTINGS(ChatColor.DARK_AQUA + "[SGEdit] Change Settings"),
	DEATHMATCH_SETTINGS(ChatColor.DARK_AQUA + "[SGEdit] Deathmatch Settings"),
	BORDER_SETTINGS(ChatColor.DARK_AQUA + "[SGEdit] Border Settings");
	
	public final String title;
	
	EditorInventories(String title) {
		this.title = title;
	}

	public Inventory getInventory() {
		Inventory inventory = Bukkit.createInventory(null, 9);
		return inventory;
	}
}

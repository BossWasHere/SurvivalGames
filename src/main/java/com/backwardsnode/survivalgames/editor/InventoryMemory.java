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
package com.backwardsnode.survivalgames.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.base.Preconditions;

public class InventoryMemory {

	private final Player p;
	private ItemStack s0, s1, s2, s3, s4, s5, s6, s7, s8;
	
	public InventoryMemory(Player p) {
		this.p = p;
		PlayerInventory inv = p.getInventory();
		s0 = inv.getItem(0);
		s1 = inv.getItem(1);
		s2 = inv.getItem(2);
		s3 = inv.getItem(3);
		s4 = inv.getItem(4);
		s5 = inv.getItem(5);
		s6 = inv.getItem(6);
		s7 = inv.getItem(7);
		s8 = inv.getItem(8);
	}
	
	public void replaceHotbar(ItemStack... items) {
		if (!p.isOnline()) {
			return;
		}
		Preconditions.checkNotNull(items);
		for (int i = 0; i < 9; i++) {
			if (items.length > i) {
				if (items[i] == null) {
					p.getInventory().clear(i);
				} else {
					p.getInventory().setItem(i, items[i]);
				}
			} else {
				p.getInventory().clear(i);
			}
		}
	}
	
	public void resetHotbar() {
		replaceHotbar(s0, s1, s2, s3, s4, s5, s6, s7, s8);
	}
}

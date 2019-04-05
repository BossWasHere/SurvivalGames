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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtil {

	public static ItemStack addNameAndLore(ItemStack item, String name, String... lore) {
		ItemMeta itemMeta = item.getItemMeta();
		if (name != null) {
			itemMeta.setDisplayName(name);
		}
		if (lore != null) {
			itemMeta.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public static boolean matchesMetaAndType(ItemStack item, ItemStack reference) {
		if (item == null) {
			return false;
		}
		checkNotNull(reference);
		if (item.getType() != reference.getType()) {
			return false;
		}
		if (!item.hasItemMeta() || !reference.hasItemMeta()) {
			return false;
		}
		ItemMeta itemMeta = item.getItemMeta();
		ItemMeta referenceMeta = reference.getItemMeta();
		if (itemMeta.hasDisplayName()) {
			if (referenceMeta.hasDisplayName()) {
				if (!itemMeta.getDisplayName().equals(referenceMeta.getDisplayName())) {
					return false;
				}
			} else {
				return false;
			}
		}
		if (itemMeta.hasLore()) {
			if (referenceMeta.hasLore()) {
				if (itemMeta.getLore().size() != referenceMeta.getLore().size()) {
					return false;
				}
				for (int i = 0; i < itemMeta.getLore().size(); i++) {
					if (!itemMeta.getLore().get(i).equals(referenceMeta.getLore().get(i))) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}
}

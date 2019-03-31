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
package com.backwardsnode.survivalgames.game;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemModel {

	public String id;
	public int max;
	public String name;
	public List<String> lore;
	public List<EnchantmentModel> enchantments;
	
	public ItemStack getEquivalent() {
		try {
			ItemStack item = new ItemStack(Material.matchMaterial(id), max);
			ItemMeta meta = item.getItemMeta();
			if (name != null) {
				meta.setDisplayName(name);
			}
			if (lore != null) {
				meta.setLore(lore);
			}
			for (int i = 0; i < enchantments.size(); i++) {
				Enchantment enc = enchantments.get(i).resolveEnchantment();
				if (enc == null) {
					Bukkit.getLogger().warning("[SG ItemModel] Enchantment " + enchantments.get(i).id + " cannot be resolved. This is a problem with the configuration file");
				} else {
					meta.addEnchant(enc, enchantments.get(i).level, true);
				}
			}
			item.setItemMeta(meta);
			return item;
		} catch (NullPointerException | IllegalArgumentException e) {
			return null;
		}
	}
	
	public static ItemModel fromItemStack(ItemStack item) {
		checkNotNull(item);
		ItemModel m = new ItemModel();
		m.id = item.getType().name().toLowerCase();
		m.max = item.getAmount();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				m.name = item.getItemMeta().getDisplayName();
			}
			if (item.getItemMeta().hasLore()) {
				m.lore = item.getItemMeta().getLore();
			}
		}
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		if (enchantments != null) {
			m.enchantments = new ArrayList<EnchantmentModel>();
			for (Enchantment e : enchantments.keySet()) {
				EnchantmentModel em = new EnchantmentModel();
				em.id = e.getKey().getKey();
				em.level = enchantments.get(e);
				m.enchantments.add(em);
			}
		}
		return m;
	}
}

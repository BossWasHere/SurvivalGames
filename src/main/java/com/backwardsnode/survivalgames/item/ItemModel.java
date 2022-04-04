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
package com.backwardsnode.survivalgames.item;

import com.backwardsnode.survivalgames.config.Copyable;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ItemModel implements Copyable<ItemModel>, Serializable {

	public String id;
	public int count;
	public String name;
	public List<String> lore;
	public List<EnchantmentModel> enchantments;
	
	public ItemStack getEquivalent() {
		try {
			int ind = id.indexOf('#');

			Material material = Material.matchMaterial(ind > 0 ? id.substring(0, ind) : id);
			if (material == null) {
				throw new GameConfigurationException("No material with id " + id);
			}

			ItemStack item = new ItemStack(material, count);
			ItemMeta meta = item.getItemMeta();
			assert meta != null;

			if (ind > 0) {
				String special = id.substring(ind + 1);

				if (meta instanceof PotionMeta potMeta) {
					PotionData eff = translatePotionData(special);
					if (eff == null) {
						Bukkit.getLogger().warning("[SG ItemModel] An unknown potion data tag has been provided. Check the config! [Provided: #" + special + "]");
					} else {
						potMeta.setBasePotionData(eff);
					}
				} else if (meta instanceof EnchantmentStorageMeta encMeta) {
					if (!addStoredEnchant(encMeta, special)) {
						Bukkit.getLogger().warning("[SG ItemModel] An unknown enchant store data tag has been provided. Check the config! [Provided: #" + special + "]");
					}
				}
			}

			if (name != null) {
				meta.setDisplayName(name);
			}	
			if (lore != null) {
				meta.setLore(lore);
			}
			for (EnchantmentModel enchantment : enchantments) {
				Enchantment enc = enchantment.resolveEnchantment();
				if (enc == null) {
					Bukkit.getLogger().warning("[SG ItemModel] Enchantment " + enchantment.id + " cannot be resolved. This is a problem with the configuration file");
				} else {
					meta.addEnchant(enc, enchantment.level, true);
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
		if (item.getItemMeta() instanceof PotionMeta) {
			m.id = item.getType().name().toLowerCase() + "#" + fromPotionData((PotionMeta)item.getItemMeta());
		} else if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
			m.id = item.getType().name().toLowerCase() + "#" + fromEnchantmentData((EnchantmentStorageMeta)item.getItemMeta());
		} else {
			m.id = item.getType().name().toLowerCase();
		}
		m.count = item.getAmount();
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				m.name = item.getItemMeta().getDisplayName();
			}
			if (item.getItemMeta().hasLore()) {
				m.lore = item.getItemMeta().getLore();
			}
		}
		Map<Enchantment, Integer> enchantments = item.getEnchantments();
		m.enchantments = new ArrayList<>();
		for (Enchantment e : enchantments.keySet()) {
			EnchantmentModel em = new EnchantmentModel();
			em.id = e.getKey().getKey();
			em.level = enchantments.get(e);
			m.enchantments.add(em);
		}
		return m;
	}
	
	private static PotionData translatePotionData(String data) {
		if (!data.contains(".")) {
			return null;
		}
		String[] parts = data.split("\\.");
		String name = parts[0].toUpperCase();
		boolean extended = parts.length > 1 && "1".equals(parts[1]);
		boolean upgraded = parts.length > 2 && "1".equals(parts[2]);
		try {
			return new PotionData(PotionType.valueOf(name), extended, upgraded);
		} catch (Exception e) {
			return null;
		}
	}
	
	private static String fromPotionData(PotionMeta potion) {
		checkNotNull(potion);
		PotionData pd = potion.getBasePotionData();
		return pd.getType().name() + "." + (pd.isExtended() ? 1 : 0) + "." + (pd.isUpgraded() ? 1 : 0);
	}
	
	private static boolean addStoredEnchant(EnchantmentStorageMeta meta, String data) {
		if (!data.contains(".")) {
			return false;
		}
		String[] parts = data.split("\\.");
		String name = parts[0].toLowerCase();
		int level = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
		Enchantment enc = new EnchantmentWrapper(name);
		return meta.addStoredEnchant(enc, level, true);
	}
	
	private static String fromEnchantmentData(EnchantmentStorageMeta enc) {
		checkNotNull(enc);
		for (Enchantment e : enc.getStoredEnchants().keySet()) {
			return e.getKey().getKey() + "." + enc.getStoredEnchantLevel(e);
		}
		return "";
	}

	@Override
    public ItemModel deepCopy() {
		ItemModel itemModel = new ItemModel();

		itemModel.id = id;
		itemModel.count = count;
		itemModel.name = name;
		itemModel.lore = new ArrayList<>(lore);
		itemModel.enchantments = new ArrayList<>(enchantments.size());

		for (EnchantmentModel model : enchantments) {
			itemModel.enchantments.add(model.deepCopy());
		}

		return itemModel;
    }
}

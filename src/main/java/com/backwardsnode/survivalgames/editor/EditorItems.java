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

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum EditorItems {

	/*
	 * Hotbar items
	 */
	LOOT_CHEST(Material.CHEST, "chest"),
	SPAWN_PLATE(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "plate"),
	ITEMSET_SWORD(Material.DIAMOND_SWORD, "itemset"),
	SETTINGS_COMPARATOR(Material.COMPARATOR, "settings"),
	CHEST_SETS_BOOK(Material.BOOK, "chestitems"),
	WORLDBORDER_FENCE(Material.NETHER_BRICK_FENCE, "borderitem"),
	INVSWITCH_BOOKSHELF(Material.BOOKSHELF, "invswitch"),
	
	/*
	 * Settings items
	 */
	NEW_ITEMSET_CONCRETE(Material.GREEN_CONCRETE, "additemset"),
	RENAME_MAP_NAMETAG(Material.NAME_TAG, "mapname"),
	BORDER_DPS_CACTUS(Material.CACTUS, "borderdps"),
	DEATHMATCH_CONFIG_FISHINGROD(Material.FISHING_ROD, "deathmatch"),
	SHRINK_TIME_AXE(Material.IRON_AXE, "shrinktime"),
	BORDER_START_MAP(Material.MAP, "borderstart"),
	WAIT_PERIOD_CLOCK(Material.CLOCK, "waittime"),
	GRACE_PERIOD_POPPY(Material.POPPY, "nopvptime"),
	PREFILL_CHESTMINECART(Material.CHEST_MINECART, "prefill"),
	DEATH_FIREWORK(Material.FIREWORK_ROCKET, "deathfirework"),
	KILL_FIREWORK(Material.FIREWORK_ROCKET, "killfirework"),
	LIGHTNING_ROD(Material.LIGHTNING_ROD, "lightningrod"),
	ISWIP_BRICKS(Material.BRICKS, "iswip"),
	
	INITIAL_BORDER_MAP(Material.MAP, "initialborder"),
	DEATHMATCH_BORDER_SWORD(Material.DIAMOND_SWORD, "deathmatchborder"),
	HIDE_BORDER_BARRIER(Material.BARRIER, "hideborder");
	
	private final Material material;
	private final String unlocalized;
	
	EditorItems(Material material, String unlocalized) {
		this.material = material;
		this.unlocalized = unlocalized;
	}
	
	public ItemStack getItem(Plugin plugin, String locale) {
		String l = plugin.getMessageProvider().compileUnregisteredMessage("item.lore." + unlocalized, locale);
		return Utils.addNameAndLore(new ItemStack(material), plugin.getMessageProvider().compileUnregisteredMessage("item.name." + unlocalized, locale), l.split("\\n"));
	}
	
	public boolean isSimilar(Plugin plugin, String locale, ItemStack item) {
		return isSimilar(plugin,this, locale, item);
	}
	
	public static boolean isSimilar(Plugin plugin, EditorItems model, String locale, ItemStack item) {
		if (item == null) {
			return false;
		}
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				return item.getItemMeta().getDisplayName().contentEquals(plugin.getMessageProvider().compileUnregisteredMessage("item.name." + model.unlocalized, locale));
			}
		}
		return false;
	}
}

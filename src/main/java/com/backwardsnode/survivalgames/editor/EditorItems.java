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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public enum EditorItems {

	/*
	 * Hotbar items
	 */
	LOOT_CHEST(Material.CHEST, "chest"),
	SPAWN_PLATE(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, "plate"),
	ITEMSET_VIEWER(Material.DIAMOND_SWORD, "itemsetviewer"),
	SETTINGS(Material.COMPARATOR, "settings"),
	CHEST_ITEMSET_MANAGER(Material.BOOK, "chestitems"),
	WORLDBORDER(Material.NETHER_BRICK_FENCE, "borderitem"),
	INVSWITCH(Material.BOOKSHELF, "invswitch"),
	
	/*
	 * Settings items
	 */
	ADD_ITEMSET(Material.GREEN_CONCRETE, "additemset"),
	RENAME_MAP(Material.NAME_TAG, "mapname"),
	BORDER_DPS(Material.CACTUS, "borderdps"),
	DEATHMATCH_CONFIG(Material.FISHING_ROD, "deathmatch"),
	SHRINK_TIME(Material.IRON_AXE, "shrinktime"),
	BORDER_START(Material.MAP, "borderstart"),
	WAIT_PERIOD(Material.CLOCK, "waittime"),
	GRACE_PERIOD(Material.POPPY, "nopvptime"),
	PREFILL_CHEST(Material.CHEST_MINECART, "prefill"),
	DEATH_FIREWORK(Material.FIREWORK_ROCKET, "deathfirework"),
	KILL_FIREWORK(Material.FIREWORK_ROCKET, "killfirework"),
	DEATH_LIGHTNING(Material.LIGHTNING_ROD, "lightningrod"),
	IS_WIP(Material.BRICKS, "iswip"),
	
	SET_INITIAL_BORDER(Material.MAP, "initialborder"),
	SET_DEATHMATCH_BORDER(Material.DIAMOND_SWORD, "deathmatchborder"),
	SET_HIDE_BORDER(Material.BARRIER, "hideborder"),

	ENABLED_DEFAULT_ITEMSET(Material.LIME_STAINED_GLASS, "enableddefaultset"),
	DISABLED_DEFAULT_ITEMSET(Material.RED_STAINED_GLASS, "disableddefaultset"),
	ENABLED_ITEMSET(Material.LIME_CONCRETE, "enabledset"),
	DISABLED_ITEMSET(Material.RED_CONCRETE, "disabledset"),

	ITEMSET(Material.IRON_SWORD, "itemset"),

	SELECT_BORDER(Material.OAK_FENCE, "borderselect"),
	SELECTED_BORDER(Material.NETHER_BRICK_FENCE, "borderselected");

	private static final Map<String, EditorItems> ITEMS_MAP = new HashMap<>();

	private final Material material;
	private final String unlocalized;
	
	EditorItems(Material material, String unlocalized) {
		this(material, unlocalized, false);
	}

	EditorItems(Material material, String unlocalized, boolean isTemplateItem) {
		this.material = material;
		this.unlocalized = unlocalized;
	}
	
	public ItemStack getItem(Plugin plugin, String locale) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(plugin.getMessageProvider().compileUnregisteredMessage("item.name." + unlocalized, locale));
		String lore = plugin.getMessageProvider().compileUnregisteredMessage("item.lore." + unlocalized, locale);
		meta.setLore(Arrays.asList(lore.split("\\n")));
		meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "id"), PersistentDataType.STRING, unlocalized);
		item.setItemMeta(meta);

		return item;
	}

	public ItemStack getTemplatedItem(Plugin plugin, String templateId, String name, String loreLocale) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		String lore = plugin.getMessageProvider().compileUnregisteredMessage("item.lore." + unlocalized, loreLocale);
		meta.setLore(Arrays.asList(lore.split("\\n")));
		PersistentDataContainer pdc = meta.getPersistentDataContainer();
		pdc.set(new NamespacedKey(plugin, "id"), PersistentDataType.STRING, unlocalized);
		pdc.set(new NamespacedKey(plugin, "meta"), PersistentDataType.STRING, templateId);
		item.setItemMeta(meta);

		return item;
	}
	
	public boolean isSimilar(Plugin plugin, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			PersistentDataContainer container = meta.getPersistentDataContainer();
			NamespacedKey key = new NamespacedKey(plugin, "id");
			if (container.has(key, PersistentDataType.STRING)) {
				return unlocalized.equals(container.get(key, PersistentDataType.STRING));
			}
		}

		return false;
	}

	public static String getTemplateId(Plugin plugin, ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			PersistentDataContainer container = meta.getPersistentDataContainer();
			NamespacedKey template = new NamespacedKey(plugin, "meta");
			return container.has(template, PersistentDataType.STRING) ? container.get(template, PersistentDataType.STRING) : null;
		}

		return null;
	}

	public static EditorItems getRepresentingItem(Plugin plugin, ItemStack item) {
		if (ITEMS_MAP.isEmpty()) {
			for (EditorItems constant : values()) {
				ITEMS_MAP.put(constant.unlocalized, constant);
			}
		}

		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			PersistentDataContainer container = meta.getPersistentDataContainer();
			NamespacedKey key = new NamespacedKey(plugin, "id");
			if (container.has(key, PersistentDataType.STRING)) {
				String id = container.get(key, PersistentDataType.STRING);

				return ITEMS_MAP.get(id);
			}
		}

		return null;
	}
}

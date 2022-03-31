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

import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.config.IConfigurable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChestObject implements IConfigurable {
	
	public String loc;
	public List<String> itemSets;
	
	public transient Location location;

	public void configure() {
		location = Utils.locationFromString(loc, false);
		if (location == null) {
			// TODO bring inside plugin logger
			Bukkit.getLogger().warning("Invalid chest configuration @ " + loc);
		}
	}

    public boolean fill(Chest chest, List<ItemSet> allItemSets) {
		List<ItemSet> selector = new ArrayList<>();
		for (ItemSet set : allItemSets) {
			if (itemSets.contains(set.name)) {
				selector.add(set);
			}
		}

		List<ItemModel> shuffler = new ArrayList<>();
		for (ItemSet select : selector) {
			shuffler.addAll(select.items);
		}
		Collections.shuffle(shuffler);

		Random r = new Random();
		int m = r.nextInt(4) + 2;
		int[] slots = Utils.getRandomSlots(m, 27);

		chest.setCustomName("Loot Chest");
		chest.update(true, false);

		Inventory blockInv = chest.getBlockInventory();
		blockInv.clear();

		boolean isOkay = true;
		for (int i = 0; i < shuffler.size() && i < m; i++) {
			ItemModel model = shuffler.get(i);
			ItemStack item = model.getEquivalent();
			if (item == null) {
				Bukkit.getLogger().warning("Unknown item [" + model.id + "], you should check the config file");
				isOkay = false;
				m++;
				continue;
			}
			item.setAmount(r.nextInt(model.count) + 1);
			blockInv.setItem(slots[i], item);
		}

		return isOkay;
    }
}

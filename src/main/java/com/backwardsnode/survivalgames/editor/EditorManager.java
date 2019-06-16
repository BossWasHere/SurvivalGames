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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.game.ItemModel;
import com.backwardsnode.survivalgames.game.ItemSet;
import com.backwardsnode.survivalgames.util.ItemUtil;
import com.backwardsnode.survivalgames.util.WorldUtil;

public class EditorManager {

	static final String ITEM_SET_INV_TITLE = ChatColor.DARK_AQUA + "[SGEdit] Item Sets";
	static final String ITEM_SET_CUST_TITLE = ChatColor.DARK_AQUA + "[SGEdit] Item Set: ";
	static final String ITEM_SET_CHOOSE_TITLE = ChatColor.DARK_AQUA + "[SGEdit] Choose Item Sets";
	static final String OPTION_SET_TITLE = ChatColor.DARK_AQUA + "[SGEdit] Change Settings";
	static final String DEATHMATCH_INV_TITLE = ChatColor.DARK_AQUA + "[SGEdit] Deathmatch Settings";
	
	private final Plugin plugin;
	private EditorManagerListener listener;
	
	final ItemStack chestItem,plateItem,swordItem,comparatorItem,bookItem;
	final ItemStack addItemSetItem;
	final ItemStack mapNameItem,implementItem,borderDpsItem,deathmatchConfigItem,toDeathmatchItem,borderStartItem,waitPeriodItem,gracePeriodItem,preFillItem;

	Map<String, Scene> editorsActive;
	Map<String, InventoryMemory> editorMemory;

	public EditorManager(Plugin plugin) {
		this.plugin = plugin;
		editorsActive = new HashMap<String, Scene>();
		editorMemory = new HashMap<String, InventoryMemory>();
		createListener();
		
		chestItem = ItemUtil.addNameAndLore(new ItemStack(Material.CHEST), ChatColor.GREEN + "Place loot chest", "Place a chest to", "add to the current scene");
		plateItem = ItemUtil.addNameAndLore(new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE), ChatColor.GREEN + "Place spawn pad", "Place a plate to", "add to the current scene");
		swordItem = ItemUtil.addNameAndLore(new ItemStack(Material.DIAMOND_SWORD), ChatColor.GREEN + "Edit item sets", "Click to modify the", "the items which", "spawn in chests");
		comparatorItem = ItemUtil.addNameAndLore(new ItemStack(Material.COMPARATOR), ChatColor.GREEN + "Change settings", "Click to modify the", "game settings");
		bookItem = ItemUtil.addNameAndLore(new ItemStack(Material.BOOK), ChatColor.GREEN + "Change chest item sets", "Click to modify the", "item sets for this", "chest");
		
		addItemSetItem = ItemUtil.addNameAndLore(new ItemStack(Material.GREEN_CONCRETE), ChatColor.GREEN + "Add item set", "Click to add an item", "set for the current scene");
		
		mapNameItem = ItemUtil.addNameAndLore(new ItemStack(Material.NAME_TAG), ChatColor.GREEN + "Change name", "Set the display", "name of the map");
		implementItem = ItemUtil.addNameAndLore(new ItemStack(Material.BOOK), ChatColor.GREEN + "Change child maps", "Set the maps from", "which itemsets are taken");
		borderDpsItem = ItemUtil.addNameAndLore(new ItemStack(Material.CACTUS), ChatColor.GREEN + "Change border DPS", "Set the damage", "done per second");
		deathmatchConfigItem = ItemUtil.addNameAndLore(new ItemStack(Material.FISHING_ROD), ChatColor.GREEN + "Change deathmatch locations", "Add or remove different", "locations for deathmatches");
		toDeathmatchItem = ItemUtil.addNameAndLore(new ItemStack(Material.IRON_AXE), ChatColor.GREEN + "Change time until deathmatch", "Set the time where", "players can loot freely");
		borderStartItem = ItemUtil.addNameAndLore(new ItemStack(Material.MAP), ChatColor.GREEN + "Change border start radius", "Set the radius of the", "border before game starts", "(should be small enough that", "border doesn't shrink 100m in 1s)");
		waitPeriodItem = ItemUtil.addNameAndLore(new ItemStack(Material.CLOCK), ChatColor.GREEN + "Change the wait period", "Set the amount of time", "before looting begins");
		gracePeriodItem = ItemUtil.addNameAndLore(new ItemStack(Material.POPPY), ChatColor.GREEN + "Change PvP Off-time", "Set the 'grace period'", "before PvP is enabled");
		preFillItem = ItemUtil.addNameAndLore(new ItemStack(Material.CHEST_MINECART), ChatColor.GREEN + "Toggle chest fill at start", "Toggle filling at start", "to on-demand (useful for", "large maps)");
		
	}

	private void createListener() {
		listener = new EditorManagerListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	public boolean addEditor(Player player, Scene scene) {
		if (editorsActive.containsKey(player.getName())) {
			return false;
		}
		player.sendMessage(ChatColor.DARK_AQUA + "[!] Opening the editor... [!]");
		InventoryMemory memory = new InventoryMemory(player);
		memory.replaceHotbar(chestItem, null, bookItem, null, plateItem, null, swordItem, null, comparatorItem);
		editorMemory.put(player.getName(), memory);
		editorsActive.put(player.getName(), scene);
		return true;
	}
	
	public boolean closeEditor(Player player, boolean save) {
		if (editorsActive.containsKey(player.getName())) {
			if (save) {
				editorsActive.get(player.getName()).save();
			}
			editorsActive.remove(player.getName());
			InventoryMemory memory = editorMemory.get(player.getName());
			if (memory != null) {
				memory.resetHotbar();
			}
			editorMemory.remove(player.getName());
			player.sendMessage(ChatColor.DARK_AQUA + "[!] Leaving the editor... [!]");
			listener.chatQueries.remove(player.getName());
			return true;
		}
		return false;
	}
	
	public void closeAllEditors() {
		for (Scene scene : editorsActive.values()) {
			scene.save();
		}
		editorsActive.clear();
		for (InventoryMemory memory : editorMemory.values()) {
			memory.resetHotbar();
		}
		editorMemory.clear();
		listener.chatQueries.clear();
	}
	
	public boolean isEditor(Player player) {
		return editorsActive.containsKey(player.getName());
	}
	
	public boolean openItemSetInventory(Player player) {
		Scene current = editorsActive.get(player.getName());
		if (current != null) {
			int values = current.getItemSets().size();
			if (values > 0) {
				while (values % 9 != 0) {
					values++;
				}
				if (values < 54) {
					values += 9;
				}
				Inventory i = Bukkit.createInventory(null, values, ITEM_SET_INV_TITLE);
				for (int j = 0; j < current.getItemSets().size(); j++) {
					i.addItem(ItemUtil.addNameAndLore(new ItemStack(Material.IRON_SWORD), current.getItemSets().get(j).name, "ItemSet #" + j));
				}
				i.setItem(values - 1, addItemSetItem);
				player.openInventory(i);
				return true;
			} else {
				Inventory i = Bukkit.createInventory(null, 9, ITEM_SET_INV_TITLE);
				i.setItem(8, addItemSetItem);
				player.openInventory(i);
			}
		}
		return false;
	}
	
	public boolean openItemSetCustomiseInventory(Player player, String itemSet) {
		Scene current = editorsActive.get(player.getName());
		if (current != null) {
			for (int i = 0; i < current.getItemSets().size(); i++) {
				ItemSet target = current.getItemSets().get(i);
				if (target.name.equals(itemSet)) {
					Inventory inv = Bukkit.createInventory(player, 54, ITEM_SET_CUST_TITLE + itemSet);
					if (target.items != null) {
						List<ItemModel> items = target.items;
						for (int j = 0; j < items.size() && j < 54; j++) {
							ItemStack p = items.get(j).getEquivalent();
							if (p == null) {
								Bukkit.getLogger().warning("[SGEdit] Cannot instantiate ItemStack (id=" + items.get(j).id + ")");
								return false;
							}
							inv.addItem(p);
						}
						player.openInventory(inv);
						return true;
					} else {
						player.openInventory(inv);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean openChestItemSetInventory(Player player, Location location) {
		Scene current = editorsActive.get(player.getName());
		int values = current.getItemSets().size();
		while (values % 9 != 0 || values < 1) {
			values++;
		}
		if (values < 54) {
			values += 9;
		}
		Inventory inv = Bukkit.createInventory(null, values, ITEM_SET_CHOOSE_TITLE);
		List<String> enabledItemSets = current.getChestItemSets(location);
		for (ItemSet set : current.getItemSets()) {
			if (enabledItemSets.stream().anyMatch(str -> str == null ? false : str.equals(set.name))) {
				if (set.isDefault) {
					inv.addItem(ItemUtil.addNameAndLore(new ItemStack(Material.LIME_STAINED_GLASS), set.name, "Default itemset", "Click me to disable", "this item set for chest"));
				} else {
					inv.addItem(ItemUtil.addNameAndLore(new ItemStack(Material.LIME_CONCRETE), set.name, "Click me to disable", "this item set for chest"));
				}
			} else {
				if (set.isDefault) {
					inv.addItem(ItemUtil.addNameAndLore(new ItemStack(Material.RED_STAINED_GLASS), set.name, "Default itemset", "Click me to enable", "this item set for chest"));
				} else {
					inv.addItem(ItemUtil.addNameAndLore(new ItemStack(Material.RED_CONCRETE), set.name, "Click me to enable", "this item set for chest"));
				}
			}
		}
		inv.setItem(values - 1, ItemUtil.addNameAndLore(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE), "Inventory-Info", WorldUtil.stringFromLocation(location, true)));
		player.openInventory(inv);		
		return true;
	}
	
	public boolean openSettingInventory(Player player) {
		Inventory i = Bukkit.createInventory(null, 18, OPTION_SET_TITLE);
		i.addItem(mapNameItem, implementItem, borderDpsItem, deathmatchConfigItem, toDeathmatchItem, borderStartItem, waitPeriodItem, gracePeriodItem, preFillItem);
		player.openInventory(i);
		return true;
	}
	
	public boolean openDeathmatchInventory(Player player) {
		Inventory i = Bukkit.createInventory(null, 18, DEATHMATCH_INV_TITLE);
		player.openInventory(i);
		return true;
	}
}

class EditorManagerListener implements Listener {

	private EditorManager handler;
	Map<String, ChatQueryObject> chatQueries;

	public EditorManagerListener(EditorManager handler) {
		this.handler = handler;
		chatQueries = new HashMap<String, ChatQueryObject>();
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		Scene current = handler.editorsActive.get(e.getPlayer().getName());
		if (current != null) {
			ItemStack item = e.getItemInHand();
			if (item.equals(handler.chestItem) && e.getBlock().getType() == Material.CHEST) {
				current.addItemChest(e.getBlock().getLocation());
				e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Added item chest to game configuration");
			} else if (item.equals(handler.plateItem) && e.getBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
				current.addSpawnPlate(e.getBlock().getLocation());
				e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Added spawn position to game configuration");
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		Block target = e.getClickedBlock();
		if (e.getHand() == EquipmentSlot.HAND) {
			if (handler.editorsActive.containsKey(e.getPlayer().getName())) {
				Scene current = handler.editorsActive.get(e.getPlayer().getName());
				if (target != null) {
					if (target.getType() == Material.CHEST) {
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (current.isItemChest(target.getLocation())) {
								if (ItemUtil.matchesMetaAndType(e.getItem(), handler.bookItem)) {
									handler.openChestItemSetInventory(e.getPlayer(), target.getLocation());
								} else {
									if (e.getItem() == null) {
										current.removeItemChest(target.getLocation());
										e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Removed item chest from game configuration");
									}
								}
							} else {
								if (ItemUtil.matchesMetaAndType(e.getItem(), handler.bookItem)) {
									e.getPlayer().sendMessage(ChatColor.AQUA + "[!] Click chest without book first to add to list, then click with book to modify it [!]");
								} else if (e.getItem() == null) {
									current.addItemChest(target.getLocation());
									e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Added item chest to game configuration");
								}
							}
						} else {
							if (current.removeItemChest(target.getLocation())) {
								e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Removed item chest from game configuration");
							}
							return;
						}
						e.setCancelled(true);
						return;
					} else if (target.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
						if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (current.isSpawnPlate(target.getLocation())) {
								if (e.getItem() == null) {
									current.removeSpawnPlate(target.getLocation());
									e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Removed spawn position from game configuration");
								}
							} else {
								if (e.getItem() == null) {
									current.addSpawnPlate(target.getLocation());
									e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Added spawn position to game configuration");
								}
							}
						} else {
							if (current.removeSpawnPlate(target.getLocation())) {
								e.getPlayer().sendMessage(ChatColor.AQUA + "[SGEdit] Removed spawn position from game configuration");
							}
							return;
						}
						e.setCancelled(true);
						return;
					}
				}
				if (ItemUtil.matchesMetaAndType(e.getItem(), handler.swordItem)) {
					handler.openItemSetInventory(e.getPlayer());
				} else if (ItemUtil.matchesMetaAndType(e.getItem(), handler.comparatorItem)) {
					handler.openSettingInventory(e.getPlayer());
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerInventoryEvent(InventoryClickEvent e) {
		Scene current = handler.editorsActive.get(e.getWhoClicked().getName());
		if (current != null) {
			Inventory i = e.getClickedInventory();
			ItemStack c = e.getCurrentItem();
			String title = i == null ? "" : e.getView().getTitle();
			if (i != null && c != null) {
				if (title.equals(EditorManager.ITEM_SET_INV_TITLE)) {
					if (ItemUtil.matchesMetaAndType(c, handler.addItemSetItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the name for the new item set:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.NEW_ITEMSET_NAME);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Created item set: " + cqo.getLastValue());
								handler.openItemSetCustomiseInventory((Player)e.getWhoClicked(), cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else {
						if (c.hasItemMeta()) {
							if (c.getItemMeta().hasDisplayName()) {
								String set = c.getItemMeta().getDisplayName();
								handler.openItemSetCustomiseInventory((Player)e.getWhoClicked(), set);
							}
						}
					}
					e.setCancelled(true);
				} else if (title.equals(EditorManager.ITEM_SET_CHOOSE_TITLE)) {
					ItemStack chestBlockData = i.getItem(i.getSize() - 1);
					if (chestBlockData != null) {
						Location loc = WorldUtil.locationFromString(chestBlockData.getItemMeta().getLore().get(0));
						ItemStack[] items = i.getContents();
						List<String> selected = new ArrayList<String>();
						for (int j = 0; j < items.length; j++) {
							if (items[j] != null) {
								if (items[j].equals(c)) {
									switch (items[j].getType()) {
									case LIME_STAINED_GLASS:
										items[j].setType(Material.RED_STAINED_GLASS);
										break;
									case LIME_CONCRETE:
										items[j].setType(Material.RED_CONCRETE);
										break;
									case RED_STAINED_GLASS:
										items[j].setType(Material.LIME_STAINED_GLASS);
										break;
									case RED_CONCRETE:
										items[j].setType(Material.LIME_CONCRETE);
										break;
									default:
										continue;
									}
								}
								switch (items[j].getType()) {
								case LIME_STAINED_GLASS:
								case LIME_CONCRETE:
									selected.add(items[j].getItemMeta().getDisplayName());
								default:
									continue;
								}
							}
						}
						if (current.updateAllChestItemSets(loc, selected)) {
							e.getWhoClicked().sendMessage(ChatColor.AQUA + "Modified item sets for chest!");
						} else {
							e.getWhoClicked().sendMessage(ChatColor.AQUA + "Could not modify item sets!");
							e.getWhoClicked().closeInventory();
						}
					}
					e.setCancelled(true);
				} else if (title.equals(EditorManager.OPTION_SET_TITLE)) {
					if (ItemUtil.matchesMetaAndType(c, handler.mapNameItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the name for the map:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.MAP_NAME);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Set map name set: " + cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else if (ItemUtil.matchesMetaAndType(c, handler.implementItem)) {
						//TODO
					} else if (ItemUtil.matchesMetaAndType(c, handler.borderDpsItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the border damage per second:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.BORDER_DPS);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Set border DPS: " + cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else if (ItemUtil.matchesMetaAndType(c, handler.deathmatchConfigItem)) {
						e.getWhoClicked().closeInventory();
						handler.openDeathmatchInventory((Player)e.getWhoClicked());
					} else if (ItemUtil.matchesMetaAndType(c, handler.toDeathmatchItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the amount of time (seconds) before deathmatch begins:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.TIME_TO_DM);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Set time-to-deathmatch: " + cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else if (ItemUtil.matchesMetaAndType(c, handler.borderStartItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the border start radius:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.BORDER_START_RADIUS);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Set border radius: " + cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else if (ItemUtil.matchesMetaAndType(c, handler.waitPeriodItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the amount of time (seconds) before game starts on command:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.WAIT_PERIOD);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Set wait time: " + cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else if (ItemUtil.matchesMetaAndType(c, handler.gracePeriodItem)) {
						e.getWhoClicked().closeInventory();
						e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Please enter the amount of time (seconds) before PvP is enabled:");
						ChatQueryObject cqo = new ChatQueryObject(current, QueryUpdater.GRACE_PERIOD);
						cqo.attachRunnable(new Runnable() {
							
							@Override
							public void run() {
								e.getWhoClicked().sendMessage(ChatColor.AQUA + "Set PvP-Off time: " + cqo.getLastValue());
							}
						});
						chatQueries.put(e.getWhoClicked().getName(), cqo);
					} else if (ItemUtil.matchesMetaAndType(c, handler.preFillItem)) {
						e.getWhoClicked().closeInventory();
						current.setPreFillChest(!current.getPreFillChests());
						if (current.getPreFillChests()) {
							e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Chests will now be filled at the start of each game");
						} else {
							e.getWhoClicked().sendMessage(ChatColor.AQUA + "[SGEdit] Chests will now be filled when a player opens one");
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerInventoryCloseEvent(InventoryCloseEvent e) {
		Scene current = handler.editorsActive.get(e.getPlayer().getName());
		if (current != null) {
			Inventory i = e.getInventory();
			if (i != null) {
				if (e.getView().getTitle().startsWith(EditorManager.ITEM_SET_CUST_TITLE)) {
					String set = e.getView().getTitle().substring(EditorManager.ITEM_SET_CUST_TITLE.length(), e.getView().getTitle().length());
					if (current.replaceItemSet(i.getContents(), set)) {
						e.getPlayer().sendMessage(ChatColor.AQUA + "[!] Updated items for set " + set + " [!]");
					} else {
						e.getPlayer().sendMessage(ChatColor.RED + "Error while updating items for set " + set);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		Scene current = handler.editorsActive.get(e.getPlayer().getName());
		if (current != null) {
			ChatQueryObject cqo = chatQueries.get(e.getPlayer().getName());
			if (cqo != null) {
				e.setCancelled(true);
				cqo.setValue(e.getMessage());
			}
			chatQueries.remove(e.getPlayer().getName());
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		Scene current = handler.editorsActive.get(e.getPlayer().getName());
		if (current != null) {
			current.save();
			handler.editorsActive.remove(e.getPlayer().getName());
			InventoryMemory memory = handler.editorMemory.get(e.getPlayer().getName());
			memory.resetHotbar();
			handler.editorMemory.remove(e.getPlayer().getName());
		}
	}
}
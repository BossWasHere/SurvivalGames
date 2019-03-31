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
package com.backwardsnode.survivalgames.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.exception.GameRunningException;
import com.backwardsnode.survivalgames.game.GameConfiguration;
import com.backwardsnode.survivalgames.game.GameManager;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class SGStart extends Command {
	
	private final Plugin plugin;

	public SGStart(Plugin plugin) {
		super("sgstart", "Start the game", ChatColor.RED + "Usage: /sgstart (file)", Arrays.asList("sgs"));
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("survivalgames.sgstart")) {
			if (sender instanceof Player) {
				if (args.length > 0) {
					try {
						File target = new File(plugin.getDataFolder(), args[0]);
						if (!target.isFile()) {
							target = new File(plugin.getDataFolder(), args[0] + ".json");
						}
						GameConfiguration c = GameConfiguration.loadGameConfiguration(plugin.getDataFolder(), target);
						List<Location> invalidLocations = c.checkChests();
						if (invalidLocations.size() > 0) {
							sender.sendMessage(ChatColor.RED + "Warning: Missing chests detected (Run /checkconfig command)");
						} else {
							try {
								plugin.gameManager = GameManager.createInstance(plugin, c);
								plugin.gameManager.begin((Player)sender);
							} catch (GameRunningException | GameConfigurationException e) {
								sender.sendMessage(ChatColor.RED + e.getMessage());
							}
						}
					} catch (FileNotFoundException e) {
						sender.sendMessage(ChatColor.RED + "1. Error: No file with name " + args[0]);
					} catch (JsonIOException | JsonSyntaxException e) {
						sender.sendMessage(ChatColor.RED + "1. Error: There is a io/syntax error in the file:\n" + e.getMessage());
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
		}
		return true;
	}
}

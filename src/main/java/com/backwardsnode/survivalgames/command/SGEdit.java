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
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.game.GameConfiguration;

public class SGEdit extends Command {

	private final Plugin plugin;

	public SGEdit(Plugin plugin) {
		super("sgedit", "Edit a survival game configuration file", ChatColor.RED + "Usage: /sgedit (file)", Arrays.asList("survivalgamesedit", "sge"));
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("survivalgames.sgedit")) {
			if (sender instanceof Player) {
				if (args.length > 0) {
					sender.sendMessage(ChatColor.LIGHT_PURPLE + "[!] Loading file " + args[0] + " [!]");
					try {
						if (plugin.editorManager.isEditor((Player)sender)) {
							plugin.editorManager.closeEditor((Player) sender, true);
						} else {
							File target;
							if (args[0].toLowerCase().endsWith(".json")) {
								target = new File(plugin.getDataFolder(), args[0]);
							} else {
								target = new File(plugin.getDataFolder(), args[0] + ".json");
							}
							if (!target.exists()) {
								sender.sendMessage(ChatColor.AQUA + "File doesn't exist, creating one now");
								target.createNewFile();
							}
							plugin.editorManager.addEditor((Player) sender, GameConfiguration.loadGameConfigurationOrCreateEmpty(plugin.getDataFolder(), target).createScene());
						}
					} catch (IOException e) {
						sender.sendMessage(ChatColor.RED + "Error: Cannot load or create configuration file");
					}
				} else {
					if (plugin.editorManager.isEditor((Player)sender)) {
						plugin.editorManager.closeEditor((Player)sender, true);
					} else {
						sender.sendMessage(usageMessage);
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
		}
		return true;
	}

}

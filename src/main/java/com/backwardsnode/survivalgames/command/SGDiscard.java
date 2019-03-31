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

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.backwardsnode.survivalgames.Plugin;

public class SGDiscard extends Command {

	private final Plugin plugin;

	public SGDiscard(Plugin plugin) {
		super("sgdiscard", "Discard current SGEdit Scene", ChatColor.RED + "Usage: /sgdiscard", Arrays.asList("sgdisc"));
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("survivalgames.sgedit")) {
			if (sender instanceof Player) {
				if (plugin.editorManager.isEditor((Player)sender)) {
					plugin.editorManager.closeEditor((Player)sender, false);
					sender.sendMessage(ChatColor.AQUA + "Discarded all changes!");
				} else {
					sender.sendMessage(ChatColor.RED + "You are not currently editing!");
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
		}
		return true;
	}

}

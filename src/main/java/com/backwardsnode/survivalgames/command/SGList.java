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
import java.io.FilenameFilter;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.json.JsonMessage;
import com.backwardsnode.survivalgames.json.JsonMessage.CompoundJsonMessage;
import com.backwardsnode.survivalgames.util.ChatUtil;
import com.backwardsnode.survivalgames.json.TextEvent;

public class SGList extends Command {

	private final Plugin plugin;

	public SGList(Plugin plugin) {
		super("sglist", "List all SG Maps", ChatColor.RED + "Usage: /sglist", Arrays.asList("sgl"));
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (sender.hasPermission("survivalgames.sglist")) {
			File[] jsons = plugin.getDataFolder().listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".json");
				}
			});
			if (jsons.length < 1) {
				sender.sendMessage(ChatColor.RED + "[SG] Found no saved files!");
			} else {
				if (sender instanceof Player) {
					CompoundJsonMessage cmsg = new CompoundJsonMessage();
					cmsg.messages.add(new JsonMessage().setText("[SG] Available Game Files:").setColor("dark_aqua").setBold(true));
					for (File json : jsons) {
						cmsg.messages.add(new JsonMessage().setText("\n[Load]").setColor("green").setUnderlined(true).setClickEvent(TextEvent.runCommand("/sgs " + json.getName())));
						cmsg.messages.add(new JsonMessage().setText(" - ").setColor("green"));
						cmsg.messages.add(new JsonMessage().setText(json.getName()).setColor("gray"));
					}
					ChatUtil.sendRawMessage((Player)sender, cmsg);
				} else {
					String msg = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[SG] Available Game Files:" + ChatColor.RESET;
					for (File json : jsons) {
						msg += "\n" + ChatColor.GRAY + json.getName();
					}
					sender.sendMessage(msg);
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this command");
		}
		return true;
	}

}

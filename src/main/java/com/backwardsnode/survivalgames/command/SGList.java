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
package com.backwardsnode.survivalgames.command;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.command.base.BaseCommand;
import com.backwardsnode.survivalgames.command.base.CommandType;
import com.backwardsnode.survivalgames.command.base.ExecutionStatus;
import com.backwardsnode.survivalgames.message.JsonMessage;
import com.backwardsnode.survivalgames.message.JsonTextEvent;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FilenameFilter;

public class SGList extends BaseCommand {

	public SGList(Plugin plugin) {
		super(plugin, CommandType.SG_LIST);
	}

	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String locale = player.getLocale();
			JsonMessage.CompoundJsonMessage cmsg = new JsonMessage.CompoundJsonMessage();
			boolean canStartAll = player.hasPermission(CommandType.SG_START.getBasicPermission() + ".all");
			boolean canEdit = player.hasPermission(CommandType.SG_EDIT.getBasicPermission());
			boolean canCheck = player.hasPermission(CommandType.SG_CHECK.getBasicPermission());

			// TODO check colors as JSON messages work (or implement a JSON message factory inside/with MessageProvider)

			cmsg.messages.add(new JsonMessage().setText(PLUGIN.getMessageProvider().compileMessage(Messages.Command.SGList.TITLE, locale)));
			String loadCmd = "\n" + PLUGIN.getMessageProvider().compileMessage(Messages.Command.SGList.LOAD_INVITE, locale);
			String loadCmdAll = PLUGIN.getMessageProvider().compileMessage(Messages.Command.SGList.LOAD_ALL, locale);
			String edit = PLUGIN.getMessageProvider().compileMessage(Messages.Command.SGList.EDIT, locale);
			String checkconfig = PLUGIN.getMessageProvider().compileMessage(Messages.Command.SGList.CHECK_CONFIG, locale);

			String sgStartCommand = '/' + CommandType.SG_START.getCommand() + " ";
			String sgEditCommand = '/' + CommandType.SG_EDIT.getCommand() + " ";
			String sgCheckCommand = '/' + CommandType.SG_CHECK.getCommand() + " ";

			for (File json : getJsons(PLUGIN.getMapFolder())) {
				cmsg.messages.add(new JsonMessage().setText(loadCmd).setClickEvent(JsonTextEvent.runCommand(sgStartCommand + json.getName() + " invite")));
				if (canStartAll) {
					cmsg.messages.add(new JsonMessage().setText(" " + loadCmdAll).setClickEvent(JsonTextEvent.runCommand(sgStartCommand + json.getName() + " all")));
				}
				if (canEdit) {
					cmsg.messages.add(new JsonMessage().setText(" " + edit).setClickEvent(JsonTextEvent.runCommand(sgEditCommand + json.getName())));
				}
				if (canCheck) {
					cmsg.messages.add(new JsonMessage().setText(" " + checkconfig).setClickEvent(JsonTextEvent.runCommand(sgCheckCommand + json.getName())));
				}
				cmsg.messages.add(new JsonMessage().setText(" - ").setColor(ChatColor.GREEN).setBold(true));
				cmsg.messages.add(new JsonMessage().setText(json.getName()).setColor(ChatColor.GRAY));
			}

			Utils.sendJsonMessage(player, cmsg);
		} else {
			sendMessage(sender, Messages.Command.SGList.TITLE);
			for (File json : getJsons(PLUGIN.getMapFolder())) {
				sender.sendMessage(ChatColor.GRAY + json.getName());
			}
		}

		return ExecutionStatus.SUCCESS;
	}
	
	private File[] getJsons(File dataFolder) {
		return dataFolder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});
	}

}

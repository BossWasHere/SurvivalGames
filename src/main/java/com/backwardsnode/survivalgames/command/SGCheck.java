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
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.message.JsonMessage;
import com.backwardsnode.survivalgames.message.JsonTextEvent;
import com.backwardsnode.survivalgames.message.Messages;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

public class SGCheck extends BaseCommand {

	public SGCheck(Plugin plugin) {
		super(plugin, CommandType.SG_CHECK);
	}

	// TODO move checking logic outside of command method
	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		if (args.length > 0) {
			File target = new File(PLUGIN.getMapFolder(), args[0]);
			try {
				if (!target.isFile()) {
					target = new File(PLUGIN.getMapFolder(), args[0] + ".json");
				}
				if (!target.isFile()) {
					sendMessage(sender, Messages.Plugin.IO_FILE_MISSING, args[0]);
				} else {
					sendMessage(sender, Messages.Plugin.IO_FILE_FOUND, target.getName());
					GameConfigurationWrapper gcw = new GameConfigurationWrapper(target, true);
					sendMessage(sender, Messages.Plugin.IO_FILE_LOADED);
					Set<Location> invalidLocations = gcw.checkChests();
					if (invalidLocations.size() > 0) {
						StringBuilder builder = new StringBuilder();
						builder.append("At: ");
						int i = 0;
						for (Location location : invalidLocations) {
							builder.append("[").append(Utils.stringFromLocation(location, false, true)).append("], ");
							if (i++ % 3 == 0) {
								builder.append('\n');
							}
							if (i > 9) {
								builder.append(" + ").append(invalidLocations.size() - 10).append(" more  ");
								break;
							}
						}

						builder.substring(0, builder.length() - 2);
						if (sender instanceof Player) {
							Player player = (Player) sender;
							JsonMessage msg = new JsonMessage().setColor(ChatColor.RED)
							.setText(PLUGIN.getMessageProvider().compileMessage(Messages.Config.CHEST_MISSING, player.getLocale(), invalidLocations.size(), gcw.getChests().size()))
							.setHoverEvent(JsonTextEvent.showText(builder.toString()));
							Utils.sendJsonMessage(player, msg);
						} else {
							sendMessage(sender, Messages.Config.CHEST_MISSING_CONSOLE, invalidLocations.size(), gcw.getChests().size(), builder.toString());
						}
					} else {
						sendMessage(sender, Messages.Config.CHEST_LOCATED, gcw.getChests().size());
					}
				}
			} catch (FileNotFoundException | JsonIOException e) {
				sendMessage(sender, Messages.Plugin.IO_EXCEPTION);
			} catch (JsonSyntaxException e) {
				sendMessage(sender, Messages.Config.SYNTAX);
				e.printStackTrace();
			}
		} else {
			return ExecutionStatus.BAD_USAGE;
		}
		return ExecutionStatus.SUCCESS;
	}

}

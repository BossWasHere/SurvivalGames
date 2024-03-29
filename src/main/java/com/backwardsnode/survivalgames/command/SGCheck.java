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
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.message.JsonMessage;
import com.backwardsnode.survivalgames.message.JsonTextEvent;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.world.BlockLocation;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.bukkit.ChatColor;
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
			File target = new File(plugin.getMapFolder(), args[0]);
			try {
				if (!target.isFile()) {
					target = new File(plugin.getMapFolder(), args[0] + ".json");
				}
				if (!target.isFile()) {
					sendMessage(sender, Messages.Plugin.IO_FILE_MISSING, args[0]);
				} else {
					sendMessage(sender, Messages.Plugin.IO_FILE_FOUND, target.getName());
					GameConfigurationWrapper gcw = new GameConfigurationWrapper(target, true);
					sendMessage(sender, Messages.Plugin.IO_FILE_LOADED);
					Set<BlockLocation> invalidLocations = gcw.checkChests();
					if (invalidLocations.size() > 0) {
						StringBuilder builder = new StringBuilder();
						builder.append("At: ");
						int i = 0;
						for (BlockLocation location : invalidLocations) {
							builder.append("[").append(location.toString()).append("], ");
							if (i++ % 3 == 0) {
								builder.append('\n');
							}
							if (i > 9) {
								builder.append(" + ").append(invalidLocations.size() - 10).append(" more  ");
								break;
							}
						}

						if (sender instanceof Player player) {
							JsonMessage msg = new JsonMessage().setColor(ChatColor.RED)
							.setText(plugin.getMessageProvider().compileMessage(Messages.Config.CHEST_MISSING, player.getLocale(), invalidLocations.size(), gcw.getChests().size()))
							.setHoverEvent(JsonTextEvent.showText(builder.substring(0, builder.length() - 2)));
							Utils.sendJsonMessage(player, msg);
						} else {
							sendMessage(sender, Messages.Config.CHEST_MISSING_CONSOLE, invalidLocations.size(), gcw.getChests().size(), builder.substring(0, builder.length() - 2));
						}
					} else {
						sendMessage(sender, Messages.Config.CHEST_LOCATED, gcw.getChests().size());
					}
				}
			} catch (GameConfigurationException unused) {
				sendMessage(sender, Messages.Config.OUTDATED);
			} catch (FileNotFoundException | JsonIOException e) {
				sendMessage(sender, Messages.Plugin.IO_EXCEPTION);
				e.printStackTrace();
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

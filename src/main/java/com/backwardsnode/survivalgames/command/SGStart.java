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
import com.backwardsnode.survivalgames.game.PlayerSelectionMethod;
import com.backwardsnode.survivalgames.message.JsonMessage;
import com.backwardsnode.survivalgames.message.JsonTextEvent;
import com.backwardsnode.survivalgames.message.Messages;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

public class SGStart extends BaseCommand {

	public SGStart(Plugin plugin) {
		super(plugin, CommandType.SG_START);
	}

	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (args.length > 0) {
			boolean canStartAll = player.hasPermission(TYPE.getBasicPermission() + ".all");
			if (args.length > 1) {
				args[1] = args[1].toLowerCase();
				if (args[1].contentEquals("all")) {
					if (canStartAll) {
						start(player, args[0], Bukkit.getServer().getOnlinePlayers());
					} else {
						return ExecutionStatus.NO_PERMISSION;
					}
				} else if (args[1].contentEquals("invite")) {
					startInvite(player, args[0]);
				} else {
					return ExecutionStatus.BAD_USAGE;
				}
			} else {
				if (canStartAll) {
					start(player, args[0], Bukkit.getServer().getOnlinePlayers());
				} else {
					startInvite(player, args[0]);
				}
			}
		} else {
			return ExecutionStatus.BAD_USAGE;
		}
		return ExecutionStatus.SUCCESS;
	}
	
	private void start(Player initiator, String mapName, Collection<? extends Player> players) {
		GameConfigurationWrapper config = tryLoad(initiator, mapName);
		
		if (config != null) {
			if (PLUGIN.getHost().isMapInUse(config.getFileName())) {
				sendMessage(initiator, Messages.Game.MAP_IN_USE, mapName);
				return;
			}
			PLUGIN.getHost().getGameManager().startGame(config, PLUGIN.getCacheSettings(), initiator, players, true, PlayerSelectionMethod.SHUFFLED_WITH_SPECTATORS);
		}
	}
	
	private void startInvite(Player initiator, String mapName) {
		GameConfigurationWrapper config = tryLoad(initiator, mapName);
		
		if (config != null) {
			if (PLUGIN.getHost().isMapInUse(config.getFileName())) {
				sendMessage(initiator, Messages.Game.MAP_IN_USE, mapName);
				return;
			}

			String indexName = config.getMapName().replace(' ', '-');
			if (PLUGIN.getHost().mapHasPendingInvitation(indexName)) {
				sendMessage(initiator, Messages.Command.SGStart.EXISTING_INVITE);
				return;
			}
			String pName = initiator.getDisplayName();
			// TODO cleanup
			JsonMessage message = new JsonMessage().setText("[Click here to join]").setItalic(true).setColor(ChatColor.GOLD).setClickEvent(JsonTextEvent.runCommand("/sgj " + indexName));
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission(CommandType.SG_JOIN.getBasicPermission())) {
					sendMessage(p, Messages.Game.INVITE_OPEN, pName, config.getMapName());
					Utils.sendJsonMessage(p, message);
					sendMessage(p, Messages.Game.INVITE_DURATION);
				}
			}
			PLUGIN.getHost().addInvitation(initiator, config, indexName);
		}
	}
	
	private GameConfigurationWrapper tryLoad(Player player, String mapName) {
		try {
			File target = new File(PLUGIN.getMapFolder(), mapName);
			if (!target.isFile()) {
				target = new File(PLUGIN.getMapFolder(), mapName + ".json");
			}
			if (!target.isFile()) {
				sendMessage(player, Messages.Plugin.IO_FILE_MISSING, mapName);
				return null;
			}
			return new GameConfigurationWrapper(target, true);
			
		} catch (FileNotFoundException e) {
			sendMessage(player, Messages.Plugin.IO_EXCEPTION);
			e.printStackTrace();
		} catch (GameConfigurationException e) {
			sendMessage(player, Messages.Config.OUTDATED);
		} catch (JsonIOException | JsonSyntaxException e) {
			sendMessage(player, Messages.Config.SYNTAX, mapName);
			e.printStackTrace();
		}
		return null;
	}
}

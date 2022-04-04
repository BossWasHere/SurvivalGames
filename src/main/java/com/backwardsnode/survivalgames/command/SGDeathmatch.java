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
import com.backwardsnode.survivalgames.command.base.BaseCommand;
import com.backwardsnode.survivalgames.command.base.CommandType;
import com.backwardsnode.survivalgames.command.base.ExecutionStatus;
import com.backwardsnode.survivalgames.game.GameInstance;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SGDeathmatch extends BaseCommand {
	
	public SGDeathmatch(Plugin plugin) {
		super(plugin, CommandType.SG_DEATHMATCH);
	}

	// TODO prettify
	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		if (plugin.getHost().getGameManager() == null) {
			return ExecutionStatus.SUCCESS;
		}

		if (args.length > 0) {
			// when we provide the name of the map
			GameInstance i = plugin.getHost().getGameManager().getGameByMap(args[0]);
			if (i != null) {
				startDeathmatch(sender, i);
			} else {
				return ExecutionStatus.BAD_USAGE;
			}
		} else {
			// when we don't
			if (sender instanceof Player player) {
				GameInstance i = plugin.getHost().getGameManager().getGame(player);
				if (i != null) {
					startDeathmatch(player, i);
				} else {
					sendMessage(sender, Messages.Game.NOT_PLAYING);
				}
			} else {
				return ExecutionStatus.NOT_PLAYER;
			}
		}
		return ExecutionStatus.SUCCESS;
	}


	// TODO why is this here and not somewhere else
	private void startDeathmatch(CommandSender sender, GameInstance i) {
		if (!i.isActive()) {
			sendMessage(sender, Messages.Game.NOT_RUNNING);
			return;
		}
		if (i.hasDeathmatchStarted()) {
			sendMessage(sender, Messages.Command.SGDeathmatch.ALREADY_STARTED);
		} else {
			i.forceDeathmatch();
		}
	}
}

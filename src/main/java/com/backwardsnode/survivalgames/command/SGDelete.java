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
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.command.CommandSender;

import java.io.File;

public class SGDelete extends BaseCommand {

	public SGDelete(Plugin plugin) {
		super(plugin, CommandType.SG_DELETE);
	}

	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		if (args.length > 0) {
			try {
				File target = new File(plugin.getMapFolder(), args[0]);
				if (!target.isFile()) {
					target = new File(plugin.getMapFolder(), args[0] + ".json");
				}
				if (target.isFile() && target.delete()) {
					sendMessage(sender, Messages.Command.SGDelete.SUCCESS, target.getName());
				} else {
					sendMessage(sender, Messages.Plugin.IO_FILE_MISSING, args[0]);
				}
			} catch (Exception e) {
				sendMessage(sender, Messages.Plugin.IO_EXCEPTION);
			}
			return ExecutionStatus.SUCCESS;
		}
		return ExecutionStatus.BAD_USAGE;
	}

}

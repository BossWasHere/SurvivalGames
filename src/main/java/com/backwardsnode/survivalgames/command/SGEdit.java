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
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SGEdit extends BaseCommand {

	public SGEdit(Plugin plugin) {
		super(plugin, CommandType.SG_EDIT);
	}

	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (args.length > 0) {
			try {
				if (PLUGIN.getHost().getEditorManager().closeEditor(player, true)) {
					sendMessage(sender, Messages.COMMAND.SG_EDIT.CLOSING);
				} else {
					File target;
					if (args[0].toLowerCase().endsWith(".json")) {
						target = new File(PLUGIN.getMapFolder(), args[0]);
					} else {
						target = new File(PLUGIN.getMapFolder(), args[0] + ".json");
					}
					GameConfiguration gc;
					if (!target.exists()) {
						target.createNewFile();
						gc = GameConfiguration.createEmptyConfiguration(target);
						sendMessage(sender, Messages.COMMAND.SG_EDIT.CREATED);
					} else {
						gc = GameConfiguration.loadGameConfiguration(target);
						sendMessage(sender, Messages.COMMAND.SG_EDIT.LOADED, target.getName());
					}

					if (gc == null) {
						sendMessage(sender, Messages.CONFIG.OUTDATED);
					} else {
						PLUGIN.getHost().getEditorManager().addEditor(player, gc);
						sendMessage(sender, Messages.COMMAND.SG_EDIT.OPENING);
					}
				}
			} catch (IOException e) {
				sendMessage(sender, Messages.PLUGIN.IO_EXCEPTION);
			}
		} else {
			if (PLUGIN.getHost().getEditorManager().isEditor(player)) {
				PLUGIN.getHost().getEditorManager().closeEditor(player, true);
				sendMessage(sender, Messages.COMMAND.SG_EDIT.CLOSING);
			} else {
				return ExecutionStatus.BAD_USAGE;
			}
		}
		return ExecutionStatus.SUCCESS;
	}

}

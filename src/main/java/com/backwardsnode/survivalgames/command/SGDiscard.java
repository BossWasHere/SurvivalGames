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
import org.bukkit.entity.Player;

public class SGDiscard extends BaseCommand {

	public SGDiscard(Plugin plugin) {
		super(plugin, CommandType.SG_DISCARD);
	}

	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		Player player = (Player) sender;

		if (plugin.getHost().getEditorManager().isEditor(player)) {
			plugin.getHost().getEditorManager().closeEditor(player, false);
			sendMessage(sender, Messages.Command.SGDiscard.DISCARDED);
		} else {
			sendMessage(sender, Messages.Command.SGEdit.NOT_EDITING);
		}

		return ExecutionStatus.SUCCESS;
	}

}

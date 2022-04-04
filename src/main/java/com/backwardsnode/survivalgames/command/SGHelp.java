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
import com.backwardsnode.survivalgames.message.MessageProvider;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SGHelp extends BaseCommand {

    public SGHelp(Plugin plugin) {
        super(plugin, CommandType.SG_HELP);
    }

    @Override
    public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
        sendMessage(sender, Messages.Command.SGHelp.HELP_BANNER);

        MessageProvider mp = plugin.getMessageProvider();
        if (sender instanceof Player player) {
            String locale = player.getLocale();
            for (BaseCommand bc : plugin.getCommandRegistry().getCommands()) {
                if (sender.hasPermission(bc.getType().getBasicPermission())) {
                    sender.sendMessage(ChatColor.GRAY + "/" + bc.getLabel() + " - " + ChatColor.DARK_AQUA + mp.compileMessage(bc.getType().getDescriptionMessage(), locale));
                }
            }
        } else {
            for (BaseCommand bc : plugin.getCommandRegistry().getCommands()) {
                sender.sendMessage(ChatColor.GRAY + bc.getLabel() + " - " + ChatColor.DARK_AQUA + mp.compileDefaultMessage(bc.getType().getDescriptionMessage()));
            }
        }


        return ExecutionStatus.SUCCESS;
    }
}


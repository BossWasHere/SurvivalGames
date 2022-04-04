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

public class SGReloadLang extends BaseCommand {

    public SGReloadLang(Plugin plugin) {
        super(plugin, CommandType.SG_RELOAD_LANG);
    }

    @Override
    public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
        if (args.length > 0) {
            boolean success = true;
            if (args[0].contentEquals("*")) {
                File[] files = plugin.getLanguageFolder().listFiles();
                if (files != null) {
                    for (File file : files) {
                        String name = file.getName();
                        if (!name.endsWith(".yml")) {
                            continue;
                        }
                        plugin.getMessageProvider().reExtractLanguage(name.substring(0, name.length() - 4));
                    }
                }
            } else {
                success = plugin.getMessageProvider().reExtractLanguage(args[0]);
            }

            sendMessage(sender, success ? Messages.Command.SGReloadLang.SUCCESS : Messages.Command.SGReloadLang.NO_SUCH_LANG);
            return ExecutionStatus.SUCCESS;
        }
        return ExecutionStatus.BAD_USAGE;
    }
}

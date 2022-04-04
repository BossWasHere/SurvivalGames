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

package com.backwardsnode.survivalgames.command.base;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.message.PluginMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class BaseCommand extends Command {

    protected final Plugin plugin;
    protected final CommandType type;

    public BaseCommand(Plugin plugin, CommandType type) {
        super(type.getCommand(), type.getDefaultDescription(plugin), type.getDefaultUsage(plugin), type.getAliases());
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        ExecutionStatus currentStatus = type.canExecute(sender);

        if (currentStatus == ExecutionStatus.SUCCESS) {
            currentStatus = executeDelegate(sender, args);
        }

        sendStatusMessage(sender, currentStatus);
        return true;
    }

    protected final void sendMessage(CommandSender sender, PluginMessage message, Object... formatArgs) {
        plugin.getMessageProvider().sendMessage(sender, message, formatArgs);
    }

    private void sendStatusMessage(CommandSender sender, ExecutionStatus status) {
        switch (status) {
            case BAD_USAGE -> plugin.getMessageProvider().sendMessage(sender, type.getUsageMessage());
            case NO_PERMISSION -> plugin.getMessageProvider().sendMessage(sender, Messages.Command.NO_PERM);
            case NOT_PLAYER -> plugin.getMessageProvider().sendMessage(sender, Messages.Command.ONLY_PLAYERS);
            case NOT_CONSOLE -> plugin.getMessageProvider().sendMessage(sender, Messages.Command.ONLY_CONSOLE);
        }
    }

    public CommandType getType() {
        return type;
    }

    public abstract ExecutionStatus executeDelegate(CommandSender sender, String[] args);

}

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

import com.backwardsnode.survivalgames.CommandRegistry;
import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.message.PluginMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public enum CommandType {

    SG_AUTO("sgauto", "SG Auto Start", Messages.Command.SGAuto.DESC, Messages.Command.SGAuto.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sga"),
    SG_CANCEL("sgcancel", "SG Cancel", Messages.Command.SGCancel.DESC, Messages.Command.SGCancel.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION, "sgleave", "sgc"),
    SG_CHECK("sgcheck", "SG Check Config", Messages.Command.SGCheck.DESC, Messages.Command.SGCheck.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "checksurvivalconfig", "checksc"),
    SG_DEATHMATCH("sgdeathmatch", "SG Deathmatch", Messages.Command.SGDeathmatch.DESC, Messages.Command.SGDeathmatch.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sgdeath", "sgdm"),
    SG_DELETE("sgdelete", "SG Delete", Messages.Command.SGDelete.DESC, Messages.Command.SGDelete.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sgd"),
    SG_EDIT("sgedit", "SG Edit", Messages.Command.SGEdit.DESC, Messages.Command.SGEdit.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION, "survivalgamesedit", "sge"),
    SG_DISCARD("sgdiscard", "SG Discard Edit", Messages.Command.SGDiscard.DESC, Messages.Command.SGDiscard.USAGE, SG_EDIT.getBasicPermission(), ExecutionCheck.IS_PLAYER_WITH_PERMISSION, "sgdisc"),
    SG_HELP("sghelp", "SG Help", Messages.Command.SGHelp.DESC, Messages.Command.SGHelp.USAGE, ExecutionCheck.ALWAYS, "sgh"),
    SG_JOIN("sgjoin", "SG Join", Messages.Command.SGJoin.DESC, Messages.Command.SGJoin.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION, "sgj"),
    SG_LIST("sglist", "SG List", Messages.Command.SGList.DESC, Messages.Command.SGList.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sgl"),
    SG_MIGRATE("sgmigrate", "SG Migrate", Messages.Command.SGMigrate.DESC, Messages.Command.SGMigrate.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sgm", "sgconvert"),
    SG_RELOAD_LANG("sgreloadlang", "SG Reload Language", Messages.Command.SGReloadLang.DESC, Messages.Command.SGReloadLang.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sgrl"),
    SG_START("sgstart", "SG Start", Messages.Command.SGStart.DESC, Messages.Command.SGStart.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION, "sgs"),
    SG_STOP("sgstop", "SG Stop", Messages.Command.SGStop.DESC, Messages.Command.SGStop.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION_OR_CONSOLE, "sgend"),
    SG_VOTE("sgvote", "SG Vote", Messages.Command.SGVote.DESC, Messages.Command.SGVote.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION, "sgv"),

    TEST_LOOT_DROP("testlootdrop", "Test Loot Drop", Messages.Command.TestCommand.DESC, Messages.Command.TestCommand.USAGE, ExecutionCheck.IS_PLAYER_WITH_PERMISSION);

    private final String command;
    private final String friendlyName;
    private final String[] aliases;
    private final String permission;
    private final ExecutionCheck check;
    private final PluginMessage descriptionMessage;
    private final PluginMessage usageMessage;

    CommandType(String command, String friendlyName, PluginMessage descriptionMessage, PluginMessage usageMessage, ExecutionCheck check, String... aliases) {
        this(command, friendlyName, descriptionMessage, usageMessage, null, check, aliases);
    }

    CommandType(String command, String friendlyName, PluginMessage descriptionMessage, PluginMessage usageMessage, String overridePermission, ExecutionCheck check, String... aliases) {
        this.command = command;
        this.friendlyName = friendlyName;
        this.descriptionMessage = descriptionMessage;
        this.usageMessage = usageMessage;
        permission = overridePermission == null ? CommandRegistry.NAMESPACE + "." + command : overridePermission;
        this.check = check;
        this.aliases = aliases;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getAliases() {
        return Arrays.asList(aliases);
    }

    public String getBasicPermission() {
        return permission;
    }

    public PluginMessage getDescriptionMessage() {
        return descriptionMessage;
    }

    public PluginMessage getUsageMessage() {
        return usageMessage;
    }

    public String getDefaultDescription(Plugin plugin) {
        return plugin.getMessageProvider().compileDefaultMessage(descriptionMessage);
    }

    public String getDefaultUsage(Plugin plugin) {
        return plugin.getMessageProvider().compileDefaultMessage(usageMessage);
    }

    public ExecutionStatus canExecute(CommandSender sender) {
        switch (check) {
            case ALWAYS:
                return ExecutionStatus.SUCCESS;
            case IS_PLAYER_WITH_PERMISSION:
                if (!sender.hasPermission(getBasicPermission())) return ExecutionStatus.NO_PERMISSION;
            case IS_PLAYER:
                return sender instanceof Player ? ExecutionStatus.SUCCESS : ExecutionStatus.NOT_PLAYER;
            case IS_CONSOLE:
                return sender instanceof ConsoleCommandSender ? ExecutionStatus.SUCCESS : ExecutionStatus.NOT_CONSOLE;
            case IS_PLAYER_WITH_PERMISSION_OR_CONSOLE:
                return sender.hasPermission(getBasicPermission()) ? ExecutionStatus.SUCCESS : ExecutionStatus.NO_PERMISSION;
            default:
                return ExecutionStatus.NO_PERMISSION;
        }
    }

    @Override
    public String toString() {
        return friendlyName;
    }
}

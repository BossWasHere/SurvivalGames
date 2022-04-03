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
package com.backwardsnode.survivalgames;

import com.backwardsnode.survivalgames.command.*;
import com.backwardsnode.survivalgames.command.base.BaseCommand;
import com.backwardsnode.survivalgames.command.debug.TestLootDrop;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public final class CommandRegistry {

    public static final String NAMESPACE = "survivalgames";

    private final Plugin PLUGIN;
    private final BaseCommand[] COMMANDS;

    private final BaseCommand TEST_LOOT_DROP_COMMAND;

    private boolean registered = false;

    public CommandRegistry(Plugin plugin) {
        PLUGIN = plugin;

        COMMANDS = new BaseCommand[]{
                new SGAuto(plugin),
                new SGCancel(plugin),
                new SGCheck(plugin),
                new SGDeathmatch(plugin),
                new SGDelete(plugin),
                new SGDiscard(plugin),
                new SGEdit(plugin),
                new SGHelp(plugin),
                new SGJoin(plugin),
                new SGList(plugin),
                new SGMigrate(plugin),
                new SGReloadLang(plugin),
                new SGStart(plugin),
                new SGStop(plugin),
                new SGVote(plugin)
        };

        TEST_LOOT_DROP_COMMAND = new TestLootDrop(plugin);
    }

    public void registerCommands() {
        if (registered) return;
        registered = true;

        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer().getPluginManager());

            for (BaseCommand baseCommand : COMMANDS) {
                commandMap.register(baseCommand.getName(), NAMESPACE, baseCommand);
            }

            if (Plugin.TEST) {
                commandMap.register(TEST_LOOT_DROP_COMMAND.getName(), NAMESPACE, TEST_LOOT_DROP_COMMAND);
            }

        } catch (IllegalAccessException | NoSuchFieldException e) {
            PLUGIN.getLogger().severe("An error occured while building the CommandMap for " + PLUGIN.getName());
            e.printStackTrace();
        }
    }

    public BaseCommand[] getCommands() {
        return COMMANDS;
    }
}

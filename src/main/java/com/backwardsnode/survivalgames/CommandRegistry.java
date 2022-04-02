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

public final class CommandRegistry {

    public static final String NAMESPACE = "survivalgames";

    private final Plugin PLUGIN;

    private boolean registered = false;

    private final BaseCommand SG_AUTO_COMMAND;
    private final BaseCommand SG_CANCEL_COMMAND;
    private final BaseCommand SG_CHECK_COMMAND;
    private final BaseCommand SG_DEATHMATCH_COMMAND;
    private final BaseCommand SG_DELETE_COMMAND;
    private final BaseCommand SG_DISCARD_COMMAND;
    private final BaseCommand SG_EDIT_COMMAND;
    private final BaseCommand SG_JOIN_COMMAND;
    private final BaseCommand SG_LIST_COMMAND;
    private final BaseCommand SG_MIGRATE_COMMAND;
    private final BaseCommand SG_RELOAD_LANG;
    private final BaseCommand SG_START_COMMAND;
    private final BaseCommand SG_STOP_COMMAND;
    private final BaseCommand SG_VOTE_COMMAND;

    private final BaseCommand TEST_LOOT_DROP_COMMAND;

    public CommandRegistry(Plugin plugin) {
        PLUGIN = plugin;

        SG_AUTO_COMMAND = new SGAuto(plugin);
        SG_CANCEL_COMMAND = new SGCancel(plugin);
        SG_CHECK_COMMAND = new SGCheck(plugin);
        SG_DEATHMATCH_COMMAND = new SGDeathmatch(plugin);
        SG_DELETE_COMMAND = new SGDelete(plugin);
        SG_DISCARD_COMMAND = new SGDiscard(plugin);
        SG_EDIT_COMMAND = new SGEdit(plugin);
        SG_JOIN_COMMAND = new SGJoin(plugin);
        SG_LIST_COMMAND = new SGList(plugin);
        SG_MIGRATE_COMMAND = new SGMigrate(plugin);
        SG_RELOAD_LANG = new SGReloadLang(plugin);
        SG_START_COMMAND = new SGStart(plugin);
        SG_STOP_COMMAND = new SGStop(plugin);
        SG_VOTE_COMMAND = new SGVote(plugin);

        TEST_LOOT_DROP_COMMAND = new TestLootDrop(plugin);
    }

    public void registerCommands() {
        if (registered) return;
        registered = true;

        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer().getPluginManager());

            commandMap.register(SG_AUTO_COMMAND.getName(), NAMESPACE, SG_AUTO_COMMAND);
            commandMap.register(SG_CANCEL_COMMAND.getName(), NAMESPACE, SG_CANCEL_COMMAND);
            commandMap.register(SG_CHECK_COMMAND.getName(), NAMESPACE, SG_CHECK_COMMAND);
            commandMap.register(SG_DEATHMATCH_COMMAND.getName(), NAMESPACE, SG_DEATHMATCH_COMMAND);
            commandMap.register(SG_DELETE_COMMAND.getName(), NAMESPACE, SG_DELETE_COMMAND);
            commandMap.register(SG_DISCARD_COMMAND.getName(), NAMESPACE, SG_DISCARD_COMMAND);
            commandMap.register(SG_EDIT_COMMAND.getName(), NAMESPACE, SG_EDIT_COMMAND);
            commandMap.register(SG_JOIN_COMMAND.getName(), NAMESPACE, SG_JOIN_COMMAND);
            commandMap.register(SG_LIST_COMMAND.getName(), NAMESPACE, SG_LIST_COMMAND);
            commandMap.register(SG_MIGRATE_COMMAND.getName(), NAMESPACE, SG_MIGRATE_COMMAND);
            commandMap.register(SG_RELOAD_LANG.getName(), NAMESPACE, SG_RELOAD_LANG);
            commandMap.register(SG_START_COMMAND.getName(), NAMESPACE, SG_START_COMMAND);
            commandMap.register(SG_STOP_COMMAND.getName(), NAMESPACE, SG_STOP_COMMAND);
            commandMap.register(SG_VOTE_COMMAND.getName(), NAMESPACE, SG_VOTE_COMMAND);

            if (Plugin.TEST) {
                commandMap.register(TEST_LOOT_DROP_COMMAND.getName(), NAMESPACE, TEST_LOOT_DROP_COMMAND);
            }

        } catch (IllegalAccessException | NoSuchFieldException e) {
            PLUGIN.getLogger().severe("An error occured while building the CommandMap for " + PLUGIN.getName());
            e.printStackTrace();
        }
    }
}

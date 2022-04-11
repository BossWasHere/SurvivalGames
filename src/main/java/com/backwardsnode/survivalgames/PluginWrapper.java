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

import com.backwardsnode.survivalgames.dependency.DependencyManager;
import com.backwardsnode.survivalgames.game.PlayerCacheSettings;
import com.backwardsnode.survivalgames.message.MessageProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;

public class PluginWrapper implements PluginAccess {

    protected final Plugin plugin;

    public PluginWrapper(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public File getBackupFolder() {
        return plugin.getBackupFolder();
    }

    @Override
    public File getLanguageFolder() {
        return plugin.getLanguageFolder();
    }

    @Override
    public File getLibraryFolder() {
        return plugin.getLibraryFolder();
    }

    @Override
    public File getMapFolder() {
        return plugin.getMapFolder();
    }

    @Override
    public CommandRegistry getCommandRegistry() {
        return plugin.getCommandRegistry();
    }

    @Override
    public PluginListener getDefaultListener() {
        return plugin.getDefaultListener();
    }

    @Override
    public DependencyManager getDependencyManager() {
        return plugin.getDependencyManager();
    }

    @Override
    public MessageProvider getMessageProvider() {
        return plugin.getMessageProvider();
    }

    @Override
    public PlayerCacheSettings getCacheSettings() {
        return plugin.getCacheSettings();
    }

    @Override
    public SGHost getHost() {
        return plugin.getHost();
    }

    @Override
    public boolean extractFileSafe(Path destination, String internalPath, String errorMsg) {
        return plugin.extractFileSafe(destination, internalPath, errorMsg);
    }

    @Override
    public void extractFile(Path destination, String internalPath, CopyOption... copyOptions) throws IOException {
        plugin.extractFile(destination, internalPath, copyOptions);
    }
}

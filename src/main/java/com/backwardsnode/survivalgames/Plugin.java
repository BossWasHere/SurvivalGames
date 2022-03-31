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

import com.backwardsnode.survivalgames.game.PlayerCacheSettings;
import com.backwardsnode.survivalgames.message.MessageProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public final class Plugin extends JavaPlugin {
	
	public static boolean TEST = false;

	private CommandRegistry commandRegistry;
	private DependencyManager dependencyManager;
	private MessageProvider messageProvider;
	private PlayerCacheSettings cacheSettings;
	private PluginListener pluginListener;
	private SGHost sgHost;
	
	@Override
	public void onLoad() {

		// Pre init
		getLanguageFolder().mkdirs();
		getMapFolder().mkdir();
		getBackupFolder().mkdir();

		saveDefaultConfig();

		String language = getConfig().getString("default-language", MessageProvider.DEFAULT_LOCALE);
		messageProvider = new MessageProvider(this, language, true);

		// Extract example.json
		File exampleFile = new File(getMapFolder(), "example.json");
		if (!exampleFile.exists()) {
			extractFileSafe(exampleFile.toPath(), "example.json", "Could not copy example.json - this could indicate an OS permission error!");
		}

		dependencyManager = new DependencyManager(this);
		cacheSettings = new PlayerCacheSettings();
	}
	
	@Override
	public void onEnable() {
		getLogger().info("BackwardsNode's Survival Games (C) 2019-2022 BossWasHere/BackwardsNode | Version: " + getDescription().getVersion());

		dependencyManager.connect();

		pluginListener = new PluginListener(this);
		sgHost = new SGHost(this);


		// TODO help command
		if (commandRegistry == null) {
			commandRegistry = new CommandRegistry(this);
		}
		commandRegistry.registerCommands();
	}

	@Override
	public void onDisable() {
		sgHost.close();

		pluginListener.reset();

		dependencyManager.disconnect();
	}

	public File getBackupFolder() {
		return new File(getDataFolder(), "backup/");
	}

	public File getLanguageFolder() {
		return new File(getDataFolder(), "lang/");
	}

	public File getMapFolder() {
		return new File(getDataFolder(), "maps/");
	}

	public PluginListener getDefaultListener() {
		return pluginListener;
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	public MessageProvider getMessageProvider() {
		return messageProvider;
	}

	public PlayerCacheSettings getCacheSettings() {
		return cacheSettings;
	}

	public SGHost getHost() {
		return sgHost;
	}

	public boolean extractFileSafe(Path destination, String internalPath, String errorMsg) {
		try {
			extractFile(destination, internalPath);
			return true;
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, errorMsg, e);
		}
		return false;
	}

	public void extractFile(Path destination, String internalPath, CopyOption... copyOptions) throws IOException {
		InputStream is = this.getClass().getResourceAsStream("/" + internalPath);
		Files.copy(is, destination, copyOptions);
	}
}

/*
 *  BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 *  Copyright (C) 2019 BackwardsNode/BossWasHere
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames;

import java.lang.reflect.Field;

import org.bukkit.command.CommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.backwardsnode.survivalgames.command.CheckConfig;
import com.backwardsnode.survivalgames.command.SGDeathmatch;
import com.backwardsnode.survivalgames.command.SGDelete;
import com.backwardsnode.survivalgames.command.SGDiscard;
import com.backwardsnode.survivalgames.command.SGEdit;
import com.backwardsnode.survivalgames.command.SGStart;
import com.backwardsnode.survivalgames.command.SGStop;
import com.backwardsnode.survivalgames.editor.EditorManager;
import com.backwardsnode.survivalgames.game.GameManager;

public class Plugin extends JavaPlugin {
	
	public EditorManager editorManager;
	public GameManager gameManager;
	
	private static final String NAMESPACE_KEY_ID = "survivalgames";
	
	public Plugin() {
		VersionGetter.init();
	}
	
	@Override
	public void onLoad() {
		getDataFolder().mkdirs();
	}
	
	@Override
	public void onEnable() {
		getLogger().info("BackwardsNode's Survival Games Copyright (C) 2019 BossWasHere/BackwardsNode | Version: " + getDescription().getVersion());
		editorManager = new EditorManager(this);
		
		CommandMap commandMap = null;
		try {
			Field field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			commandMap = (CommandMap)(field.get(getServer().getPluginManager()));
			commandMap.register(NAMESPACE_KEY_ID, new CheckConfig(this));
			commandMap.register(NAMESPACE_KEY_ID, new SGEdit(this));
			commandMap.register(NAMESPACE_KEY_ID, new SGStart(this));
			commandMap.register(NAMESPACE_KEY_ID, new SGStop(this));
			commandMap.register(NAMESPACE_KEY_ID, new SGDeathmatch(this));
			commandMap.register(NAMESPACE_KEY_ID, new SGDelete(this));
			commandMap.register(NAMESPACE_KEY_ID, new SGDiscard(this));

		} catch (NoSuchFieldException e) {
			getLogger().severe("[EC] An error occured while building the new commandmap");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			getLogger().severe("[EC] An error occured while building the new commandmap");
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		if (gameManager != null) {
			gameManager.terminate(true);
		}
		editorManager.closeAllEditors();
	}
}

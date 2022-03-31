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

import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameTest {

	@BeforeAll
	static void initAll() {
		Plugin.TEST = true;
	}
	
	@Test
	void testConfigurations() {
		assertThrows(NullPointerException.class, () -> {
			GameConfiguration.loadGameConfiguration(null);
		});
		assertDoesNotThrow(() -> {
			GameConfiguration gc = GameConfiguration.loadGameConfiguration(getResourceByName("full.json"));
			fakeBuild(gc);
		});
		assertDoesNotThrow(() -> {
			GameConfiguration gc = GameConfiguration.loadGameConfiguration(getResourceByName("minimal.json"));
			fakeBuild(gc);
		});
		assertThrows(JsonSyntaxException.class, () -> {
			GameConfiguration gc = GameConfiguration.loadGameConfiguration(getResourceByName("bad_syntax.json"));
			fakeBuild(gc);
		});
		assertThrows(GameConfigurationException.class, () -> {
			GameConfiguration gc = GameConfiguration.loadGameConfiguration(getResourceByName("missing.json"));
			fakeBuild(gc);
		});
	}
	
	void fakeBuild(GameConfiguration gc) {
		if (gc.chestLocations == null || gc.itemSets == null || gc.strSpawns == null) {
			throw new GameConfigurationException("Missing configuration entries");
		}
		for (int i = 0; i < gc.itemSets.size(); i++) {
			if (gc.itemSets.get(i).isDefault) {
				gc.defaultSetIndex = i;
				break;
			}
		}
	}
	
	static File getResourceByName(String resourceName) {
		return new File(TestSettings.RESOURCES, resourceName);
	}
}

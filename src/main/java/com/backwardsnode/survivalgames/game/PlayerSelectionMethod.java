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
package com.backwardsnode.survivalgames.game;

public enum PlayerSelectionMethod {

	ARBITRARY,
	ARBITRARY_WITH_SPECTATORS,
	SHUFFLED,
	SHUFFLED_WITH_SPECTATORS;

	public boolean allowSpectators() {
		return this == ARBITRARY_WITH_SPECTATORS || this == SHUFFLED_WITH_SPECTATORS;
	}

	public boolean shouldShuffle() {
		return this == SHUFFLED || this == SHUFFLED_WITH_SPECTATORS;
	}
}

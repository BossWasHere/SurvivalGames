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

public enum GameStatus {

	WAITING,
	START_ERR_MAP_IN_USE,
	START_ERR_PLAYER_IN_GAME,
	START_ERR_FEW_PLAYERS,
	START_SUCCESS_WITH_SPECTATORS,
	START_SUCCESS_WITHOUT_SPECTATORS,
	START_SUCCESS,
	
	RELEASE_PLAYERS,
	ENABLE_PVP,
	SHRINK_PLAY_AREA,
	START_DEATHMATCH,
	CLOSE_PLAY_AREA,
	FINISH_GAME
}

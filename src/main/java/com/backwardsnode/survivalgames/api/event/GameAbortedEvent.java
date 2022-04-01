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

package com.backwardsnode.survivalgames.api.event;

import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.game.GameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nullable;

/**
 * Event called when a game is about to start but was aborted instead
 */
public class GameAbortedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameStatus REASON;
    private final GameConfiguration GAME_CONFIGURATION;
    private final Player INITIATOR;

    public GameAbortedEvent(GameStatus reason, GameConfiguration gameConfiguration, Player initiator) {
        REASON = reason;
        GAME_CONFIGURATION = gameConfiguration;
        INITIATOR = initiator;
    }

    /**
     * Gets the reason for the abort
     * @return The {@link GameStatus} reason
     */
    public GameStatus getReason() {
        return REASON;
    }

    /**
     * Gets the configuration of the aborted game
     * @return The {@link GameConfiguration}
     */
    public GameConfiguration getGameConfiguration() {
        return GAME_CONFIGURATION;
    }

    /**
     * Gets the player who originally tried to start the game, or null if started by the server
     * @return The {@link Player}
     */
    @Nullable
    public Player getInitiator() {
        return INITIATOR;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}

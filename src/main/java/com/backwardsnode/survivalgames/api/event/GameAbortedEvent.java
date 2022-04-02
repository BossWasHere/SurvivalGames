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

import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.game.GameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event called when a game is about to start but was aborted instead
 */
public class GameAbortedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameStatus REASON;
    private final GameConfigurationWrapper GAME_CONFIGURATION;
    private final Player INITIATOR;

    public GameAbortedEvent(@NotNull GameStatus reason, @NotNull GameConfigurationWrapper gameConfiguration, @Nullable Player initiator) {
        REASON = reason;
        GAME_CONFIGURATION = gameConfiguration;
        INITIATOR = initiator;
    }

    /**
     * Gets the reason for the abort
     * @return The {@link GameStatus} reason
     */
    public @NotNull GameStatus getReason() {
        return REASON;
    }

    /**
     * Gets the configuration of the aborted game
     * @return The {@link GameConfigurationWrapper}
     */
    public @NotNull GameConfigurationWrapper getGameConfiguration() {
        return GAME_CONFIGURATION;
    }

    /**
     * Gets the player who originally tried to start the game, or null if started by the server
     * @return The {@link Player}
     */
    public @Nullable Player getInitiator() {
        return INITIATOR;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

}

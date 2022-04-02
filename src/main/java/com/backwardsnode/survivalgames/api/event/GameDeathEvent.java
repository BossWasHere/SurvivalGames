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

import com.backwardsnode.survivalgames.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event called when a player dies in a game
 */
public class GameDeathEvent extends GameEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player WHO_DIED;
    private final Player KILLED_BY;

    public GameDeathEvent(@NotNull GameInstance gameInstance, @NotNull Player whoDied, @Nullable Player killedBy) {
        super(gameInstance);
        WHO_DIED = whoDied;
        KILLED_BY = killedBy;
    }

    /**
     * Gets the player who died
     * @return The {@link Player} who died
     */
    public @NotNull Player getWhoDied() {
        return WHO_DIED;
    }

    /**
     * Gets if the player was killed by another player
     * @return True if was killed
     */
    public boolean wasKilledByPlayer() {
        return KILLED_BY != null;
    }

    /**
     * Gets the player who killed the player who died, or null if wasn't killed
     * @return The {@link Player} who killed
     */
    public @Nullable Player getKiller() {
        return KILLED_BY;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
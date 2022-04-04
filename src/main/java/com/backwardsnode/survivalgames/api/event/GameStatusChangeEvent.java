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
import com.backwardsnode.survivalgames.game.GameStatus;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a game changes its status (phase)
 */
public class GameStatusChangeEvent extends GameEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameStatus status;
    private boolean cancelled = false;

    public GameStatusChangeEvent(@NotNull GameInstance gameInstance, @NotNull GameStatus status) {
        super(gameInstance);
        this.status = status;
    }

    /**
     * Gets the new status of the game
     * @return The {@link GameStatus}
     */
    public @NotNull GameStatus getStatus() {
        return status;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}

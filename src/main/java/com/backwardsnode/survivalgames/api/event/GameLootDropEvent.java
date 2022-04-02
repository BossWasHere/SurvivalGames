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

import com.backwardsnode.survivalgames.config.LootDropConfiguration;
import com.backwardsnode.survivalgames.game.GameInstance;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Event called when a loot drop is about to be dispatched
 */
public class GameLootDropEvent extends GameEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final LootDropConfiguration TO_DROP;
    private final boolean WAS_CALLED_IN;

    private boolean cancelled = false;

    public GameLootDropEvent(GameInstance gameInstance, LootDropConfiguration toDrop, boolean wasCalledIn) {
        super(gameInstance);
        TO_DROP = toDrop;
        WAS_CALLED_IN = wasCalledIn;
    }

    /**
     * Gets the data associated with this current loot drop
     * @return the {@link LootDropConfiguration}
     */
    public LootDropConfiguration getLootDropConfiguration() {
        return TO_DROP;
    }

    /**
     * Gets if this loot drop was called in using an item or not
     * @return True if called in, false if dropped automatically
     */
    public boolean wasCalledIn() {
        return WAS_CALLED_IN;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

}

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

import com.backwardsnode.survivalgames.config.RewardConfiguration;
import com.backwardsnode.survivalgames.game.GameInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event called when a player is about to receive a reward for their placement at the end of a game
 */
public class GameRewardEvent extends GameEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int placement;
    private final RewardConfiguration rewards;

    private boolean cancelled = false;

    public GameRewardEvent(@NotNull GameInstance gameInstance, @NotNull Player player, int placement, @Nullable RewardConfiguration rewards) {
        super(gameInstance);
        this.player = player;
        this.placement = placement;
        this.rewards = rewards;
    }

    /**
     * Gets the player receiving rewards
     * @return The {@link Player}
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * Gets the player's placement position (1st, 2nd, ...)
     * @return The placement position
     */
    public int getPlacement() {
        return placement;
    }

    /**
     * Gets the rewards to be given to this player.
     * @return The {@link RewardConfiguration}
     * @apiNote this object will respect changes to rewards
     */
    public @Nullable RewardConfiguration getRewards() {
        return rewards;
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

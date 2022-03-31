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

import com.backwardsnode.survivalgames.game.GameStatus;
import com.backwardsnode.survivalgames.game.InvitationCancelType;
import com.backwardsnode.survivalgames.game.InvitedGameConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when an invitation is left or cancelled
 */
public class GameInvitationCancelledEvent extends GameInvitationEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player PLAYER;
    private final InvitationCancelType CANCEL_TYPE;

    public GameInvitationCancelledEvent(Player player, InvitedGameConfiguration gameConfiguration, InvitationCancelType cancelType) {
        super(gameConfiguration);
        PLAYER = player;
        CANCEL_TYPE = cancelType;
    }

    /**
     * Gets the player who is no longer playing
     * @return The {@link Player}
     */
    public Player getWhoCancelled() {
        return PLAYER;
    }

    /**
     * Gets the reason for leaving the invitation
     * @return The {@link InvitationCancelType}
     */
    public InvitationCancelType getCancelType() {
        return CANCEL_TYPE;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

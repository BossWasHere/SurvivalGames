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

import com.backwardsnode.survivalgames.editor.Scene;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Event called when a player closes an editor session
 */
public class EditorClosedEvent extends EditorEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean saved;

    public EditorClosedEvent(@NotNull Scene scene, boolean saved) {
        super(scene);
        this.saved = saved;
    }

    /**
     * Gets if changes to this scene will be saved to its configuration file
     * @return True if changes are about to be saved
     */
    public boolean getSaved() {
        return saved;
    }

    /**
     * Sets if the file will be saved
     * @param saved true to save the file
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}

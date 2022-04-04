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
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for all events relating to an editor session.
 */
public abstract class EditorEvent extends Event {

    private final Scene scene;

    public EditorEvent(@NotNull Scene scene) {
        this.scene = scene;
    }

    /**
     * Gets the current editor scene associated with this event
     * @return The {@link Scene}
     */
    public @NotNull Scene getScene() {
        return scene;
    }
}

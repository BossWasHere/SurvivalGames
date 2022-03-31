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

package com.backwardsnode.survivalgames.editor;

public class PredicateResult<T, S> {

    public final boolean SUCCESSFUL;
    public final T INPUT;
    public final S OUTPUT;

    public PredicateResult(boolean successful, T input, S output) {
        SUCCESSFUL = successful;
        INPUT = input;
        OUTPUT = output;
    }

    public <A> PredicateResult<T, A> wrap(Class<A> clazz) {
        if (clazz.isInstance(OUTPUT)) {
            return new PredicateResult<>(SUCCESSFUL, INPUT, clazz.cast(OUTPUT));
        }
        return null;
    }

    public <A> A wrapOutput(Class<A> clazz) {
        if (clazz.isInstance(OUTPUT)) {
            return clazz.cast(OUTPUT);
        }
        return null;
    }
}

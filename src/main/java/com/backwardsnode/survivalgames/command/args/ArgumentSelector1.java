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

package com.backwardsnode.survivalgames.command.args;

import com.backwardsnode.survivalgames.command.args.type.IArgumentType;

public class ArgumentSelector1<T> extends BaseArgumentSelector {

    private final T ARG_RESULT_1;

    public ArgumentSelector1(String[] args, IArgumentType<T> argType1) {
        super(args);
        ARG_RESULT_1 = readNextArg(argType1);
    }

    public T getFirstArgument() {
        return ARG_RESULT_1;
    }
}
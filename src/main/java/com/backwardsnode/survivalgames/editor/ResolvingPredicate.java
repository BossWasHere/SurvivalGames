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

public abstract class ResolvingPredicate<S, T> {

    public static final ResolvingPredicate<String, String> STRING_PREDICATE = new ResolvingPredicate<>() {
        protected String convert(String object) {
            return object;
        }
    };

    public static final ResolvingPredicate<String, Boolean> TRUE_PREDICATE = new ResolvingPredicate<>() {
        @Override
        protected Boolean convert(String string) {
            return switch (string.toLowerCase()) {
                case "true", "yes", "1", "accept" -> true;
                case "false", "no", "0", "deny" -> false;
                default -> null;
            };
        }
    };

    public static final ResolvingPredicate<String, Integer> INTEGER_PREDICATE = new ResolvingPredicate<>() {
        @Override
        protected Integer convert(String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException ignored) {
            }
            return null;
        }
    };

    public static final ResolvingPredicate<String, Double> DOUBLE_PREDICATE = new ResolvingPredicate<>() {
        @Override
        protected Double convert(String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException ignored) {
            }
            return null;
        }
    };

    public PredicateResult<S, T> validate(S object) {
        assert object != null;
        T value = convert(object);
        if (value != null) {
            return new PredicateResult<>(true, object, value);
        }
        return new PredicateResult<>(false, object, null);
    }

    protected abstract T convert(S object);
}

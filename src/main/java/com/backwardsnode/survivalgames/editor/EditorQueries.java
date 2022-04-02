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

import com.backwardsnode.survivalgames.message.Messages;
import com.backwardsnode.survivalgames.message.PluginMessage;

public enum EditorQueries {
	NEW_ITEMSET_NAME(Messages.Editor.NAME_ITEM_SET_REQUEST),
	RENAME_ITEMSET_NAME(Messages.Editor.RENAME_ITEM_SET_REQUEST),
	MAP_NAME(Messages.Editor.RENAME_MAP_REQUEST),
	BORDER_DPS(Messages.Editor.BORDER_DPS_REQUEST, ResolvingPredicate.DOUBLE_PREDICATE),
	TIME_TO_SHRINK(Messages.Editor.PRE_SHRINK_DURATION_REQUEST, ResolvingPredicate.INTEGER_PREDICATE),
	BORDER_START_RADIUS(Messages.Editor.BORDER_RADIUS_REQUEST, ResolvingPredicate.DOUBLE_PREDICATE),
	WAIT_PERIOD(Messages.Editor.WAIT_PERIOD_REQUEST, ResolvingPredicate.INTEGER_PREDICATE),
	GRACE_PERIOD(Messages.Editor.PVP_OFF_TIME_REQUEST, ResolvingPredicate.INTEGER_PREDICATE);

	private final PluginMessage message;
	private final ResolvingPredicate<String, ?> predicate;
	
	EditorQueries(PluginMessage message) {
		this.message = message;
		this.predicate = ResolvingPredicate.STRING_PREDICATE;
	}
	
	EditorQueries(PluginMessage message, ResolvingPredicate<String, ?> predicate) {
		this.message = message;
		this.predicate = predicate;
	}

	public PluginMessage getPluginMessage() {
		return message;
	}

	public ResolvingPredicate<String, ?> getPredicate() {
		return predicate;
	}
}
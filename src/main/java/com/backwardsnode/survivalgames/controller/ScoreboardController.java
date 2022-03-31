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
package com.backwardsnode.survivalgames.controller;

import com.backwardsnode.survivalgames.game.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Collection;
import java.util.EnumMap;

public class ScoreboardController implements Controller {

	private final Scoreboard bukkitScoreboard;
	private final Objective bukkitObjective;
	
	private EnumMap<ScoreboardElement, String> lastTags;
	
	public ScoreboardController(String displayName) {
		lastTags = new EnumMap<>(ScoreboardElement.class);
		ScoreboardManager bukkitManager = Bukkit.getScoreboardManager();
		bukkitScoreboard = bukkitManager.getNewScoreboard();
		
		bukkitObjective = bukkitScoreboard.registerNewObjective(hashCode() + "_main", "dummy", displayName);
		bukkitObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void setVisibleTo(Collection<PlayerState> playerStates) {
		playerStates.forEach(ps -> ps.cache.getPlayer().setScoreboard(bukkitScoreboard));
	}
	
	public void setVisibleTo(Player player) {
		player.setScoreboard(bukkitScoreboard);
	}
	
	public void initialize() {
		for (ScoreboardElement e : ScoreboardElement.values()) {
			lastTags.put(e, e.getTag());
			Score score = bukkitObjective.getScore(e.getTag());
			score.setScore(e.getPos());
		}
	}
	
	public void updateScoreboardElement(ScoreboardElement element, String value) {
		if (lastTags.get(element).equals(element.getTag(value))) {
			return;
		}
		bukkitScoreboard.resetScores(lastTags.get(element));
		lastTags.put(element, element.getTag(value));
		Score score = bukkitObjective.getScore(lastTags.get(element));
		score.setScore(element.getPos());
	}
	
	@Override
	public void close() {
		bukkitObjective.unregister();
	}
}

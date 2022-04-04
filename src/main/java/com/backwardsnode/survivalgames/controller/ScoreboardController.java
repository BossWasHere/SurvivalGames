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

	private final Scoreboard scoreboard;
	private final Objective objective;
	
	private final EnumMap<ScoreboardElement, String> lastTags;
	
	public ScoreboardController(String displayName) {
		lastTags = new EnumMap<>(ScoreboardElement.class);
		ScoreboardManager bukkitManager = Bukkit.getScoreboardManager();
		assert bukkitManager != null;
		scoreboard = bukkitManager.getNewScoreboard();
		
		objective = scoreboard.registerNewObjective(hashCode() + "_main", "dummy", displayName);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	@Override
	public void setVisibleTo(Collection<PlayerState> playerStates) {
		playerStates.forEach(ps -> ps.cache.getPlayer().setScoreboard(scoreboard));
	}

	@Override
	public void setVisibleTo(Player player) {
		player.setScoreboard(scoreboard);
	}

	@Override
	public void unsetVisibleTo(Collection<PlayerState> playerStates) {
		playerStates.forEach(ps -> unsetVisibleTo(ps.cache.getPlayer()));
	}

	@Override
	public void unsetVisibleTo(Player player) {
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

		if (scoreboardManager != null) {
			player.setScoreboard(scoreboardManager.getMainScoreboard());
		}
	}
	
	public void initialize() {
		for (ScoreboardElement e : ScoreboardElement.values()) {
			lastTags.put(e, e.getTag());
			Score score = objective.getScore(e.getTag());
			score.setScore(e.getPos());
		}
	}
	
	public void updateScoreboardElement(ScoreboardElement element, String value) {
		if (lastTags.get(element).equals(element.getTag(value))) {
			return;
		}
		scoreboard.resetScores(lastTags.get(element));
		lastTags.put(element, element.getTag(value));
		Score score = objective.getScore(lastTags.get(element));
		score.setScore(element.getPos());
	}
	
	@Override
	public void close() {
		objective.unregister();
	}
}

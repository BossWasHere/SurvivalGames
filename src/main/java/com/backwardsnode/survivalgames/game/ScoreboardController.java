/*
 *  BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 *  Copyright (C) 2019 BackwardsNode/BossWasHere
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames.game;

import java.util.EnumMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.backwardsnode.survivalgames.Plugin;

public class ScoreboardController {

	private ScoreboardManager bukkitManager;
	private Scoreboard bukkitScoreboard;
	private Objective bukkitObjective;
	
	private int timerId, remaining;
	
	private EnumMap<ScoreboardElement, String> lastTags;
	
	public ScoreboardController(String displayName, List<Player> visibleTo) {
		lastTags = new EnumMap<ScoreboardElement, String>(ScoreboardElement.class);
		bukkitManager = Bukkit.getScoreboardManager();
		bukkitScoreboard = bukkitManager.getNewScoreboard();
		
		bukkitObjective = bukkitScoreboard.registerNewObjective(hashCode() + "_main", "dummy", displayName);
		bukkitObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		visibleTo.forEach(player -> player.setScoreboard(bukkitScoreboard));
	}
	
	public void initialize() {
		for (ScoreboardElement e : ScoreboardElement.values()) {
			lastTags.put(e, e.getTag());
			Score score = bukkitObjective.getScore(lastTags.get(e));
			score.setScore(e.getPos());
		}
	}
	
	public void setTimer(Plugin plugin, int seconds, Runnable after) {
		setTimer(plugin, seconds, null, after);
	}
	
	public void setTimer(Plugin plugin, int seconds, Runnable extension, Runnable after) {
		updateScoreboardElement(ScoreboardElement.TIME_LEFT, secondsToString(seconds));
		remaining = seconds;
		timerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				remaining--;
				if (remaining == 0) {
					Bukkit.getServer().getScheduler().cancelTask(timerId);
					if (after != null) {
						after.run();
					}
					return;
				}
				updateScoreboardElement(ScoreboardElement.TIME_LEFT, secondsToString(remaining));
				if (extension != null) {
					extension.run();
				}
			}
		}, 20, 20);
	}
	
	private String secondsToString(int pTime) {
	    return String.format("%02d:%02d", pTime / 60, pTime % 60);
	}
	
	public void detachTimer() {
		Bukkit.getServer().getScheduler().cancelTask(timerId);
	}
	
	public void updateScoreboardElement(ScoreboardElement element, String value) {
		if (lastTags.get(element).contentEquals(element.getTag(value))) {
			return;
		}
		bukkitScoreboard.resetScores(lastTags.get(element));
		lastTags.put(element, element.getTag(value));
		Score score = bukkitObjective.getScore(lastTags.get(element));
		score.setScore(element.getPos());
	}
	
	public void deleteScoreboard() {
		bukkitObjective.unregister();
	}
	
	public enum ScoreboardElement {
		
		STATUS(ChatColor.DARK_PURPLE + "[%]", "Waiting", 8),
		MAP_NAME(ChatColor.GREEN + "[Map] " + ChatColor.WHITE + "- %", "unknown", 7),
		PLAYERS_LEFT(ChatColor.GREEN + "[Players Left] " + ChatColor.WHITE + "- %", "unknown", 6),
		ZONE_SIZE(ChatColor.GREEN + "[Play Zone] " + ChatColor.WHITE + "- %", "no zone", 5),
		MOST_KILLS_HEADER(ChatColor.GREEN + "[Most Kills] " + ChatColor.WHITE + "-", 4),
		MOST_KILLS_1(ChatColor.GRAY + "  1. %", "No data", 3),
		MOST_KILLS_2(ChatColor.GRAY + "  2. %", "No data", 2),
		MOST_KILLS_3(ChatColor.GRAY + "  3. %", "No data", 1),
		TIME_LEFT(ChatColor.GREEN + "[Time Remaining] " + ChatColor.WHITE + "- %", "00:00", 0);
		
		private final String wrap, def;
		private final int pos;
		
		
		private ScoreboardElement(String staticValue, int pos) {
			wrap = null;
			def = staticValue;
			this.pos = pos;
		}
		
		private ScoreboardElement(String wrap, String def, int pos) {
			this.wrap = wrap;
			this.def = def;
			this.pos = pos;
		}
		
		public String getTag() {
			return getTag(def);
		}
		
		public String getTag(String placeholder) {
			return wrap == null ? placeholder : wrap.replace("%", placeholder);
		}
		
		public int getPos() {
			return pos;
		}
	}
}

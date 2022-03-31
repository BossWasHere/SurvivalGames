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
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Collection;

public class BossBarController implements Controller {

	private final BossBar bossBar;
	
	private double maxHealth;
	private double health;
	
	public BossBarController(String name, BarColor color, double health) {
		bossBar = Bukkit.createBossBar(name, color, BarStyle.SOLID);
		bossBar.setProgress(1);
		maxHealth = health;
		this.health = health;
	}
	
	public void setVisibleTo(Collection<PlayerState> players) {
		players.forEach(p -> bossBar.addPlayer(p.cache.getPlayer()));
	}
	
	public void setVisibleTo(Player player) {
		bossBar.addPlayer(player);
	}
	
	public void unsetVisibleTo(Collection<PlayerState> players) {
		players.forEach(p -> bossBar.removePlayer(p.cache.getPlayer()));
	}
	
	public void unsetVisibleTo(Player player) {
		bossBar.removePlayer(player);
	}
	
	public double getHealth() {
		return health;
	}
	
	public void setHealth(double health) {
		if (health < 0) {
			health = 0;
		}
		this.health = health;
		bossBar.setProgress(health/maxHealth);
	}
	
	public void setMaxHealth(int maxHealth) {
		if (maxHealth < 0) {
			return;
		}
		this.maxHealth = maxHealth;
		bossBar.setProgress(health/maxHealth);
	}
	
	public void resetHealth(double health) {
		this.health = this.maxHealth = health;
		bossBar.setProgress(1);
	}
	
	public void setOptions(String name, BarColor color) {
		bossBar.setTitle(name);
		bossBar.setColor(color);
	}
	
	public void close() {
		bossBar.removeAll();
	}
}

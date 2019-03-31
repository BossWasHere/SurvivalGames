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

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarController {

	private final BossBar bukkitBar;
	
	private int maxHealth;
	private int health;
	
	private BossBarController(BossBar bukkitBar) {
		this.bukkitBar = bukkitBar;
	}
	
	public static BossBarController initBossBar(String name, BarColor color, int health) {
		BossBar bar = Bukkit.createBossBar(name, color, BarStyle.SOLID);
		bar.setProgress(1.0);
		BossBarController bossbar = new BossBarController(bar);
		bossbar.maxHealth = health;
		bossbar.health = health;
		return bossbar;
	}
	
	public void addPlayers(Collection<? extends Player> players) {
		players.forEach(p -> bukkitBar.addPlayer(p));
	}
	
	public int getHealth() {
		return health;
	}
	
	public void setHealth(int health) {
		if (health < 0) {
			health = 0;
		}
		this.health = health;
		bukkitBar.setProgress((double)health/(double)maxHealth);
	}
	
	public void setMaxHealth(int maxHealth) {
		if (maxHealth < 0) {
			return;
		}
		this.maxHealth = maxHealth;
		bukkitBar.setProgress((double)health/(double)maxHealth);
	}
	
	public void resetHealth(int health) {
		this.health = this.maxHealth = health;
		bukkitBar.setProgress(1);
	}
	
	public void setOptions(String name, BarColor color) {
		bukkitBar.setTitle(name);
		bukkitBar.setColor(color);
	}
	
	public void release() {
		bukkitBar.removeAll();
	}
}

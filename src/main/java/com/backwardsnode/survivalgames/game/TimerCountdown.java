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
package com.backwardsnode.survivalgames.game;

public class TimerCountdown implements Runnable {
	
	private final GameInstance instance;
	private long duration;
	private long remaining;
	private GameStatus nextOperation;
	
	private boolean isZero;
	
	public TimerCountdown(GameInstance instance) {
		this.instance = instance;
	}
	
	@Override
	public void run() {
		instance.tick(remaining, nextOperation);
		if (!isZero) {
			double cV = remaining--;
			if (cV < 1) {
				isZero = true;
				instance.triggerEvent(nextOperation);
			}
		}
	}
	
	public void zeroCountdown(boolean skipEvent) {
		isZero = skipEvent;
		remaining = 0;
	}

	public long getDuration() {
		return duration;
	}

	public long getRemaining() {
		return remaining;
	}
	
	public void setOperation(GameStatus nextOperation, long duration) {
		this.duration = duration;
		remaining = duration;
		this.nextOperation = nextOperation;
		isZero = false;
	}
}
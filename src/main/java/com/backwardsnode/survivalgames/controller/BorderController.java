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

import com.backwardsnode.survivalgames.dependency.plugin.ProtocolConnector;
import com.backwardsnode.survivalgames.game.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class BorderController implements Controller {

	public static final int MAX_SIZE = 29999984;

	private final ProtocolConnector protocolConnector;
	private final HashSet<Player> observingPlayers;
	
	private double lastDiameter, targetDiameter, centerX, centerZ;
	private long shrinkStartedAt, millisToTake;
	private int portalTeleportBoundary, warningTime, warningBlocks;
	
	private double defaultDiameter, defaultCenterX, defaultCenterZ;
	private int defaultWarningTime, defaultWarningBlocks;
	
	public BorderController(ProtocolConnector protocolConnector, World defaultWorld) {
		this.protocolConnector = protocolConnector;
		observingPlayers = new HashSet<>();
		loadDefaults(defaultWorld);
		setDefaults();
	}
	
	public void setCenter(double xPos, double zPos) {
		centerX = xPos;
		centerZ = zPos;
		if (protocolConnector != null) {
			protocolConnector.worldBorderPacketCenter(this, observingPlayers);
		}
	}
	
	public void setTarget(double radius, long closeSpeed) {
		lastDiameter = getDiameter();
		targetDiameter = radius;
		millisToTake = closeSpeed;
		shrinkStartedAt = new Date().getTime();
		if (protocolConnector != null) {
			protocolConnector.worldBorderPacketLerp(this, observingPlayers);
		}
	}
	
	public void setTarget(double xPos, double zPos, double diameter, long closeSpeed) {
		centerX = xPos;
		centerZ = zPos;
		lastDiameter = getDiameter();
		targetDiameter = diameter;
		millisToTake = closeSpeed;
		shrinkStartedAt = new Date().getTime();
		if (protocolConnector != null) {
			protocolConnector.worldBorderPacketInitialize(this, observingPlayers);
		}
	}

	@Override
	public void setVisibleTo(Collection<PlayerState> players) {
		players.forEach(p -> setVisibleTo(p.cache.getPlayer()));
	}

	@Override
	public void setVisibleTo(Player player) {
		if (protocolConnector != null && observingPlayers.add(player)) {
			protocolConnector.worldBorderPacketInitialize(this, player);
		}
	}

	@Override
	public void unsetVisibleTo(Collection<PlayerState> players) {
		players.forEach(p -> unsetVisibleTo(p.cache.getPlayer()));
	}

	@Override
	public void unsetVisibleTo(Player player) {
		if (protocolConnector != null && observingPlayers.remove(player)) {
			protocolConnector.worldBorderPacketReset(this, player);
		}
	}
	
	public boolean isObserving(Player player) {
		return observingPlayers.contains(player);
	}
	
	public void loadDefaults(World defaultWorld) {
		if (defaultWorld == null) {
			defaultWorld = Bukkit.getWorlds().get(0);
		}

		WorldBorder ingameBorder = defaultWorld.getWorldBorder();
		defaultDiameter = ingameBorder.getSize();
		defaultCenterX = ingameBorder.getCenter().getX();
		defaultCenterZ = ingameBorder.getCenter().getZ();
		defaultWarningTime = ingameBorder.getWarningTime();
		defaultWarningBlocks = ingameBorder.getWarningDistance();
	}
	
	public void setDefaults() {
		lastDiameter = defaultDiameter;
		targetDiameter = defaultDiameter;
		millisToTake = 0;
		shrinkStartedAt = 0;
		centerX = defaultCenterX;
		centerZ = defaultCenterZ;
		portalTeleportBoundary = MAX_SIZE;
		warningTime = defaultWarningTime;
		warningBlocks = defaultWarningBlocks;
	}
	
	public void updatePlayers() {
		if (protocolConnector != null) {
			protocolConnector.worldBorderPacketInitialize(this, observingPlayers);
		}
	}

	@Override
	public void close() {
		if (protocolConnector != null) {
			setDefaults();
			protocolConnector.worldBorderPacketInitialize(this, observingPlayers);
		}
	}

	public double getDiameter() {
		if (millisToTake == 0) {
			return targetDiameter;
		}
		double diff = new Date().getTime() - shrinkStartedAt;
		if (diff > millisToTake) {
			return targetDiameter;
		}
		if (targetDiameter > lastDiameter) {
			return lastDiameter + (diff / millisToTake) * (targetDiameter - lastDiameter);
		}
		return targetDiameter - (diff / millisToTake) * (lastDiameter - targetDiameter);
	}

	public double getPreviousDiameter() {
		return lastDiameter;
	}

	public double getTargetDiameter() {
		return targetDiameter;
	}

	public long getMillisToTarget() {
		if (millisToTake == 0 || shrinkStartedAt == 0) {
			return 0;
		}
		long diff = new Date().getTime() - shrinkStartedAt;
		diff = millisToTake - diff;
		return diff < 0 ? 0 : diff;
	}

	public double getCenterX() {
		return centerX;
	}

	public double getCenterZ() {
		return centerZ;
	}

	public int getPortalTeleportBoundary() {
		return portalTeleportBoundary;
	}

	public int getWarningTime() {
		return warningTime;
	}

	public int getWarningBlocks() {
		return warningBlocks;
	}
	
	public double getDefaultDiameter() {
		return defaultDiameter;
	}
	
	public double getDefaultCenterX() {
		return defaultCenterX;
	}
	
	public double getDefaultCenterZ() {
		return defaultCenterZ;
	}
	
	public int getDefaultWarningTime() {
		return defaultWarningTime;
	}
	
	public int getDefaultWarningBlocks() {
		return defaultWarningBlocks;
	}
	
	public void setDefaultWarning(int time, int distance) {
		defaultWarningTime = time;
		defaultWarningBlocks = distance;
	}
	
	public void setDefaultWarningTime(int time) {
		defaultWarningTime = time;
	}
	
	public void setDefaultWarningBlocks(int distance) {
		defaultWarningBlocks = distance;
	}

}

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

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class InvitedGameConfiguration {

	private final Plugin PLUGIN;
	private final Player PLAYER;
	private final GameConfiguration CONFIGURATION;
	
	private HashSet<Player> players;
	
	public InvitedGameConfiguration(Plugin plugin, Player player, GameConfiguration configuration) {
		PLUGIN = plugin;
		PLAYER = player;
		CONFIGURATION = configuration;
		
		players = new HashSet<>();
		players.add(player);
	}

	public Player getInviter() {
		return PLAYER;
	}

	public GameConfiguration getGameConfiguration() {
		return CONFIGURATION;
	}
	
	public boolean addPlayer(Player player) {
		return players.add(player);
	}
	
	public boolean removePlayer(Player player) {
		return players.remove(player);
	}
	
	public boolean hasPlayer(Player player) {
		return players.contains(player);
	}
	
	public void start() {
		PLUGIN.getHost().getGameManager().startGame(CONFIGURATION, PLUGIN.getCacheSettings(), PLAYER, players, true, PlayerSelectionMethod.SHUFFLED_WITH_SPECTATORS);
	}

	public void announceCancelled(boolean hostLeft) {
		for (Player p : players) {
			if (p.isOnline()) {
				if (hostLeft) {
					PLUGIN.getMessageProvider().sendMessage(p, Messages.COMMAND.SG_JOIN.HOST_LEFT);
				} else {
					PLUGIN.getMessageProvider().sendMessage(p, Messages.COMMAND.SG_JOIN.CANCELLED);
				}
			}
		}
	}
}

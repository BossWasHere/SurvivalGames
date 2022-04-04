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
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class InvitedGameConfiguration {

	private final Plugin plugin;
	private final Player player;
	private final GameConfigurationWrapper gameConfiguration;
	private final HashSet<Player> players;
	
	public InvitedGameConfiguration(Plugin plugin, Player player, GameConfigurationWrapper configuration) {
		this.plugin = plugin;
		this.player = player;
		gameConfiguration = configuration;
		
		players = new HashSet<>();
		players.add(player);
	}

	public Player getInviter() {
		return player;
	}

	public GameConfigurationWrapper getGameConfiguration() {
		return gameConfiguration;
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
		plugin.getHost().getGameManager().startGame(gameConfiguration, plugin.getCacheSettings(), player, players, true, PlayerSelectionMethod.SHUFFLED_WITH_SPECTATORS);
	}

	public void announceCancelled(boolean hostLeft) {
		for (Player p : players) {
			if (p.isOnline()) {
				if (hostLeft) {
					plugin.getMessageProvider().sendMessage(p, Messages.Command.SGJoin.HOST_LEFT);
				} else {
					plugin.getMessageProvider().sendMessage(p, Messages.Command.SGJoin.CANCELLED);
				}
			}
		}
	}
}

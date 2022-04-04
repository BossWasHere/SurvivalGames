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
import com.backwardsnode.survivalgames.api.event.GameAbortedEvent;
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.config.PluginConfigKeys;
import com.backwardsnode.survivalgames.exception.GameConfigurationException;
import com.backwardsnode.survivalgames.message.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class GameManager {

	private final Plugin plugin;

	private final HashMap<UUID, GameInstance> playerGameMap;
	private final HashMap<String, GameInstance> nameGameMap;

	private final boolean permitTimeControl;

	private boolean closing = false;
	
	public GameManager(Plugin plugin) {
		this.plugin = plugin;
		playerGameMap = new HashMap<>();
		nameGameMap = new HashMap<>();
		permitTimeControl = PluginConfigKeys.PERMIT_TIME_CONTROL.get(plugin.getConfig());
		GameListener listener = new GameListener(this);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public boolean startGame(GameConfigurationWrapper gcw, PlayerCacheSettings cacheSettings, Player initiator, Collection<? extends Player> players, boolean ignoreIngamePlayers, PlayerSelectionMethod selectorMode) {

		if (isMapInUse(gcw.getFileName())) {
			Bukkit.getPluginManager().callEvent(new GameAbortedEvent(GameStatus.START_ERR_MAP_IN_USE, gcw, initiator));

			plugin.getMessageProvider().sendMessage(initiator, Messages.Game.MAP_IN_USE);
			return false;
		}

		GameInstance instance;
		try {
			instance = new GameInstance(this, gcw, cacheSettings);
		} catch (GameConfigurationException e) {
			plugin.getMessageProvider().sendMessage(initiator, Messages.Config.SYNTAX, gcw.getFileName());
			e.printStackTrace();
			return false;
		}

		List<Player> listPlayers = new ArrayList<>(players);
		instance.begin(initiator, listPlayers, ignoreIngamePlayers, selectorMode);
		switch (instance.getStatus()) {
		case START_ERR_FEW_PLAYERS:
			Bukkit.getPluginManager().callEvent(new GameAbortedEvent(GameStatus.START_ERR_FEW_PLAYERS, gcw, initiator));
			plugin.getMessageProvider().sendMessage(initiator, Messages.Game.INSUFFICIENT_PLAYERS);
			return false;
		case START_ERR_PLAYER_IN_GAME:
			Bukkit.getPluginManager().callEvent(new GameAbortedEvent(GameStatus.START_ERR_PLAYER_IN_GAME, gcw, initiator));
			plugin.getMessageProvider().sendMessage(initiator, Messages.Game.PLAYER_IN_GAME);
			return false;
		case START_SUCCESS_WITH_SPECTATORS:
			plugin.getMessageProvider().sendMessage(initiator, Messages.Game.START_WITH_SPECTATORS);
			break;
		case START_SUCCESS_WITHOUT_SPECTATORS:
			plugin.getMessageProvider().sendMessage(initiator, Messages.Game.START_WITHOUT_SPECTATORS);
			break;
		case START_SUCCESS:
			plugin.getMessageProvider().sendMessage(initiator, Messages.Game.START);
			break;
		default:
			return false;
		}
		nameGameMap.put(gcw.getFileName(), instance);
		return true;
	}
	
	public boolean isMapInUse(String fileName) {
		return nameGameMap.containsKey(fileName);
	}

	public boolean isPlayerIngame(Player player) {
		GameInstance i = getGame(player);
		if (i != null) {
			return i.isActive();
		}
		return false;
	}

	public GameInstance getGame(Player player) {
		UUID uuid = player.getUniqueId();
		GameInstance i = playerGameMap.get(uuid);
		if (i != null) {
			if (i.isActive()) {
				return i;
			}
			playerGameMap.remove(uuid);
		}
		// TODO this shouldn't happen? was this just some extra check?
//		for (GameInstance gi : instances) {
//			if (gi.isActive()) {
//				if (gi.getPlayerState(player) != null) {
//					playerGameMap.put(uuid, gi);
//					return gi;
//				}
//			}
//		}
		return null;
	}

	public void onGameFinished(GameInstance instance) {
		if (!closing) {
			nameGameMap.remove(instance.getGameConfiguration().getFileName());
			for (Entry<UUID, GameInstance> es : playerGameMap.entrySet()) {
				if (es.getValue().equals(instance)) {
					playerGameMap.remove(es.getKey());
				}
			}
		}
	}

	public boolean timeControlEnabled() {
		return permitTimeControl;
	}
	
	public void close(boolean force) {
		closing = true;
		for (GameInstance i : nameGameMap.values()) {
			i.terminate();
		}
		nameGameMap.clear();
	}

	public GameInstance getGameByMap(String fileName) {
		return nameGameMap.get(fileName);
	}
}
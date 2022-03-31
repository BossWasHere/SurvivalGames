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
package com.backwardsnode.survivalgames;

import com.backwardsnode.survivalgames.api.event.GameInvitationAcceptEvent;
import com.backwardsnode.survivalgames.api.event.GameInvitationCancelledEvent;
import com.backwardsnode.survivalgames.api.event.GameNewInvitationEvent;
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.editor.EditorManager;
import com.backwardsnode.survivalgames.game.GameInstance;
import com.backwardsnode.survivalgames.game.GameManager;
import com.backwardsnode.survivalgames.game.InvitationCancelType;
import com.backwardsnode.survivalgames.game.InvitedGameConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Hosts and manages games and editor sessions for the plugin
 */
public class SGHost {

    private final Plugin PLUGIN;

    private final EditorManager EDITOR_MANAGER;
    private final GameManager GAME_MANAGER;

    private final HashMap<String, InvitedGameConfiguration> INVITATIONS;

    private boolean closed = false;

    public SGHost(Plugin plugin) {
        PLUGIN = plugin;

        EDITOR_MANAGER = new EditorManager(plugin);
        GAME_MANAGER = new GameManager(plugin);
        INVITATIONS = new HashMap<>();
    }

    /**
     * Gets the current manager for editor sessions
     * @return The current {@link EditorManager}
     */
    public EditorManager getEditorManager() {
        return EDITOR_MANAGER;
    }

    /**
     * Gets the current manager for game sessions
     * @return The current {@link GameManager}
     */
    public GameManager getGameManager() {
        return GAME_MANAGER;
    }

    /**
     * Determines whether a given player is "busy" (editing or playing)
     * @param player the player to check
     * @return True if the player is editing/playing
     */
    public boolean isPlayerBusy(Player player) {
        if (closed) return false;

        if (EDITOR_MANAGER.isEditor(player)) {
            return true;
        }
        return GAME_MANAGER.isPlayerIngame(player);
    }

    /**
     * Determines whether a map (by name) is currently in use (has an active game)
     * @param mapName the name of the map to check
     * @return True if the map is currently in-use
     */
    public boolean isMapInUse(String mapName) {
        if (closed) return false;

        return GAME_MANAGER.isMapInUse(mapName);
    }

    /**
     * Checks if there is an invitation for a given map
     * @param mapName the name of the map to check
     * @return True if there is an open invitation for this map
     */
    public boolean mapHasPendingInvitation(String mapName) {
        if (closed) return false;

        return INVITATIONS.containsKey(mapName.toLowerCase());
    }

    /**
     * Attempts to add a gameinvitation and announce it
     * @param initiator the player responsible for inviting other players
     * @param configuration the map configuration for the game
     * @param mapName the name of the map to put the invitation under
     * @return True if the invitation was successfully added
     */
    public boolean addInvitation(Player initiator, GameConfiguration configuration, String mapName) {
        if (closed) return false;

        for (InvitedGameConfiguration game : INVITATIONS.values()) {
            if (game.hasPlayer(initiator)) {
                return false;
            }
            if (game.getInviter().equals(initiator)) {
                return false;
            }
        }
        InvitedGameConfiguration igc = new InvitedGameConfiguration(PLUGIN, initiator, configuration);

        GameNewInvitationEvent gameNewInvitationEvent = new GameNewInvitationEvent(igc);
        Bukkit.getPluginManager().callEvent(gameNewInvitationEvent);
        if (gameNewInvitationEvent.isCancelled()) {
            return false;
        }

        INVITATIONS.put(mapName.toLowerCase(), igc);

        PLUGIN.getLogger().info(igc.getInviter().getName() + " started an invitational game on " + igc.getGameConfiguration().mapName);
        PLUGIN.getServer().getScheduler().scheduleSyncDelayedTask(PLUGIN, () -> startInvitationalGame(igc), 1200);


        return true;
    }

    // TODO this is messy
    /**
     * Attempts to cancel an invitation started or accepted by a player
     * @param player the player to cancel the invitation for
     * @param disconnected if the invitation is being cancelled because the player disconnected
     * @return The {@link InvitationCancelType} indicating the result of the cancellation
     */
    public InvitationCancelType cancelInvitations(Player player, boolean disconnected) {
        if (closed) return InvitationCancelType.NONE;

        InvitationCancelType ct = InvitationCancelType.NONE;
        for (Map.Entry<String, InvitedGameConfiguration> entry : INVITATIONS.entrySet()) {
            InvitedGameConfiguration igc = entry.getValue();

            if (igc.removePlayer(player)) {
                ct = InvitationCancelType.LEAVE_INVITATION;
            }

            if (igc.getInviter().equals(player)) {
                igc.announceCancelled(disconnected);
                INVITATIONS.remove(entry.getKey());
                PLUGIN.getLogger().info("Cancelled game - Host: " + player.getName() + ", Map: " + igc.getGameConfiguration().mapName + ", Disconnected: " + disconnected);
                ct = InvitationCancelType.DELETE_INVITATION;
            }

            if (ct != InvitationCancelType.NONE) {
                Bukkit.getPluginManager().callEvent(new GameInvitationCancelledEvent(player, igc, ct));

                return ct;
            }
        }
        return ct;
    }

    /**
     * Tries to sign up a player to play in an invitational game
     * @param player the player to sign up
     * @param mapName the name of the map to sign up to
     * @return True if the player was signed up
     */
    public boolean signUpInvitation(Player player, String mapName) {
        if (closed) return false;

        InvitedGameConfiguration igc = INVITATIONS.get(mapName.toLowerCase());
        if (igc != null) {

            GameInvitationAcceptEvent gameInvitationAcceptEvent = new GameInvitationAcceptEvent(player, igc);
            Bukkit.getPluginManager().callEvent(gameInvitationAcceptEvent);

            if (!gameInvitationAcceptEvent.isCancelled()) {
                igc.addPlayer(player);
                return true;
            }
        }
        return false;
    }

    /**
     * Immediately starts an invitational game
     * @param igc the invitation to start
     */
    public void startInvitationalGame(InvitedGameConfiguration igc) {
        if (closed) return;

        if (INVITATIONS.remove(igc.getGameConfiguration().mapName.toLowerCase()) != null) {
            igc.start();
        }
    }

    /**
     * Determines if a player should have world protection features enabled
     * @param player the player to check
     * @return True if world protection should be used
     * @apiNote used to prevent anvils breaking after use
     */
    public boolean hasWorldProtection(Player player) {
        return GAME_MANAGER.isPlayerIngame(player);
    }

    /**
     * Tries to get the world border controller for a given player
     * @param player the player to get the border controller for
     * @return An instance of {@link BorderController}, or null if the player doesn't have a custom border active
     */
    public BorderController tryGetBorder(Player player) {
        BorderController controller = null;

        GameInstance gameInstance = GAME_MANAGER.getGame(player);
        if (gameInstance != null) {
            controller = gameInstance.getBorderController();
        }
        if (controller == null) {
            controller = EDITOR_MANAGER.getBorderFor(player);
        }

        return controller;
    }

    /**
     * Closes and disposes of resources held by this host
     */
    public void close() {
        if (closed) return;
        closed = true;

        INVITATIONS.clear();

        GAME_MANAGER.close(true);
        EDITOR_MANAGER.closeAllEditors(true);
    }
}

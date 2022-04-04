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
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.backwardsnode.survivalgames.editor.EditorManager;
import com.backwardsnode.survivalgames.editor.Scene;
import com.backwardsnode.survivalgames.game.GameInstance;
import com.backwardsnode.survivalgames.game.GameManager;
import com.backwardsnode.survivalgames.game.InvitationCancelType;
import com.backwardsnode.survivalgames.game.InvitedGameConfiguration;
import com.backwardsnode.survivalgames.world.LootDropManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Hosts and manages games and editor sessions for the plugin
 */
public class SGHost {

    private final Plugin plugin;

    private final EditorManager editorManager;
    private final GameManager gameManager;
    private final LootDropManager lootDropManager;

    private final HashMap<String, InvitedGameConfiguration> invitations;

    private boolean closed = false;

    public SGHost(Plugin plugin) {
        this.plugin = plugin;

        editorManager = new EditorManager(plugin);
        gameManager = new GameManager(plugin);
        lootDropManager = new LootDropManager();
        invitations = new HashMap<>();
    }

    /**
     * Gets the current manager for editor sessions
     * @return The current {@link EditorManager}
     */
    public EditorManager getEditorManager() {
        return editorManager;
    }

    /**
     * Gets the current manager for game sessions
     * @return The current {@link GameManager}
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * Gets the current manager for loot drops
     * @return The current {@link LootDropManager}
     */
    public LootDropManager getLootDropManager() {
        return lootDropManager;
    }

    /**
     * Determines whether a given player is "busy" (editing or playing)
     * @param player the player to check
     * @return True if the player is editing/playing
     */
    public boolean isPlayerBusy(Player player) {
        if (closed) return false;

        if (editorManager.isEditor(player)) {
            return true;
        }
        return gameManager.isPlayerIngame(player);
    }

    public GameConfigurationWrapper getCurrentEditorOrGameConfiguration(Player player) {
        if (closed) return null;

        Scene scene = editorManager.getEditor(player);
        if (scene != null) return scene.getGameConfiguration();

        GameInstance gameInstance = gameManager.getGame(player);
        if (gameInstance != null) return gameInstance.getGameConfiguration();

        return null;
    }

    /**
     * Determines whether a map (by name) is currently in use (has an active game)
     * @param mapName the name of the map to check
     * @return True if the map is currently in-use
     */
    public boolean isMapInUse(String mapName) {
        if (closed) return false;

        return gameManager.isMapInUse(mapName);
    }

    /**
     * Checks if there is an invitation for a given map
     * @param mapName the name of the map to check
     * @return True if there is an open invitation for this map
     */
    public boolean mapHasPendingInvitation(String mapName) {
        if (closed) return false;

        return invitations.containsKey(mapName.toLowerCase());
    }

    /**
     * Attempts to add a gameinvitation and announce it
     * @param initiator the player responsible for inviting other players
     * @param configuration the map configuration for the game
     * @param mapName the name of the map to put the invitation under
     * @return True if the invitation was successfully added
     */
    public boolean addInvitation(Player initiator, GameConfigurationWrapper configuration, String mapName) {
        if (closed) return false;

        for (InvitedGameConfiguration game : invitations.values()) {
            if (game.hasPlayer(initiator)) {
                return false;
            }
            if (game.getInviter().equals(initiator)) {
                return false;
            }
        }
        InvitedGameConfiguration igc = new InvitedGameConfiguration(plugin, initiator, configuration);

        GameNewInvitationEvent gameNewInvitationEvent = new GameNewInvitationEvent(igc);
        Bukkit.getPluginManager().callEvent(gameNewInvitationEvent);
        if (gameNewInvitationEvent.isCancelled()) {
            return false;
        }

        invitations.put(mapName.toLowerCase(), igc);

        plugin.getLogger().info(initiator.getName() + " started an invitational game on " + configuration.getMapName());
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> startInvitationalGame(igc), 1200);


        return true;
    }

    /**
     * Attempts to cancel an invitation started or accepted by a player
     * @param player the player to cancel the invitation for
     * @param disconnected if the invitation is being cancelled because the player disconnected
     * @return The {@link InvitationCancelType} indicating the result of the cancellation
     */
    public InvitationCancelType cancelInvitations(Player player, boolean disconnected) {
        if (closed) return InvitationCancelType.NONE;

        InvitationCancelType ct = InvitationCancelType.NONE;
        for (Map.Entry<String, InvitedGameConfiguration> entry : invitations.entrySet()) {
            InvitedGameConfiguration igc = entry.getValue();

            if (igc.removePlayer(player)) {
                ct = InvitationCancelType.LEAVE_INVITATION;
            }

            if (igc.getInviter().equals(player)) {
                igc.announceCancelled(disconnected);
                invitations.remove(entry.getKey());
                //plugin.getLogger().info("Rejected invitation - Host: " + player.getName() + ", Map: " + igc.getGameConfiguration().getMapName() + ", Disconnected: " + disconnected);
                ct = InvitationCancelType.DELETE_INVITATION;
            }

            Bukkit.getPluginManager().callEvent(new GameInvitationCancelledEvent(player, igc, ct));
            if (ct == InvitationCancelType.DELETE_INVITATION) {
                // TODO enforce player cannot accept invite <- player sent their own invite
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

        InvitedGameConfiguration igc = invitations.get(mapName.toLowerCase());
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

        if (invitations.remove(igc.getGameConfiguration().getMapName().toLowerCase()) != null) {
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
        return gameManager.isPlayerIngame(player);
    }

    /**
     * Tries to get the world border controller for a given player
     * @param player the player to get the border controller for
     * @return An instance of {@link BorderController}, or null if the player doesn't have a custom border active
     */
    public BorderController tryGetBorder(Player player) {
        BorderController controller = null;

        GameInstance gameInstance = gameManager.getGame(player);
        if (gameInstance != null) {
            controller = gameInstance.getBorderController();
        }
        if (controller == null) {
            controller = editorManager.getBorderFor(player);
        }

        return controller;
    }

    public void passConfirmationResolution(Player player, boolean confirm) {
        GameInstance gameInstance = gameManager.getGame(player);
        if (gameInstance != null) {
            gameInstance.confirmationResolution(player, confirm);
            return;
        }

        Scene scene = editorManager.getEditor(player);
        if (scene != null) {
            scene.confirmationResolution(confirm);
        }
    }

    /**
     * Closes and disposes of resources held by this host
     */
    public void close() {
        if (closed) return;
        closed = true;

        invitations.clear();

        gameManager.close(true);
        editorManager.closeAllEditors(true);
        lootDropManager.clearAll();
    }
}

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

package com.backwardsnode.survivalgames.dependency.plugin;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.game.GameInstance;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIConnector extends PlaceholderExpansion {

    private final Plugin plugin;

    public PlaceholderAPIConnector(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "survivalgames";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BackwardsNode";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String lowercasePlaceholder = params.toLowerCase();

        switch (lowercasePlaceholder) {
            case "sg_is_alive" -> {
                GameInstance gameInstance = plugin.getHost().getGameManager().getGame(player);
                return gameInstance != null && gameInstance.getPlayerState(player).alive ? "alive" : "dead";
            }
            case "sg_current_kills" -> {
                GameInstance gameInstance = plugin.getHost().getGameManager().getGame(player);
                return gameInstance == null ? "0" : String.valueOf(gameInstance.getPlayerState(player).kills);
            }
            case "sg_current_map" -> {
                GameConfigurationWrapper gcw = plugin.getHost().getCurrentEditorOrGameConfiguration(player);
                return gcw == null ? "none" : gcw.getMapName();
            }
            case "sg_current_map_file" -> {
                GameConfigurationWrapper gcw = plugin.getHost().getCurrentEditorOrGameConfiguration(player);
                return gcw == null ? "none" : gcw.getFileName();
            }
        }

        return null;
    }
}

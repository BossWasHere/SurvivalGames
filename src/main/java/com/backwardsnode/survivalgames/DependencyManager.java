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

import org.bukkit.Bukkit;

public class DependencyManager {

    private final Plugin PLUGIN;

    private ProtocolConnector protocolConnector;
    private VaultConnector vaultConnector;

    public DependencyManager(Plugin plugin) {
        PLUGIN = plugin;
    }

    public void connect() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolConnector = new ProtocolConnector(PLUGIN);
            protocolConnector.listenPackets();
            PLUGIN.getLogger().info("Loaded plugin hook with ProtocolLib");
        } else {
            PLUGIN.getLogger().warning("Loaded plugin without ProtocolLib, some features will be disabled");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            vaultConnector = new VaultConnector();
            if (vaultConnector.isConnected()) {
                PLUGIN.getLogger().info("Loaded plugin hook with Vault");
            } else {
                PLUGIN.getLogger().warning("Could not connect to economy service, some features will be disabled");
            }
        }
    }

    public void disconnect() {
        if (protocolConnector != null) {
            protocolConnector.stopListeningPackets();
        }

        protocolConnector = null;
        vaultConnector = null;
    }

    public ProtocolConnector getProtocolConnector() {
        return protocolConnector;
    }

    public VaultConnector getVaultConnector() {
        return vaultConnector;
    }
}

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
package com.backwardsnode.survivalgames.dependency;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.config.PluginConfigKeys;
import com.backwardsnode.survivalgames.database.SQLDataSource;
import com.backwardsnode.survivalgames.dependency.external.HikariConnector;
import com.backwardsnode.survivalgames.dependency.plugin.PlaceholderAPIConnector;
import com.backwardsnode.survivalgames.dependency.plugin.ProtocolConnector;
import com.backwardsnode.survivalgames.dependency.plugin.VaultConnector;
import com.backwardsnode.survivalgames.dependency.redundancy.JDBCDataSource;
import org.bukkit.Bukkit;

public class DependencyManager {

    private final Plugin plugin;

    private ProtocolConnector protocolConnector;
    private VaultConnector vaultConnector;
    private PlaceholderAPIConnector placeholderAPIConnector;

    private SQLDataSource dataSource;

    public DependencyManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void loadPlugins() {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            protocolConnector = new ProtocolConnector(plugin);
            protocolConnector.listenPackets();
            plugin.getLogger().info("Loaded plugin hook with ProtocolLib");
        } else {
            plugin.getLogger().warning("Loaded plugin without ProtocolLib, some features will be disabled");
        }

        if (PluginConfigKeys.USE_PLACEHOLDER_API.get(plugin.getConfig()) && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPIConnector = new PlaceholderAPIConnector(plugin);
            placeholderAPIConnector.register();

            plugin.getLogger().info("Loaded plugin hook with PlaceholderAPI");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            vaultConnector = new VaultConnector();
            if (vaultConnector.isConnected()) {
                plugin.getLogger().info("Loaded plugin hook with Vault");
            } else {
                plugin.getLogger().warning("Could not connect to economy service, some features will be disabled");
            }
        }
    }

    public void loadDataSource() {

        if (DependencyInjector.HIKARI_CP.downloadAndInject(plugin)) {
            dataSource = new HikariConnector();
            plugin.getLogger().info("Connected to HikariCP");
        } else {
            dataSource = new JDBCDataSource();
            plugin.getLogger().warning("Could not connect to HikariCP, using built-in DriverManager");
        }
    }

    public void disconnect() {
        if (protocolConnector != null) {
            protocolConnector.stopListeningPackets();
            protocolConnector = null;
        }

        if (placeholderAPIConnector != null) {
            placeholderAPIConnector.unregister();
            placeholderAPIConnector = null;
        }

        vaultConnector = null;

        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public ProtocolConnector getProtocolConnector() {
        return protocolConnector;
    }

    public PlaceholderAPIConnector getPlaceholderAPIConnector() {
        return placeholderAPIConnector;
    }

    public VaultConnector getVaultConnector() {
        return vaultConnector;
    }

    public SQLDataSource getDataSource() {
        return dataSource;
    }
}

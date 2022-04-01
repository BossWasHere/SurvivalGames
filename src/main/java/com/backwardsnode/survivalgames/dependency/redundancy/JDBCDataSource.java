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

package com.backwardsnode.survivalgames.dependency.redundancy;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.database.DataStoreSource;
import com.backwardsnode.survivalgames.database.SQLDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCDataSource implements SQLDataSource {

    private DataStoreSource store;
    private String jdbcUrl;
    private String username;
    private String password;

    @Override
    public void configure(Plugin plugin, DataStoreSource store) throws IllegalArgumentException {
        switch (store) {
            case H2 -> {
                File dbFile = new File(plugin.getDataFolder(), "survivalgames.h2.db");
                jdbcUrl = "jdbc:h2:" + dbFile.getAbsolutePath();
            }
            case SQLITE -> {
                File dbFile = new File(plugin.getDataFolder(), "survivalgames.sqlite.db");
                jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            }
            case MYSQL -> {
                ConfigurationSection fileConfig = plugin.getConfig().getConfigurationSection("storage.mysql");
                jdbcUrl = "jdbc:mysql://" + fileConfig.getString("address") + ':' + fileConfig.getInt("port", 3306);
                username = fileConfig.getString("username");
                password = fileConfig.getString("password");
            }
            default -> throw new IllegalArgumentException("Cannot initialize JDBC to " + store);
        }

        this.store = store;
    }

    @Override
    public Connection getConnection() throws IllegalStateException, SQLException {
        switch (store) {
            case H2, SQLITE -> {
                return DriverManager.getConnection(jdbcUrl);
            }
            case MYSQL -> {
                return DriverManager.getConnection(jdbcUrl, username, password);
            }
        }

        throw new IllegalStateException("No configuration set on object");
    }

    @Override
    public void close() { }

}

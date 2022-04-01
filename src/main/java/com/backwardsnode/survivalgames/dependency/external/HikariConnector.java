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

package com.backwardsnode.survivalgames.dependency.external;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.database.DataStoreSource;
import com.backwardsnode.survivalgames.database.SQLDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class HikariConnector implements SQLDataSource {

    private HikariConfig config;
    private HikariDataSource dataSource;

    @Override
    public void configure(Plugin plugin, DataStoreSource store) throws IllegalArgumentException {
        config = new HikariConfig();

        switch (store) {
            case H2 -> {
                File dbFile = new File(plugin.getDataFolder(), "survivalgames.h2.db");
                config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
                config.setJdbcUrl("jdbc:h2:" + dbFile.getAbsolutePath());
            }
            case SQLITE -> {
                File dbFile = new File(plugin.getDataFolder(), "survivalgames.sqlite.db");
                config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
                config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            }
            case MYSQL -> {
                ConfigurationSection fileConfig = plugin.getConfig().getConfigurationSection("storage.mysql");
                config.setJdbcUrl("jdbc:mysql://" + fileConfig.getString("address") + ':' + fileConfig.getInt("port", 3306));
                config.setUsername(fileConfig.getString("username"));
                config.setPassword(fileConfig.getString("password"));

                config.setMaximumPoolSize(fileConfig.getInt("pool.maximum-pool-size", 10));
                config.setMaxLifetime(fileConfig.getLong("pool.maximum-lifetime", 300000));
                config.setConnectionTimeout(fileConfig.getLong("pool.connection-timeout", 5000));

                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            }
            default -> throw new IllegalArgumentException("Cannot initialize Hikari pool to " + store);
        }
    }

    @Override
    public Connection getConnection() throws IllegalStateException, SQLException {
        if (config == null) throw new IllegalStateException("No configuration set on object");

        if (dataSource == null) {
            dataSource = new HikariDataSource(config);
        }

        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

}

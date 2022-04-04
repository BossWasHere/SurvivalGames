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

package com.backwardsnode.survivalgames.database;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.database.implementation.CSVStore;
import com.backwardsnode.survivalgames.database.implementation.DataStoreImplementation;

import java.io.Closeable;
import java.io.IOException;

public class DataFrontend implements Closeable {

    private final Plugin plugin;

    private DataStoreImplementation dataStore;

    private boolean closed = false;

    public DataFrontend(Plugin plugin, DataStoreSource source) {
        this.plugin = plugin;

        switch (source) {
            case CSV -> dataStore = new CSVStore(this.plugin.getDataFolder());
        }
    }

    public boolean isConnected() {
        return dataStore != null;
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;

        if (isConnected()) {
            dataStore.close();
        }
    }
}

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

package com.backwardsnode.survivalgames.database.implementation;

import com.backwardsnode.survivalgames.game.GameInstance;

import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

public interface DataStoreImplementation extends Closeable {

    DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    boolean initialize() throws IOException;
    boolean canWrite();
    boolean canRead();

    boolean supportsMigration();
    void migrateUp() throws IOException, DataStoreException;
    void migrateDown() throws IOException, DataStoreException;

    void addGameHistory(GameInstance instance) throws IOException, DataStoreException;
    void getGameHistory(int id) throws IOException, DataStoreException;
    void getPlayerHistory(UUID uuid) throws IOException, DataStoreException;

}

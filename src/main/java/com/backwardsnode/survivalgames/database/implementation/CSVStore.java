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

import com.backwardsnode.survivalgames.config.DeathmatchConfiguration;
import com.backwardsnode.survivalgames.game.GameInstance;
import com.backwardsnode.survivalgames.game.GameStatus;
import com.backwardsnode.survivalgames.game.PlayerState;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CSVStore implements DataStoreImplementation {

    private static final String GAME_HISTORY_HEADER = "date,map,duration,team_size,players,team_groups,entered_deathmatch,players_at_deathmatch,deathmatch_location,finished,placements,settings\n";

    private final File CSV_FILE_GAME_HISTORY;

    private boolean closed = false;

    public CSVStore(@NotNull File directory) {
        CSV_FILE_GAME_HISTORY = new File(directory, "game_history.csv");
    }

    @Override
    public boolean initialize() throws IOException {
        if (closed) throw new IOException("Data store is closed");

        return appendToFile(CSV_FILE_GAME_HISTORY, null);
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public void close() throws IOException {
        if (closed) return;
        closed = true;
    }

    @Override
    public boolean supportsMigration() {
        return false;
    }

    @Override
    public void migrateUp() throws IOException, DataStoreException {}

    @Override
    public void migrateDown() throws IOException, DataStoreException {}

    @Override
    public void addGameHistory(GameInstance instance) throws IOException, DataStoreException {
        if (closed) throw new IOException("Data store is closed");

        int playerCounter = instance.getInitialPlayerCount();
        int teamSize = instance.getTeamSize();

        // estimating size (does this help?)
        StringBuilder uuidBuilder = new StringBuilder(playerCounter * 37);
        StringBuilder teamBuilder = teamSize > 1 ? new StringBuilder(playerCounter * 3) : null;
        StringBuilder placementBuilder = new StringBuilder(playerCounter * 3);

        for (PlayerState state : instance.getPlayerStates()) {
            if (!state.spectating) {
                uuidBuilder.append(';').append(state.cache.getPlayer().getUniqueId());
                if (teamSize > 1) teamBuilder.append(';').append(state.teamNumber);
                placementBuilder.append(';').append(state.placement);
            }
        }

        // estimating size (does this help?)
        StringBuilder entryBuilder = new StringBuilder(playerCounter * 43 + 50);

        entryBuilder.append(DEFAULT_DATE_FORMAT.format(instance.getStartedAt())).append(',');
        entryBuilder.append(instance.getGameConfiguration().getFileName()).append(',');
        entryBuilder.append(instance.getDuration()).append(',');
        entryBuilder.append(uuidBuilder.substring(1)).append(',');
        if (teamSize > 1) {
            entryBuilder.append(teamBuilder.substring(1));
        }
        entryBuilder.append(',');
        entryBuilder.append(instance.hasDeathmatchStarted() ? 1 : 0).append(',');
        entryBuilder.append(instance.getPlayersAtDeathmatch()).append(',');

        DeathmatchConfiguration dc = instance.getDeathmatchConfig();
        if (dc != null) {
            entryBuilder.append(dc.location.toString().replace(',', ';'));
        }
        entryBuilder.append(',');
        entryBuilder.append(instance.getStatus() == GameStatus.FINISH_GAME ? '1' : '0').append(',');
        entryBuilder.append(placementBuilder.substring(1)).append(',');

        // TODO append settings when we implement some
        // entryBuilder.append(instance.getSettings());

        appendToFile(CSV_FILE_GAME_HISTORY, entryBuilder.toString());
    }

    @Override
    public void getGameHistory(int id) throws IOException, DataStoreException {
        throw new DataStoreException("Operation not supported for CSV storage");
    }

    @Override
    public void getPlayerHistory(UUID uuid) throws IOException, DataStoreException {
        throw new DataStoreException("Operation not supported for CSV storage");
    }

    private boolean appendToFile(File file, String data) {
        boolean appendHeader = !file.exists();
        if (data == null && !appendHeader) {
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(file, true)) {

            if (appendHeader) {
                fos.write(GAME_HISTORY_HEADER.getBytes(StandardCharsets.UTF_8));
            }
            if (data != null) {
                fos.write(data.getBytes(StandardCharsets.UTF_8));
            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}

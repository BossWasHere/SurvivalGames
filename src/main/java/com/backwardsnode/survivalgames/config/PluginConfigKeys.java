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

package com.backwardsnode.survivalgames.config;

import com.backwardsnode.survivalgames.database.DataStoreSource;
import com.backwardsnode.survivalgames.message.MessageProvider;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class PluginConfigKeys<T> {

    private static final List<PluginConfigKeys<?>> VALUES = new ArrayList<>();
    private static final String LOCATION_EXAMPLE = "0.5,100,0.5,survivalgames";
    
    public static final PluginConfigKeys<String> DEFAULT_LANGUAGE = new PluginConfigKeys<>(MessageProvider.DEFAULT_LOCALE, "default-language");
    public static final PluginConfigKeys<Boolean> UPDATE_CHECK = new PluginConfigKeys<>(true, "update-check");
    public static final PluginConfigKeys<Boolean> AUTO_START = new PluginConfigKeys<>(false, "auto-start");
    public static final PluginConfigKeys<String> AUTO_START_ABORT = new PluginConfigKeys<>("nothing", "auto-start-abort");
    public static final PluginConfigKeys<String> GAME_COMPLETION = new PluginConfigKeys<>("nothing", "game-completion");
    public static final PluginConfigKeys<Boolean> ALLOW_SPECTATE_IN_PROGRESS = new PluginConfigKeys<>(true, "allow-spectate-in-progress");
    public static final PluginConfigKeys<Boolean> ALLOW_SPECTATORS_SEE_INVENTORY = new PluginConfigKeys<>(true, "allow-spectators-see-inventory");
    public static final PluginConfigKeys<Boolean> USE_BUNGEE = new PluginConfigKeys<>(false, "bungee");
    public static final PluginConfigKeys<Boolean> USE_PLACEHOLDER_API = new PluginConfigKeys<>(true, "placeholderapi");
    public static final PluginConfigKeys<Boolean> ENABLE_PARTY = new PluginConfigKeys<>(true, "enable-party");
    public static final PluginConfigKeys<Boolean> ENABLE_COSMETICS = new PluginConfigKeys<>(true, "enable-cosmetics");

    public static final PluginConfigKeys<List<String>> DISABLE_ITEMS = new PluginConfigKeys<>(null, "globalSettings", "disable-items");
    public static final PluginConfigKeys<Boolean> PERMIT_BOUNTIES = new PluginConfigKeys<>(true, "globalSettings", "permit-bounties");
    public static final PluginConfigKeys<Boolean> PERMIT_CHEST_REFILL = new PluginConfigKeys<>(true, "globalSettings", "permit-chest-refill");
    public static final PluginConfigKeys<Boolean> PERMIT_CALLABLE_LOOT_DROPS = new PluginConfigKeys<>(true, "globalSettings", "permit-callable-loot-drops");
    public static final PluginConfigKeys<Boolean> PERMIT_LOOT_DROPS = new PluginConfigKeys<>(true, "globalSettings", "permit-loot-drops");
    public static final PluginConfigKeys<Boolean> PERMIT_MAP_LOBBY_OVERRIDE = new PluginConfigKeys<>(true, "globalSettings", "permit-map-lobby-override");
    public static final PluginConfigKeys<Boolean> PERMIT_SURVIVAL_MODE = new PluginConfigKeys<>(true, "globalSettings", "permit-survival-mode");
    public static final PluginConfigKeys<Boolean> PERMIT_TRACKING_COMPASS = new PluginConfigKeys<>(true, "globalSettings", "permit-tracking-compass");
    public static final PluginConfigKeys<String> TIME_CONTROL_MODE = new PluginConfigKeys<>("protocol", "globalSettings", "time-control-mode");

    public static final PluginConfigKeys<String> MAP_VOTING_MODE = new PluginConfigKeys<>("vote", "map-voting", "mode");
    public static final PluginConfigKeys<Integer> MAP_VOTING_SELECTION = new PluginConfigKeys<>(3, "map-voting", "voting-selection");
    public static final PluginConfigKeys<String> MAP_DEFAULT = new PluginConfigKeys<>("example", "map-voting", "default-map");

    public static final PluginConfigKeys<Boolean> FEATURE_BOUNTY_VOTING = new PluginConfigKeys<>(false, "feature-voting", "enable-bounty-voting");
    public static final PluginConfigKeys<Boolean> FEATURE_CHEST_REFILL_VOTING = new PluginConfigKeys<>(false, "feature-voting", "chest-refill-voting");
    public static final PluginConfigKeys<Boolean> FEATURE_ENCHANTMENT_VOTING = new PluginConfigKeys<>(false, "feature-voting", "enchantment-voting");
    public static final PluginConfigKeys<Boolean> FEATURE_HEALTH_VOTING = new PluginConfigKeys<>(false, "feature-voting", "health-voting");
    public static final PluginConfigKeys<Boolean>  FEATURE_LOOT_DROP_VOTING = new PluginConfigKeys<>(false, "feature-voting", "loot-drop-voting");
    public static final PluginConfigKeys<Boolean> FEATURE_LOOT_STRENGTH_VOTING = new PluginConfigKeys<>(false, "feature-voting", "loot-strength-voting");

    public static final PluginConfigKeys<String> SPONSOR_VOTING_MODE = new PluginConfigKeys<>("random", "sponsor-voting", "mode");
    public static final PluginConfigKeys<Integer> SPONSOR_VOTING_REQUIRED_SPECTATORS = new PluginConfigKeys<>(2, "sponsor-voting", "required-spectators");
    public static final PluginConfigKeys<Integer> SPONSOR_VOTING_REQUIRED_PLAYERS = new PluginConfigKeys<>(3, "sponsor-voting", "required-players");
    public static final PluginConfigKeys<Integer> SPONSOR_VOTING_MAX_GAME = new PluginConfigKeys<>(2, "sponsor-voting", "max-sponsor-votes-per-game");
    public static final PluginConfigKeys<Integer> SPONSOR_VOTING_MAX_PHASE = new PluginConfigKeys<>(1, "sponsor-voting", "max-sponsor-votes-per-phase");
    public static final PluginConfigKeys<Integer> SPONSOR_VOTING_MIN_DELAY = new PluginConfigKeys<>(800, "sponsor-voting", "voting-delay-min");
    public static final PluginConfigKeys<Integer> SPONSOR_VOTING_TRIGGER_WITHIN = new PluginConfigKeys<>(500, "sponsor-voting", "voting-try-trigger-within");
    public static final PluginConfigKeys<Float> SPONSOR_VOTING_INITIAL_PROBABILITY = new PluginConfigKeys<>(0.5f, "sponsor-voting", "voting-trigger-probability");
    public static final PluginConfigKeys<Float> SPONSOR_VOTING_PROBABILITY_DELTA = new PluginConfigKeys<>(0.1f, "sponsor-voting", "voting-trigger-probability-increment");

    public static final PluginConfigKeys<Boolean> LOBBY_PREGAME_ENABLE = new PluginConfigKeys<>(false, "lobby.pregame", "enabled");
    public static final PluginConfigKeys<String> LOBBY_PREGAME_LOCATION = new PluginConfigKeys<>(LOCATION_EXAMPLE, "lobby.pregame", "location");
    public static final PluginConfigKeys<Boolean> LOBBY_POSTGAME_ENABLE = new PluginConfigKeys<>(false, "lobby.postgame", "enabled");
    public static final PluginConfigKeys<String> LOBBY_POSTGAME_LOCATION = new PluginConfigKeys<>(LOCATION_EXAMPLE, "lobby.postgame", "location");
    public static final PluginConfigKeys<Integer> LOBBY_POSTGAME_DURATION = new PluginConfigKeys<>(200, "lobby.postgame", "duration");
    public static final PluginConfigKeys<Boolean> LOBBY_POSTGAME_TELEPORT_BACK = new PluginConfigKeys<>(true, "lobby.postgame", "teleport-back");
    public static final PluginConfigKeys<Boolean> LOBBY_POSTGAME_PODIUM_ENABLE = new PluginConfigKeys<>(false, "lobby.postgame.podium", "enabled");
    public static final PluginConfigKeys<Boolean> LOBBY_POSTGAME_PODIUM_CAN_MOVE = new PluginConfigKeys<>(true, "lobby.postgame.podium", "can-move");
    public static final PluginConfigKeys<String> LOBBY_POSTGAME_PODIUM_POSITION_FIRST = new PluginConfigKeys<>(LOCATION_EXAMPLE, "lobby.postgame.podium", "positions.1");
    public static final PluginConfigKeys<String> LOBBY_POSTGAME_PODIUM_POSITION_SECOND = new PluginConfigKeys<>(LOCATION_EXAMPLE, "lobby.postgame.podium", "positions.2");
    public static final PluginConfigKeys<String> LOBBY_POSTGAME_PODIUM_POSITION_THIRD = new PluginConfigKeys<>(LOCATION_EXAMPLE, "lobby.postgame.podium", "positions.3");
    public static final PluginConfigKeys<String> LOBBY_POSTGAME_PODIUM_POSITION_LAST = new PluginConfigKeys<>(LOCATION_EXAMPLE, "lobby.postgame.podium", "positions.last");

    public static final PluginConfigKeys<Boolean> TEAMS_ENABLE = new PluginConfigKeys<>(false, "teams", "enabled");
    public static final PluginConfigKeys<String> TEAMS_BALANCING_MODE = new PluginConfigKeys<>("required", "teams", "balancing-mode");
    public static final PluginConfigKeys<Boolean> TEAMS_ALLOW_SOLO_QUEUE = new PluginConfigKeys<>(true, "teams", "allow-solo-queueing");
    public static final PluginConfigKeys<Boolean> TEAMS_ALLOW_SMALL_PARTY_QUEUE = new PluginConfigKeys<>(true, "teams", "allow-small-party-queueing");
    public static final PluginConfigKeys<Boolean> TEAMS_ALLOW_RESPAWNS = new PluginConfigKeys<>(true, "teams", "allow-respawns");

    public static final PluginConfigKeys<Boolean> RECORD_GAMES = new PluginConfigKeys<>(true, "record-games");
    public static final PluginConfigKeys<Boolean> RECORD_STATS = new PluginConfigKeys<>(true, "record-stats");

    public static final PluginConfigKeys<DataStoreSource> STORAGE_MODE = new PluginConfigKeys<>(DataStoreSource.NONE, DataStoreSource.class, "storage", "mode");
    public static final PluginConfigKeys<String> STORAGE_TABLE_PREFIX = new PluginConfigKeys<>("sg_", "storage", "table-prefix");
    public static final PluginConfigKeys<String> STORAGE_MYSQL_ADDRESS = new PluginConfigKeys<>("127.0.0.1", "storage.mysql", "address");
    public static final PluginConfigKeys<Integer> STORAGE_MYSQL_PORT = new PluginConfigKeys<>(3306, "storage.mysql", "port");
    public static final PluginConfigKeys<String> STORAGE_MYSQL_USERNAME = new PluginConfigKeys<>("root", "storage.mysql", "username");
    public static final PluginConfigKeys<String> STORAGE_MYSQL_PASSWORD = new PluginConfigKeys<>("", "storage.mysql", "password");
    public static final PluginConfigKeys<String> STORAGE_MYSQL_DATABASE = new PluginConfigKeys<>("sg", "storage.mysql", "database");
    public static final PluginConfigKeys<Integer> STORAGE_MYSQL_POOL_MAX_SIZE = new PluginConfigKeys<>(10, "storage.mysql", "pool.maximum-pool-size");
    public static final PluginConfigKeys<Long> STORAGE_MYSQL_POOL_MAX_LIFETIME = new PluginConfigKeys<>(1800000L, "storage.mysql", "pool.maximum-lifetime");
    public static final PluginConfigKeys<Long> STORAGE_MYSQL_POOL_CONNECTION_TIMEOUT = new PluginConfigKeys<>(5000L, "storage.mysql", "pool.connection-timeout");

    private final T DEF;
    private final Class<? extends Enum> ENUM_CLASS;
    private final String SECTION;
    private final String KEY;
    private final String FULL_KEY;

    private PluginConfigKeys(T def, String key) {
        this(def, null, null, key);
    }

    private PluginConfigKeys(T def, String section, String key) {
        this(def, null, section, key);
    }

    private PluginConfigKeys(T def, Class<? extends Enum> enumClass, String key) {
        this(def, enumClass, null, key);
    }

    private PluginConfigKeys(T def, Class<? extends Enum> enumClass, String section, String key) {
        DEF = def;
        ENUM_CLASS = enumClass;
        SECTION = section;
        KEY = key;
        FULL_KEY = section == null ? KEY : section + '.' + KEY;

        VALUES.add(this);
    }

    public String getSection() {
        return SECTION;
    }

    public String getKey() {
        return KEY;
    }

    public String getPath() {
        return FULL_KEY;
    }

    public T getDefault() {
        return DEF;
    }

    public T get(Configuration configuration) {
        if (ENUM_CLASS == null) {
            return (T) configuration.get(FULL_KEY, DEF);
        }

        try {
            return (T) Enum.valueOf(ENUM_CLASS, configuration.getString(FULL_KEY, "").toUpperCase());
        } catch (IllegalArgumentException unused) {
            unused.printStackTrace();
            return DEF;
        }
    }

    public T getFromSection(ConfigurationSection configurationSection) {
        if (ENUM_CLASS == null) {
            return (T) configurationSection.get(KEY, DEF);
        }

        try {
            return (T) Enum.valueOf(ENUM_CLASS, configurationSection.getString(KEY, "").toUpperCase());
        } catch (IllegalArgumentException unused) {
            return DEF;
        }
    }

    public static Iterable<PluginConfigKeys<?>> values() {
        return Collections.unmodifiableList(VALUES);
    }
}

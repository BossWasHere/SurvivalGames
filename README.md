# BackwardsNode's SurvivalGames

Survival Games evolved and updated to Minecraft 1.18.x.

A lightweight plugin boasting an intuitive map editor and interface, fast-paced gameplay elements and many more features!

## Features
+ Built-in map editor!
  + Create your own maps with ease
  + Place chests to be filled for every game
  + Customize items that generate in each chest
  + Customize every setting without touching configuration files! - **WIP**
+ Fully customizable Survival Games experience
  + Play with 2 or more players!
  + Loot drops
  + Optional entry fees (requires Vault)
  + Item or cash rewards (cash rewards require Vault)
  + Shrinking play area and support for multiple deathmatch arenas
  + Control time between phases
    + PvP off duration
    + Time until deathmatch
    + Etc.
  + Lightning and firework kill effects
  + Custom spawn locations
  + Control time of day
  + Runs as a dedicated gamemode or alongside an existing world
+ Spectator mode
  + Look at player inventories
  + Vote on game events (bounties, loot drops, etc.) - **WIP**
+ World safety features
  + Adventure mode by default
  + Anvil protection
  + Prevents use of ender chests
+ Multi-world support
+ Update migration support
+ Plugin hooks
  + [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)
  + [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) (required for world border)
  + [Vault](https://github.com/MilkBowl/Vault) (required for fees and rewards)
+ Event API for your own plugins to use

## Planned and upcoming features
+ Map voting
+ Chest refilling
+ Blocking specific items in-game
+ Blocking specific commands in-game
+ History & database support
  + Game history
  + Player stats
    + Games played
    + K/D
    + Kills
    + Wins
    + Play time
+ Pre- and post-game lobbies
+ Game modifier voting
  + Loot strength
  + Starting health
  + Etc.
+ Auto-start game on server start
+ Auto-rematch
+ Bounties
+ Player sponsor voting
+ Party mode (join with friends)
+ Team modes
  + Respawn modes
  + Team deathmatch modes
+ Bungeecord support
  + Vote pass-in
  + Game events
  + Party support
+ Cosmetic items & rewards
+ Online statistics
+ World rollback support
  + Regional
  + Map-wide
+ World rules
  + Mob spawning
  + Grief management
+ Support for "survival mode"
  + Customizable block lists
+ Special items
  + Call in loot drops
  + Player tracking compass
+ Automatic update checking

### Placeholder API
The following placeholders can be used from this plugin:
- %sg_is_alive% - returns "alive" or "dead" when the player is in a game
- %sg_current_kills% - returns the number of kills a player has when in a game
- %sg_current_map% - returns the map the player is playing on/editing
- %sg_current_map_file% - returns the start of the filename the player is playing on/editing

## Compiling

This plugin uses Gradle for building. This plugin currently requires JDK 16+ (kept for potential future backward-compatibility projects).

To build:

```./gradlew build```

Or on Windows:

```gradlew.bat build```

The plugin will be saved to `build/libs/SurvivalGames.jar`

## Contributing

Pull requests for features or bug fixes are welcome! PRs may be rejected if there is a valid reason to do so, but if you wish to contribute, I invite you to do so!

If you find a bug or wish to report an issue/feature suggestion, please use the appropriate issue templates.

## Version History

### Version 2.0.0
+ Updated to support MC 1.18
+ Updated to Java 16 & Refactored codebase
+ Added Vault/Economy support for entry fees/rewards
+ Added new post-game rewards
+ Added loot drops
+ Added basic world protection
+ Added new event API
+ Updated to configuration v2
+ Improved localization features
+ Improved performance
+ Quality-of-life improvements

### Versions < 1.0.1
+ A lot of this plugin has been rewritten since v1, and is no longer supported.

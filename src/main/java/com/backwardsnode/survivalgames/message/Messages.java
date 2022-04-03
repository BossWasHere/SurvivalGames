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
package com.backwardsnode.survivalgames.message;

public class Messages {

    public static final class Command {

        public static final class SGAuto {

            public static final PluginMessage DESC = new PluginMessage("command.sgauto.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgauto.usage", false);
            public static final PluginMessage PERMISSION_SUGGEST = new PluginMessage("command.sgauto.permissionsuggest", false);
            public static final PluginMessage USER_VOTE_PLACEHOLD = new PluginMessage("command.sgauto.uservote", false);
            public static final PluginMessage IS_ENABLED = new PluginMessage("command.sgauto.isenabled");
            public static final PluginMessage IS_INVITE = new PluginMessage("command.sgauto.isinvite");
            public static final PluginMessage IS_DISABLED = new PluginMessage("command.sgauto.isdisabled");
            public static final PluginMessage SKIPPED = new PluginMessage("command.sgauto.skipped");
            public static final PluginMessage UPDATED = new PluginMessage("command.sgauto.updated");

        }

        public static final class SGCancel {

            public static final PluginMessage DESC = new PluginMessage("command.sgcancel.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgcancel.usage", false);
            public static final PluginMessage CANCEL = new PluginMessage("command.sgcancel.cancel");
            public static final PluginMessage LEAVE = new PluginMessage("command.sgcancel.leave");
            public static final PluginMessage NONE = new PluginMessage("command.sgcancel.none");

        }

        public static final class SGCheck {

            public static final PluginMessage DESC = new PluginMessage("command.sgcheck.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgcheck.usage", false);

        }

        public static final class SGDeathmatch {

            public static final PluginMessage DESC = new PluginMessage("command.sgdeathmatch.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgdeathmatch.usage", false);
            public static final PluginMessage ALREADY_STARTED = new PluginMessage("command.sgdeathmatch.alreadystarted");

        }

        public static final class SGDelete {

            public static final PluginMessage DESC = new PluginMessage("command.sgdelete.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgdelete.usage", false);
            public static final PluginMessage SUCCESS = new PluginMessage("command.sgdelete.success");

        }

        public static final class SGDiscard {

            public static final PluginMessage DESC = new PluginMessage("command.sgdiscard.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgdiscard.usage", false);
            public static final PluginMessage DISCARDED = new PluginMessage("command.sgdiscard.discarded");

        }

        public static final class SGEdit {

            public static final PluginMessage DESC = new PluginMessage("command.sgedit.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgedit.usage", false);
            public static final PluginMessage CLOSING = new PluginMessage("command.sgedit.closing");
            public static final PluginMessage CREATED = new PluginMessage("command.sgedit.created");
            public static final PluginMessage LOADED = new PluginMessage("command.sgedit.loaded");
            public static final PluginMessage NOT_EDITING = new PluginMessage("command.sgedit.notediting");
            public static final PluginMessage OPENING = new PluginMessage("command.sgedit.opening");

        }

        public static final class SGHelp {

            public static final PluginMessage DESC = new PluginMessage("command.sghelp.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sghelp.usage", false);
            public static final PluginMessage HELP_BANNER = new PluginMessage("command.sghelp.banner", false);

        }

        public static final class SGJoin {

            public static final PluginMessage DESC = new PluginMessage("command.sgjoin.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgjoin.usage", false);
            public static final PluginMessage CANCELLED = new PluginMessage("command.sgjoin.cancelled");
            public static final PluginMessage HOST_LEFT = new PluginMessage("command.sgjoin.hostleft");
            public static final PluginMessage JOINED = new PluginMessage("command.sgjoin.joined");
            public static final PluginMessage NO_INVITE = new PluginMessage("command.sgjoin.noinvite");

        }

        public static final class SGList {

            public static final PluginMessage DESC = new PluginMessage("command.sglist.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sglist.usage", false);
            public static final PluginMessage CHECK_CONFIG = new PluginMessage("command.sglist.checkconfig", false);
            public static final PluginMessage EDIT = new PluginMessage("command.sglist.edit", false);
            public static final PluginMessage LOAD_INVITE = new PluginMessage("command.sglist.loadinvite", false);
            public static final PluginMessage LOAD_ALL = new PluginMessage("command.sglist.loadall", false);
            public static final PluginMessage TITLE = new PluginMessage("command.sglist.title");

        }

        public static final class SGMigrate {

            public static final PluginMessage DESC = new PluginMessage("command.sgmigrate.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgmigrate.usage", false);
            public static final PluginMessage ATTEMPT = new PluginMessage("command.sgmigrate.attempt");
            public static final PluginMessage FUTURE = new PluginMessage("command.sgmigrate.future");
            public static final PluginMessage SUCCESS = new PluginMessage("command.sgmigrate.success");
            public static final PluginMessage UP_TO_DATE = new PluginMessage("command.sgmigrate.uptodate");

        }

        public static final class SGReloadLang {

            public static final PluginMessage DESC = new PluginMessage("command.sgreloadlang.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgreloadlang.usage", false);
            public static final PluginMessage SUCCESS = new PluginMessage("command.sgreloadlang.success");
            public static final PluginMessage NO_SUCH_LANG = new PluginMessage("command.sgreloadlang.nosuchlang");

        }

        public static final class SGStart {

            public static final PluginMessage DESC = new PluginMessage("command.sgstart.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgstart.usage", false);
            public static final PluginMessage EXISTING_INVITE = new PluginMessage("command.sgstart.existinginvite");

        }

        public static final class SGStop {

            public static final PluginMessage DESC = new PluginMessage("command.sgstop.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgstop.usage", false);
            public static final PluginMessage STOPPED = new PluginMessage("command.sgstop.stopped");

        }

        public static final class SGVote {

            public static final PluginMessage DESC = new PluginMessage("command.sgvote.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgvote.usage", false);

        }

        public static final class TestCommand {

            public static final PluginMessage DESC = new PluginMessage("command.test.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.test.usage", false);

        }

        public static final PluginMessage ONLY_CONSOLE = new PluginMessage("command.default.onlyconsole");
        public static final PluginMessage ONLY_PLAYERS = new PluginMessage("command.default.onlyplayers");
        public static final PluginMessage IN_COOLDOWN = new PluginMessage("command.default.incooldown");
        public static final PluginMessage NO_PERM = new PluginMessage("command.default.noperm");
        public static final PluginMessage PLAYER_NOT_FOUND = new PluginMessage("command.default.playernotfound");

    }

    public static final class Config {

        public static final PluginMessage CHEST_BAD_ITEMS = new PluginMessage("config.chests.baditems");
        public static final PluginMessage CHEST_IGNORE_MISSING = new PluginMessage("config.chests.ignoremissing");
        public static final PluginMessage CHEST_LOCATED = new PluginMessage("config.chests.located");
        public static final PluginMessage CHEST_MISSING = new PluginMessage("config.chests.missing");
        public static final PluginMessage CHEST_MISSING_CONSOLE = new PluginMessage("config.chests.missingconsole");
        public static final PluginMessage CHEST_MISSING_SIMPLE = new PluginMessage("config.chests.missingsimple");
        public static final PluginMessage SYNTAX = new PluginMessage("config.syntax");
        public static final PluginMessage OUTDATED = new PluginMessage("config.outdated");
        
    }

    public static final class Editor {

        public static final class Inventory {

            public static final PluginMessage ALL_ITEM_SETS_TITLE = new PluginMessage("editor.inventory.allitemsetstitle");
            public static final PluginMessage ITEM_SET_TITLE = new PluginMessage("editor.inventory.itemsettitle");
            public static final PluginMessage CHOOSE_SET_TITLE = new PluginMessage("editor.inventory.choosesetstitle");
            public static final PluginMessage BORDER_TITLE = new PluginMessage("editor.inventory.bordertitle");
            public static final PluginMessage DEATHMATCH_TITLE = new PluginMessage("editor.inventory.deathmatchtitle");
            public static final PluginMessage SETTINGS_TITLE = new PluginMessage("editor.inventory.settingstitle");

        }

        public static final PluginMessage ADD_CHEST_FIRST = new PluginMessage("editor.addchestfirst");
        public static final PluginMessage ADDED_CHEST = new PluginMessage("editor.addeditemchest");
        public static final PluginMessage ADDED_ITEM_SET = new PluginMessage("editor.addeditemset");
        public static final PluginMessage ADDED_SPAWN_POS = new PluginMessage("editor.addedspawnpos");
        public static final PluginMessage REMOVED_CHEST = new PluginMessage("editor.removeditemchest");
        public static final PluginMessage REMOVED_SPAWN_POS = new PluginMessage("editor.removedspawnpos");

        public static final PluginMessage BAD_VALUE = new PluginMessage("editor.badvalue");
        public static final PluginMessage CANCEL_INPUT = new PluginMessage("editor.cancelinput");

        public static final PluginMessage BORDER_DPS_REQUEST = new PluginMessage("editor.borderdps");
        public static final PluginMessage BORDER_RADIUS_REQUEST = new PluginMessage("editor.borderradius");
        public static final PluginMessage BORDER_UNAVAILABLE = new PluginMessage("editor.borderunavailable");
        public static final PluginMessage UPDATED_BORDER_DPS = new PluginMessage("editor.setborderdps");
        public static final PluginMessage UPDATED_BORDER_RADIUS = new PluginMessage("editor.setborderradius");

        public static final PluginMessage DISABLED_AUTOFILL = new PluginMessage("editor.disableautofill");
        public static final PluginMessage DISABLED_BORDER = new PluginMessage("editor.disableborder");
        public static final PluginMessage DISABLED_DEATH_FIREWORK = new PluginMessage("editor.disabledeathfirework");
        public static final PluginMessage DISABLED_KILL_FIREWORK = new PluginMessage("editor.disablekillfirework");
        public static final PluginMessage DISABLED_DEATH_LIGHTNING = new PluginMessage("editor.disabledeathlightning");
        public static final PluginMessage DISABLED_WIP = new PluginMessage("editor.disablewip");
        public static final PluginMessage ENABLED_AUTOFILL = new PluginMessage("editor.enableautofill");
        public static final PluginMessage ENABLED_BORDER = new PluginMessage("editor.enableborder");
        public static final PluginMessage ENABLED_DEATH_FIREWORK = new PluginMessage("editor.enabledeathfirework");
        public static final PluginMessage ENABLED_KILL_FIREWORK = new PluginMessage("editor.enablekillfirework");
        public static final PluginMessage ENABLED_DEATH_LIGHTNING = new PluginMessage("editor.enabledeathlightning");
        public static final PluginMessage ENABLED_WIP = new PluginMessage("editor.enablewip");

        public static final PluginMessage MODIFIED_ITEMS = new PluginMessage("editor.modifyitems");
        public static final PluginMessage MODIFY_ITEMS_ERR = new PluginMessage("editor.modifyitemserr");

        public static final PluginMessage NAME_ITEM_SET_REQUEST = new PluginMessage("editor.nameitemset");
        public static final PluginMessage REMOVED_ITEM_SET = new PluginMessage("editor.removeditemset");
        public static final PluginMessage RENAME_ITEM_SET_REQUEST = new PluginMessage("editor.renameitemset");
        public static final PluginMessage RENAMED_ITEM_SET = new PluginMessage("editor.setitemsetname");
        public static final PluginMessage UPDATED_ITEM_SET = new PluginMessage("editor.updateditems");
        public static final PluginMessage UPDATE_ITEM_SET_ERR = new PluginMessage("editor.updateditemserr");

        public static final PluginMessage NO_DEATHMATCH_LOCS = new PluginMessage("editor.nodeathmatch");

        public static final PluginMessage PRE_SHRINK_DURATION_REQUEST = new PluginMessage("editor.timebeforeshrink");
        public static final PluginMessage PVP_OFF_TIME_REQUEST = new PluginMessage("editor.nopvptime");
        public static final PluginMessage WAIT_PERIOD_REQUEST = new PluginMessage("editor.waitperiod");
        public static final PluginMessage UPDATED_PRE_SHRINK_DURATION = new PluginMessage("editor.settimebeforeshrink");
        public static final PluginMessage UPDATED_PVP_OFF_TIME = new PluginMessage("editor.setnopvptime");
        public static final PluginMessage UPDATED_WAIT_PERIOD = new PluginMessage("editor.setwaitperiod");

        public static final PluginMessage RENAME_MAP_REQUEST = new PluginMessage("editor.renamemap");
        public static final PluginMessage RENAMED_MAP = new PluginMessage("editor.setmapname");

    }

    public static final class Game {

        public static final PluginMessage AS_SPECTATOR = new PluginMessage("game.asspectator");
        public static final PluginMessage BORDER_SHRINKING = new PluginMessage("game.bordershrinking");
        public static final PluginMessage DEATH_GENERIC = new PluginMessage("game.death.generic");
        public static final PluginMessage DEATH_KILLED = new PluginMessage("game.death.killed");
        public static final PluginMessage DEATHMATCH = new PluginMessage("game.deathmatch");
        public static final PluginMessage FREE_ENTRY = new PluginMessage("game.freeentry");
        public static final PluginMessage INSUFFICIENT_FUNDS = new PluginMessage("game.insufficientfunds");
        public static final PluginMessage INSUFFICIENT_FUNDS_SPECTATING_INSTEAD = new PluginMessage("game.insufficientfundsspectating");
        public static final PluginMessage INSUFFICIENT_PLAYERS = new PluginMessage("game.fewplayers");
        public static final PluginMessage INVITE_OPEN = new PluginMessage("invite.open");
        public static final PluginMessage INVITE_DURATION = new PluginMessage("invite.duration");
        public static final PluginMessage KILLSTAT_1 = new PluginMessage("game.killstat.1", false);
        public static final PluginMessage KILLSTAT_2 = new PluginMessage("game.killstat.2", false);
        public static final PluginMessage KILLSTAT_3 = new PluginMessage("game.killstat.3", false);
        public static final PluginMessage KILLSTATS = new PluginMessage("game.killstats");
        public static final PluginMessage MAP_IN_USE = new PluginMessage("game.mapinuse");
        public static final PluginMessage NO_GAME = new PluginMessage("game.nogame");
        public static final PluginMessage NOT_PLAYING = new PluginMessage("game.notplaying");
        public static final PluginMessage NOT_RUNNING = new PluginMessage("game.notrunning");
        public static final PluginMessage PLAYER_IN_GAME = new PluginMessage("game.playeringame");
        public static final PluginMessage PLAYING_ON = new PluginMessage("game.playingon");
        public static final PluginMessage PVP_ENABLED = new PluginMessage("game.pvpenabled");
        public static final PluginMessage PVP_OFF_TIME = new PluginMessage("game.pvpofftime");
        public static final PluginMessage START = new PluginMessage("game.start");
        public static final PluginMessage START_WITHOUT_SPECTATORS = new PluginMessage("game.startwithoutspec");
        public static final PluginMessage START_WITH_SPECTATORS = new PluginMessage("game.startwithspec");
        public static final PluginMessage LOOT_DROPPING = new PluginMessage("game.lootdropping");
        public static final PluginMessage WON = new PluginMessage("game.won");
        public static final PluginMessage REWARDS_OVERFLOW = new PluginMessage("game.rewardsoverflow");
    }

    public static final class Misc {

        public static final PluginMessage BORDER_SELECT_LORE = new PluginMessage("item.borderselect.lore", false);

    }

    public static final class Plugin {

        public static final PluginMessage PREFIX = new PluginMessage("_prefix", false);

        public static final PluginMessage IO_FILE_FOUND = new PluginMessage("io.filefound", false);
        public static final PluginMessage IO_FILE_MISSING = new PluginMessage("io.filemissing", false);
        public static final PluginMessage IO_FILE_LOADED = new PluginMessage("io.fileloaded", false);
        public static final PluginMessage IO_EXCEPTION = new PluginMessage("io.exception", false);

    }

}

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

    public static final class COMMAND {

        public static final class SG_CANCEL {

            public static final PluginMessage DESC = new PluginMessage("command.sgcancel.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgcancel.usage", false);
            public static final PluginMessage CANCEL = new PluginMessage("command.sgcancel.cancel");
            public static final PluginMessage LEAVE = new PluginMessage("command.sgcancel.leave");
            public static final PluginMessage NONE = new PluginMessage("command.sgcancel.none");

        }

        public static final class SG_CHECK {

            public static final PluginMessage DESC = new PluginMessage("command.sgcheck.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgcheck.usage", false);

        }

        public static final class SG_DEATHMATCH {

            public static final PluginMessage DESC = new PluginMessage("command.sgdeathmatch.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgdeathmatch.usage", false);
            public static final PluginMessage ALREADY_STARTED = new PluginMessage("command.sgdeathmatch.alreadystarted");

        }

        public static final class SG_DELETE {

            public static final PluginMessage DESC = new PluginMessage("command.sgdelete.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgdelete.usage", false);
            public static final PluginMessage SUCCESS = new PluginMessage("command.sgdelete.success");

        }

        public static final class SG_DISCARD {

            public static final PluginMessage DESC = new PluginMessage("command.sgdiscard.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgdiscard.usage", false);
            public static final PluginMessage DISCARDED = new PluginMessage("command.sgdiscard.discarded");

        }

        public static final class SG_EDIT {

            public static final PluginMessage DESC = new PluginMessage("command.sgedit.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgedit.usage", false);
            public static final PluginMessage CLOSING = new PluginMessage("command.sgedit.closing");
            public static final PluginMessage CREATED = new PluginMessage("command.sgedit.created");
            public static final PluginMessage LOADED = new PluginMessage("command.sgedit.loaded");
            public static final PluginMessage NOT_EDITING = new PluginMessage("command.sgedit.notediting");
            public static final PluginMessage OPENING = new PluginMessage("command.sgedit.opening");

        }

        public static final class SG_JOIN {

            public static final PluginMessage DESC = new PluginMessage("command.sgjoin.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgjoin.usage", false);
            public static final PluginMessage CANCELLED = new PluginMessage("command.sgjoin.cancelled");
            public static final PluginMessage HOST_LEFT = new PluginMessage("command.sgjoin.hostleft");
            public static final PluginMessage JOINED = new PluginMessage("command.sgjoin.joined");
            public static final PluginMessage NO_INVITE = new PluginMessage("command.sgjoin.noinvite");

        }

        public static final class SG_LIST {

            public static final PluginMessage DESC = new PluginMessage("command.sglist.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sglist.usage", false);
            public static final PluginMessage CHECK_CONFIG = new PluginMessage("command.sglist.checkconfig", false);
            public static final PluginMessage EDIT = new PluginMessage("command.sglist.edit", false);
            public static final PluginMessage LOAD_INVITE = new PluginMessage("command.sglist.loadinvite", false);
            public static final PluginMessage LOAD_ALL = new PluginMessage("command.sglist.loadall", false);
            public static final PluginMessage TITLE = new PluginMessage("command.sglist.title");

        }

        public static final class SG_MIGRATE {

            public static final PluginMessage DESC = new PluginMessage("command.sgmigrate.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgmigrate.usage", false);
            public static final PluginMessage ATTEMPT = new PluginMessage("command.sgmigrate.attempt");
            public static final PluginMessage FUTURE = new PluginMessage("command.sgmigrate.future");
            public static final PluginMessage SUCCESS = new PluginMessage("command.sgmigrate.success");
            public static final PluginMessage UP_TO_DATE = new PluginMessage("command.sgmigrate.uptodate");

        }

        public static final class SG_RELOAD_LANG {

            public static final PluginMessage DESC = new PluginMessage("command.sgreloadlang.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgreloadlang.usage", false);
            public static final PluginMessage SUCCESS = new PluginMessage("command.sgreloadlang.success");
            public static final PluginMessage NO_SUCH_LANG = new PluginMessage("command.sgreloadlang.nosuchlang");

        }

        public static final class SG_START {

            public static final PluginMessage DESC = new PluginMessage("command.sgstart.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgstart.usage", false);
            public static final PluginMessage EXISTING_INVITE = new PluginMessage("command.sgstart.existinginvite");

        }

        public static final class SG_STOP {

            public static final PluginMessage DESC = new PluginMessage("command.sgstop.description", false);
            public static final PluginMessage USAGE = new PluginMessage("command.sgstop.usage", false);
            public static final PluginMessage STOPPED = new PluginMessage("command.sgstop.stopped");

        }

        public static final PluginMessage ONLY_CONSOLE = new PluginMessage("command.default.onlyconsole");
        public static final PluginMessage ONLY_PLAYERS = new PluginMessage("command.default.onlyplayers");
        public static final PluginMessage IN_COOLDOWN = new PluginMessage("command.default.incooldown");
        public static final PluginMessage NO_PERM = new PluginMessage("command.default.noperm");
        public static final PluginMessage PLAYER_NOT_FOUND = new PluginMessage("command.default.playernotfound");

    }

    public static final class CONFIG {

        public static final PluginMessage CHEST_BAD_ITEMS = new PluginMessage("config.chests.baditems");
        public static final PluginMessage CHEST_IGNORE_MISSING = new PluginMessage("config.chests.ignoremissing");
        public static final PluginMessage CHEST_LOCATED = new PluginMessage("config.chests.located");
        public static final PluginMessage CHEST_MISSING = new PluginMessage("config.chests.missing");
        public static final PluginMessage CHEST_MISSING_CONSOLE = new PluginMessage("config.chests.missingconsole");
        public static final PluginMessage CHEST_MISSING_SIMPLE = new PluginMessage("config.chests.missingsimple");
        public static final PluginMessage SYNTAX = new PluginMessage("config.syntax");
        public static final PluginMessage OUTDATED = new PluginMessage("config.outdated");
        
    }

    public static final class EDITOR {

        public static final class INVENTORY {

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
        public static final PluginMessage DISABLED_WIP = new PluginMessage("editor.disablewip");
        public static final PluginMessage ENABLED_AUTOFILL = new PluginMessage("editor.enableautofill");
        public static final PluginMessage ENABLED_BORDER = new PluginMessage("editor.enableborder");
        public static final PluginMessage ENABLED_DEATH_FIREWORK = new PluginMessage("editor.enabledeathfirework");
        public static final PluginMessage ENABLED_KILL_FIREWORK = new PluginMessage("editor.enablekillfirework");
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

    public static final class GAME {

        public static final PluginMessage AS_SPECTATOR = new PluginMessage("game.asspectator");
        public static final PluginMessage BORDER_SHRINKING = new PluginMessage("game.bordershrinking");
        public static final PluginMessage DEATH_GENERIC = new PluginMessage("game.death.generic");
        public static final PluginMessage DEATH_KILLED = new PluginMessage("game.death.killed");
        public static final PluginMessage DEATHMATCH = new PluginMessage("game.deathmatch");
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
        public static final PluginMessage WON = new PluginMessage("game.won");
        public static final PluginMessage REWARDS_OVERFLOW = new PluginMessage("game.rewardsoverflow");

    }

    public static final class ITEM {
//
//        public static final PluginMessage ADD_ITEM_SET_LORE = new PluginMessage("item.additemset.lore", false);
//        public static final PluginMessage ADD_ITEM_SET_NAME = new PluginMessage("item.additemset.name", false);
//        public static final PluginMessage BORDER_DPS_LORE = new PluginMessage("item.borderdps.lore", false);
//        public static final PluginMessage BORDER_DPS_NAME = new PluginMessage("item.borderdps.name", false);
//        public static final PluginMessage BORDER_ITEM_LORE = new PluginMessage("item.borderitem.lore", false);
//        public static final PluginMessage BORDER_ITEM_NAME = new PluginMessage("item.borderitem.name", false);
        public static final PluginMessage BORDER_SELECT_LORE = new PluginMessage("item.borderselect.lore", false);
//        public static final PluginMessage BORDER_SELECT_NAME = new PluginMessage("item.borderselect.name", false);
//        public static final PluginMessage BORDER_START_LORE = new PluginMessage("item.borderstart.lore", false);
//        public static final PluginMessage BORDER_START_NAME = new PluginMessage("item.borderstart.name", false);
//        public static final PluginMessage CHEST_LORE = new PluginMessage("item.chest.lore", false);
//        public static final PluginMessage CHEST_NAME = new PluginMessage("item.chest.name", false);
//        public static final PluginMessage CHEST_ITEMS_LORE = new PluginMessage("item.chestitems.lore", false);
//        public static final PluginMessage CHEST_ITEMS_NAME = new PluginMessage("item.chestitems.name", false);
//        public static final PluginMessage DEATH_FIREWORK_LORE = new PluginMessage("item.deathfirework.lore", false);
//        public static final PluginMessage DEATH_FIREWORK_NAME = new PluginMessage("item.deathfirework.name", false);
//        public static final PluginMessage DEATHMATCH_LORE = new PluginMessage("item.deathmatch.lore", false);
//        public static final PluginMessage DEATHMATCH_NAME = new PluginMessage("item.deathmatch.name", false);
//        public static final PluginMessage DEATHMATCH_BORDER_LORE = new PluginMessage("item.deathmatchborder.lore", false);
//        public static final PluginMessage DEATHMATCH_BORDER_NAME = new PluginMessage("item.deathmatchborder.name", false);
//        public static final PluginMessage HIDE_BORDER_LORE = new PluginMessage("item.hideborder.lore", false);
//        public static final PluginMessage HIDE_BORDER_NAME = new PluginMessage("item.hideborder.name", false);
//        public static final PluginMessage IMPLEMENTS_LORE = new PluginMessage("item.implements.lore", false);
//        public static final PluginMessage IMPLEMENTS_NAME = new PluginMessage("item.implements.name", false);
//        public static final PluginMessage INITIAL_BORDER_LORE = new PluginMessage("item.initialborder.lore", false);
//        public static final PluginMessage INITIAL_BORDER_NAME = new PluginMessage("item.initialborder.name", false);
//        public static final PluginMessage INV_SWITCH_LORE = new PluginMessage("item.invswitch.lore", false);
//        public static final PluginMessage INV_SWITCH_NAME = new PluginMessage("item.invswitch.name", false);
//        public static final PluginMessage ISWIP_LORE = new PluginMessage("item.iswip.lore", false);
//        public static final PluginMessage ISWIP_NAME = new PluginMessage("item.iswip.name", false);
//        public static final PluginMessage ITEMSET_LORE = new PluginMessage("item.itemset.lore", false);
//        public static final PluginMessage ITEMSET_NAME = new PluginMessage("item.itemset.name", false);
//        public static final PluginMessage KILL_FIREWORK_LORE = new PluginMessage("item.killfirework.lore", false);
//        public static final PluginMessage KILL_FIREWORK_NAME = new PluginMessage("item.killfirework.name", false);
//        public static final PluginMessage MAP_NAME_LORE = new PluginMessage("item.mapname.lore", false);
//        public static final PluginMessage MAP_NAME_NAME = new PluginMessage("item.mapname.name", false);
//        public static final PluginMessage NO_PVP_TIME_LORE = new PluginMessage("item.nopvptime.lore", false);
//        public static final PluginMessage NO_PVP_TIME_NAME = new PluginMessage("item.nopvptime.name", false);
//        public static final PluginMessage PLATE_LORE = new PluginMessage("item.plate.lore", false);
//        public static final PluginMessage PLATE_NAME = new PluginMessage("item.plate.name", false);
//        public static final PluginMessage PREFILL_LORE = new PluginMessage("item.prefill.lore", false);
//        public static final PluginMessage PREFILL_NAME = new PluginMessage("item.prefill.name", false);
//        public static final PluginMessage SETTINGS_LORE = new PluginMessage("item.settings.lore", false);
//        public static final PluginMessage SETTINGS_NAME = new PluginMessage("item.settings.name", false);
//        public static final PluginMessage SHRINK_TIME_LORE = new PluginMessage("item.shrinktime.lore", false);
//        public static final PluginMessage SHRINK_TIME_NAME = new PluginMessage("item.shrinktime.name", false);
//        public static final PluginMessage WAIT_TIME_LORE = new PluginMessage("item.waittime.lore", false);
//        public static final PluginMessage WAIT_TIME_NAME = new PluginMessage("item.waittime.name", false);
//
    }

    public static final class PLUGIN {

        public static final PluginMessage PREFIX = new PluginMessage("_prefix", false);

        public static final PluginMessage IO_FILE_FOUND = new PluginMessage("io.filefound", false);
        public static final PluginMessage IO_FILE_MISSING = new PluginMessage("io.filemissing", false);
        public static final PluginMessage IO_FILE_LOADED = new PluginMessage("io.fileloaded", false);
        public static final PluginMessage IO_EXCEPTION = new PluginMessage("io.exception", false);

    }

}

/*
 *  BackwardsNode's Survival Games, a Minecraft Bukkit custom gamemode
 *  Copyright (C) 2019 BackwardsNode/BossWasHere
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.backwardsnode.survivalgames.json;

import static com.google.common.base.Preconditions.*;

public class TextEvent {

	public String action;
	public String value;
	
	public static TextEvent openURL(String url) {
		TextEvent te = new TextEvent();
		te.action = "open_url";
		te.value = url;
		return te;
	}
	
	public static TextEvent runCommand(String command) {
		TextEvent te = new TextEvent();
		te.action = "run_command";
		te.value = command;
		return te;
	}
	
	public static TextEvent showText(String msg) {
		TextEvent te = new TextEvent();
		te.action = "show_text";
		te.value = msg;
		return te;
	}
	
	public boolean isClickEvent() {
		return isClickEvent(this);
	}
	
	public boolean isHoverEvent() {
		return isHoverEvent(this);
	}
	
	public static final boolean isClickEvent(TextEvent e) {
		checkNotNull(e);
		switch (e.action.toLowerCase()) {
		case "run_command":
		case "suggest_command":
		case "open_url":
		case "change_page":
			return true;
		}
		return false;
	}
	
	public static final boolean isHoverEvent(TextEvent e) {
		checkNotNull(e);
		switch (e.action.toLowerCase()) {
		case "show_text":
		case "show_item":
		case "show_entity":
		case "show_achievement":
			return true;
		}
		return false;
	}
}

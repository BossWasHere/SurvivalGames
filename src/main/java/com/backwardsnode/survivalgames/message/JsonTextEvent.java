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

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

/**
 * A class for serializing JSON message events
 *
 */
public class JsonTextEvent {

    /**
     * The name of the action
     */
    public JsonTextAction action;
    /**
     * The value of the action
     */
    public String value;

    /**
     * Builds a new {@code JsonTextEvent} with a URL click event
     * @param url The target URL
     * @return The new JsonTextEvent object
     */
    public static JsonTextEvent openURL(String url) {
        return new JsonTextEvent(JsonTextAction.OPEN_URL, url);
    }

    /**
     * Builds a new {@code JsonTextEvent} with a command click event
     * @param command The command to execute
     * @return The new JsonTextEvent object
     */
    public static JsonTextEvent runCommand(String command) {
        return new JsonTextEvent(JsonTextAction.RUN_COMMAND, command);
    }

    /**
     * Builds a new {@code JsonTextEvent} with a show text hover event
     * @param msg The text to reveal
     * @return The new JsonTextEvent object
     */
    public static JsonTextEvent showText(String msg) {
        return new JsonTextEvent(JsonTextAction.SHOW_TEXT, msg);
    }

    /**
     * Constructs a new instance of {@code JsonTextEvent}
     * @param action The action which should trigger this event
     * @param value The value associated with the event
     */
    public JsonTextEvent(JsonTextAction action, String value) {
        this.action = action;
        this.value = value;
    }

    /**
     * Verifies if the given event is a click event
     * @return True if the event is a click event, false if not
     */
    public boolean isClickEvent() {
        return isClickEvent(this);
    }

    /**
     * Verifies if the given event is a hover event
     * @return True if the event is a hover event, false if not
     */
    public boolean isHoverEvent() {
        return isHoverEvent(this);
    }

    /**
     * Verifies if the given event is a click event
     * @return True if the event is a click event, false if not
     */
    public static boolean isClickEvent(JsonTextEvent e) {
        return !e.action.isHoverAction;
    }

    /**
     * Verifies if the given event is a hover event
     * @return True if the event is a hover event, false if not
     */
    public static boolean isHoverEvent(JsonTextEvent e) {
        return e.action.isHoverAction;
    }

    public ClickEvent.Action toBungeeClickAction() throws IllegalStateException {
        if (isHoverEvent()) throw new IllegalStateException("Action is not a click action");

        return switch (action) {
            case OPEN_URL -> ClickEvent.Action.OPEN_URL;
            case RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND;
            case SUGGEST_COMMAND -> ClickEvent.Action.SUGGEST_COMMAND;
            case CHANGE_PAGE -> ClickEvent.Action.CHANGE_PAGE;
            case COPY_CLIPBOARD -> ClickEvent.Action.COPY_TO_CLIPBOARD;
            default -> null;
        };
    }

    public HoverEvent.Action toBungeeHoverAction() throws IllegalStateException {
        if (!isHoverEvent()) throw new IllegalStateException("Action is not a hover action");

        return switch (action) {
            case SHOW_TEXT -> HoverEvent.Action.SHOW_TEXT;
            case SHOW_ITEM -> HoverEvent.Action.SHOW_ITEM;
            case SHOW_ENTITY -> HoverEvent.Action.SHOW_ENTITY;
            default -> null;
        };
    }

}

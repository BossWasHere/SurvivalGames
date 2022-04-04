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

import com.backwardsnode.survivalgames.config.serialization.FalseBooleanAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for constructing JSON message strings
 *
 */
public class JsonMessage {

    /**
     * The main text within the message
     */
    public String text;
    /**
     * The color of the main text
     */
    public String color;
    /**
     * If the main text should be displayed in bold characters
     */
    @JsonAdapter(FalseBooleanAdapter.class)
    public boolean bold;
    /**
     * If the main text should be displayed with a strikethrough
     */
    @JsonAdapter(FalseBooleanAdapter.class)
    public boolean strikethrough;
    /**
     * If the main text should be displayed with an underline
     */
    @JsonAdapter(FalseBooleanAdapter.class)
    public boolean underlined;
    /**
     * If the main text should be displayed as slightly slanted
     */
    @JsonAdapter(FalseBooleanAdapter.class)
    public boolean italic;
    /**
     * If the main text should be displayed as obfuscated characters
     */
    @JsonAdapter(FalseBooleanAdapter.class)
    public boolean obfuscated;

    /**
     * The event which should be executed when the text is clicked
     */
    public JsonTextEvent clickEvent;
    /**
     * The event which should be executed when the pointer is hovered over the text
     */
    public JsonTextEvent hoverEvent;

    /**
     * The string insertion which should be used with the main text
     */
    public String insertion;

    public transient ChatColor textColor;

    /**
     * Sets the main text within the message
     * @param text The text to set
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Sets the color of the main text
     * @param color The color to set
     * @return This {@link JsonMessage} for chaining
     */
    @Deprecated
    public JsonMessage setColor(String color) {
        ChatColor cc = ChatColor.valueOf(color.toUpperCase());
        setColor(cc);
        return this;
    }

    public JsonMessage setColor(ChatColor color) {
        if (color.isColor()) {
            this.color = color.name().toLowerCase();
            textColor = color;
        }
        return this;
    }

    /**
     * Sets if the main text should be displayed in bold characters
     * @param bold If the text should be bold
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setBold(boolean bold) {
        this.bold = bold;
        return this;
    }

    /**
     * Sets if the main text should be displayed with a strikethrough
     * @param strikethrough If the text should be strikethrough
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        return this;
    }

    /**
     * Sets if the main text should be displayed with an underline
     * @param underlined If the text should be underlined
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setUnderlined(boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    /**
     * Sets if the main text should be displayed as slightly slanted
     * @param italic If the text should be italic
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setItalic(boolean italic) {
        this.italic = italic;
        return this;
    }

    /**
     * Sets if the main text should be displayed as obfuscated characters
     * @param obfuscated If the text should be obfuscated
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setObfuscated(boolean obfuscated) {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     * Applys a given {@link ChatColor} to this message
     * @param color The formatting to apply
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage apply(ChatColor color) {
        setColor(color);
        switch (color) {
            case MAGIC -> setObfuscated(true);
            case BOLD -> setBold(true);
            case STRIKETHROUGH -> setStrikethrough(true);
            case UNDERLINE -> setUnderlined(true);
            case ITALIC -> setItalic(true);
            case RESET -> {
                setObfuscated(false);
                setBold(false);
                setStrikethrough(false);
                setUnderlined(false);
                setItalic(false);
            }
        }

        return this;
    }

    /**
     * Sets the event which should be executed when the text is clicked
     * @param clickEvent The event to set
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setClickEvent(JsonTextEvent clickEvent) throws IllegalArgumentException {
        if (!clickEvent.isClickEvent()) {
            throw new IllegalArgumentException("Text Event is not of method Click Event");
        }
        this.clickEvent = clickEvent;
        return this;
    }

    /**
     * Sets the event which should be executed when the pointer is hovered over the text
     * @param hoverEvent The event to set
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setHoverEvent(JsonTextEvent hoverEvent) throws IllegalArgumentException {
        if (!hoverEvent.isHoverEvent()) {
            throw new IllegalArgumentException("Text Event is not of method Hover Event");
        }
        this.hoverEvent = hoverEvent;
        return this;
    }

    /**
     * Sets the string insertion which should be used with the main text
     * @param insertion The insertion text to set
     * @return This {@link JsonMessage} for chaining
     */
    public JsonMessage setInsertion(String insertion) {
        this.insertion = insertion;
        return this;
    }

    /**
     * Transforms this JsonMessage into a JSON string
     * @return The string value of the JsonMessage
     */
    @Override
    public String toString() {
        return build();
    }

    /**
     * Transforms this JsonMessage into a JSON string
     * @return The string value of the JsonMessage
     */
    public String build() {
        try {
            return new GsonBuilder().create().toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"text\":\"internal error\"}";
    }

    public TextComponent toBungeeChatComponent() {
        TextComponent component = new TextComponent();
        component.setText(text);

        if (textColor != null) {
            component.setColor(textColor.asBungee());
        }

        component.setBold(bold);
        component.setStrikethrough(strikethrough);
        component.setUnderlined(underlined);
        component.setItalic(italic);
        component.setObfuscated(obfuscated);

        if (insertion != null) {
            component.setInsertion(insertion);
        }

        if (clickEvent != null) {
            component.setClickEvent(new ClickEvent(clickEvent.toBungeeClickAction(), clickEvent.value));
        }

        if (hoverEvent != null) {
            component.setHoverEvent(new HoverEvent(hoverEvent.toBungeeHoverAction(), new Text(hoverEvent.value)));
        }

        return component;
    }

    /**
     * A class representing multiple {@link JsonMessage} objects in a sorted list
     *
     */
    public static class CompoundJsonMessage extends ArrayList<JsonMessage> {

        public CompoundJsonMessage() {
            super();
        }

    }
}

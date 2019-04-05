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

import java.util.ArrayList;
import java.util.List;

public class JsonMessage {

	public String text;
	public String color;
	public boolean bold;
	public boolean strikethrough;
	public boolean underlined;
	public boolean italic;
	public boolean obfuscated;
	
	public TextEvent clickEvent;
	public TextEvent hoverEvent;
	
	public String insertion;
	
	public JsonMessage setText(String text) {
		this.text = text;
		return this;
	}
	
	public JsonMessage setColor(String color) {
		this.color = color;
		return this;
	}
	
	public JsonMessage setBold(boolean bold) {
		this.bold = bold;
		return this;
	}
	
	public JsonMessage setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
		return this;
	}
	public JsonMessage setUnderlined(boolean underlined) {
		this.underlined = underlined;
		return this;
	}
	public JsonMessage setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}
	
	public JsonMessage setObfuscated(boolean obfuscated) {
		this.obfuscated = obfuscated;
		return this;
	}
	
	public JsonMessage setClickEvent(TextEvent clickEvent) throws IllegalArgumentException {
		if (!clickEvent.isClickEvent()) {
			throw new IllegalArgumentException("Text Event is not of method Click Event");
		}
		this.clickEvent = clickEvent;
		return this;
	}
	
	public JsonMessage setHoverEvent(TextEvent hoverEvent) throws IllegalArgumentException {
		if (!hoverEvent.isHoverEvent()) {
			throw new IllegalArgumentException("Text Event is not of method Hover Event");
		}
		this.hoverEvent = hoverEvent;
		return this;
	}
	
	public JsonMessage setInsertion(String insertion) {
		this.insertion = insertion;
		return this;
	}
	
	public static class CompoundJsonMessage {
		
		public List<JsonMessage> messages = new ArrayList<JsonMessage>();
		
	}
}

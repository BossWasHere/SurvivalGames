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
package com.backwardsnode.survivalgames.editor;

public class ChatQueryObject {

	private final Scene scene;
	private final QueryUpdater type;
	private Runnable after;
	
	private String value;
	private String key;
	
	public ChatQueryObject(Scene scene, QueryUpdater type) {
		this.scene = scene;
		this.type = type;
		after = null;
	}
	
	public ChatQueryObject(Scene scene, QueryUpdater type, Runnable after) {
		this.scene = scene;
		this.type = type;
		this.after = after;
	}
	
	public ChatQueryObject(Scene scene, QueryUpdater type, String key) {
		this.scene = scene;
		this.type = type;
		this.key = key;
	}
	
	public ChatQueryObject(Scene scene, QueryUpdater type, Runnable after, String key) {
		this.scene = scene;
		this.type = type;
		this.after = after;
		this.key = key;
	}
	
	public void attachRunnable(Runnable after) {
		this.after = after;
	}
	
	public boolean setValue(String value) {
		this.value = value;
		switch (type) {
		case NEW_ITEMSET_NAME:
			scene.createItemSet(value, false);
			break;
		case RENAME_ITEMSET_NAME:
			scene.renameItemSet(key, value);
			break;
		case MAP_NAME:
			scene.setMapName(value);
			break;
		case TIME_TO_DM:
			try {
				int i = Integer.parseInt(value);
				scene.setTimeToDeathmatch(i);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		case BORDER_DPS:
			try {
				double i = Double.parseDouble(value);
				scene.setBorderDps(i);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		case BORDER_START_RADIUS:
			try {
				int i = Integer.parseInt(value);
				scene.setBorderStartRadius(i);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		case GRACE_PERIOD:
			try {
				int i = Integer.parseInt(value);
				scene.setGracePeriod(i);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		case WAIT_PERIOD:
			try {
				int i = Integer.parseInt(value);
				scene.setWaitPeriod(i);
			} catch (NumberFormatException e) {
				return false;
			}
			break;
		default:
			break;
		}
		if (after != null) {
			after.run();
		}
		return true;
	}
	
	public String getLastValue() {
		return value;
	}
}

enum QueryUpdater {
	NEW_ITEMSET_NAME,
	RENAME_ITEMSET_NAME,
	MAP_NAME,
	BORDER_DPS,
	TIME_TO_DM,
	BORDER_START_RADIUS,
	WAIT_PERIOD,
	GRACE_PERIOD
}
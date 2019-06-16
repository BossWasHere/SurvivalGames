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
package com.backwardsnode.survivalgames.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.backwardsnode.survivalgames.VersionGetter;
import com.backwardsnode.survivalgames.json.JsonMessage;
import com.backwardsnode.survivalgames.json.JsonMessage.CompoundJsonMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChatUtil {

	public static boolean sendRawMessage(Player p, String message) {
		try {
			Class<?> packetClass = Class.forName(VersionGetter.getPackageHeader() + ".Packet");
			Class<?> packetChatClass = Class.forName(VersionGetter.getPackageHeader() + ".PacketPlayOutChat");
			Class<?> chatBaseClass = Class.forName(VersionGetter.getPackageHeader() + ".IChatBaseComponent");
			Class<?> chatSerializerClass = Class.forName(VersionGetter.getPackageHeader() + ".IChatBaseComponent$ChatSerializer");
			Class<?> craftPlayerClass = Class.forName(VersionGetter.getBukkitPackageHeader() + ".entity.CraftPlayer");
			Class<?> entityPlayerClass = Class.forName(VersionGetter.getPackageHeader() + ".EntityPlayer");
			Class<?> playerConnectionClass = Class.forName(VersionGetter.getPackageHeader() + ".PlayerConnection");
			
			Method jsonSerializer = chatSerializerClass.getMethod("a", String.class);
			Object outputMessage = jsonSerializer.invoke(null, message); //IChatBaseComponent
			
			Constructor<?> packetConstructor = packetChatClass.getDeclaredConstructor(chatBaseClass);
			Object packet = packetConstructor.newInstance(outputMessage);
			
			Object craftPlayer = craftPlayerClass.cast(p);
			Method getHandle = craftPlayerClass.getMethod("getHandle", new Class<?>[] {});
			Object handle = getHandle.invoke(craftPlayer, new Object[] {}); //EntityPlayer
			Field playerConnectionField = entityPlayerClass.getField("playerConnection");
			playerConnectionField.setAccessible(true);
			Object playerConnection = playerConnectionField.get(handle);
			
			Method sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
			sendPacket.invoke(playerConnection, packet);
			
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean sendRawMessage(Player p, JsonMessage message) {
		checkNotNull(message);
		return sendRawMessage(p, buildRawMessage(message));
	}
	
	public static boolean sendRawMessage(Player p, CompoundJsonMessage compoundMessage) {
		checkNotNull(compoundMessage);
		String construct = "[\"\"";
		for (JsonMessage msg : compoundMessage.messages) {
			construct += "," + buildRawMessage(msg);
		}
		construct += "]";
		//Bukkit.getLogger().info(construct);
		return sendRawMessage(p, construct);
	}
	
	protected static String buildRawMessage(JsonMessage message) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		return gson.toJson(message);
	}
}
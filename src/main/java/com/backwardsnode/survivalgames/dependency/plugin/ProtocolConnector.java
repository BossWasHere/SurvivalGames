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
package com.backwardsnode.survivalgames.dependency.plugin;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.controller.BorderController;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class ProtocolConnector {

	private final Plugin plugin;
	private ProtocolManager protocolManager;
	
	public ProtocolConnector(Plugin plugin) {
		this.plugin = plugin;
		protocolManager = ProtocolLibrary.getProtocolManager();
	}
	
	public void listenPackets() {
		
//		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_BORDER) {
//			@Override
//			public void onPacketSending(PacketEvent event) {
//				worldBorderPacketOut(event);
//			}
//		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.INITIALIZE_BORDER) {
			@Override
			public void onPacketSending(PacketEvent event) {
				BorderController controller = getControllerForPlayer(event.getPlayer());
				if (controller != null) worldBorderInitializePacketOut(controller, event);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_BORDER_CENTER) {
			@Override
			public void onPacketSending(PacketEvent event) {
				BorderController controller = getControllerForPlayer(event.getPlayer());
				if (controller != null) worldBorderCenterPacketOut(controller, event);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_BORDER_LERP_SIZE) {
			@Override
			public void onPacketSending(PacketEvent event) {
				BorderController controller = getControllerForPlayer(event.getPlayer());
				if (controller != null) worldBorderLerpPacketOut(controller, event);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_BORDER_SIZE) {
			@Override
			public void onPacketSending(PacketEvent event) {
				BorderController controller = getControllerForPlayer(event.getPlayer());
				if (controller != null) worldBorderSizePacketOut(controller, event);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_BORDER_WARNING_DELAY) {
			@Override
			public void onPacketSending(PacketEvent event) {
				BorderController controller = getControllerForPlayer(event.getPlayer());
				if (controller != null) worldBorderWarningDelayPacketOut(controller, event);
			}
		});

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SET_BORDER_WARNING_DISTANCE) {
			@Override
			public void onPacketSending(PacketEvent event) {
				BorderController controller = getControllerForPlayer(event.getPlayer());
				if (controller != null) worldBorderWarningDistancePacketOut(controller, event);
			}
		});
		
	}

	public void stopListeningPackets() {
		protocolManager.removePacketListeners(plugin);
	}

	private BorderController getControllerForPlayer(Player player) {
		BorderController controller = plugin.getHost().tryGetBorder(player);

		if (controller != null && controller.isObserving(player)) {
			return controller;
		}
		return null;
	}

	//https://wiki.vg/Protocol#World_Border
	//https://github.com/dmulloy2/PacketWrapper/blob/master/PacketWrapper/src/main/java/com/comphenix/packetwrapper/WrapperPlayServerWorldBorder.java
	
//	public void worldBorderPacketOut(PacketEvent event) {
//		GameInstance instance = plugin.getHost().getGameManager().getRouter(event.getPlayer());
//		BorderController controller = null;
//		if (instance != null) {
//			controller = instance.getBorderIfProtocol();
//		}
//		if (controller == null) {
//			controller = plugin.getHost().getEditorManager().getBorderFor(event.getPlayer());
//		}
//		if (controller != null) {
//			if (!controller.isObserving(event.getPlayer())) {
//				return;
//			}
//			PacketContainer packet = event.getPacket();
//			switch (packet.getWorldBorderActions().read(0)) {
//			case SET_SIZE:
//				packet.getDoubles().write(0, controller.getRadius());
//				break;
//			case LERP_SIZE:
//				//Order switched here
//				packet.getDoubles().write(0, controller.getTargetRadius())
//				.write(1, controller.getPreviousRadius());
//
//				packet.getLongs().write(0, controller.getMillisToTarget());
//				break;
//			case SET_CENTER:
//				packet.getDoubles().write(0, controller.getCenterX())
//				.write(1, controller.getCenterZ());
//				break;
//			case INITIALIZE:
//				packet.getDoubles().write(0, controller.getCenterX())
//				.write(1, controller.getCenterZ())
//
//				//Order switched here
//				.write(2, controller.getTargetRadius())
//				.write(3, controller.getPreviousRadius());
//
//				packet.getLongs().write(0, controller.getMillisToTarget());
//				packet.getIntegers().write(0, controller.getPortalTeleportBoundary());
//				packet.getIntegers().write(1, controller.getWarningTime())
//				.write(2, controller.getWarningBlocks());
//				break;
//			case SET_WARNING_TIME:
//				packet.getIntegers().write(0, controller.getWarningTime());
//				break;
//			case SET_WARNING_BLOCKS:
//				packet.getIntegers().write(0, controller.getWarningBlocks());
//				break;
//			}
//		}
//	}

	public void worldBorderInitializePacketOut(BorderController controller, PacketEvent event) {
		PacketContainer packet = event.getPacket();

		//TODO: Should order be switched?
		packet.getDoubles()
				.write(0, controller.getCenterX())
				.write(1, controller.getCenterZ())
				.write(2, controller.getTargetRadius())
				.write(3, controller.getPreviousRadius());

		packet.getLongs().write(0, controller.getMillisToTarget());
		packet.getIntegers()
				.write(0, controller.getPortalTeleportBoundary())
				.write(1, controller.getWarningTime())
				.write(2, controller.getWarningBlocks());
	}

	public void worldBorderCenterPacketOut(BorderController controller, PacketEvent event) {
		PacketContainer packet = event.getPacket();

		packet.getDoubles()
				.write(0, controller.getCenterX())
				.write(1, controller.getCenterZ());
	}

	public void worldBorderLerpPacketOut(BorderController controller, PacketEvent event) {
		PacketContainer packet = event.getPacket();

		//TODO: Should order be switched?
		packet.getDoubles()
				.write(0, controller.getPreviousRadius())
				.write(1, controller.getTargetRadius());

		packet.getLongs().write(0, controller.getMillisToTarget());
	}

	public void worldBorderSizePacketOut(BorderController controller, PacketEvent event) {
		PacketContainer packet = event.getPacket();

		packet.getDoubles().write(0, controller.getRadius());
	}

	public void worldBorderWarningDelayPacketOut(BorderController controller, PacketEvent event) {
		PacketContainer packet = event.getPacket();

		packet.getIntegers().write(0, controller.getWarningTime());
	}

	public void worldBorderWarningDistancePacketOut(BorderController controller, PacketEvent event) {
		PacketContainer packet = event.getPacket();

		packet.getIntegers().write(0, controller.getWarningBlocks());
	}

	public void worldBorderPacketInitialize(BorderController controller, HashSet<Player> observingPlayers) {
		if (observingPlayers.size() < 1) {
			return;
		}
		PacketContainer packet = getInitPacketFromController(controller);
		
		for (Player player : observingPlayers) {
			try {
				protocolManager.sendServerPacket(player, packet, false);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void worldBorderPacketInitialize(BorderController controller, Player updatePlayer) {
		PacketContainer packet = getInitPacketFromController(controller);
		try {
			protocolManager.sendServerPacket(updatePlayer, packet, false);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void worldBorderPacketCenter(BorderController controller, HashSet<Player> observingPlayers) {
		if (observingPlayers.size() < 1) {
			return;
		}
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_BORDER_CENTER);
		StructureModifier<Double> doubles = packet.getDoubles();

		doubles.write(0, controller.getCenterX());
		doubles.write(1, controller.getCenterZ());
		
		for (Player player : observingPlayers) {
			try {
				protocolManager.sendServerPacket(player, packet, false);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void worldBorderPacketLerp(BorderController controller, HashSet<Player> observingPlayers) {
		if (observingPlayers.size() < 1) {
			return;
		}
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_BORDER_LERP_SIZE);
		StructureModifier<Double> doubles = packet.getDoubles();

		doubles.write(0, controller.getPreviousRadius());
		doubles.write(1, controller.getTargetRadius());

		packet.getLongs().write(0, controller.getMillisToTarget());
		
		for (Player player : observingPlayers) {
			try {
				protocolManager.sendServerPacket(player, packet, false);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void worldBorderPacketReset(BorderController controller, Player updatePlayer) {
		double d = controller.getDefaultRadius();
		try {
			protocolManager.sendServerPacket(updatePlayer, generateInitializePacket(controller.getDefaultCenterX(), controller.getDefaultCenterZ(), d, d, 0,
					BorderController.MAX_SIZE, controller.getDefaultWarningTime(), controller.getDefaultWarningBlocks()));
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private PacketContainer getInitPacketFromController(BorderController controller) {
		return generateInitializePacket(controller.getCenterX(), controller.getCenterZ(),
				controller.getPreviousRadius(), controller.getTargetRadius(), controller.getMillisToTarget(),
				controller.getPortalTeleportBoundary(), controller.getWarningTime(), controller.getWarningBlocks());
	}
	
	private PacketContainer generateInitializePacket(double centerX, double centerZ, double oldRadius, double newRadius, long speed, int portalBoundary, int warningTime, int warningBlocks) {
		PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.INITIALIZE_BORDER);
		StructureModifier<Double> doubles = packet.getDoubles();
		StructureModifier<Integer> ints = packet.getIntegers();

		doubles.write(0, centerX);
		doubles.write(1, centerZ);
		//TODO: Should order be switched?
		doubles.write(2, oldRadius);
		doubles.write(3, newRadius);
		packet.getLongs().write(0, speed);
		ints.write(0, portalBoundary);
		ints.write(1, warningBlocks);
		ints.write(2, warningTime);
		
		return packet;
	}
}

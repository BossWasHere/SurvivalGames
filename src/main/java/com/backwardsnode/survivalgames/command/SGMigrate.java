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

package com.backwardsnode.survivalgames.command;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.Utils;
import com.backwardsnode.survivalgames.command.base.BaseCommand;
import com.backwardsnode.survivalgames.command.base.CommandType;
import com.backwardsnode.survivalgames.command.base.ExecutionStatus;
import com.backwardsnode.survivalgames.config.GameConfiguration;
import com.backwardsnode.survivalgames.config.GameConfigurationWrapper;
import com.backwardsnode.survivalgames.config.migration.Version0Model;
import com.backwardsnode.survivalgames.config.migration.Version1Model;
import com.backwardsnode.survivalgames.message.Messages;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class SGMigrate extends BaseCommand {

	public SGMigrate(Plugin plugin) {
		super(plugin, CommandType.SG_MIGRATE);
	}

	@Override
	public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
		if (args.length > 0) {
			File target = null;
			try {
				if (args[0].toLowerCase().endsWith(".json")) {
					target = new File(PLUGIN.getMapFolder(), args[0]);
				} else {
					target = new File(PLUGIN.getMapFolder(), args[0] + ".json");
				}
				if (!target.exists()) {
					sendMessage(sender, Messages.Plugin.IO_FILE_MISSING, args[0]);
				} else {
					String data = Utils.readFile(target, Charset.defaultCharset());
					JsonObject json = new Gson().fromJson(data, JsonObject.class);
					JsonElement jsonver = json.get("configVersion");
					int version = 0;
					if (jsonver != null) {
						version = jsonver.getAsInt();
					}
					sendMessage(sender, Messages.Command.SGMigrate.ATTEMPT, target.getName(), version);
					GameConfiguration gc;
					switch (version) {
						case 0:
							Version0Model model0 = new Gson().fromJson(data, Version0Model.class);
							gc = model0.migrate();
							break;
						case 1:
							Version1Model model1 = new Gson().fromJson(data, Version1Model.class);
							gc = model1.migrate();
							break;
						case 2:
							sendMessage(sender, Messages.Command.SGMigrate.UP_TO_DATE);
							return ExecutionStatus.SUCCESS;
						default:
							sendMessage(sender, Messages.Command.SGMigrate.FUTURE);
							return ExecutionStatus.SUCCESS;
					}
					Files.copy(target, new File(PLUGIN.getBackupFolder(), Utils.timestamp(target.getName())));

					new GameConfigurationWrapper(gc).saveCopyTo(target);
					sendMessage(sender, Messages.Command.SGMigrate.SUCCESS, target.getName());
				}
			} catch (IOException e) {
				sendMessage(sender, Messages.Plugin.IO_EXCEPTION);
			} catch (JsonSyntaxException e) {
				sendMessage(sender, Messages.Config.SYNTAX, target != null ? target.getName() : args[0]);
				e.printStackTrace();
			}
			return ExecutionStatus.SUCCESS;
		}
		return ExecutionStatus.BAD_USAGE;
	}

}

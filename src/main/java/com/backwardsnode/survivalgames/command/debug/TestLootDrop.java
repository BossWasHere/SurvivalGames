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

package com.backwardsnode.survivalgames.command.debug;

import com.backwardsnode.survivalgames.Plugin;
import com.backwardsnode.survivalgames.command.base.BaseCommand;
import com.backwardsnode.survivalgames.command.base.CommandType;
import com.backwardsnode.survivalgames.command.base.ExecutionStatus;
import com.backwardsnode.survivalgames.config.LootDropConfiguration;
import com.backwardsnode.survivalgames.world.BlockLocation;
import com.backwardsnode.survivalgames.world.LootDrop;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TestLootDrop extends BaseCommand {

    public TestLootDrop(Plugin plugin) {
        super(plugin, CommandType.TEST_LOOT_DROP);
    }

    @Override
    public ExecutionStatus executeDelegate(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length >= 3) {
            try {
                int blockX = Integer.parseInt(args[0]);
                int blockY = Integer.parseInt(args[1]);
                int blockZ = Integer.parseInt(args[2]);

                LootDropConfiguration ldc = new LootDropConfiguration();
                ldc.location = new BlockLocation(player.getWorld(), blockX, blockY, blockZ);

                LootDrop lootDrop = PLUGIN.getHost().getLootDropManager().summonLootDrop(ldc, true);

                Bukkit.getScheduler().scheduleSyncDelayedTask(PLUGIN, () -> lootDrop.popAndClose(new ArrayList<>(0)), 200);
                return ExecutionStatus.SUCCESS;

            } catch (NumberFormatException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ExecutionStatus.BAD_USAGE;
    }
}
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

package com.backwardsnode.survivalgames;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultConnector {

    private final Economy ECONOMY;

    public VaultConnector() {
        RegisteredServiceProvider<Economy> econServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        ECONOMY = econServiceProvider == null ? null : econServiceProvider.getProvider();
    }

    public boolean isConnected() {
        return ECONOMY != null;
    }

    public double getBalance(OfflinePlayer player) {
        return ECONOMY == null ? 0 : ECONOMY.getBalance(player);
    }

    public void setBalance(OfflinePlayer player, double balance) {
        double current = ECONOMY.getBalance(player);
        depositPlayer(player, balance - current);
    }

    public double depositPlayer(OfflinePlayer player, double amount) {
        if (ECONOMY == null) {
            return 0;
        }

        EconomyResponse response = ECONOMY.depositPlayer(player, amount);
        return response.type == EconomyResponse.ResponseType.SUCCESS ? response.balance : 0;
    }

    public double withdrawPlayer(OfflinePlayer player, double amount) {
        if (ECONOMY == null) {
            return 0;
        }

        EconomyResponse response = ECONOMY.withdrawPlayer(player, amount);
        return response.type == EconomyResponse.ResponseType.SUCCESS ? response.balance : 0;
    }
}

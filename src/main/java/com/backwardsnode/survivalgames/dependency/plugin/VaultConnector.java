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

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultConnector {

    private final Economy economy;

    public VaultConnector() {
        RegisteredServiceProvider<Economy> econServiceProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        economy = econServiceProvider == null ? null : econServiceProvider.getProvider();
    }

    public boolean isConnected() {
        return economy != null;
    }

    public double getBalance(OfflinePlayer player) {
        return economy == null ? 0 : economy.getBalance(player);
    }

    public void setBalance(OfflinePlayer player, double balance) {
        double current = economy.getBalance(player);
        depositPlayer(player, balance - current);
    }

    public boolean depositPlayer(OfflinePlayer player, double amount) {
        if (economy == null) {
            return false;
        }

        EconomyResponse response = economy.depositPlayer(player, amount);
        return response.transactionSuccess();
    }

    public boolean withdrawPlayer(OfflinePlayer player, double amount) {
        if (economy == null) {
            return false;
        }

        EconomyResponse response = economy.withdrawPlayer(player, amount);
        return response.transactionSuccess();
    }
}

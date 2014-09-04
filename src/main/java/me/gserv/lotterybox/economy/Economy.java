package me.gserv.lotterybox.economy;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Economy {

    private final LotteryBox plugin;
    private net.milkbowl.vault.economy.Economy economy;

    public Economy(LotteryBox plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> economyProvider
                = this.plugin.getServer().getServicesManager().getRegistration(
                    net.milkbowl.vault.economy.Economy.class
                );
        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
            return true;
        }

        return false;
    }

    public void addReward(Player player, int reward) {
        if (!this.economy.hasAccount(player)) {
            this.economy.createPlayerAccount(player);
        }

        this.economy.depositPlayer(player, reward);
    }
}

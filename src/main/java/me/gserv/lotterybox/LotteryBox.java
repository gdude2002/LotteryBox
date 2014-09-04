package me.gserv.lotterybox;

/**
 * Lottery box plugin
 *
 * This allows designation of chests as lottery boxes, which can be "opened" using "keys".
 */

import me.gserv.lotterybox.commands.*;
import me.gserv.lotterybox.economy.Economy;
import me.gserv.lotterybox.listeners.InteractListener;
import me.gserv.lotterybox.storage.ConfigHandler;
import me.gserv.lotterybox.storage.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class LotteryBox extends JavaPlugin {

    // Data/config handlers
    private ConfigHandler config;
    private DataHandler data;

    // Commands
    private ChBoxCommand chBoxCommand;
    private LsBoxCommand lsBoxCommand;
    private MkBoxCommand mkBoxcommand;
    private MkKeyCommand mkKeyCommand;
    private RmBoxCommand rmBoxCommand;

    // Listener for chat events
    private InteractListener listener;

    private Economy economy = null;

    @Override
    public void onEnable() {
        // Load up the config
        this.config = new ConfigHandler(this);
        this.data = new DataHandler(this);

        this.setupEconomy();
        this.data.load();

        // Create command handlers
        this.chBoxCommand = new ChBoxCommand(this);
        this.lsBoxCommand = new LsBoxCommand(this);
        this.mkBoxcommand = new MkBoxCommand(this);
        this.mkKeyCommand = new MkKeyCommand(this);
        this.rmBoxCommand = new RmBoxCommand(this);

        // Register command handlers
        this.getCommand("chbox").setExecutor(this.chBoxCommand);
        this.getCommand("lsbox").setExecutor(this.lsBoxCommand);
        this.getCommand("mkbox").setExecutor(this.mkBoxcommand);
        this.getCommand("mkkey").setExecutor(this.mkKeyCommand);
        this.getCommand("rmbox").setExecutor(this.rmBoxCommand);

        // Create a new listener and register it
        listener = new InteractListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().warning("Unable to find the 'Vault' plugin. Money rewards will be unavailable.");
            return false;
        }

        this.economy = new Economy(this);
        this.economy.setup();

        return true;
    }

    public boolean addMoneyReward(Player player, int reward) {
        if (this.economy == null) {
            return false;
        }

        this.economy.addReward(player, reward);
        return true;
    }

    public void reload() {
        this.config.reload();
    }

    public DataHandler getDataHandler() {
        return this.data;
    }

    public ConfigHandler getConfigHandler() {
        return this.config;
    }

    public boolean hasEconomy() {
        return this.economy != null;
    }
}

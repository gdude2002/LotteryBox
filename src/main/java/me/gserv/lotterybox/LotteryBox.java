package me.gserv.lotterybox;

/**
 * Lottery box plugin
 *
 * This allows designation of chests as lottery boxes, which can be "opened" using "keys".
 */

/*
    TODO: High-level list of stuff

    * Make item in hand a key
    * Generate keys in dungeon chests
    * Economy support?
    * Prism support (if possible), because why the fuck not
    * Permissions (obviously)
 */

import me.gserv.lotterybox.commands.MkBoxCommand;
import me.gserv.lotterybox.storage.ConfigHandler;
import me.gserv.lotterybox.listeners.ChatListener;
import me.gserv.lotterybox.storage.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class LotteryBox extends JavaPlugin {

    // Configuration handler
    private ConfigHandler config;

    // Data handler
    private DataHandler data;

    // mkbox command
    private MkBoxCommand mkBoxcommand;

    // Listener for chat events
//    private ChatListener listener;

    @Override
    public void onEnable() {
        // Load up the config
        this.config = new ConfigHandler(this);
        this.data = new DataHandler(this);

        this.data.load();

        this.mkBoxcommand = new MkBoxCommand(this);

        this.getCommand("mkbox").setExecutor(this.mkBoxcommand);

        // Create a new chat listener and register it
//        listener = new ChatListener(this);
//        this.getServer().getPluginManager().registerEvents(listener, this);
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
}

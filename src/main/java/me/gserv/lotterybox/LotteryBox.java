package me.gserv.lotterybox;

/**
 * Base plugin designed for use with my other projects.
 *
 * It's simply a template for a Bukkit plugin. It should be used
 * as a template. Copy it elsewhere and edit.
 */

/*
    TODO: High-level list of stuff

    * Make item in hand a key
    * Generate keys in dungeon chests
    * Economy support?
    * Prism support (if possible), because why the fuck not
    * Permissions (obviously)
 */

import me.gserv.lotterybox.storage.ConfigHandler;
import me.gserv.lotterybox.listeners.ChatListener;
import me.gserv.lotterybox.storage.DataHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class LotteryBox extends JavaPlugin {

    // Configuration handler
    public ConfigHandler config;

    // Data handler
    public DataHandler data;

    // Listener for chat events
    public ChatListener listener;

    @Override
    public void onEnable() {
        // Load up the config
        this.config = new ConfigHandler(this);
        this.data = new DataHandler(this);

        this.data.load();

        // Create a new chat listener and register it
        listener = new ChatListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void reload() {
        this.config.reload();
    }
}

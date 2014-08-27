package me.gserv.lotterybox.listeners;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class ChatListener implements Listener {

    // Instance of the plugin
    LotteryBox plugin;

    public ChatListener(LotteryBox plugin) {
        // Store the plugin instance
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        // Check to see if the player has a bypass
        if (event.getPlayer().hasPermission("baseplugin.bypass")) {
            return;
        }

        // Send a message
        event.getPlayer().sendMessage("Hi! This is a base plugin!");
    }
}

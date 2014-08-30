package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

public class RmBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public RmBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /rmbox <name>

        boolean hasPermission = false;

        if (commandSender instanceof ConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 1) {
                commandSender.sendMessage("/rmbox - Remove a lottery box");
                commandSender.sendMessage("Console usage: rmbox <name>");
                return true;
            }
        } else if (commandSender instanceof BlockCommandSender) {
            hasPermission = true;

            if(strings.length < 1) {
                commandSender.sendMessage("Command block usage: /rmbox <name>");
                return true;
            }
        } else if (commandSender instanceof CommandMinecart) {
            hasPermission = true;

            if(strings.length < 1) {
                commandSender.sendMessage("Command minecart usage: /rmbox <name>");
                return true;
            }
        } else if (commandSender instanceof RemoteConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 1) {
                commandSender.sendMessage("/rmbox - Remove a lottery box");
                commandSender.sendMessage("RCON usage: rmbox <name>");
                return true;
            }
        } else if (! (commandSender instanceof Player)) {
            commandSender.sendMessage("This plugin only supports commands from players, command blocks, and consoles.");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.rmbox");

            if (strings.length < 1) {
                commandSender.sendMessage("/rmbox - Remove a lottery box");
                commandSender.sendMessage("Usage: /rmbox <name>");
                return true;
            }
        }

        if (!hasPermission) {
            commandSender.sendMessage("You don't have permission to run this command.");
            return true;
        }

        if (!this.plugin.getDataHandler().boxExists(strings[0])) {
            commandSender.sendMessage(String.format("No such box: %s", strings[0]));
            return true;
        }

        if (!this.plugin.getDataHandler().removeBox(strings[0])) {
            commandSender.sendMessage("Failed to remove box, please check the console");
            return true;
        }

        commandSender.sendMessage(String.format("Box removed: %s", strings[0]));
        return true;
    }
}

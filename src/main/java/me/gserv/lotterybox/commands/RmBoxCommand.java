package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.HashMap;

public class RmBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public RmBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /rmbox <name>

        HashMap<String, String> args = new HashMap<>();

        boolean hasPermission;

        if (commandSender instanceof ConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 1) {
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
                commandSender.sendMessage("RCON usage: rmbox <name>");
                return true;
            }
        } else if (! (commandSender instanceof Player)) {
            this.plugin.sendMessage(commandSender, "other.bad_sender");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.rmbox");

            if (strings.length < 1) {
                this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&crmbox &a<name>");
                return true;
            }
        }

        if (!hasPermission) {
            this.plugin.sendMessage(commandSender, "other.no_permission");
            return true;
        }

        args.clear();
        args.put("box", strings[0]);

        if (!this.plugin.getDataHandler().boxExists(strings[0])) {
            this.plugin.sendMessage(commandSender, "other.no_box", args);
            return true;
        }

        if (!this.plugin.getDataHandler().removeBox(strings[0])) {
            this.plugin.sendMessage(commandSender, "rmbox.error");
            return true;
        }

        this.plugin.sendMessage(commandSender, "rmbox.removed", args);
        return true;
    }
}

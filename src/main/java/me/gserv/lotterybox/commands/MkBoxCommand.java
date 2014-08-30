package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

public class MkBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public MkBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /mkbox <name> [world] [x] [y] [z]

        boolean hasPermission = false;

        if (commandSender instanceof ConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 5) {
                commandSender.sendMessage("/mkbox - Create a lottery box");
                commandSender.sendMessage("Console usage: mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (commandSender instanceof BlockCommandSender) {
            hasPermission = true;

            if(strings.length < 5) {
                commandSender.sendMessage("Command block usage: /mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (commandSender instanceof CommandMinecart) {
            hasPermission = true;

            if(strings.length < 5) {
                commandSender.sendMessage("Command minecart usage: /mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (commandSender instanceof RemoteConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 5) {
                commandSender.sendMessage("/mkbox - Create a lottery box");
                commandSender.sendMessage("RCON usage: mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (! (commandSender instanceof Player)) {
            commandSender.sendMessage("This plugin only supports commands from players, command blocks, and consoles.");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.mkbox");

            if (strings.length < 1) {
                commandSender.sendMessage("/mkbox - Create a lottery box");
                commandSender.sendMessage("Usage: mkbox <name> [world] [x] [y] [z]");
                return true;
            }
        }

        if (!hasPermission) {
            commandSender.sendMessage("You don't have permission to run this command.");
            return true;
        }

        Location location;

        if (strings.length >= 5) {
            // World, etc can be got from the params, maybe

            World world = Bukkit.getWorld(strings[1]);
            int x, y, z;

            if (world == null) {
                commandSender.sendMessage(String.format("No such world: %s", strings[1]));
                return true;
            }

            try {
                x = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(String.format("%s is not a number", strings[2]));
                return true;
            }

            try {
                y = Integer.parseInt(strings[3]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(String.format("%s is not a number", strings[3]));
                return true;
            }

            try {
                z = Integer.parseInt(strings[4]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(String.format("%s is not a number", strings[4]));
                return true;
            }

            location = new Location(world, x, y, z);
            Block block = location.getBlock();

            if (block == null || !(block.getType() == Material.CHEST)) {
                commandSender.sendMessage("This block isn't a chest.");
                return true;
            }
        } else {
            Player player = (Player) commandSender;
            Block block = player.getTargetBlock(null, 100);

            if (block == null || !(block.getType() == Material.CHEST)) {
                commandSender.sendMessage("Please look at a chest. Perhaps you're not close enough?");
                return true;
            }

            location = block.getLocation();
        }

        boolean result = this.plugin.getDataHandler().boxExists(strings[0]);

        if (result) {
            commandSender.sendMessage(String.format("There's already a box named %s", strings[0]));
            return true;
        }

        result = this.plugin.getDataHandler().boxExistsAtLocation(location);

        if (result) {
            commandSender.sendMessage("A box already exists at this location");
            return true;
        }

        result = this.plugin.getDataHandler().addBox(strings[0], location);

        if (!result) {
            commandSender.sendMessage("Failed to create box, please check the console");
            return true;
        }

        commandSender.sendMessage(String.format("Created box: %s", strings[0]));
        return true;
    }
}

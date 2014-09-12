package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

import java.util.HashMap;

public class MkBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public MkBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /mkbox <name> [world] [x] [y] [z]

        HashMap<String, String> args = new HashMap<>();

        boolean hasPermission;

        if (commandSender instanceof ConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 5) {
                this.plugin.sendColouredMessage(commandSender, "Console usage: mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (commandSender instanceof BlockCommandSender) {
            hasPermission = true;

            if(strings.length < 5) {
                this.plugin.sendColouredMessage(commandSender, "Command block usage: /mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (commandSender instanceof CommandMinecart) {
            hasPermission = true;

            if(strings.length < 5) {
                this.plugin.sendColouredMessage(commandSender, "Command minecart usage: /mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (commandSender instanceof RemoteConsoleCommandSender) {
            hasPermission = true;

            if(strings.length < 5) {
                this.plugin.sendColouredMessage(commandSender, "RCON usage: mkbox <name> <world> <x> <y> <z>");
                return true;
            }
        } else if (! (commandSender instanceof Player)) {
            this.plugin.sendColouredMessage(commandSender, "This plugin only supports commands from players, command blocks, and consoles.");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.mkbox");

            if (strings.length < 1) {
                this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cmkbox &a<name> &d[world] [x] [y] [z]");
                return true;
            }
        }

        if (!hasPermission) {
            this.plugin.sendMessage(commandSender, "other.no_permission");
            return true;
        }

        Location location;

        if (strings.length >= 5) {
            // World, etc can be got from the params, maybe

            World world = Bukkit.getWorld(strings[1]);
            int x, y, z;

            if (world == null) {
                args.clear();
                args.put("world", strings[1]);
                this.plugin.sendMessage(commandSender, "mkbox.no_world", args);
                return true;
            }

            try {
                x = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                args.clear();
                args.put("value", strings[2]);
                this.plugin.sendMessage(commandSender, "other.nan", args);
                return true;
            }

            try {
                y = Integer.parseInt(strings[3]);
            } catch (NumberFormatException e) {
                args.clear();
                args.put("value", strings[3]);
                this.plugin.sendMessage(commandSender, "other.nan", args);
                return true;
            }

            try {
                z = Integer.parseInt(strings[4]);
            } catch (NumberFormatException e) {
                args.clear();
                args.put("value", strings[4]);
                this.plugin.sendMessage(commandSender, "other.nan", args);
                return true;
            }

            location = new Location(world, x, y, z);
            Block block = location.getBlock();

            if (block == null || !(block.getType() == Material.CHEST)) {
                this.plugin.sendMessage(commandSender, "mkbox.not_chest");
                return true;
            }
        } else {
            Player player = (Player) commandSender;
            Block block = player.getTargetBlock(null, 100);

            if (block == null || !(block.getType() == Material.CHEST)) {
                this.plugin.sendMessage(commandSender, "mkbox.look_at_chest");
                return true;
            }

            location = block.getLocation();
        }

        args.clear();
        args.put("box", strings[0]);

        if (this.plugin.getDataHandler().boxExists(strings[0])) {
            this.plugin.sendMessage(commandSender, "mkbox.already_box_named", args);
            return true;
        }

        if (this.plugin.getDataHandler().boxExistsAtLocation(location)) {
            this.plugin.sendMessage(commandSender, "mkbox.already_box_location");
            return true;
        }

        if (!this.plugin.getDataHandler().addBox(strings[0], location)) {
            this.plugin.sendMessage(commandSender, "mkbox.box_creation_failed");
            return true;
        }

        this.plugin.sendMessage(commandSender, "mkbox.box_created", args);
        return true;
    }
}

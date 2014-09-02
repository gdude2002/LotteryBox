package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Box;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;

public class LsBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public LsBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /lsbox [name] ["rewards"]

        boolean hasPermission = false;

        if (commandSender instanceof ConsoleCommandSender ||
            commandSender instanceof BlockCommandSender ||
            commandSender instanceof CommandMinecart ||
            commandSender instanceof RemoteConsoleCommandSender) {

            hasPermission = true;
        } else if (! (commandSender instanceof Player)) {
            commandSender.sendMessage("This plugin only supports commands from players, command blocks, and consoles.");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.lsbox");
        }

        if (!hasPermission) {
            commandSender.sendMessage("You don't have permission to run this command.");
            return true;
        }

        if (strings.length < 1) {
            HashSet<Box> boxes = this.plugin.getDataHandler().getBoxes();

            if (boxes.size() < 1) {
                commandSender.sendMessage("No boxes found");
                return true;
            }

            for (Box box : boxes) {
                commandSender.sendMessage(
                        String.format(
                                "%s [%s - %s, %s, %s] - chance: %s",
                                box.name, box.world, box.x,
                                box.y, box.z, box.getChance()
                                )
                );
            }
        } else if (strings.length < 2) {
            Box box = this.plugin.getDataHandler().getBox(strings[0]);

            if (box == null) {
                commandSender.sendMessage(String.format("No such box: %s", strings[0]));
                return true;
            }

            commandSender.sendMessage(String.format("== %s ==", strings[0]));
            commandSender.sendMessage(String.format("Location: %s - %s, %s, %s", box.world, box.x, box.y, box.z));
            commandSender.sendMessage(String.format("Chance: %s", box.getChance()));

            if (box.isInfinite()) {
                commandSender.sendMessage("Uses: Infinite");
            } else {
                commandSender.sendMessage(String.format("Uses: %s", box.getUses()));
            }

            commandSender.sendMessage(String.format("Rewards: %s", box.numRewards()));

            if (box.isNamedKeys()) {
                commandSender.sendMessage("== Requires named keys ==");
            } else {
                commandSender.sendMessage("== Can use any key ==");
            }
        } else if ("rewards".equalsIgnoreCase(strings[1])) {
            Box box = this.plugin.getDataHandler().getBox(strings[0]);

            if (box == null) {
                commandSender.sendMessage(String.format("No such box: %s", strings[0]));
                return true;
            }

            commandSender.sendMessage(String.format("== Rewards: %s ==", strings[0]));

            HashMap<String, HashMap<String, Object>> rewards = box.getRewards();

            if (rewards.size() < 1) {
                commandSender.sendMessage("No rewards found");
            }

            for (String key : rewards.keySet()) {
                HashMap<String, Object> reward = rewards.get(key);

                if ("item".equals(reward.get("type"))) {
                    ItemStack item = ItemStack.deserialize((java.util.Map<String, Object>) reward.get("item"));
                    commandSender.sendMessage(
                            String.format(
                                    "[%s] %s (%s chance) - %s*%s",
                                    "Item", key, reward.get("chance"),
                                    item.getType().toString(), item.getAmount()
                            )
                    );
                } else if ("command".equals(reward.get("type"))) {
                    commandSender.sendMessage(
                            String.format(
                                    "[%s] %s (%s chance) - %s",
                                    "Command", key, reward.get("chance"),
                                    reward.get("command")
                            )
                    );
                } else if ("money".equals(reward.get("type"))) {
                    if ((int) reward.get("amount") >= 0) {
                        commandSender.sendMessage(
                                String.format(
                                        "[%s] %s (%s chance) - +%s",
                                        "Money", key, reward.get("chance"),
                                        reward.get("amount")
                                )
                        );
                    } else {
                        commandSender.sendMessage(
                                String.format(
                                        "[%s] %s (%s chance) - -%s",
                                        "Money", key, reward.get("chance"),
                                        reward.get("amount")
                                )
                        );

                    }
                }
            }
        } else {
            commandSender.sendMessage("Usage: /lsbox [name] [\"rewards\"]");
        }

        return true;
    }
}

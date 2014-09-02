package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Box;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ChBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public ChBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /chbox <operation> [name] [params]
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
            hasPermission = commandSender.hasPermission("lotterybox.chbox");
        }

        if (!hasPermission) {
            commandSender.sendMessage("You don't have permission to run this command.");
            return true;
        }

        if (strings.length < 1) {
            commandSender.sendMessage("Usage: /chbox <operation> [name] [params]");
            commandSender.sendMessage("For more info, see /chbox help");
            return true;
        }

        String operation = strings[0];

        switch (operation) {
            case "help":
                this.helpCommand(commandSender, command, s, strings);
                break;
            case "set":
                this.setCommand(commandSender, command, s, strings);
                break;
            case "reward":
                this.rewardCommand(commandSender, command, s, strings);
                break;
            default:
                commandSender.sendMessage(String.format("Unknown operation: %s", operation));
                commandSender.sendMessage("For more info, see /chbox help");
                break;
        }

        return true;
    }

    public void helpCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage("== /chbox help ==");

        if (strings.length < 2) {
            commandSender.sendMessage("");
            commandSender.sendMessage("Usage: /chbox <operation> [name] [params]");
            commandSender.sendMessage("Operations: help, set, reward");
            commandSender.sendMessage("Use /chbox help <operation> for more information");
        } else {

            String operation = strings[1];

            switch (operation) {
                case "help":
                    commandSender.sendMessage("Operation: help");
                    commandSender.sendMessage("");
                    commandSender.sendMessage("Usage: /chbox help [operation]");
                    commandSender.sendMessage("You're using this right now!");
                    break;
                case "set":
                    commandSender.sendMessage("Operation: set");
                    commandSender.sendMessage("");
                    commandSender.sendMessage("This is for setting options on specific boxes.");
                    commandSender.sendMessage("Usage: /chbox set <name> <option> <value>");
                    commandSender.sendMessage("");
                    commandSender.sendMessage("/chbox set <name> chance <value> - Set overall chance for a box");
                    commandSender.sendMessage("/chbox set <name> keys <named/all> - Set whether named keys will work");
                    commandSender.sendMessage("/chbox set <name> uses <value/infinite> - Set how many times the box can dispense rewards");
                    break;
                case "reward":
                    commandSender.sendMessage("Operation: reward");
                    commandSender.sendMessage("");
                    commandSender.sendMessage("Usage: /chbox reward <name> <operation> <reward name> [value]");
                    commandSender.sendMessage("");
                    commandSender.sendMessage("/chbox reward <name> add <reward name> <type> - Add a reward to a box");
                    commandSender.sendMessage("/chbox reward <name> remove <reward name> - Remove a reward");
                    commandSender.sendMessage("/chbox reward <name> chance <reward name> <value> - Set the relative chance of the reward");
                    commandSender.sendMessage("/chbox reward <name> set <reward name> <value> - Set the reward item/amount/command");
                    commandSender.sendMessage("");
                    commandSender.sendMessage("Reward types: item, command, money");
                    commandSender.sendMessage("Specifying items: \"hand\" or \"type[:amount]\"");
                    break;
                default:
                    commandSender.sendMessage(String.format("Unknown operation: %s", operation));
                    commandSender.sendMessage("For more info, see /chbox help");
                    break;
            }
        }
    }

    public void setCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 4) {
            commandSender.sendMessage("Usage: /chbox set <name> <option> <value>");
            commandSender.sendMessage("See /chbox help set for more information.");
            return;
        }

        String name, option, value;

        name = strings[1];
        option = strings[2];
        value = strings[3];

        Box box = this.plugin.getDataHandler().getBox(name);

        if (box == null) {
            commandSender.sendMessage(String.format("No such box: %s", name));
            return;
        }

        switch(option.toLowerCase()) {
            case "chance":
                try {
                    int x = Integer.parseInt(value);
                    box.setChance(x);
                    commandSender.sendMessage(String.format("Chance for box %s to dispense a reward is now %s", name, value));
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(String.format("%s is not a number or is too large", value));
                }
                break;
            case "keys":
                if ("named".equalsIgnoreCase(value)) {
                    box.setNamedKeys(true);
                    commandSender.sendMessage(String.format("Box %s now requires named keys", name));
                } else if ("all".equalsIgnoreCase(value)) {
                    box.setNamedKeys(false);
                    commandSender.sendMessage(String.format("Box %s no longer requires named keys", name));
                } else {
                    commandSender.sendMessage(String.format("Unknown value: %s (expected named or all)", value));
                }
                break;
            case "uses":
                if ("infinite".equalsIgnoreCase(value)) {
                    box.setUses(0);
                    box.setInfinite(true);
                    commandSender.sendMessage(String.format("Box %s will now dispense infinite rewards", name));
                } else {
                    try {
                        int x = Integer.parseInt(value);
                        box.setUses(x);
                        box.setInfinite(false);
                        commandSender.sendMessage(String.format("Box %s will now dispense %s rewards", name, value));
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(String.format("%s is not \"infinite\" or a number, or is too large", value));
                    }
                }
                break;
            default:
                commandSender.sendMessage(String.format("Unknown option: %s", option));
                commandSender.sendMessage("See /chbox help set for more information.");
                break;
        }
    }

    public void rewardCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 4) {
            commandSender.sendMessage("Usage: /chbox reward <name> <operation> <reward name> [value]");
            commandSender.sendMessage("See /chbox help reward for more information.");
            return;
        }

        String name, operation, rewardName;

        name = strings[1];
        operation = strings[2];
        rewardName = strings[3];

        Box box = this.plugin.getDataHandler().getBox(name);

        if (box == null) {
            commandSender.sendMessage(String.format("No such box: %s", name));
            return;
        }

        switch(operation.toLowerCase()) {
            case "add":
                if (strings.length < 5) {
                    commandSender.sendMessage("Usage: /chbox reward <name> add <reward name> <command/item/money>");
                    commandSender.sendMessage("See /chbox help reward for more information.");
                } else {
                    if (box.hasReward(rewardName)) {
                        commandSender.sendMessage(String.format("Reward %s already exists", rewardName));
                    } else {
                        String type = strings[4];

                        if ("command".equalsIgnoreCase(type)) {
                            box.addCommandReward(rewardName, "", 0);
                            commandSender.sendMessage(String.format("Empty command reward %s with a chance of 0 added", rewardName));
                            commandSender.sendMessage("Remember to set the chance and reward value!");
                        } else if ("item".equalsIgnoreCase(type)) {
                            box.addItemReward(rewardName, new ItemStack(Material.AIR), 0);
                            commandSender.sendMessage(String.format("Empty item reward %s with a chance of 0 added", rewardName));
                            commandSender.sendMessage("Remember to set the chance and reward value!");
                        } else if ("money".equalsIgnoreCase(type)) {
                            // TODO: Check for Vault
                            box.addMoneyReward(rewardName, 0, 0);
                            commandSender.sendMessage(String.format("Empty money reward %s with a chance of 0 added", rewardName));
                            commandSender.sendMessage("Remember to set the chance and reward value!");
                        } else {
                            commandSender.sendMessage(String.format("Unknown reward type: %s", type));
                            commandSender.sendMessage("See /chbox help reward for more information.");
                        }
                    }
                }
                break;
            case "remove":
                if (box.hasReward(rewardName)) {
                    box.removeReward(rewardName);
                    commandSender.sendMessage(String.format("Removed reward: %s", rewardName));
                } else {
                    commandSender.sendMessage(String.format("Unknown reward: %s", rewardName));
                }
                break;
            case "chance":
                if (strings.length < 5) {
                    commandSender.sendMessage("Usage: /chbox reward <name> chance <reward name> <chance>");
                    commandSender.sendMessage("See /chbox help reward for more information.");
                } else {
                    if (!box.hasReward(rewardName)) {
                        commandSender.sendMessage(String.format("Unknown reward: %s", rewardName));
                    } else {
                        String value = strings[4];

                        try {
                            int x = Integer.parseInt(value);
                            box.setRewardChance(rewardName, x);
                            commandSender.sendMessage(String.format("Reward %s now has a %s chance of being dispensed", rewardName, x));
                        } catch (NumberFormatException e) {
                            commandSender.sendMessage(String.format("%s is not \"infinite\" or a number, or is too large", value));
                        }
                    }
                }
                break;
            case "set":
                if (strings.length < 5) {
                    commandSender.sendMessage("Usage: /chbox reward <name> set <reward name> <value>");
                    commandSender.sendMessage("See /chbox help reward for more information.");
                } else {
                    if (!box.hasReward(rewardName)) {
                        commandSender.sendMessage(String.format("Unknown reward: %s", rewardName));
                    } else {
                        String value = strings[4];

                        HashMap<String, Object> reward = box.getRewards().get(rewardName);
                        String type = (String) reward.get("type");
                        boolean r;

                        if ("command".equalsIgnoreCase(type)) {
                            if (value.charAt(0) == '/') {
                                value = value.substring(1);
                            }

                            r = box.setRewardValue(rewardName, value);

                            if (!r) {
                                commandSender.sendMessage("Invalid reward command specified!");
                            } else {
                                commandSender.sendMessage(String.format("Reward command set: %s", value));
                            }

                        } else if ("item".equalsIgnoreCase(type)) {
                            ItemStack item;

                            if ("hand".equalsIgnoreCase(value) && commandSender instanceof Player) {
                                Player player = (Player) commandSender;
                                item = player.getItemInHand();
                            } else {
                                String[] parts = value.split(":");
                                Material mat = Material.getMaterial(parts[0]);
                                int amount = 1;

                                if (mat == null) {
                                    commandSender.sendMessage(String.format("Unknown item type: %s", parts[0]));
                                    return;
                                } else {
                                    if (parts.length > 1) {
                                        try {
                                            amount = Integer.parseInt(parts[1]);
                                        } catch (NumberFormatException e) {
                                            commandSender.sendMessage(String.format("%s is not a number, or is too large", parts[1]));
                                            return;
                                        }
                                    }

                                    item = new ItemStack(mat, amount);
                                }
                            }

                            box.setRewardValue(rewardName, item);
                            commandSender.sendMessage(String.format("Item reward set: %s %s(s)", item.getAmount(), item.getType().toString()));

                        } else if ("money".equalsIgnoreCase(type)) {
                            Integer x;

                            try {
                                x = Integer.parseInt(value);
                                box.setRewardValue(rewardName, x);
                                commandSender.sendMessage(String.format("Reward %s now has a %s chance of being dispensed", rewardName, x));
                            } catch (NumberFormatException e) {
                                commandSender.sendMessage(String.format("%s is not a number, or is too large", value));
                            }

                        } else {
                            commandSender.sendMessage(String.format("Unknown type: %s - this should never happen", type));
                        }
                    }
                }
                break;
            default:
                commandSender.sendMessage(String.format("Unknown operation: %s", operation));
                commandSender.sendMessage("See /chbox help reward for more information.");
                break;
        }
    }
}

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
            this.plugin.sendMessage(commandSender, "other.bad_sender");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.chbox");
        }

        if (!hasPermission) {
            this.plugin.sendMessage(commandSender, "other.no_permission");
            return true;
        }

        if (strings.length < 1) {
            this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &a<operation> &d[name] [params]");
            this.plugin.sendMessage(commandSender, "chbox.help.more_info");
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
                this.plugin.sendColouredMessage(commandSender, String.format("&6Unknown operation: &c%s", operation));
                this.plugin.sendMessage(commandSender, "chbox.help.more_info");
                break;
        }

        this.plugin.getDataHandler().save();

        return true;
    }

    public void helpCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        this.plugin.sendMessage(commandSender, "chbox.help.header");
        HashMap<String, String> args = new HashMap<>();

        if (strings.length < 2) {
            this.plugin.sendColouredMessage(commandSender, "");
            this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &a<operation> &d[name] [params]");
            this.plugin.sendColouredMessage(commandSender, "&6Operations: &ahelp&6, &aset&6, &areward");
            this.plugin.sendMessage(commandSender, "chbox.help.more_info");
        } else {

            String operation = strings[1];

            switch (operation) {
                case "help":
                    args.clear();
                    args.put("operation", "help");
                    this.plugin.sendMessage(commandSender, "chbox.help.current_operation", args);

                    this.plugin.sendColouredMessage(commandSender, "");
                    this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6help &a<operation>");
                    this.plugin.sendColouredMessage(commandSender, "&6You're using this right now!");
                    break;
                case "set":
                    args.clear();
                    args.put("operation", "set");
                    this.plugin.sendMessage(commandSender, "chbox.help.current_operation", args);

                    this.plugin.sendColouredMessage(commandSender, "");
                    this.plugin.sendColouredMessage(commandSender, "&6This is for setting options on specific boxes.");
                    this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6set &a<name> <option> <value>");
                    this.plugin.sendColouredMessage(commandSender, "");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6set &a<name> &6chance &a<value> &c- &aSet overall chance for a box");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6set &a<name> &6keys &a<named&c/&aall> &c- &aSet whether named keys will work");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6set &a<name> &6uses &a<value&c/&ainfinite> &c- &aSet how many times the box can dispense rewards");
                    break;
                case "reward":
                    args.clear();
                    args.put("operation", "set");
                    this.plugin.sendMessage(commandSender, "chbox.help.current_operation", args);

                    this.plugin.sendColouredMessage(commandSender, "");
                    this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6reward &a<name> <operation> <reward name> &d[value]");
                    this.plugin.sendColouredMessage(commandSender, "");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6reward &a<name> &6add &a<reward name> <command/item/money> &c- &aAdd a reward to a box");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6reward &a<name> &6remove &a<reward name> &c- &aRemove a reward");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6reward &a<name> &6chance &a<reward name> <value> &c- &aSet the relative chance of the reward");
                    this.plugin.sendColouredMessage(commandSender, "&a/&cchbox &6reward &a<name> &6set &a<reward name> <value> &c- &aSet the reward item/amount/command");
                    this.plugin.sendColouredMessage(commandSender, "");
                    this.plugin.sendColouredMessage(commandSender, "&6Reward types: &aitem&c, &acommand&c, &amoney");
                    this.plugin.sendColouredMessage(commandSender, "&6Specifying items: &ahand &6or &atype[:amount]");
                    break;
                default:
                    this.plugin.sendColouredMessage(commandSender, String.format("&cUnknown operation: &6%s", operation));
                    this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                    break;
            }
        }
    }

    public void setCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 4) {
            this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6set &a<name> <option> <value>");
            this.plugin.sendMessage(commandSender, "chbox.help.see_more");
            return;
        }
        HashMap<String, String> args = new HashMap<>();

        String name, option, value;

        name = strings[1];
        option = strings[2];
        value = strings[3];

        Box box = this.plugin.getDataHandler().getBox(name);

        if (box == null) {
            args.clear();
            args.put("box", name);
            this.plugin.sendMessage(commandSender, "other.no_box", args);
            return;
        }

        switch(option.toLowerCase()) {
            case "chance":
                try {
                    int x = Integer.parseInt(value);
                    box.setChance(x);

                    args.clear();
                    args.put("box", box.name);
                    args.put("chance", String.valueOf(x));
                    this.plugin.sendMessage(commandSender, "chbox.set.chance_ok", args);
                } catch (NumberFormatException e) {

                    args.clear();
                    args.put("value", value);
                    this.plugin.sendMessage(commandSender, "chbox.set.chance_fail", args);
                }
                break;
            case "keys":
                if ("named".equalsIgnoreCase(value)) {
                    box.setNamedKeys(true);

                    args.clear();
                    args.put("box", box.name);
                    this.plugin.sendMessage(commandSender, "chbox.set.named_has", args);
                } else if ("all".equalsIgnoreCase(value)) {
                    box.setNamedKeys(false);

                    args.clear();
                    args.put("box", box.name);
                    this.plugin.sendMessage(commandSender, "chbox.set.named_has_not", args);
                } else {
                    args.clear();
                    args.put("value", value);
                    this.plugin.sendMessage(commandSender, "chbox.set.named_fail", args);
                }
                break;
            case "uses":
                if ("infinite".equalsIgnoreCase(value)) {
                    box.setUses(0);
                    box.setInfinite(true);

                    args.clear();
                    args.put("box", box.name);
                    this.plugin.sendMessage(commandSender, "chbox.set.uses_infinite", args);
                } else {
                    try {
                        int x = Integer.parseInt(value);
                        box.setUses(x);
                        box.setInfinite(false);

                        args.clear();
                        args.put("box", box.name);
                        args.put("amount", String.valueOf(x));
                        this.plugin.sendMessage(commandSender, "chbox.set.uses_number", args);
                    } catch (NumberFormatException e) {
                        args.clear();
                        args.put("value", value);
                        this.plugin.sendMessage(commandSender, "chbox.set.uses_fail", args);
                    }
                }
                break;
            default:
                args.clear();
                args.put("option", option);
                this.plugin.sendMessage(commandSender, "chbox.set.unknown_option", args);

                this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                break;
        }
    }

    public void rewardCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length < 4) {
            this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6reward &a<name> <operation> <reward name> &d[value]");
            this.plugin.sendMessage(commandSender, "chbox.help.see_more");
            return;
        }

        HashMap<String, String> args = new HashMap<>();

        String name, operation, rewardName;

        name = strings[1];
        operation = strings[2];
        rewardName = strings[3];

        Box box = this.plugin.getDataHandler().getBox(name);

        if (box == null) {
            args.clear();
            args.put("box", name);
            this.plugin.sendMessage(commandSender, "other.no_box", args);
            return;
        }

        switch(operation.toLowerCase()) {
            case "add":
                if (strings.length < 5) {
                    this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6reward &a<name> &6add &a<reward name> <command/item/money>");
                    this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                } else {
                    if (box.hasReward(rewardName)) {
                        args.clear();
                        args.put("reward", rewardName);
                        this.plugin.sendMessage(commandSender, "chbox.reward.exists", args);
                    } else {
                        String type = strings[4];

                        if ("command".equalsIgnoreCase(type)) {
                            box.addCommandReward(rewardName, "", 0);

                            args.clear();
                            args.put("name", rewardName);
                            args.put("type", "command");

                            this.plugin.sendMessage(commandSender, "chbox.reward.exists", args);
                            this.plugin.sendMessage(commandSender, "chbox.reward.reminder");
                        } else if ("item".equalsIgnoreCase(type)) {
                            box.addItemReward(rewardName, new ItemStack(Material.AIR), 0);

                            args.clear();
                            args.put("name", rewardName);
                            args.put("type", "item");

                            this.plugin.sendMessage(commandSender, "chbox.reward.exists", args);
                            this.plugin.sendMessage(commandSender, "chbox.reward.reminder");
                        } else if ("money".equalsIgnoreCase(type)) {
                            if (this.plugin.hasEconomy()) {
                                box.addMoneyReward(rewardName, 0, 0);

                                args.clear();
                                args.put("name", rewardName);
                                args.put("type", "money");

                                this.plugin.sendMessage(commandSender, "chbox.reward.exists", args);
                                this.plugin.sendMessage(commandSender, "chbox.reward.reminder");
                            } else {
                                this.plugin.sendMessage(commandSender, "chbox.reward.no_economy");
                            }
                        } else {
                            args.clear();
                            args.put("type", type);

                            this.plugin.sendMessage(commandSender, "chbox.reward.unknown_reward_type", args);
                            this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                        }
                    }
                }
                break;
            case "remove":
                if (box.hasReward(rewardName)) {
                    box.removeReward(rewardName);

                    args.clear();
                    args.put("reward", rewardName);

                    this.plugin.sendMessage(commandSender, "chbox.reward.reward_removed", args);
                } else {
                    args.clear();
                    args.put("name", rewardName);

                    this.plugin.sendMessage(commandSender, "chbox.reward.unknown_reward", args);
                }
                break;
            case "chance":
                if (strings.length < 5) {
                    this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6reward &a<name> &6chance &a<reward name> <value>");
                    this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                } else {
                    if (!box.hasReward(rewardName)) {
                        args.clear();
                        args.put("name", rewardName);

                        this.plugin.sendMessage(commandSender, "chbox.reward.unknown_reward", args);
                    } else {
                        String value = strings[4];

                        try {
                            int x = Integer.parseInt(value);
                            box.setRewardChance(rewardName, x);

                            args.clear();
                            args.put("reward", rewardName);
                            args.put("chance", String.valueOf(x));

                            this.plugin.sendMessage(commandSender, "chbox.reward.chance_set", args);
                        } catch (NumberFormatException e) {
                            args.clear();
                            args.put("chance", value);

                            this.plugin.sendMessage(commandSender, "chbox.reward.chance_fail", args);
                        }
                    }
                }
                break;
            case "set":
                if (strings.length < 5) {
                    this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cchbox &6reward &a<name> &6set &a<reward name> <value>");
                    this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                } else {
                    if (!box.hasReward(rewardName)) {
                        args.clear();
                        args.put("name", rewardName);

                        this.plugin.sendMessage(commandSender, "chbox.reward.unknown_reward", args);
                    } else {
                        String value = "";
                        for (int i = 4; i < strings.length; i += 1) {
                            value += (strings[i] + " ");
                        }

                        value = value.trim();

                        HashMap<String, Object> reward = box.getRewards().get(rewardName);
                        String type = (String) reward.get("type");
                        boolean r;

                        if ("command".equalsIgnoreCase(type)) {
                            if (value.charAt(0) == '/') {
                                value = value.substring(1);
                            }

                            r = box.setRewardValue(rewardName, value);

                            if (!r) {
                                this.plugin.sendMessage(commandSender, "chbox.reward.command_fail");
                            } else {
                                args.clear();
                                args.put("command", value);

                                this.plugin.sendMessage(commandSender, "chbox.reward.command_set", args);
                            }

                        } else if ("item".equalsIgnoreCase(type)) {
                            ItemStack item;

                            if ("hand".equalsIgnoreCase(value) && commandSender instanceof Player) {
                                Player player = (Player) commandSender;
                                item = player.getItemInHand();

                                if (item == null || item.getType() == Material.AIR || item.getAmount() < 1) {
                                    this.plugin.sendMessage(commandSender, "chbox.reward.held_item_invalid");
                                    return;
                                }
                            } else {
                                String[] parts = value.split(":");
                                Material mat = Material.matchMaterial(parts[0]);
                                int amount = 1;

                                if (mat == null) {
                                    args.clear();
                                    args.put("type", parts[0]);

                                    this.plugin.sendMessage(commandSender, "chbox.reward.unknown_item", args);
                                    return;
                                } else {
                                    if (parts.length > 1) {
                                        try {
                                            amount = Integer.parseInt(parts[1]);
                                        } catch (NumberFormatException e) {
                                            args.clear();
                                            args.put("value", parts[1]);

                                            this.plugin.sendMessage(commandSender, "chbox.reward.bad_number", args);
                                            return;
                                        }
                                    }

                                    if (amount < 1) {
                                        this.plugin.sendMessage(commandSender, "chbox.reward.low_item_amount");
                                        return;
                                    }

                                    item = new ItemStack(mat, amount);
                                }
                            }

                            box.setRewardValue(rewardName, item);
                            args.clear();
                            args.put("amount", String.valueOf(item.getAmount()));
                            args.put("type", item.getType().toString());

                            this.plugin.sendMessage(commandSender, "chbox.reward.item_set", args);

                        } else if ("money".equalsIgnoreCase(type)) {
                            Integer x;

                            try {
                                x = Integer.parseInt(value);
                                box.setRewardValue(rewardName, x);
                                args.clear();
                                args.put("value", String.valueOf(x));

                                this.plugin.sendMessage(commandSender, "chbox.reward.money_set", args);
                            } catch (NumberFormatException e) {
                                args.clear();
                                args.put("value", value);

                                this.plugin.sendMessage(commandSender, "chbox.reward.bad_number", args);
                            }

                        } else {
                            args.clear();
                            args.put("name", rewardName);

                            this.plugin.sendMessage(commandSender, "chbox.unknown_reward", args);
                        }
                    }
                }
                break;
            default:
                this.plugin.sendColouredMessage(commandSender, String.format("&cUnknown operation: &6%s", operation));
                this.plugin.sendMessage(commandSender, "chbox.help.see_more");
                break;
        }
    }
}

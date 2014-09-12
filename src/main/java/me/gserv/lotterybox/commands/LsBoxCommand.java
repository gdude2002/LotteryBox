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

        HashMap <String, String> args = new HashMap<>();
        boolean hasPermission;

        if (commandSender instanceof ConsoleCommandSender ||
            commandSender instanceof BlockCommandSender ||
            commandSender instanceof CommandMinecart ||
            commandSender instanceof RemoteConsoleCommandSender) {

            hasPermission = true;
        } else if (! (commandSender instanceof Player)) {
            this.plugin.sendMessage(commandSender, "other.bad_sender");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.lsbox");
        }

        if (!hasPermission) {
            this.plugin.sendMessage(commandSender, "other.no_permission");
            return true;
        }

        if (strings.length < 1) {
            HashSet<Box> boxes = this.plugin.getDataHandler().getBoxes();

            if (boxes.size() < 1) {
                this.plugin.sendMessage(commandSender, "other.no_boxes");
                return true;
            }

            for (Box box : boxes) {
                args.clear();

                args.put("name", box.name);
                args.put("world", box.world);
                args.put("x", String.valueOf(box.x));
                args.put("y", String.valueOf(box.y));
                args.put("z", String.valueOf(box.z));
                args.put("chance", String.valueOf(box.getChance()));

                this.plugin.sendMessage(commandSender, "lsbox.box_entry", args);

            }
        } else if (strings.length < 2) {
            Box box = this.plugin.getDataHandler().getBox(strings[0]);

            if (box == null) {
                args.clear();
                args.put("box", strings[0]);

                this.plugin.sendMessage(commandSender, "other.no_box", args);
                return true;
            }

            args.clear();
            args.put("box", box.name);
            this.plugin.sendMessage(commandSender, "lsbox.box_header", args);

            args.clear();
            args.put("world", box.world);
            args.put("x", String.valueOf(box.x));
            args.put("y", String.valueOf(box.y));
            args.put("z", String.valueOf(box.z));
            this.plugin.sendMessage(commandSender, "lsbox.box_location", args);

            args.clear();
            args.put("chance", String.valueOf(box.getChance()));
            this.plugin.sendMessage(commandSender, "lsbox.box_chance", args);

            args.clear();

            if (box.isInfinite()) {
                args.put("uses", "infinite");
            } else {
                args.put("uses", String.valueOf(box.getUses()));
            }

            this.plugin.sendMessage(commandSender, "lsbox.box_uses", args);

            args.clear();
            args.put("count", String.valueOf(box.numRewards()));
            this.plugin.sendMessage(commandSender, "lsbox.box_rewards_count", args);

            if (box.isNamedKeys()) {
                this.plugin.sendMessage(commandSender, "lsbox.box_named_keys");
            } else {
                this.plugin.sendMessage(commandSender, "lsbox.box_any_keys");
            }
        } else if ("rewards".equalsIgnoreCase(strings[1])) {
            Box box = this.plugin.getDataHandler().getBox(strings[0]);

            if (box == null) {
                args.clear();
                args.put("box", strings[0]);

                this.plugin.sendMessage(commandSender, "other.no_box", args);
                return true;
            }

            args.clear();
            args.put("box", strings[0]);

            this.plugin.sendMessage(commandSender, "lsbox.rewards_header", args);

            HashMap<String, HashMap<String, Object>> rewards = box.getRewards();

            if (rewards.size() < 1) {
                this.plugin.sendMessage(commandSender, "lsbox.no_rewards");
            }

            for (String key : rewards.keySet()) {
                HashMap<String, Object> reward = rewards.get(key);
                args.clear();
                args.put("reward", key);
                args.put("chance", String.valueOf(reward.get("chance")));

                if ("item".equals(reward.get("type"))) {
                    ItemStack item = ItemStack.deserialize((java.util.Map<String, Object>) reward.get("item"));

                    args.put("amount", String.valueOf(item.getAmount()));
                    args.put("type", item.getType().toString());

                    this.plugin.sendMessage(commandSender, "lsbox.item_reward", args);
                } else if ("command".equals(reward.get("type"))) {
                    args.put("command", (String) reward.get("command"));

                    this.plugin.sendMessage(commandSender, "lsbox.command_reward", args);
                } else if ("money".equals(reward.get("type"))) {
                    args.put("amount", String.valueOf(reward.get("amount")));

                    if ((int) reward.get("amount") > 0) {
                        args.put("string", "+");
                    } else if ((int) reward.get("amount") < 0) {
                        args.put("string", "-");
                    }

                    this.plugin.sendMessage(commandSender, "lsbox.money_reward", args);
                }
            }
        } else {
            this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&clsbox &d[name] [\"rewards\"]");
        }

        return true;
    }
}

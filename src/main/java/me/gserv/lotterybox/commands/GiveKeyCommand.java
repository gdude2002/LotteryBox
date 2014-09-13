package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Keys;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class GiveKeyCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public GiveKeyCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /givekey <player> [item] [name]

        HashMap<String, String> args = new HashMap<>();

        boolean hasPermission = commandSender.hasPermission("lotterybox.givekey");

        if (!hasPermission) {
            this.plugin.sendMessage(commandSender, "other.no_permission");
            return true;
        }

        if (strings.length < 1) {
            this.plugin.sendColouredMessage(commandSender, "&6Usage: &a/&cgivekey &a<player> &d[item] [name]");
            return true;
        }

        String playerName = strings[0];
        Player target = this.plugin.getServer().getPlayer(playerName);

        if (target == null) {
            args.clear();
            args.put("player", playerName);

            this.plugin.sendMessage(commandSender, "givekey.player_offline", args);
            return true;
        }

        ItemStack item;

        if (strings.length > 1) {
            Material mat = Material.getMaterial(strings[1]);

            if (mat == null) {
                args.clear();
                args.put("item", strings[1]);

                this.plugin.sendMessage(commandSender, "givekey.invalid_item", args);
                return true;
            } else if (mat.equals(Material.AIR)) {
                this.plugin.sendMessage(commandSender, "givekey.item_must_not_be_air");
                return true;
            }

            item = new ItemStack(mat);
        } else {
            item = new ItemStack(Material.TRIPWIRE_HOOK);
        }

        if (strings.length > 2) {
            String keyName = strings[2];

            args.clear();
            args.put("box", keyName);

            if (!this.plugin.getDataHandler().boxExists(keyName)) {
                this.plugin.sendMessage(commandSender, "other.no_box", args);
                return true;
            }

            Keys.makeKey(item, keyName);
        } else {
            Keys.makeKey(item);
        }

        this.plugin.sendMessage(target, "givekey.message_to_player");

        HashMap<Integer, ItemStack> insertResult = target.getInventory().addItem(item);

        if (!insertResult.isEmpty()) {
            this.plugin.sendColouredMessage(
                    target,
                    "It looks like your inventory is full! Dropping the items at your feet.."
            );

            for (ItemStack toDrop : insertResult.values()) {
                target.getWorld().dropItem(target.getLocation(), toDrop);
            }
        }

        return true;
    }
}

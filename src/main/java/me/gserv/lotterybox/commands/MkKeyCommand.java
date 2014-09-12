package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Keys;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MkKeyCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public MkKeyCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /rmbox <name>

        HashMap<String, String> args = new HashMap<>();

        boolean hasPermission;

        if (! (commandSender instanceof Player)) {
            this.plugin.sendMessage(commandSender, "mkkey.players_only");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.mkkey");
        }

        if (!hasPermission) {
            this.plugin.sendMessage(commandSender, "other.no_permission");
            return true;
        }

        Player p = (Player) commandSender;

        ItemStack item = p.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            this.plugin.sendMessage(commandSender, "mkkey.hold_item");
            return true;
        }

        if (strings.length < 1) {
            Keys.makeKey(item);
            this.plugin.sendMessage(commandSender, "mkkey.generic_key");
        } else {
            String name = strings[0];

            args.clear();
            args.put("box", name);

            if (!this.plugin.getDataHandler().boxExists(name)) {
                this.plugin.sendMessage(commandSender, "other.no_box", args);
                return true;
            }

            Keys.makeKey(item, name);
            this.plugin.sendMessage(commandSender, "mkkey.specific_key", args);
        }

        return true;
    }
}

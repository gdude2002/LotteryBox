package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Keys;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MkKeyCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public MkKeyCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // /rmbox <name>

        boolean hasPermission;

        if (! (commandSender instanceof Player)) {
            commandSender.sendMessage("This command may only be used by players.");
            return true;
        } else {
            hasPermission = commandSender.hasPermission("lotterybox.mkkey");
        }

        if (!hasPermission) {
            commandSender.sendMessage("You don't have permission to run this command.");
            return true;
        }

        Player p = (Player) commandSender;

        ItemStack item = p.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            p.sendMessage("You're not holding an item to turn into a key!");
            return true;
        }

        if (strings.length < 1) {
            Keys.makeKey(item);
            p.sendMessage("Item has been turned into a generic key.");
        } else {
            String name = strings[0];

            if (!this.plugin.getDataHandler().boxExists(name)) {
                commandSender.sendMessage(String.format("No such box: %s", name));
                return true;
            }

            Keys.makeKey(item, name);
            p.sendMessage(String.format("Item has been turned into a box key for %s", name));
        }

        return true;
    }
}

package me.gserv.lotterybox.listeners;

import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Box;
import me.gserv.lotterybox.boxes.Keys;
import me.gserv.lotterybox.boxes.Reason;
import me.gserv.lotterybox.boxes.Result;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


public class InteractListener implements Listener {

    // Instance of the plugin
    private final LotteryBox plugin;

    public InteractListener(LotteryBox plugin) {
        // Store the plugin instance
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Block block = event.getClickedBlock();

        if (this.plugin.getDataHandler().boxExistsAtLocation(block.getLocation())) {
            event.setCancelled(true);

            if (!p.hasPermission("lotterybox.open")) {
                p.sendMessage("You don't have permission to open lottery boxes");
                return;
            }

            ItemStack item = p.getItemInHand();

            if (Keys.isKey(item)) {
                Box box = this.plugin.getDataHandler().getBoxAtLocation(block.getLocation());
                Result result;

                if (box.isNamedKeys()) {
                    if (box.name.equals(Keys.boxNameFromKey(item))) {
                        result = box.use();
                    } else {
                        p.sendMessage(String.format("This box requires \"%s\" keys", box.name));
                        return;
                    }
                } else {
                    if ("Generic".equals(Keys.boxNameFromKey(item))) {
                        result = box.use();
                    } else if (box.name.equals(Keys.boxNameFromKey(item))) {
                        result = box.use();
                    } else {
                        p.sendMessage(String.format("This box requires \"%s\" or \"Generic\" keys", box.name));
                        return;
                    }
                }

                switch (result.getReason()) {
                    case OK:
                        this.useKey(p, item);

                        HashMap<String, Object> reward = result.getReward();
                        String type = (String) reward.get("type");

                        switch (type) {
                            case "item":
                                ItemStack itemReward = ItemStack.deserialize(
                                        (java.util.Map<String, Object>) reward.get("item")
                                );

                                p.sendMessage(String.format(
                                        "Congratulations! You've received item reward \"%s\" (%s * %s)!",
                                        result.getName(), itemReward.getType().toString(), itemReward.getAmount()
                                ));

                                HashMap<Integer, ItemStack> insertResult = p.getInventory().addItem(item);

                                if (!insertResult.isEmpty()) {
                                    p.sendMessage(
                                            "It looks like your inventory is full! Dropping the items at your feet.."
                                    );

                                    for (ItemStack toDrop : insertResult.values()) {
                                        p.getWorld().dropItem(p.getLocation(), toDrop);
                                    }
                                }
                            case "command":
                                String command = (String) reward.get("command");

                                p.sendMessage(String.format(
                                        "Congratulations! You've received command reward \"%s\"!",
                                        result.getName()
                                ));

                                command = command.replace("{NAME}", p.getName());
                                command = command.replace("{WORLD}", p.getWorld().getName());
                                command = command.replace("{X}", String.valueOf(p.getLocation().getBlockX()));
                                command = command.replace("{Y}", String.valueOf(p.getLocation().getBlockY()));
                                command = command.replace("{Z}", String.valueOf(p.getLocation().getBlockZ()));
                                command = command.replace("{BOX}", box.name);
                                command = command.replace("{REWARD}", result.getName());

                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            case "money":
                                // TODO: Vault
                                break;
                        }
                        break;
                    case OUT_OF_USES:
                        p.sendMessage("This box is out of rewards. Come back later!");
                        break;
                    case FAILED:
                        this.useKey(p, item);
                        p.sendMessage("Sorry, no reward for you! Better luck next time!");
                        break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void useKey(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
        } else {
            player.getInventory().clear(player.getInventory().getHeldItemSlot());
        }

        player.updateInventory();
    }
}

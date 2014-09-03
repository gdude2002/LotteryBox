package me.gserv.lotterybox.boxes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Keys {
    public static String boxNameFromKey(ItemStack item) {
        if (Keys.isKey(item)) {
            List<String> lore = item.getItemMeta().getLore();

            if (lore.size() > 0) {
                String loreLine = lore.get(0);
                String[] parts = loreLine.split(" ", 2);

                return parts[1];
            }
        }

        return null;
    }

    public static boolean isKey(ItemStack item) {
        if (item.getItemMeta().hasLore()) {
            List<String> lore = item.getItemMeta().getLore();

            if (lore.size() > 0) {
                String loreLine = lore.get(0);

                if (loreLine.toLowerCase().startsWith("key")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void makeKey(ItemStack item, String name) {
        List<String> lore = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();

        lore.add(String.format("Key: %s", name));
        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    public static void makeKey(ItemStack item) {
        Keys.makeKey(item, "Generic");
    }
}

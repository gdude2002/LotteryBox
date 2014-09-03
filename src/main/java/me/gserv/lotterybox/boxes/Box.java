package me.gserv.lotterybox.boxes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Box {

    //<editor-fold desc="Fields">

    public final int x;
    public final int y;
    public final int z;
    public final String world;
    public final String name;

    private HashMap<String, HashMap<String, Object>> rewards;

    private int chance;
    private int uses;

    private boolean infinite;
    private boolean namedKeys;

    private final Random random = new Random();
    //</editor-fold>

    //<editor-fold desc="Constructors">
    public Box(String name, Location location) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
        this.name = name;

        // Defaults
        this.rewards = new HashMap<>();
        this.chance = 20;
        this.uses = 1;
        this.infinite = false;
        this.namedKeys = false;
    }

    public Box(String name, int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.name = name;

        // Defaults
        this.rewards = new HashMap<>();
        this.chance = 20;
        this.uses = 1;
        this.infinite = false;
        this.namedKeys = false;
    }

    public Box(String name, Location location, HashMap<String, HashMap<String, Object>> rewards, int chance, int uses, boolean infinite, boolean namedKeys) {
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
        this.name = name;
        this.rewards = rewards;
        this.chance = chance;
        this.uses = uses;
        this.infinite = infinite;
        this.namedKeys = namedKeys;
    }

    public Box(String name, int x, int y, int z, String world, HashMap<String, HashMap<String, Object>> rewards, int chance, int uses, boolean infinite, boolean namedKeys) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.name = name;
        this.rewards = rewards;
        this.chance = chance;
        this.uses = uses;
        this.infinite = infinite;
        this.namedKeys = namedKeys;
    }
    //</editor-fold>

    //<editor-fold desc="Methods">
    public boolean addCommandReward(String name, String command, int chance) {
        if (!this.hasReward(name)) {
            HashMap<String, Object> map = new HashMap<>();

            map.put("type", "command");
            map.put("command", command);
            map.put("chance", chance);

            this.rewards.put(name, map);
            return true;
        }

        return false;
    }

    public boolean addItemReward(String name, ItemStack item, int chance) {
        if (!this.hasReward(name)) {
            HashMap<String, Object> map = new HashMap<>();

            map.put("type", "item");
            map.put("item", item.serialize());
            map.put("chance", chance);

            this.rewards.put(name, map);
            return true;
        }

        return false;
    }

    public boolean addMoneyReward(String name, int amount, int chance) {
        if (!this.hasReward(name)) {
            HashMap<String, Object> map = new HashMap<>();

            map.put("type", "money");
            map.put("amount", amount);
            map.put("chance", chance);

            this.rewards.put(name, map);
            return true;
        }

        return false;
    }

    public boolean setRewardChance(String name, int chance) {
        if (!this.hasReward(name)) {
            return false;
        }

        HashMap<String, Object> reward = this.getRewards().get(name);
        reward.put("chance", chance);

        return true;
    }

    public boolean setRewardValue(String name, Object value) {
        if (!this.hasReward(name)) {
            return false;
        }

        HashMap<String, Object> reward = this.getRewards().get(name);
        String type = (String) reward.get("type");

        switch(type.toLowerCase()) {
            case "command":
                if (value instanceof String) {
                    reward.put("command", value);
                } else {
                    return false;
                }
                break;
            case "item":
                if (value instanceof ItemStack) {
                    reward.put("item", ((ItemStack) value).serialize());
                } else {
                    return false;
                }
                break;
            case "money":
                if (value instanceof Integer) {
                    reward.put("amount", value);
                } else {
                    return false;
                }
                break;
            default:
                return false;
        }

        return true;
    }

    public boolean removeReward(String name) {
        if (this.hasReward(name)) {
            this.rewards.remove(name);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, HashMap<String, Object>> getRewards() {
        return (HashMap<String, HashMap<String, Object>>) this.rewards.clone();
    }

    public int numRewards() {
        return this.rewards.size();
    }

    public boolean hasReward(String name) {
        return this.rewards.containsKey(name);
    }

    public Result use() {
        if ((!this.isInfinite()) && this.uses < 1) {
            return new Result(null, Reason.OUT_OF_USES);
        }

        if (this.random.nextInt(this.chance) == 0) {
            ArrayList<String> rewards = new ArrayList<>();

            for (String key : this.rewards.keySet()) {
                Integer chance;
                chance = (Integer) this.rewards.get(key).get("chance");

                for (int i = 0; i < chance; i += 1) {
                    rewards.add(key);
                }
            }

            String reward_key = rewards.get(this.random.nextInt(rewards.size()));
            HashMap <String, Object> reward = this.rewards.get(reward_key);

            if (!this.isInfinite()) {
                this.uses -= 1;
            }

            return new Result(reward_key, Reason.OK, reward);
        }

        return new Result(null, Reason.FAILED);
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(this.world);

        if (world == null) {
            return null;
        }

        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }

    private int totalChance() {
        int total = 0;

        for (String key : this.rewards.keySet()) {
            int chance = (int) this.rewards.get(key).get("chance");
            total += chance;
        }

        return total;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) throws IllegalArgumentException {
        this.chance = chance;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public boolean isNamedKeys() {
        return namedKeys;
    }

    public void setNamedKeys(boolean namedKeys) {
        this.namedKeys = namedKeys;
    }

    public boolean validate() {
        Location l = this.getLocation();

        if (l == null) {
            return false;
        }

        Block b = l.getBlock();

        return !(b == null || b.getType() != Material.CHEST);
    }

    public void fixRewards() {
        for (String key : this.rewards.keySet()) {
            HashMap<String, Object> reward = this.rewards.get(key);

            this.iterateMap(reward);
        }
    }

    @SuppressWarnings("unchecked")
    private void iterateMap(Map<String, Object> map) {
        for (String key : map.keySet()) {
            Object obj = map.get(key);

            if (obj instanceof Double) {
                map.put(key, ((Double) obj).intValue());
            } else if (obj instanceof Float) {
                map.put(key, ((Float) obj).intValue());
            } else if (obj instanceof Map) {
                this.iterateMap((Map) obj);
            }
        }
    }
    //</editor-fold>
}

package me.gserv.lotterybox.storage;

import com.google.gson.Gson;
import me.gserv.lotterybox.LotteryBox;
import org.bukkit.Location;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

public class DataHandler {

    /* Data storage formatting for `boxes`
        {
            "chest name": {
                "x": 123,
                "y": 123,
                "z": 123,
                "rewards": {
                    "provide": {
                        "type": "item",     // Stringly typed
                        "item": "DIAMOND",  // Item name
                        "amount": 1,        // Item amount
                        "chance": 5         // Relative chance
                    },
                    "promote": {
                        "type": "command",                                // Stringly typed
                        "command": "pex user {PLAYER} group set winner",  // Command, with tokens
                        "chance": 10                                      // Relative chance
                    },
                    "pay": {
                        "type": "money",  // Stringly typed
                        "amount": 50,     // Amount, negative for reduction
                        "chance": 20      // Relative chance
                    }
                },
                "chance": 20,  // Chance of getting a reward, out of 100
                "infinite": false,  // Whether players can receive rewards infinitely or the box has to be reset
                "uses": 1,  // Number of rewards to be received if not infinite
                "named_keys": false  // Whether only named keys can open this chest
            }
        }
     */

    // Internal storage for the boxes themselves
    private HashMap<String,
                HashMap<String, Object>
            > boxes;

    // Locations for quicker lookups
    private HashMap<String,  // World
                HashMap<Integer,  // X
                    HashMap<Integer,  // Y
                        HashMap<Integer,  // Z
                                String    // Box name
                        >
                    >
                >
            > locations;

    // GSON serializer
    private final Gson gson = new Gson();

    // Main plugin
    private final LotteryBox plugin;

    // File object
    private final File fh;

    public DataHandler(LotteryBox plugin) {
        this.plugin = plugin;
        this.fh = new File(this.plugin.getDataFolder() + "/boxes.json");
    }

    @SuppressWarnings("unchecked")
    public boolean load() {
        if (!this.fh.exists()) {
            this.boxes = new HashMap<>();
            return this.save();
        } else {
            try (
                    InputStreamReader reader = new InputStreamReader(
                            new FileInputStream(this.fh),
                            Charset.forName("UTF-8")
                    )
            ) {
                this.boxes = gson.fromJson(reader, HashMap.class);
                reader.close();

                for (String key : this.boxes.keySet()) {
                    boolean result = this.addLocation(this.boxes.get(key), key);

                    if (!result) {
                        this.plugin.getLogger().warning(
                                String.format("Error loading box %s: There's already another box there!", key)
                        );
                    }
                }
                return true;
            } catch (IOException e) {
                this.plugin.getLogger().warning("Unable to read from boxes data file!");
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean save() {
        if (!this.fh.exists()) {
            this.plugin.getLogger().info("Boxes data file not found - creating..");
            try {
                boolean created = this.fh.createNewFile();

                if (!created) {
                    this.plugin.getLogger().warning("Unable to create boxes data file!");
                    return false;
                }
            } catch (IOException e) {
                this.plugin.getLogger().warning("Unable to create boxes data file!");
                e.printStackTrace();
                return false;
            }
        }

        try (
                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(this.fh, false),
                        Charset.forName("UTF-8")
                )
        ) {
            gson.toJson(this.boxes, writer);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            this.plugin.getLogger().warning("Unable to write boxes data file!");
            e.printStackTrace();
            return false;
        }
    }

    private boolean addLocation(HashMap<String, Object> box, String name) {
        Integer x, y, z;
        String world;

        x = (Integer) box.get("x");
        y = (Integer) box.get("y");
        z = (Integer) box.get("z");
        world = (String) box.get("world");

        if (! this.locations.containsKey(world)) {
            this.locations.put(world, new HashMap<Integer, HashMap<Integer, HashMap<Integer, String>>>());
        }

        if (! this.locations.get(world).containsKey(x)) {
            this.locations.get(world).put(x, new HashMap<Integer, HashMap<Integer, String>>());
        }

        if (! this.locations.get(world).get(x).containsKey(y)) {
            this.locations.get(world).get(x).put(y, new HashMap<Integer, String>());
        }

        if (! this.locations.get(world).get(x).get(y).containsKey(z)) {
            this.locations.get(world).get(x).get(y).put(z, name);
            return true;
        }

        return false;
    }

    // Getting methods

    /**
     * Get a box if it exists, or null otherwise.
     *
     * @param name The name of the box
     * @return HashMap representing the box, or null if it doesn't exist
     */
    public HashMap<String, Object> getBox(String name) {
        if (this.boxes.containsKey(name)) {
            return this.boxes.get(name);
        }
        return null;
    }

    /**
     * Get the number of boxes in existence.
     *
     * @return The number of boxes that exist
     */
    public int getNumBoxes() {
        return this.boxes.size();
    }

    /**
     * Check whether a box exists.
     *
     * @param name The name of the box
     * @return boolean representing whether the box exists
     */
    public boolean boxExists(String name) {
        return this.boxes.containsKey(name);
    }

    public Object getBoxOption(String name, String option) {
        if (this.boxExists(name)) {
            if (this.getBox(name).containsKey(option)) {
                return this.getBox(name).get(option);
            }
        }

        return null;
    }

    // Adding methods

    public boolean addBox(String name, Location location) {
        if (this.boxes.containsKey(name)) {
            return false;
        }

        HashMap<String, Object> box = new HashMap<>();

        // Location of the box
        box.put("x", location.getBlockX());
        box.put("y", location.getBlockY());
        box.put("z", location.getBlockZ());
        box.put("world", location.getWorld().getName());

        // Default rewards and chance
        box.put("rewards", new HashMap<String, HashMap<String, Object>>());
        box.put("chance", 20);

        // Uses information
        box.put("infinite", false);
        box.put("uses", 1);

        // Whether the box requires named keys
        box.put("named_keys", false);

        boolean result = this.addLocation(box, name);

        if (result) {
            this.boxes.put(name, box);
        }

        this.save();

        this.plugin.getLogger().info(
                String.format(
                        "New lottery box created at %s, %s, %s on world %s.",
                        box.get("x"), box.get("y"), box.get("z"), box.get("world")
                )
        );

        return result;
    }

    // Setting methods

    public boolean setBoxOption(String name, String option, Object value) {
        if (this.boxExists(name)) {
            if (this.getBox(name).containsKey(option)) {
                this.getBox(name).put(option, value);
                return true;
            }
        }

        return false;
    }

}

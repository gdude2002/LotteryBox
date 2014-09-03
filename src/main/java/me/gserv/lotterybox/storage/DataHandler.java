package me.gserv.lotterybox.storage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.gserv.lotterybox.LotteryBox;
import me.gserv.lotterybox.boxes.Box;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;

public class DataHandler {

    // Internal storage for the boxes themselves
    private HashMap<String, Box> boxes;

    // TypeToken for deserializing the JSON
    private Type token = new TypeToken<HashMap<String, Box>>(){}.getType();

    // Locations for quicker lookups
    private HashMap<Location, Box> locations = new HashMap<>();

    // GSON serializer (which converts doubles to ints)
    Gson gson;

    // Main plugin
    private final LotteryBox plugin;

    // File object
    private final File fh;

    public DataHandler(LotteryBox plugin) {
        this.plugin = plugin;
        this.fh = new File(this.plugin.getDataFolder(), "boxes.json");

        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {

            @Override
            public JsonElement serialize(Double src, Type typeOfSrc,
                                         JsonSerializationContext context) {
                Integer value = (int) Math.round(src);
                return new JsonPrimitive(value);
            }
        });

        gsonBuilder.registerTypeAdapter(Double.class,  new JsonDeserializer<Integer>() {

            @Override
            public Integer deserialize(JsonElement json, Type typeOfT,
                                      JsonDeserializationContext context) throws JsonParseException {
                return context.deserialize(json, Integer.class);
            }
        });

        this.gson = gsonBuilder.create();
    }

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
                this.boxes = this.gson.fromJson(reader, this.token);
                reader.close();

                for (String key : this.boxes.keySet()) {
                    boolean result = this.addLocation(this.boxes.get(key));

                    if (!result) {
                        this.plugin.getLogger().warning(
                                String.format("Error loading box %s: There's already another box there!", key)
                        );
                    }

                    if (!this.boxes.get(key).validate()) {
                        this.plugin.getLogger().warning(
                                String.format("Box %s has been removed or its location doesn't exist, removing..", key)
                        );
                        this.removeBox(key);
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

    // Getting methods

    public Box getBox(String name) {
        if (this.boxes.containsKey(name)) {
            return this.boxes.get(name);
        }
        return null;
    }

    public Box getBoxAtLocation(Location location) {
        if (this.boxExistsAtLocation(location)) {
            return this.locations.get(location);
        }
        return null;
    }

    public HashSet<Box> getBoxes() {
        HashSet<Box> boxes = new HashSet<>();
        boxes.addAll(this.boxes.values());

        return boxes;
    }

    public int getNumBoxes() {
        return this.boxes.size();
    }

    public boolean boxExists(String name) {
        return this.boxes.containsKey(name);
    }

    public boolean boxExistsAtLocation(Location location) {
        return this.locations.containsKey(location);
    }

    // Adding methods

    public boolean addBox(String name, Location location) {
        return this.addBox(new Box(name, location));
    }

    public boolean addBox(Box box) {
        if (this.boxExists(box.name) || this.boxExistsAtLocation(box.getLocation())) {
            return false;
        }

        boolean result = this.addLocation(box);

        if (result) {
            this.boxes.put(box.name, box);
        }

        this.save();
        return result;
    }

    private boolean addLocation(Box box) {
        Location location = box.getLocation();

        if (!this.locations.containsKey(location)) {
            this.locations.put(location, box);
            return true;
        }

        return false;
    }

    // Removing methods

    public boolean removeBox(String name) {
        return this.boxExists(name) && this.removeBox(this.getBox(name));

    }

    public boolean removeBox(Box box) {
        if (this.boxExists(box.name)) {
            if (this.boxExistsAtLocation(box.getLocation())) {
                this.locations.remove(box.getLocation());
            }

            this.boxes.remove(box.name);
            this.save();

            return true;
        }

        return false;
    }

}

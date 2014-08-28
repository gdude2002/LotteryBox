package me.gserv.lotterybox.storage;

import com.google.gson.Gson;
import me.gserv.lotterybox.LotteryBox;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    // Internal storage for the boxes themselves
    private HashMap<String, Map<String, Object>> boxes;

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

    public HashMap<String, Map<String, Object>> getBoxes() {
        return this.boxes;
    }
}

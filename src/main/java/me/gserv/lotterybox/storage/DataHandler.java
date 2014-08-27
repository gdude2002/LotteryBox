package me.gserv.lotterybox.storage;

import com.google.gson.Gson;
import me.gserv.lotterybox.LotteryBox;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class DataHandler {

    // Internal storage for the boxes themselves
    HashMap<String, Map<String, Object>> boxes;

    // GSON serializer
    Gson gson = new Gson();

    // Main plugin
    LotteryBox plugin;

    public DataHandler(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean load() {
        File fh = new File(this.plugin.getDataFolder() + "/boxes.json");
        if (!fh.exists()) {
            try {
                boolean created = fh.createNewFile();

                if (!created) {
                    this.plugin.getLogger().warning("Unable to create boxes.json!");
                    return false;
                }
            } catch (IOException e) {
                this.plugin.getLogger().warning("Unable to create boxes.json!");
                e.printStackTrace();
                return false;
            }

            this.boxes = new HashMap<>();
            HashMap<String, Object> inner = new HashMap<>();
            inner.put("key", "value");
            inner.put("another key", 2);

            this.boxes.put("box1", inner);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fh, false), Charset.forName("UTF-8"))) {
                gson.toJson(this.boxes, writer);
                writer.flush();
                writer.close();
                return true;
            } catch (IOException e) {
                this.plugin.getLogger().warning("Unable to write to boxes.json!");
                e.printStackTrace();
                return false;
            }
        } else {
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fh), Charset.forName("UTF-8"))) {
                this.boxes = gson.fromJson(reader, HashMap.class);
                reader.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}

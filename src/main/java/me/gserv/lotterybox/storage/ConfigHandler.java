package me.gserv.lotterybox.storage;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class ConfigHandler {

    private LotteryBox plugin;
    private FileConfiguration config;

    public ConfigHandler(LotteryBox plugin) {
        this.plugin = plugin;
        File fh = new File(this.plugin.getDataFolder() + "/config.yml");

        if (!fh.isFile()) {
            // Only recreate if it's gone
            this.plugin.saveDefaultConfig();
        }

        this.config = this.plugin.getConfig();
    }

    public void update() {
        String version = this.getVersion();

        switch (version) {
            case "":
                // No version in the config
                this.config.set("version", this.plugin.getDescription().getVersion());
                this.reload();
                break;
            case "0.0.1":
                // Correct version
                break;
            default:
                this.plugin.getLogger().warning(
                        String.format("Unknown version in config: %s", version)
                );
                this.plugin.getLogger().warning(
                        String.format("Setting to %s", this.plugin.getDescription().getVersion())
                );

                this.config.set("version", this.plugin.getDescription().getVersion());
                this.reload();
                break;
        }
    }

    public void reload() {
        this.plugin.reloadConfig();
    }

    public String getVersion() {
        return this.config.getString("version", "");
    }
}

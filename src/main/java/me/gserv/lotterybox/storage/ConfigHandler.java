package me.gserv.lotterybox.storage;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigHandler {

    private final LotteryBox plugin;
    private final FileConfiguration config;

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
                // Beta - wipe it.
                this.plugin.getLogger().warning(
                        "As you're upgrading from the 0.0.1 dev builds, I'm going to have to wipe your config. Sorry!"
                );
                try {
                    Files.delete((new File(this.plugin.getDataFolder(), "config.yml")).toPath());
                    this.plugin.saveDefaultConfig();
                } catch (IOException e) {
                    this.plugin.getLogger().warning("Unable to replace old config!");
                    e.printStackTrace();
                }

                break;
            case "0.0.2":
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

    public String getMessage(String message) {
        return this.config.getString("messages.".concat(message));
    }

    public String getMessagePrefix() {
        return this.config.getString("prefix");
    }
}

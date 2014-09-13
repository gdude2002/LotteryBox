package me.gserv.lotterybox;

/**
 * Lottery box plugin
 *
 * This allows designation of chests as lottery boxes, which can be "opened" using "keys".
 */

import me.gserv.lotterybox.commands.*;
import me.gserv.lotterybox.economy.Economy;
import me.gserv.lotterybox.listeners.InteractListener;
import me.gserv.lotterybox.storage.ConfigHandler;
import me.gserv.lotterybox.storage.DataHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public final class LotteryBox extends JavaPlugin {

    // Data/config handlers
    private ConfigHandler config;
    private DataHandler data;

    // Commands
    private ChBoxCommand chBoxCommand;
    private GiveKeyCommand giveKeyCommand;
    private LsBoxCommand lsBoxCommand;
    private MkBoxCommand mkBoxcommand;
    private MkKeyCommand mkKeyCommand;
    private RmBoxCommand rmBoxCommand;

    // Listener for chat events
    private InteractListener listener;

    private Economy economy = null;

    @Override
    public void onEnable() {
        // Load up the config
        this.config = new ConfigHandler(this);
        this.config.update();

        // Load up the boxes too
        this.data = new DataHandler(this);

        this.setupEconomy();
        this.data.load();

        // Create command handlers
        this.chBoxCommand = new ChBoxCommand(this);
        this.giveKeyCommand = new GiveKeyCommand(this);
        this.lsBoxCommand = new LsBoxCommand(this);
        this.mkBoxcommand = new MkBoxCommand(this);
        this.mkKeyCommand = new MkKeyCommand(this);
        this.rmBoxCommand = new RmBoxCommand(this);

        // Register command handlers
        this.getCommand("chbox").setExecutor(this.chBoxCommand);
        this.getCommand("givekey").setExecutor(this.giveKeyCommand);
        this.getCommand("lsbox").setExecutor(this.lsBoxCommand);
        this.getCommand("mkbox").setExecutor(this.mkBoxcommand);
        this.getCommand("mkkey").setExecutor(this.mkKeyCommand);
        this.getCommand("rmbox").setExecutor(this.rmBoxCommand);

        // Create a new listener and register it
        listener = new InteractListener(this);
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().warning("Unable to find the 'Vault' plugin. Money rewards will be unavailable.");
            return false;
        }

        this.economy = new Economy(this);
        boolean loaded = this.economy.setup();

        if (!loaded) {
            this.getLogger().warning("Unable to set up economy handler - Do you have an economy plugin installed?");
        }

        return loaded;
    }

    public boolean addMoneyReward(Player player, int reward) {
        if (this.economy == null) {
            return false;
        }

        this.economy.addReward(player, reward);
        return true;
    }

    public void reload() {
        this.config.reload();
    }

    public DataHandler getDataHandler() {
        return this.data;
    }

    public ConfigHandler getConfigHandler() {
        return this.config;
    }

    public boolean hasEconomy() {
        return this.economy != null;
    }


    /**
     * Send a message to a player, where the message has been defined in the
     * plugin's <code>config.yml</code>.
     *
     * <p>
     *
     * The <code>args</code> map is used to replace tokens in the message. For example..
     *
     * <p>
     *
     * <code>
     *     Map<String, String> args = new HashMap<>();<br/>
     *     args.put("PERMISSION", "my.awesome.permission");
     * </code>
     *
     * <p>
     *
     * This will replace <code>"{PERMISSION}"</code> in the message with
     * <code>"my.awesome.permission"</code>.
     *
     * @param player The CommandSender (Console or Player) to send the message to.
     * @param messageID The name of the message, as configured in <code>config.yml</code>.
     * @param args A Map of arguments that will be replacing tokens in the message itself.
     */
    public void sendMessage(CommandSender player, String messageID, Map<String, String> args) {
        String msg = this.getConfigHandler().getMessage(messageID);

        if (msg == null) {
            this.sendColouredMessage(player, String.format("&cUnknown or missing message: &6%s", messageID));
            this.sendColouredMessage(player, "&cPlease notify the server owner so that they may fix this.");
            this.sendColouredMessage(player, "&cIf you just ran a command, this message doesn't necessarily mean it didn't complete!");

            this.getLogger().warning(String.format("Unknown or missing message: %s", messageID));
            this.getLogger().warning(String.format("Please check your configuration and make sure messages.%s exists!", messageID));
            this.getLogger().warning("If you're sure you configured Painter properly, then please report this to the BukkitDev page.");
            return;
        }

        if (msg.isEmpty()) {
            return;
        }

        if (args != null) {
            // Not all messages have tokens
            for (String key : args.keySet()) {
                String origKey = key;
                key = key.toUpperCase();
                key = String.format("{%s}", key);

                msg = msg.replace(key, args.get(origKey));
            }
        }
        this.sendColouredMessage(player, msg);
    }

    public void sendMessage(CommandSender player, String messageID) {
        this.sendMessage(player, messageID, null);
    }

    public void sendColouredMessage(CommandSender player, String message) {
        String prefix = this.config.getMessagePrefix();

        if (!prefix.isEmpty()) {
            message = String.format("%s %s", prefix, message);
        }

        player.sendMessage(translateAlternateColorCodes('&', message));
    }
}

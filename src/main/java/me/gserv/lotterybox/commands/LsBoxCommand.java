package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LsBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public LsBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return true;
    }
}

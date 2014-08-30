package me.gserv.lotterybox.commands;

import me.gserv.lotterybox.LotteryBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RmBoxCommand implements CommandExecutor {
    private final LotteryBox plugin;

    public RmBoxCommand(LotteryBox plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return true;
    }
}

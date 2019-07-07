package com.mineaurion.commands;

import com.mineaurion.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ReloadCommand implements CommandExecutor {
    private Main main;

    public ReloadCommand() {
        this.main = Main.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        main.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Plugin Jump has been reloaded");
        main.getJump().reload();
        return true;
    }
}
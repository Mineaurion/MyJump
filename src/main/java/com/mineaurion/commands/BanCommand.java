package com.mineaurion.commands;

import com.mineaurion.Jumper;
import com.mineaurion.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class BanCommand implements CommandExecutor {
    private Main main;

    public BanCommand() {
        this.main = Main.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1)
            return false;

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " doesn't exist, can't be banned lol!");
            return true;
        }

        Jumper fictive = new Jumper(player.getName(), true);

        if (fictive.isBan()) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " is already banned lmao!");
            return true;
        }

        fictive.ban();

        if (sender instanceof Player)
            sender.sendMessage("Player " + player.getName() + " (" + player.getUniqueId().toString() + ") has been banned!");
        return true;
    }
}
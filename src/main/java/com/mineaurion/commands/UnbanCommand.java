package com.mineaurion.commands;

import com.mineaurion.Jumper;
import com.mineaurion.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class UnbanCommand implements CommandExecutor {
    private Main main;

    public UnbanCommand() {
        this.main = Main.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1)
            return false;

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {

            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " doesn't exist, can't be unbanned huhu!");
            return true;
        }

        Jumper fictive = new Jumper(player.getName(), true);

        if (!fictive.isBan()) {
            sender.sendMessage(ChatColor.RED + "Player " + args[0] + " is not banned, why you want unbanned him lul?!");
            return true;
        }

        fictive.unban();

        if (sender instanceof Player)
            sender.sendMessage("Player " + player.getName() + " (" + player.getUniqueId().toString() + ") has been unbanned!");
        return true;
    }
}
package com.mineaurion.Bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mineaurion.Bukkit.Main;

public class CommandUpdate implements CommandExecutor {
	Main plugin;

	public CommandUpdate(Main instance) {
plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] arg3) {
		if(cmd.getName().equalsIgnoreCase("updatescore")) {
			if(sender instanceof Player) {
				if(sender.hasPermission("mineaurion.updatejumpscore")) {
					plugin.jumpClass.updateLeader((Player)sender);
					return true;
				}
			}
		}
		return false;
	}

}

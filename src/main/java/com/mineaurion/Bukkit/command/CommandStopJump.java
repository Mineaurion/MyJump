package com.mineaurion.Bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import com.mineaurion.Bukkit.Main;

public class CommandStopJump implements CommandExecutor {
	
	Main plugin;
	
	public CommandStopJump(Main instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("stopjump")) {
			if(sender instanceof Player) {	
				if(plugin.jumpClass.hasStartedJump((Player)sender)){
					plugin.jumpClass.stopJump((Player)sender, false);
				}
			}else {
				plugin.sendmessage("{{RED}}Commande utilisable seulement par les joueurs", sender.getName());
			}
			return true;
		}
		
		return false;
	}

}

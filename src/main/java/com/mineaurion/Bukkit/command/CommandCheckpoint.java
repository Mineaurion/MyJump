package com.mineaurion.Bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.mineaurion.Bukkit.Main;

public class CommandCheckpoint implements CommandExecutor {
	Main plugin;
	
	public CommandCheckpoint(Main main) {
		plugin = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		//sender -> Player/ConsoleCommandSender/
		//command -> nom de la commande
		//label -> allias
		//args -> arguments
		
		
		
		if(command.getName().equalsIgnoreCase("checkpoint")) {
			if(sender instanceof Player) {	
				plugin.jumpClass.setCheckpointPlayer((Player)sender);
			}else if(sender instanceof ConsoleCommandSender) {
				plugin.sendmessage("{{RED}}Commande utilisable seulement par les joueurs", sender.getName());
			}else {
				plugin.sendmessage("{{RED}}Commande utilisable seulement par les joueurs", sender.getName());
			}
			return true;
		}
		
		return false;
	}

}

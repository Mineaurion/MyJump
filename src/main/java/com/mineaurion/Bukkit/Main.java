package com.mineaurion.Bukkit;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.mineaurion.Bukkit.command.CommandCheckpoint;
import com.mineaurion.Bukkit.command.CommandStopJump;
import com.mineaurion.Bukkit.command.CommandUpdate;

public class Main extends JavaPlugin {
	
	public Jump jumpClass;
	public Main instance=null;
	public MySQLEngine mysqlEngine;
	
	
	
	FileConfiguration config;

	
	
	
	//Boot du plugin il executera cela pour bukkit :
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		sendmessage("{{YELLOW}}MineJump Loading", "console");
		initConfig();
		initCommand();
		initDatabase();
		initEvents();
		
		jumpClass = new Jump(this);
		
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleAsyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				jumpClass.updateScorebooard();
			}
		}, 0L, 20L);
		
		
	}

	public void initEvents() {
		// 
		Bukkit.getServer().getPluginManager().registerEvents(new EventManager(this), this);
	}

	public void initDatabase() {
		if(config.getString("Database.Address").equalsIgnoreCase("none")) {
			sendmessage("{{DARK_RED}}Configure le plugin","console");
		}else {
			sendmessage("{{GOLD}}Loading DataBase", "console");
			try {
				mysqlEngine = new MySQLEngine(instance);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void initCommand() {
		
		getCommand("checkpoint").setExecutor(new CommandCheckpoint(instance));
		getCommand("stopjump").setExecutor(new CommandStopJump(instance));
		getCommand("updatescore").setExecutor(new CommandUpdate(instance));
	}

	public void initConfig() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		config = instance.getConfig();
		saveConfig();
		
	}
	
	
	public String prefix = "{{GREEN}}[{{YELLOW}}MineaurionJump{{GREEN}}]{{RESET}} ";
	
	@SuppressWarnings("deprecation")
	public void sendmessage(String message, String sender) {
		if (sender.equalsIgnoreCase("console") || sender.equalsIgnoreCase("Server")) {
			Bukkit.getConsoleSender().sendMessage(addColor(message));
		} else {
			Bukkit.getPlayer(sender).sendMessage(addColor(message));
		}
	}

	private  String addColor(String message) {
		message = prefix + message;
		StringBuilder textmain = new StringBuilder();
		Matcher m = Pattern.compile("(\\{\\{([^\\{\\}]+)\\}\\}|[^\\{\\}]+)").matcher(message);
		ChatColor color = null;
		ChatColor style = null;
		while (m.find()) {

			String entry = m.group();
			if (entry.contains("{{")) {
				color = null;
				style = null;
				switch (entry) {
				case "{{BLACK}}":
					color = ChatColor.BLACK;
					break;
				case "{{DARK_BLUE}}":
					color = ChatColor.DARK_BLUE;
					break;
				case "{{DARK_GREEN}}":
					color = ChatColor.DARK_GREEN;
					break;
				case "{{DARK_CYAN}}":
					color = ChatColor.DARK_AQUA;
					break;
				case "{{DARK_RED}}":
					color = ChatColor.DARK_RED;
					break;
				case "{{PURPLE}}":
					color = ChatColor.DARK_PURPLE;
					break;
				case "{{GOLD}}":
					color = ChatColor.GOLD;
					break;
				case "{{GRAY}}":
					color = ChatColor.GRAY;
					break;
				case "{{DARK_GRAY}}":
					color = ChatColor.DARK_GRAY;
					break;
				case "{{BLUE}}":
					color = ChatColor.AQUA;
					break;
				case "{{GREEN}}":
					color = ChatColor.GREEN;
					break;
				case "{{RED}}":
					color = ChatColor.RED;
					break;
				case "{{LIGHT_PURPLE}}":
					color = ChatColor.LIGHT_PURPLE;
					break;
				case "{{YELLOW}}":
					color = ChatColor.YELLOW;
					break;
				case "{{WHITE}}":
					color = ChatColor.WHITE;
					break;
				case "{{OBFUSCATED}}":
					style = ChatColor.MAGIC;
					break;
				case "{{BOLD}}":
					style = ChatColor.BOLD;
					break;
				case "{{STRIKETHROUGH}}":
					style = ChatColor.STRIKETHROUGH;
					break;
				case "{{UNDERLINE}}":
					style = ChatColor.UNDERLINE;
					break;
				case "{{ITALIC}}":
					style = ChatColor.ITALIC;
					break;
				case "{{RESET}}":
					style = ChatColor.RESET;
					break;
				}
			} else {
				StringBuffer buff = new StringBuffer(entry);

				if (color != null) {
					buff.insert(0, color);

				}
				if (style != null) {
					buff.insert(0, style);
				}
				textmain.append(buff);
			}
		}
		return textmain.toString();
	}
}

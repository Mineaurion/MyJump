package com.mineaurion.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.mineaurion.Bukkit.Main;


public class MySQLEngine {
	private Main plugin;
	public String address;
	public String port;
	public String databaseName;
	public String user;
	public String password;
	public String prefix;
	private Connection connection;
	
	public MySQLEngine(Main main) throws ClassNotFoundException, SQLException {
		plugin = main;
		address = plugin.config.getString("Database.Address");
		port = plugin.config.getString("Database.Port");
		databaseName = plugin.config.getString("Database.Db");
		user = plugin.config.getString("Database.Username");
		password = plugin.config.getString("Database.Password");
		prefix = plugin.config.getString("Database.Prefix");

		
		if (connection != null && !connection.isClosed()) {
	        connection.close();
	    }
		
		Class.forName("com.mysql.jdbc.Driver");
	    connection = DriverManager.getConnection("jdbc:mysql://" + address+ ":" + port + "/" + databaseName + "?autoReconnect=true", user, password);
	    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "leader` ( "
				+ "`uuid` VARCHAR(50) NOT NULL , "
				+ "`name` VARCHAR(100) NOT NULL , "
				+ "`time` INT NOT NULL , "
				+ "PRIMARY KEY (`uuid`)"
				+ ") ENGINE = InnoDB CHARSET=utf8;")
	    .execute();
	    
	    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "banned` ( "
				+ "`uuid` VARCHAR(50) NOT NULL , "
				+ "`name` VARCHAR(100) NOT NULL , "
				+ "`time` INT NOT NULL , "
				+ "PRIMARY KEY (`uuid`)"
				+ ") ENGINE = InnoDB CHARSET=utf8;")
	    .execute();
	 
	}
	
	public void setScorePlayer(String uuid,String name,int time)
	{
		try {
			PreparedStatement statement = null;
			statement = connection.prepareStatement("SELECT time FROM " + prefix + "leader" + " WHERE uuid=?");
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				if(time<set.getInt("time")) {
					updateScorePlayer(uuid,name,time);
				}
			}else {
				updateScorePlayer(uuid,name,time);
			}
		}catch (SQLException e) {
			plugin.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}
	
	public void updateScorePlayer(String uuid,String name,int time) {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement("REPLACE INTO " + prefix + "leader" + "(uuid,name,time) VALUES(?,?,?)");
			statement.setString(1, uuid);
			statement.setString(2, name);
			statement.setInt(3, time);
			statement.executeUpdate();
			statement.close();
		}catch (SQLException e) {
			plugin.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}
	
	public void AddBannedPlayer(String uuid,String name,int time)
	{
		try {
			PreparedStatement statement = null;
			statement = connection.prepareStatement("REPLACE INTO " + prefix + "banned" + "(uuid,name,time) VALUES(?,?,?)");
			statement.setString(1, uuid);
			statement.setString(2, name);
			statement.setInt(3, time);
			statement.executeUpdate();
			statement.close();
		}catch (SQLException e) {
			plugin.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
			e.printStackTrace();
		}
	}
	
	public boolean isBannedPlayer(String uuid) {
		try {
			PreparedStatement statement = null;
			statement = connection.prepareStatement("SELECT * FROM " + prefix + "banned WHERE uuid=?");
			statement.setString(1, uuid);
			ResultSet set = statement.executeQuery();
			if (set.next()) {
				set.close();
				statement.close();
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return false;
	}
	
	
	
	
	public HashMap<String, Integer> getThirdPlayer() {
		try {
			HashMap<String,Integer> top = new HashMap<String,Integer>();
			PreparedStatement statement = null;
			statement = connection.prepareStatement("SELECT * FROM " + prefix + "leader ORDER BY `time` LIMIT 3");
			ResultSet set = statement.executeQuery();
			while(set.next()) {
				top.put(set.getString("name"), set.getInt("time"));
			}
			set.close();
			statement.close();
			return top;
		} catch (SQLException e) {
			e.printStackTrace();
			plugin.sendmessage("{{DARK_RED}}" + e.getStackTrace().toString(), "console");
		}
		return null;
	}
	
	

}

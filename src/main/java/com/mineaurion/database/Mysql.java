package com.mineaurion.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mineaurion.Main;
import org.bukkit.ChatColor;

public class Mysql {
    private static Mysql _instance = null;
    private Main plugin;
    private String address;
    private String port;
    private String databaseName;
    private String user;
    private String password;
    private String prefix;
    private Connection connection;

    public static Mysql getInstance() {
        System.out.println("CREATED DB");
        if (_instance == null) {
            try {
                return new Mysql();
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return _instance;
    }

    public Mysql() throws ClassNotFoundException, SQLException {
        plugin = Main.getInstance();
        address = plugin.getConfig().getString("database.host");
        port = plugin.getConfig().getString("database.port");
        databaseName = plugin.getConfig().getString("database.db");
        user = plugin.getConfig().getString("database.username");
        password = plugin.getConfig().getString("database.password");
        prefix = plugin.getConfig().getString("database.prefix");

        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/" + databaseName + "?autoReconnect=true", user, password);
        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "leader` ( "
                        + "`uuid` VARCHAR(50) NOT NULL , " + "`name` VARCHAR(100) NOT NULL , "
                        + "`time` INT NOT NULL , " + "PRIMARY KEY (`uuid`)" + ") ENGINE = InnoDB CHARSET=utf8;")
                .execute();

        connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + prefix + "banned` ( "
                        + "`uuid` VARCHAR(50) NOT NULL , " + "`name` VARCHAR(100) NOT NULL , "
                        + "`time` INT NOT NULL , " + "PRIMARY KEY (`uuid`)" + ") ENGINE = InnoDB CHARSET=utf8;")
                .execute();
        _instance = this;
    }

    public void setScorePlayer(String uuid, String name, int time) {
        try {
            PreparedStatement statement = null;
            statement = connection.prepareStatement("SELECT time FROM " + prefix + "leader" + " WHERE uuid=?");
            statement.setString(1, uuid);
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                if (time < set.getInt("time")) {
                    updateScorePlayer(uuid, name, time);
                }
            } else {
                updateScorePlayer(uuid, name, time);
            }
        } catch (SQLException e) {
            plugin.sendMessage(ChatColor.BLUE + e.getStackTrace().toString(), "console");
            e.printStackTrace();
        }
    }

    public void updateScorePlayer(String uuid, String name, int time) {
        PreparedStatement statement = null;
        try {
            statement = connection
                    .prepareStatement("REPLACE INTO " + prefix + "leader" + "(uuid,name,time) VALUES(?,?,?)");
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setInt(3, time);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.sendMessage(ChatColor.DARK_RED + e.getStackTrace().toString(), "console");
            e.printStackTrace();
        }
    }

    public void AddBannedPlayer(String uuid, String name, int time) {
        try {
            PreparedStatement statement = null;
            statement = connection
                    .prepareStatement("REPLACE INTO " + prefix + "banned" + "(uuid,name,time) VALUES(?,?,?)");
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setInt(3, time);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.sendMessage(ChatColor.DARK_RED + e.getStackTrace().toString(), "console");
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
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.sendMessage(ChatColor.DARK_RED + e.getStackTrace().toString(), "console");
        }
        return false;
    }

    public ResultSet getThirdPlayer() {
        try {
            PreparedStatement statement = null;
            statement = connection.prepareStatement("SELECT * FROM " + prefix + "leader ORDER BY `time` LIMIT 3");
            ResultSet set = statement.executeQuery();
            return set;

        } catch (SQLException e) {
            e.printStackTrace();
            plugin.sendMessage(ChatColor.DARK_RED + e.getStackTrace().toString(), "console");
        }
        return null;
    }

}

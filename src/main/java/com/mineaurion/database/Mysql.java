package com.mineaurion.database;

import java.io.*;
import java.sql.*;

import com.mineaurion.Main;

public class Mysql {
    private static final String DRIVER_NAME = "com.mysql.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER_NAME).newInstance();
            System.out.println("*** Driver loaded");
        } catch (Exception e) {
            System.out.println("*** Error : " + e.toString());
            System.out.println("*** ");
            System.out.println("*** Error : ");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Main plugin = Main.getInstance();
        String host = plugin.getConfig().getString("database.host");
        String port = plugin.getConfig().getString("database.port");
        String databaseName = plugin.getConfig().getString("database.db");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
        String user = plugin.getConfig().getString("database.username");
        String password = plugin.getConfig().getString("database.password");

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void executeFile(String ressourceName) throws IOException, SQLException {
        Main plugin = Main.getInstance();
        Connection con =  Mysql.getConnection();

        ScriptRunner runner = new ScriptRunner(con, false, false);
        InputStream is = plugin.getResource(ressourceName);
        Reader reader = new InputStreamReader(is);
        runner.runScript(new BufferedReader(reader));

        reader.close();
        con.close();
    }
}

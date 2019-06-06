package com.mineaurion;

import com.mineaurion.database.Mysql;
import com.mineaurion.events.OnPlayerInteract;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main _instance = null;
    public Mysql db;
    public Jump jump;
    private int commandCount = 0;
    private int commandIgnoredCount = 0;

    public Main() {
        super();
        _instance = this;
        db = Mysql.getInstance();
    }

    public static Main getInstance() {
        return _instance;
    }

    @Override
    public void onEnable() {
        sendMessage("Start plugin... ");
        this.init();
    }

    @Override
    public void onDisable() {
        jump.clear();
        sendMessage("End plugin");
    }

    public void init() {
        initConfig();
        initEvents();
        initCommands();
        jump = Jump.getInstance();
    }

    private void initConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        saveConfig();
        sendMessage("File config.yml loaded");
    }

    private void initEvents() {
        getServer().getPluginManager().registerEvents(new OnPlayerInteract(), this);
    }

    private void initCommands() {
        sendMessage("Total : " + this.commandCount + " commands(s) loaded (" + this.commandIgnoredCount + " ignored)");
    }

    private void registerCommand(String name, CommandExecutor cmd) {
        if (getConfig().getBoolean(("commands." + name))) {
            getCommand(name).setExecutor(cmd);
            sendMessage("Command : " + name + " loaded");
            this.commandCount++;
        } else {
            getCommand(name).setUsage("/" + name + " currently disabled");
            sendMessage("Command : " + name + " ignored");
            this.commandIgnoredCount++;
        }
    }

    public void sendMessage(String msg, String player) {
        Bukkit.getPlayer(player).sendMessage(msg);
    }

    public void sendMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage("[" + this.getName() + "] " + msg);
    }
}

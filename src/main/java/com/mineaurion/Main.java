package com.mineaurion;

import com.mineaurion.commands.BanCommand;
import com.mineaurion.commands.ReloadCommand;
import com.mineaurion.commands.UnbanCommand;
import com.mineaurion.events.OnPlayerInteract;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public final class Main extends JavaPlugin {

    private static Main _instance = null;
    private static boolean debug = true;
    private Jump jump;
    private int commandCount = 0;
    private int commandIgnoredCount = 0;

    public Main() throws IOException, SQLException {
        super();
        jump = Jump.getInstance();
        _instance = this;
    }

    public static void debugMap(Map<String, Integer> map, String playerName) {
        if (!debug)
            return;
        Player p = Bukkit.getPlayer(playerName);
        map.forEach((name , time)-> {
            p.sendMessage("NAME = " + name + " TIME = " + time);
        });
    }

    public static Main getInstance() {
        return _instance;
    }

    @Override
    public void onEnable() {
        sendMessage("Start plugin... ");
        this.init();
        jump.init();
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
        registerCommand("jreload", new ReloadCommand());
        registerCommand("jban", new BanCommand());
        registerCommand("junban", new UnbanCommand());
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

    public Jump getJump() {
        return jump;
    }
}

package com.mineaurion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Jump {
    private static Jump _instance = null;
    private Main plugin;
    private HashMap<String, Jumper> jumpers = new HashMap<String, Jumper>();

    private Jump() {
        plugin = Main.getInstance();
        _instance = this;
    }

    public static Jump getInstance() {
        if (_instance == null)
            return new Jump();
        return _instance;
    }

    public Jumper getJumper(String playerName) {
        return jumpers.getOrDefault(playerName, null);
    }

    public void removeJumper(String playerName) {
        jumpers.remove(playerName);
    }

    public void addJumper(Jumper jumper) {
        jumpers.put(jumper.getName(), jumper);
        jumper.start();
    }

    public boolean jumperExist(String playerName) {
        return (jumpers.containsKey(playerName));
    }

    public void clear() {
        for (String key : jumpers.keySet()) {
            jumpers.get(key).stop(false);
            jumpers.remove(key);
        }
    }
}

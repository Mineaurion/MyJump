package com.mineaurion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Jump {
    private static Jump _instance = null;
    public Main plugin;

    ScoreboardManager sbManager = Bukkit.getScoreboardManager();

    HashMap<String, HashMap<String, Object>> players = new HashMap<String,HashMap<String, Object>>();

    public Jump() {
        plugin = Main.getInstance();
        _instance = this;
    }

    public static Jump getInstance() {
        if (_instance == null)
            return new Jump();
        return _instance;
    }

    private Scoreboard initScoreBoard() {
        Scoreboard sb = sbManager.getNewScoreboard();
        Objective objective = sb.registerNewObjective("timer", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Timer");
        objective.setDisplayName("000000000000");

        return sb;
    }

    private HashMap<String, Object> initPlayerJump() {
        HashMap<String, Object> datas = new HashMap<String, Object>();
        datas.put("start_time", new Date().getTime());
        datas.put("end_time",(long)0);
        datas.put("checkpoints", new ArrayList<Location>());
        datas.put("scoreboard", initScoreBoard());

        return datas;
    }

    public void start(Player player) {
        String name = player.getName();

        if (players.containsKey(name))
            return;

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "fly "+name+" off");
        players.put(name, initPlayerJump());
        
        plugin.sendMessage(ChatColor.BLUE + "Jump lancé", name);
        plugin.sendMessage(ChatColor.BLUE + "À toi de jouer, bonne chance", name);
        plugin.sendMessage(ChatColor.BLUE + "Tu as accès au "+ ChatColor.RED + "/checkpoint "+ ChatColor.BLUE +" pour retourner au checkpoint", name);

    }

    public void stop(Player player) {
        String name = player.getName();
        HashMap<String, Object> pdata = players.get(name);

        if((long)pdata.get("end_time") == 0) {
            players.remove(name);
            player.setScoreboard(sbManager.getMainScoreboard());
            player.sendMessage(ChatColor.RED + "Jump stop");
            return;
        }

        if(checkTimeToBan(current,player)) {
            setLeader(player, current);
            MainClass.sendmessage("{{GOLD}} Bravo à toi !", player.getName());
            sendChrono(player, "{{BLUE}}Ton temps final ",current);
        }
    }


}

package com.mineaurion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Date;

public class Jumper {
    private Main plugin;
    private String name;
    private ArrayList<Location> checkpoints = new ArrayList<Location>();
    private long start_time;
    private long end_time;

    private boolean isRunning = true;

    private int milliseconds = 0;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    public Jumper(String playerName) {
        plugin = Main.getInstance();
        name = playerName;
        start_time = new Date().getTime();

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "fly " + name + " off");
        plugin.sendMessage(ChatColor.BLUE + "Jump start", name);
        plugin.sendMessage(ChatColor.BLUE + "C'est parti!", name);

        Jump.getInstance().addJumper(this);
    }

    public void start() {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = sb.registerNewObjective("timer", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Chronometre");

        Bukkit.getPlayer(name).setScoreboard(sb);

        new BukkitRunnable() {
            public void run() {
                if (!Jumper.this.isRunning)
                    this.cancel();
                else {
                    long now = new Date().getTime();
                    long diff = now - Jumper.this.start_time;

                    Jumper.this.setTimer(diff);
                }
            }

        }.runTaskTimer(plugin, 0, 1);
    }

    public void stop(boolean end) {
        Bukkit.getPlayer(name).setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        plugin.sendMessage(ChatColor.RED + "Jump stop", name);
        isRunning = false;
        if (end) {
            end_time = new Date().getTime();
            plugin.sendMessage(getTimer(), name);
        }
        Jump.getInstance().removeJumper(name);
    }

    public void update(Location newCheckpoint) {
        for (Location oldCheckpoint : checkpoints)
            if (oldCheckpoint.distance(newCheckpoint) == 0)
                return;

        checkpoints.add(newCheckpoint);
        plugin.sendMessage(getTimer(), name);
    }

    private String getTimer() {
        return hours + "h " + minutes + "mn " + seconds + "s " + milliseconds + "ms";
    }

    private void setTimer(long timestamp) {
        Objective objective = Bukkit.getPlayer(name).getScoreboard().getObjective("timer");

        Score _hour = objective.getScore(ChatColor.YELLOW + "Heures");
        Score _min = objective.getScore(ChatColor.YELLOW + "Minutes");
        Score _sec = objective.getScore(ChatColor.YELLOW + "Secondes");

        milliseconds = (int) (timestamp % 1000);
        seconds = (int) (timestamp / 1000) % 60;
        minutes = (int) ((timestamp / (1000 * 60)) % 60);
        hours = (int) (timestamp / (1000 * 60 * 60)) % 24;

        _hour.setScore(hours);
        _min.setScore(minutes);
        _sec.setScore(seconds);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Location> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(ArrayList<Location> locations) {
        this.checkpoints = locations;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }
}

package com.mineaurion;

import com.mineaurion.database.Mysql;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class Jumper {
    private Main plugin;
    private String name;
    private String uuid;
    private ArrayList<Location> checkpoints = new ArrayList<Location>();
    private long start_time;
    private long end_time;

    private boolean isRunning = true;

    private int milliseconds = 0;
    private int seconds = 0;
    private int minutes = 0;
    private int hours = 0;

    public Jumper(String playerName, boolean fictive) {
        plugin = Main.getInstance();
        name = playerName;

        if (!fictive) {
            start_time = new Date().getTime();
            Jump.getInstance().addJumper(this);
        }
    }

    public boolean isZorinova() {
        return (name.toLowerCase().equals("zorinova") || Bukkit.getPlayer(name).getUniqueId().toString().equals("74285c05-8ed4-4c24-baa0-f0cca33d29e9"));
    }

    public String getName() {
        return name;
    }

    public boolean hasCheckpoints() {
        return (!checkpoints.isEmpty());
    }

    public void goToCheckpoint() {
        Location cp = checkpoints.get(checkpoints.size() - 1);
        Player player = Bukkit.getPlayer(name);

        player.teleport(cp);
    }

    public void start() {
        if (isBan()) {
            plugin.sendMessage(ChatColor.RED + "Tu es ban du parcours...", name);
            stop(false);
            return;
        }

        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = sb.registerNewObjective("timer", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Chronometre");

        Bukkit.getPlayer(name).setScoreboard(sb);

        if (isZorinova())
            plugin.sendMessage(ChatColor.DARK_AQUA + "Zorinova :"+ ChatColor.WHITE +" joueur de type enfant commence le parcous", name);

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "fly " + name + " off");
        plugin.sendMessage(ChatColor.DARK_AQUA + "Jump start", name);
        plugin.sendMessage(ChatColor.DARK_AQUA + "C'est parti!", name);

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
        isRunning = false;
        if (end) {
            end_time = new Date().getTime();
            int time = (int) end_time - (int) start_time;
            if (isBan()) {
                Jump.getInstance().removeJumper(name);
                plugin.sendMessage(ChatColor.DARK_AQUA  + "Ah mince tu as été ban durant ton petit paroours...");
                return;
            }
            if (!valideTime(time)) {
                ban();
                plugin.sendMessage(ChatColor.DARK_AQUA + "Bien joué !" + ChatColor.WHITE + " Tu viens de te prendre un ban sur le parcours :)!", name);
            }
            else {
                save(time);
                plugin.sendMessage(ChatColor.DARK_AQUA + "Bien joué !" + ChatColor.WHITE + " Tu as fini le parcours!", name);
            }

            plugin.sendMessage("Ton temps final : " + getTimer(), name);
        }
        Jump.getInstance().removeJumper(name);
    }

    public void update(Location newCheckpoint) {
        for (Location oldCheckpoint : checkpoints)
            if (oldCheckpoint.distance(newCheckpoint) == 0)
                return;

        Vector direction = Bukkit.getPlayer(name).getLocation().getDirection();
        newCheckpoint.setDirection(direction);

        checkpoints.add(newCheckpoint);

        if (isZorinova())
            plugin.sendMessage(ChatColor.DARK_AQUA + "Zorinova lache rien petit gars!");
        plugin.sendMessage(getTimer(), name);
    }

    private void save(int time) {
        final String INSERT = "INSERT INTO scores (uuid, name, time, created_at) VALUES (?,?,?,?)";
        Connection conn = Mysql.getConnection();
        if (conn == null)
            return;

        String uuid = Bukkit.getPlayer(name).getUniqueId().toString();
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT)) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, time);
            preparedStatement.setDate(4, new java.sql.Date(start_time));
            preparedStatement.executeUpdate();

            Jump.getInstance().addScore(name, time);
            plugin.sendMessage("Player " + name + " (" + uuid + ") with time " + getTimer() + " saved in DB");
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean valideTime(int time) {
        int min = plugin.getConfig().getInt("jump.minSeconds");
        return (time / 1000) >= min;
    }

    public boolean isBan() {
        return Jump.getInstance().getBanned().contains(name);
    }

    public void ban() {
        final String INSERT = "INSERT INTO banned (uuid, name, created_at) VALUES (?,?,?)";
        Connection conn = Mysql.getConnection();
        if (conn == null)
            return;

        String uuid = Bukkit.getPlayer(name).getUniqueId().toString();
        try (PreparedStatement preparedStatement = conn.prepareStatement(INSERT)) {
            preparedStatement.setString(1, uuid);
            preparedStatement.setString(2, name);
            preparedStatement.setDate(  3, new java.sql.Date(new Date().getTime()));
            preparedStatement.executeUpdate();

            Jump.getInstance().addBanned(name);
            plugin.sendMessage("Player " + name + " (" + uuid + ") has been banned!");
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unban() {
        final String DELETE = "DELETE FROM banned WHERE name = ?";
        Connection conn = Mysql.getConnection();
        if (conn == null)
            return;

        String uuid = Bukkit.getPlayer(name).getUniqueId().toString();
        try (PreparedStatement preparedStatement = conn.prepareStatement(DELETE)) {
            preparedStatement.setString(1, name);

            preparedStatement.execute();
            Jump.getInstance().removeBanned(name);
            plugin.sendMessage("Player " + name + " (" + uuid + ") has been unbanned!");
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}

package com.mineaurion;

import com.mineaurion.database.Mysql;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Jump {
    private static Jump _instance = null;
    private Main plugin;
    // current jumpers
    private HashMap<String, Jumper> jumpers = new HashMap<String, Jumper>();
    // banned players
    private ArrayList<String> banned = new ArrayList<String>();
    // jumpers scores
    private Map<String, Integer> scores = new HashMap<>();

    private Jump() {
        plugin = Main.getInstance();

        _instance = this;
    }

    public static Jump getInstance() {
        if (_instance == null)
            return new Jump();
        return _instance;
    }

    public Jump init() {
        try {
            Mysql.executeFile("jump.sql");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        loadBannedPlayers();
        loadBestScoresPlayers();

        return this;
    }

    public void reload() {
        loadBannedPlayers();
    }

    public void cpBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block bunder = event.getClickedBlock().getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();

        if (bunder == null)
            return;

        Material startBlock = Material.LAPIS_BLOCK;
        Material checkBlock = Material.EMERALD_BLOCK;
        Material endBlock = Material.DIAMOND_BLOCK;

        boolean exist = Jump.getInstance().jumperExist(player.getName());

        if (bunder.getType().equals(startBlock) && !exist)
            new Jumper(player.getName(), false).start();
        if (bunder.getType().equals(checkBlock) && exist)
            getJumper(player.getName()).update(event.getClickedBlock().getLocation());
        if (bunder.getType().equals(endBlock) && exist)
            getJumper(player.getName()).stop(true);
    }

    public void cpItemInteract(PlayerInteractEvent event, boolean stop) {
        event.setCancelled(true);

        Player player = event.getPlayer();

        boolean exist = jumperExist(player.getName());

        if (!exist) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas encore démarrer le jump!");
            return;
        }

        Jumper jumper = getJumper(player.getName());
        if (stop) {
            jumper.stop(false);
            player.sendMessage(ChatColor.DARK_AQUA + "Event stop en cours de route!");
        } else {
            if (!jumper.hasCheckpoints()) {
                player.sendMessage(ChatColor.RED + "Tu n'as pas encore un checkpoint!");
                return;
            }

            jumper.goToCheckpoint();
        }
    }


    public void clear() {
        if (jumpers.size() > 0) {
            for (String key : jumpers.keySet()) {
                jumpers.get(key).stop(false);
                jumpers.remove(key);
            }
        }
    }

    public Jumper getJumper(String playerName) {
        return jumpers.getOrDefault(playerName, null);
    }

    public void removeJumper(String playerName) {
        jumpers.remove(playerName);
    }

    public void addJumper(Jumper jumper) {
        jumpers.put(jumper.getName(), jumper);
    }

    public boolean jumperExist(String playerName) {
        return (jumpers.containsKey(playerName));
    }

    public ArrayList<String> getBanned() {
        return banned;
    }

    public void addBanned(String name) {
        if (!banned.contains(name))
            banned.add(name);
    }

    public void removeBanned(String name) {
        banned.remove(name);
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void addScore(String playerName, Integer time) {
        if (scores.containsKey(playerName)) {
            if (scores.get(playerName) > time)
                scores.put(playerName, time);
        } else {
            scores.put(playerName, time);
        }
        sortScores();
    }

    private void sortScores() {
        class ScoresSort implements Comparator<Map.Entry<String, Integer>> {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<>(scores.entrySet());
        list.sort(new ScoresSort());

        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        scores = sortedMap;
    }

    private void loadBannedPlayers() {
        final String SELECT = "SELECT name FROM banned";
        Connection conn = Mysql.getConnection();
        if (conn == null)
            return;

        banned = new ArrayList<String>();

        try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next())
                    banned.add(rs.getString(1));
                preparedStatement.close();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBestScoresPlayers() {
        final String SELECT = "SELECT s.name, s.time FROM scores s INNER JOIN (SELECT name, MIN(time) AS mtime FROM scores GROUP BY name) AS tmp ON  tmp.name = s.name AND tmp.mtime = s.time ORDER BY time ASC;";
        Connection conn = Mysql.getConnection();
        if (conn == null)
            return;

        try (PreparedStatement preparedStatement = conn.prepareStatement(SELECT)) {
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next())
                    scores.put(rs.getString(1), rs.getInt(2));
                preparedStatement.close();
                sortScores();
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

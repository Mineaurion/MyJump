package com.mineaurion;

import com.mineaurion.database.Mysql;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


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
        loadBannedPlayers();
        loadBestScoresPlayers();
        spawnLadders();
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
        return this;
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
            new Jumper(player.getName()).start();
        if (bunder.getType().equals(checkBlock) && exist)
            getJumper(player.getName()).update(event.getClickedBlock().getLocation());
        if (bunder.getType().equals(endBlock) && exist)
            getJumper(player.getName()).stop(true);
    }

    public void cpItemInteract(PlayerInteractEvent event) {
        event.setCancelled(true);

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        boolean exist = jumperExist(player.getName());

        if (!exist) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas encore démarrer le jump!");
            return;
        }

        Jumper jumper = getJumper(player.getName());

        if (!jumper.hasCheckpoints()) {
            player.sendMessage(ChatColor.RED + "Tu n'as pas encore un checkpoint!");
            return;
        }

        jumper.goToCheckpoint();
    }


    public void clear() {
        for (String key : jumpers.keySet()) {
            jumpers.get(key).stop(false);
            jumpers.remove(key);
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

    public HashMap<String, Integer> getThirdTopScores() {
        HashMap<String, Integer> top = new HashMap<String, Integer>();
        scores.forEach((name, time) -> {
            if (top.size() < 3)
                top.put(name, time);
        });
        return top;
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

    private void spawnLadders() {
        double x = plugin.getConfig().getDouble("jump.ladders.x");
        double y = plugin.getConfig().getDouble("jump.ladders.y");
        double z = plugin.getConfig().getDouble("jump.ladders.z");

        Location location = new Location(Bukkit.getWorld("world"), x, y, z);
        spawnBaseBlock(location);
    }

    private void spawnBaseBlock(Location location) {
        Block base = location.getBlock();
        Block first = location.clone().add(0, 1, 0).getBlock();
        Block second;
        Block third;

        String direction = plugin.getConfig().getString("jump.ladders.d");

        switch (direction) {
            case "WEST":
                second = location.clone().subtract(0, 0, 1).getBlock();
                third = location.clone().add(0, 0, 1).getBlock();
                break;
            case "EAST":
                second = location.clone().add(0, 0, 1).getBlock();
                third = location.clone().subtract(0, 0, 1).getBlock();
                break;
            case "SOUTH":
                second = location.clone().subtract(1, 0, 0).getBlock();
                third = location.clone().add(1, 0, 0).getBlock();
                break;
            case "NORTH":
                second = location.clone().add(1, 0, 0).getBlock();
                third = location.clone().subtract(1, 0, 0).getBlock();
                break;
            default:
                return;
        }
        base.setType(Material.WOOD);
        first.setType(Material.DIAMOND_BLOCK);
        second.setType(Material.GOLD_BLOCK);
        third.setType(Material.IRON_BLOCK);
        spawnHeadSign(first, second, third);
    }

    private void spawnHeadSign(Block first, Block second, Block third) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        blocks.add(first);
        blocks.add(second);
        blocks.add(third);

        int index = 0;
        final String direction = plugin.getConfig().getString("jump.ladders.d");

        for(Map.Entry<String, Integer> entry : getThirdTopScores().entrySet()) {
            Location tmp = blocks.get(index).getLocation().add(0, 1, 0);
            Block current = tmp.getBlock();
            current.setType(Material.SKULL);
            Skull skull = (Skull)current.getState();
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwner(entry.getKey());
            skull.setRotation(BlockFace.valueOf(direction));
            skull.update(true);
            index++;
        }
    }

    private void loadBannedPlayers() {
        final String SELECT = "SELECT name FROM banned";
        Connection conn = Mysql.getConnection();
        if (conn == null)
            return;

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

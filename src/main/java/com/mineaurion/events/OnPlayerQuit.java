package com.mineaurion.events;

import com.mineaurion.Jump;
import com.mineaurion.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class OnPlayerQuit implements Listener {
    private Main plugin;
    private Jump jump;

    public OnPlayerQuit() {
        plugin = Main.getInstance();
        jump = Jump.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handler(PlayerQuitEvent event) {
        if (jump.jumperExist(event.getPlayer().getName())) {
            plugin.sendMessage("Player " + event.getPlayer().getName() + " leave the lobby and removed from jump event!");
            jump.getJumper(event.getPlayer().getName()).stop(false);
        }
    }
}

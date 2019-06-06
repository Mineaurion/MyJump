package com.mineaurion.events;

import com.mineaurion.Jump;
import com.mineaurion.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class OnPlayerCommandPreprocess implements Listener {
    private Main plugin;
    private Jump jump;

    public OnPlayerCommandPreprocess() {
        plugin = Main.getInstance();
        jump = Jump.getInstance();
    }

    @EventHandler
    public void handler(PlayerCommandPreprocessEvent event) {
        if((event.getMessage().contains("/sethome")
                || event.getMessage().contains("/fly")
                || event.getMessage().contains("/home")
                || event.getMessage().contains("/back")
                || event.getMessage().contains("/tp"))
                /*&& jump.hasStartedJump(event.getPlayer())*/) {
            event.setCancelled(true);
        }

    }
}

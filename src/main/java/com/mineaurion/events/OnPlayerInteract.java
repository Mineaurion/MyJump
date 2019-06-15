package com.mineaurion.events;

import com.mineaurion.Jump;
import com.mineaurion.Jumper;
import com.mineaurion.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class OnPlayerInteract implements Listener {
    private Main plugin;
    private Jump jump;

    public OnPlayerInteract(){
        plugin = Main.getInstance();
        jump = Jump.getInstance();
    }

    @EventHandler
    public void handler(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if(action.equals(Action.PHYSICAL)) {
            if (event.getClickedBlock().getType().equals(Material.GOLD_PLATE))
                jump.cpBlockInteract(event);
        }

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if ( item != null && item.getType() == Material.ENDER_PEARL)
                jump.cpItemInteract(event);
        }
    }

}

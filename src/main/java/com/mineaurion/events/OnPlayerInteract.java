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

public class OnPlayerInteract implements Listener {
    private Main plugin;
    private Jump jump;

    public OnPlayerInteract() {
        plugin = Main.getInstance();
        jump = Jump.getInstance();
    }

    @EventHandler
    public void handler(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.PHYSICAL))
            return;
        if(!event.getClickedBlock().getType().equals(Material.GOLD_PLATE))
            return;

        Player player = event.getPlayer();
        Block bunder = event.getClickedBlock().getLocation().subtract(0.0D,1.0D,0.0D).getBlock();

        if (bunder == null)
            return;

        Material startBlock = Material.LAPIS_BLOCK;
        Material checkBlock = Material.EMERALD_BLOCK;
        Material endBlock = Material.DIAMOND_BLOCK;

        boolean exist = Jump.getInstance().jumperExist(player.getName());

        if (bunder.getType().equals(startBlock) && !exist)
            new Jumper(player.getName()).start();
        if (bunder.getType().equals(checkBlock) && exist)
            jump.getJumper(player.getName()).update(event.getClickedBlock().getLocation());
        if (bunder.getType().equals(endBlock) && exist)
            jump.getJumper(player.getName()).stop(true);
    }

}

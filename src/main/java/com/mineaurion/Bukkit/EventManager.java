package com.mineaurion.Bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {

	private Main plugin;
	//constructeur pour appeler la classe en dehors du demarrage
	public EventManager(Main main) {
		plugin = main;
	}

	@EventHandler
	public void JoueursMarcheEvent(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.PHYSICAL)) {
			Player player = event.getPlayer();
			if(event.getClickedBlock().getType().equals(Material.GOLD_PLATE)) {
				Block bunder = event.getClickedBlock().getLocation().subtract(0.0D,1.0D,0.0D).getBlock();
				if(bunder != null && bunder.getType().equals(Material.LAPIS_BLOCK)) {
					if(plugin.mysqlEngine.isBannedPlayer(player.getUniqueId().toString())) {
						plugin.sendmessage("{{RED}}Le jump est pas autorisé au tricheur", player.getName());
						player.teleport(player.getWorld().getSpawnLocation());
						return;
					}
					Location location = event.getClickedBlock().getLocation().add(0.0D,1.0D,0.0D);
					plugin.jumpClass.startJump(player,location);
				} else if(bunder != null && bunder.getType().equals(Material.EMERALD_BLOCK) && plugin.jumpClass.hasStartedJump(player)) {
					Location location = event.getClickedBlock().getLocation().add(0.0D,1.0D,0.0D);
					plugin.jumpClass.setCheckpointJump(player,location,player.getLocation().getDirection());
				} else if(bunder != null && bunder.getType().equals(Material.DIAMOND_BLOCK) && plugin.jumpClass.hasStartedJump(player)) {
					plugin.jumpClass.stopJump(player,true);
				}
			}
		}
	}
	
	@EventHandler
	public void SupprCmdHome(PlayerCommandPreprocessEvent event) {
		if((event.getMessage().contains("/sethome") 
				|| event.getMessage().contains("/fly")
				|| event.getMessage().contains("/home")
				|| event.getMessage().contains("/back")
				|| event.getMessage().contains("/tp"))
				&& plugin.jumpClass.hasStartedJump(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerDisconnect(PlayerQuitEvent event) {
		plugin.jumpClass.stopJump(event.getPlayer(),false);
	}

}

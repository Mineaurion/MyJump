package com.mineaurion.minejump;

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
			Player playerplate = event.getPlayer();
			if(event.getClickedBlock().getType().equals(Material.GOLD_PLATE)) {
				Block bunder = event.getClickedBlock().getLocation().subtract(0.0D,1.0D,0.0D).getBlock();
				if(bunder != null && bunder.getType().equals(Material.LAPIS_BLOCK)) {
					Location location = event.getClickedBlock().getLocation().add(0.0D,1.0D,0.0D);
					plugin.jumpClass.startJump(playerplate,location);
				} else if(bunder != null && bunder.getType().equals(Material.EMERALD_BLOCK) && plugin.jumpClass.hasStartedJump(playerplate)) {
					Location location = event.getClickedBlock().getLocation().add(0.0D,1.0D,0.0D);
					plugin.jumpClass.setCheckpointJump(playerplate,location,playerplate.getVelocity());
				} else if(bunder != null && bunder.getType().equals(Material.DIAMOND_BLOCK) && plugin.jumpClass.hasStartedJump(playerplate)) {
					plugin.jumpClass.stopJump(playerplate,true);
				}
			}
		}
	}
	
	@EventHandler
	public void SupprCmdHome(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().contains("/sethome") && plugin.jumpClass.hasStartedJump(event.getPlayer())) {
			event.setCancelled(true);
		}else if(event.getMessage().contains("/fly") && plugin.jumpClass.hasStartedJump(event.getPlayer())) {
			event.setCancelled(true);
		}else if(event.getMessage().contains("/home") && plugin.jumpClass.hasStartedJump(event.getPlayer())) {
			event.setCancelled(true);
		}if(event.getMessage().contains("/homes") && plugin.jumpClass.hasStartedJump(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void playerDisconnect(PlayerQuitEvent event) {
		plugin.jumpClass.stopJump(event.getPlayer(),false);
	}

}

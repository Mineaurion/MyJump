package com.mineaurion.minejump;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

public class Jump {
	
	public Main MainClass;
	
	 ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
	
	HashMap<String,Long> crono = new HashMap<String,Long>();
	HashMap<String,Location> checkpoint = new HashMap<String,Location>();
	HashMap<String,Vector> checkpointRot = new HashMap<String,Vector>();
	
	HashMap<String,Scoreboard> scoreboards = new HashMap<String,Scoreboard>();

	
	
	public Jump(Main main) {
		MainClass = main;
		
	}


	public void startJump(Player playerplate, Location location) {
		if(compareTimeStarted(playerplate)) {
			MainClass.getServer().dispatchCommand(MainClass.getServer().getConsoleSender(), "fly "+playerplate.getName()+" off");
			crono.put(playerplate.getName(), System.currentTimeMillis());
			checkpoint.put(playerplate.getName(),location);
			MainClass.sendmessage("{{BLUE}}Timer lancé", playerplate.getName());
			MainClass.sendmessage("{{BLUE}}A toi de jouer, bonne chance", playerplate.getName());
			MainClass.sendmessage("{{BLUE}}Tu a accès au {{RED}}/checkpoint {{BLUE}} pour retourner au checkpoint", playerplate.getName());
			createScoreboard(playerplate);
		}
	
	}
	
	public boolean compareTimeStarted(Player player) {
		if(crono.containsKey(player.getName())) {
			Long time = (System.currentTimeMillis() - crono.get(player.getName()));
			if((time/1000)>=5) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	public boolean hasStartedJump(Player player) {
		if(crono.containsKey(player.getName())) {
			return true;
		}
		return false;
	}
	
	//HashMap->put
	//		->get
	//		->containsKey
	
	
	public boolean Checkpointegal(Player player,Location location) 
	{
		if(checkpoint.containsKey(player.getName())) {
			if(checkpoint.get(player.getName()).equals(location)) {
				return true;
			}else {
				return false;
			}	
		}else{
			return false;
		}
	}
	
	
	public void stopJump(Player playerplate,boolean finish) {
		if(finish) {
			Long current = System.currentTimeMillis();
			setFirstPosition(playerplate,current);
			MainClass.sendmessage("{{GOLD}} Bravo à toi !", playerplate.getName());
			sendChrono(playerplate, "{{BLUE}}Ton temps final ",current);
		}
		crono.remove(playerplate.getName());
		checkpoint.remove(playerplate.getName());
		scoreboards.remove(playerplate.getName());
		playerplate.setScoreboard(scoreboardmanager.getMainScoreboard());
		
	}
	
	
	
	public void setCheckpointJump(Player playerplate, Location location,Vector vector) {	
		if(!Checkpointegal(playerplate, location)) {
			checkpoint.put(playerplate.getName(),location);
			checkpointRot.put(playerplate.getName(), vector);
			MainClass.sendmessage("{{LIGHT_PURPLE}}CheckPoint", playerplate.getName());
			sendChrono(playerplate,"{{BLUE}}Ton temps au checkpoint ",System.currentTimeMillis());
		}
	}


	public void setCheckpointPlayer(Player player) {
		if(checkpoint.containsKey(player.getName())) {
			Location location = checkpoint.get(player.getName()).add(0.5D,0.5D,0.5D).setDirection(checkpointRot.get(player.getName()));
			player.teleport(location);
			
		}else {
			MainClass.sendmessage("{{DARK_CYAN}}No checkpoint", player.getName());
		}
	}
	
	
	public void sendChrono(Player player,String message,Long current) {
		Long time = (current - crono.get(player.getName()));
		
		String miliseconds = String.valueOf((int) (time%1000));
		String seconds = String.valueOf((int) (time / 1000) % 60 );
		String minutes = String.valueOf((int) ((time / (1000*60)) % 60));
		String hours   = String.valueOf((int) ((time / (1000*60*60)) % 24));
		
		String cronotime = "{{BLUE}}"+message+"est de : {{RED}}"+hours+" {{BLUE}}heures {{RED}}"+minutes+" {{BLUE}}minutes {{RED}}"+seconds+" {{BLUE}}secondes {{RED}}"+miliseconds+" {{BLUE}}milisecondes ";
		
		MainClass.sendmessage(cronotime, player.getName());
	}
	
	@SuppressWarnings("deprecation")
	public void createScoreboard(Player player) {
		Scoreboard board = scoreboardmanager.getNewScoreboard();
		Objective objective = board.registerNewObjective("timer", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Timer");
		
		Score heure = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Heure"));
		Score minute = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Minute"));
		Score second = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Seconde"));
		Score milisecond = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Milisecond"));
		Long time = (System.currentTimeMillis() - crono.get(player.getName()));
		int miliseconds = (int) (time%1000);
		int seconds = (int) (time / 1000) % 60 ;
		int minutes = (int) ((time / (1000*60)) % 60);
		int hours   = (int) ((time / (1000*60*60)) % 24);
		
		heure.setScore(hours);
		minute.setScore(minutes);
		second.setScore(seconds);
		milisecond.setScore(miliseconds);
		
		player.setScoreboard(board);
		scoreboards.put(player.getName(), board);
	}
	
	@SuppressWarnings("deprecation")
	public void updateScorebooard() {
		
		Iterator<Entry<String, Scoreboard>> it = scoreboards.entrySet().iterator();
		
		while(it.hasNext()) {
			
			Map.Entry<String,Scoreboard> pair = (Map.Entry<String,Scoreboard>)it.next();
			String key = pair.getKey();
			Scoreboard board = pair.getValue();
			
			Objective objective = board.getObjective(DisplaySlot.SIDEBAR);
			Score heure = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Heure"));
			Score minute = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Minute"));
			Score second = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Seconde"));
			Score milisecond = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.BLUE+"Milisecond"));
			Long time = (System.currentTimeMillis() - crono.get(key));
			int miliseconds = (int) (time%1000);
			int seconds = (int) (time / 1000) % 60 ;
			int minutes = (int) ((time / (1000*60)) % 60);
			int hours   = (int) ((time / (1000*60*60)) % 24);
			
			heure.setScore(hours);
			minute.setScore(minutes);
			second.setScore(seconds);
			milisecond.setScore(miliseconds);
			
			Bukkit.getPlayer(key).setScoreboard(board);
			scoreboards.put(key, board);
		}
	}
	public void setFirstPosition(Player player,Long current) {
		Long timestamp = (current - crono.get(player.getName()));
		String mili = String.valueOf((int) (timestamp%1000));
		String seconde = String.valueOf((int) (timestamp / 1000) % 60 );
		String minute = String.valueOf((int) ((timestamp / (1000*60)) % 60));
		String hour   = String.valueOf((int) ((timestamp / (1000*60*60)) % 24));
		
		
        Location locsignTime = new Location(player.getWorld(), 70, 73, -70);
        Sign timestampSign = (Sign)locsignTime.getBlock().getState();
        Long timestampSignValue = Long.valueOf(timestampSign.getLine(0));
        
        if(timestamp<timestampSignValue) {
            timestampSign.setLine(0, String.valueOf(timestamp));
            timestampSign.update();
            
            
            
            Location locTimerSign = new Location(player.getWorld(), 70, 73, -68);
            Sign timerSign = (Sign)locTimerSign.getBlock().getState();
            String oldName = timerSign.getLine(3);
            
            setTimerSign(locTimerSign,hour,minute,seconde,mili,player.getName());
            
            Location locPlayerHead = new Location(player.getWorld(), 70, 74, -69);
            spawnHeadBlock(locPlayerHead, player.getName(), "SOUTH");
            
            
            
            setSecondPosition(player,timestampSignValue,oldName);
        }else {
            setSecondPosition(player,timestamp,player.getName());
        }
    }
    
    public void setSecondPosition(Player player,Long timestamp,String name) {
 
		String mili = String.valueOf((int) (timestamp%1000));
		String seconde = String.valueOf((int) (timestamp / 1000) % 60 );
		String minute = String.valueOf((int) ((timestamp / (1000*60)) % 60));
		String hour   = String.valueOf((int) ((timestamp / (1000*60*60)) % 24));
		
        Location locsignTime = new Location(player.getWorld(), 69, 72, -70);
        Sign timestampSign = (Sign)locsignTime.getBlock().getState();
        Long timestampSignValue = Long.valueOf(timestampSign.getLine(0));
        if(timestamp<timestampSignValue) {
            timestampSign.setLine(0, String.valueOf(timestamp));
            timestampSign.update();
            
            Location locTimerSign = new Location(player.getWorld(), 69, 72, -68);
            Sign timerSign = (Sign)locTimerSign.getBlock().getState();
            String oldName = timerSign.getLine(3);
            setTimerSign(locTimerSign,hour,minute,seconde,mili,name);
            
            Location locPlayerHead = new Location(player.getWorld(), 69, 73, -69);
            spawnHeadBlock(locPlayerHead, name, "SOUTH");
            
            
            setThirdPosition(player,timestampSignValue,oldName);
        }else {
            setThirdPosition(player,timestamp,player.getName());
        }
    }
    
    public void setThirdPosition(Player player,Long timestamp,String name) {
    	String mili = String.valueOf((int) (timestamp%1000));
		String seconde = String.valueOf((int) (timestamp / 1000) % 60 );
		String minute = String.valueOf((int) ((timestamp / (1000*60)) % 60));
		String hour   = String.valueOf((int) ((timestamp / (1000*60*60)) % 24));
    	
        Location locsignTime = new Location(player.getWorld(), 71, 72, -70);
        Sign timestampSign = (Sign)locsignTime.getBlock().getState();
        Long timestampSignValue = Long.valueOf(timestampSign.getLine(0));
        
        if(timestamp<timestampSignValue) {
            timestampSign.setLine(0, String.valueOf(timestamp));
            timestampSign.update();
            
            Location locTimerSign = new Location(player.getWorld(), 71, 72, -68);
            setTimerSign(locTimerSign,hour,minute,seconde,mili,name);
            
            Location locPlayerHead = new Location(player.getWorld(), 71, 73, -69);
            spawnHeadBlock(locPlayerHead, name, "SOUTH");
        }
    }
    
    public void setTimerSign(Location loc,String hour,String minute,String seconde,String mili,String name) {
        Sign TimerSign = (Sign)loc.getBlock().getState();
        TimerSign.setLine(1, "Time");
        TimerSign.setLine(2, ChatColor.GOLD+hour+"H "+minute+"min "+seconde+"s "+mili);
        TimerSign.setLine(3, name);
        TimerSign.update();
    }
    
    
     @SuppressWarnings("deprecation")
     public void spawnHeadBlock(Location loc, String name, String face)
     {
      
     Block b = loc.getBlock();
     b.setTypeIdAndData(Material.SKULL.getId(), (byte) 1, true);
     Skull skull = (Skull) b.getState();
     skull.setSkullType(SkullType.PLAYER);
     skull.setOwner(name);
     skull.setRotation(BlockFace.valueOf(face));
     skull.update(true);
      
     }
	
}

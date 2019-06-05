package events;

import com.mineaurion.Jump;
import com.mineaurion.Main;
import org.bukkit.ChatColor;
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

        // START JUMP
        if(bunder.getType().equals(startBlock)) {
            jump.start(player);
        }
        /*
        // CHECKPOINT JUMP (WHILE RUNNING)
        if(bunder.getType().equals(checkBlock) && plugin.jumpClass.hasStartedJump(player)) {
            Location location = event.getClickedBlock().getLocation().add(0.0D,1.0D,0.0D);
            plugin.jumpClass.setCheckpointJump(player,location,player.getLocation().getDirection());
        }

        // END JUMP (WHILE RUNNING)
        if(bunder.getType().equals(endBlock)) {
            plugin.jumpClass.stopJump(player,true);
        }
        */

    }

}

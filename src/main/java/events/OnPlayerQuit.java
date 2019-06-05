package events;

import com.mineaurion.Jump;
import com.mineaurion.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuit implements Listener {
    private Main plugin;
    private Jump jump;

    public OnPlayerQuit() {
        plugin = Main.getInstance();
        jump = Jump.getInstance();
    }

    @EventHandler
    public void handler(PlayerQuitEvent event) {
        //jump.stopJump(event.getPlayer(),false);
    }
}

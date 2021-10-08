package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Config;

public class ToolListener implements Listener {

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onBlockPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      if(event.getItemInHand().equals(Config.ToolBlock) && SessionManager.getSession(player).isUsingTool()) {
         ToolManager.toolSearch(player, event.getBlock());
         if(player.getGameMode() == GameMode.SURVIVAL) {
            player.updateInventory();
         }

         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if(event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().equals(Config.ToolBlock) && SessionManager.getSession(player).isUsingTool()) {
         ToolManager.toolSearch(player, event.getClickedBlock());
         event.setCancelled(true);
      }

   }
}

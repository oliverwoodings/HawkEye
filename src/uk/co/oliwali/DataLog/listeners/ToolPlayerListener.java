package uk.co.oliwali.DataLog.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Config;

/**
 * Player listener class for DataLog Tools
 * @author oliverw92
 */
public class ToolPlayerListener extends PlayerListener {
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().getTypeId() == Config.ToolBlock && DataLog.getSession(player).isUsingTool()) {
			DataManager.toolSearch(player, event.getClickedBlock().getLocation());
			event.setCancelled(true);
		}
	}

}

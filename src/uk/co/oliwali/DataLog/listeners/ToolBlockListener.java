package uk.co.oliwali.DataLog.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Config;

/**
 * Block listener class for DataLog Tools
 * @author oliverw92
 */
public class ToolBlockListener extends BlockListener {
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		if (block.getTypeId() == Config.ToolBlock && DataLog.getSession(player).isUsingTool()) {
			DataManager.toolSearch(player, block.getLocation());
			event.setCancelled(true);
		}
	}

}

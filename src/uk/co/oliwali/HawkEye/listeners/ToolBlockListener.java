package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.Config;

/**
 * Block listener class for HawkEye Tools
 * @author oliverw92
 */
public class ToolBlockListener extends BlockListener {
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		if (block.getTypeId() == Config.ToolBlock && HawkEye.getSession(player).isUsingTool()) {
			DataManager.toolSearch(player, block.getLocation());
			event.setCancelled(true);
		}
	}

}

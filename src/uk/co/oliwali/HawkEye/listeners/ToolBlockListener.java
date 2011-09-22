package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;

/**
 * Block listener class for HawkEye Tools
 * @author oliverw92
 */
public class ToolBlockListener extends BlockListener {
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		if (BlockUtil.getBlockString(block).equals(Config.ToolBlock) && SessionManager.getSession(player).isUsingTool()) {
			ToolManager.toolSearch(player, block.getLocation());
			event.setCancelled(true);
		}
	}

}

package uk.co.oliwali.DataLog.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.BlockUtil;
import uk.co.oliwali.DataLog.util.Config;

public class ControlBlockListener extends BlockListener {
	
	public void onBlockBreak(BlockBreakEvent event) {
		Block block   = event.getBlock();	
		if (DataLog.checkRules(event.getPlayer(), DataType.BLOCK_BREAK, block.getLocation(), BlockUtil.getBlockString(block)))
			event.setCancelled(true);
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		if (block.getTypeId() == Config.ToolBlock && DataLog.getSession(player).isUsingTool()) {
			DataManager.toolSearch(player, loc);
			event.setCancelled(true);
			return;
		}
		else if (DataLog.checkRules(player, DataType.BLOCK_PLACE, loc, BlockUtil.getBlockString(event.getBlockReplacedState()) + "-" + BlockUtil.getBlockString(block)))
			event.setCancelled(true);
	}
	
	public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
    	Location loc  = event.getBlock().getLocation();
        String text = "";
        for (String line : event.getLines())
            text = text + "|" + line;
        if (DataLog.checkRules(player, DataType.SIGN_PLACE, loc, text))
        	event.setCancelled(true);
	}
	
	public void onBlockForm(BlockFormEvent event) {
		if (DataLog.checkRules("Environment", DataType.BLOCK_FORM, event.getBlock().getLocation(), "0"))
			event.setCancelled(true);
	}
	
	public void onBlockBurn(BlockBurnEvent event) {
		if (DataLog.checkRules("Environment", DataType.BLOCK_BURN, event.getBlock().getLocation(), Integer.toString(event.getBlock().getTypeId())))
			event.setCancelled(true);
	}
	
	public void onLeavesDecay(LeavesDecayEvent event) {
		if (DataLog.checkRules("Environment", DataType.LEAF_DECAY, event.getBlock().getLocation(), "18"))
			event.setCancelled(true);
	}

}

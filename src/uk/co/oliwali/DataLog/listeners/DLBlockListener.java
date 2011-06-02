package uk.co.oliwali.DataLog.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;

public class DLBlockListener extends BlockListener {
	
	public DataLog plugin;

	public DLBlockListener(DataLog dataLog) {
		plugin = dataLog;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		plugin.addDataEntry(player, DataType.BLOCK_BREAK, loc, Integer.toString(block.getTypeId()));
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		plugin.addDataEntry(player, DataType.BLOCK_PLACE, loc, Integer.toString(block.getTypeId()));
	}
	
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled())
			return;
        Player player = event.getPlayer();
    	Location loc  = event.getBlock().getLocation();
        String text = "";
        for (String line : event.getLines()) {
            text = text + "|" + line;
        }
        plugin.addDataEntry(player, DataType.SIGN_PLACE, loc, text);
	}

}

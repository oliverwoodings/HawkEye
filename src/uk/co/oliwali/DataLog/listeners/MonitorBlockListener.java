package uk.co.oliwali.DataLog.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SnowFormEvent;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.BlockUtil;

/**
 * Block listener class for DataLog
 * @author oliverw92
 */
public class MonitorBlockListener extends BlockListener {
	
	public DataLog plugin;

	public MonitorBlockListener(DataLog dataLog) {
		plugin = dataLog;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) return;
		Block block   = event.getBlock();
		DataManager.addEntry(event.getPlayer(), DataType.BLOCK_BREAK, block.getLocation(), BlockUtil.getBlockString(block));
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		DataManager.addEntry(player, DataType.BLOCK_PLACE, loc, BlockUtil.getBlockString(event.getBlockReplacedState()) + "-" + BlockUtil.getBlockString(block));
	}
	
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled()) return;
        Player player = event.getPlayer();
    	Location loc  = event.getBlock().getLocation();
        String text = "";
        for (String line : event.getLines())
            text = text + "|" + line;
        DataManager.addEntry(player, DataType.SIGN_PLACE, loc, text);
	}
	
	public void onSnowForm(SnowFormEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry("Environment", DataType.SNOW_FORM, event.getBlock().getLocation(), "0");
	}
	
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry("Environment", DataType.BLOCK_BURN, event.getBlock().getLocation(), Integer.toString(event.getBlock().getTypeId()));
	}
	
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled()) return;
		if (event.getChangedTypeId() == 18)
			DataManager.addEntry("Environment", DataType.LEAF_DECAY, event.getBlock().getLocation(), "18");
	}

}

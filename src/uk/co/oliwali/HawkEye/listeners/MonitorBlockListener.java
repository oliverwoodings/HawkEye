package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.BlockUtil;

/**
 * Block listener class for HawkEye
 * @author oliverw92
 */
public class MonitorBlockListener extends BlockListener {
	
	public HawkEye plugin;

	public MonitorBlockListener(HawkEye HawkEye) {
		plugin = HawkEye;
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
		DataManager.addEntry(player, DataType.BLOCK_PLACE, block.getLocation(), BlockUtil.getBlockString(event.getBlockReplacedState()) + "-" + BlockUtil.getBlockString(block));
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
	
	public void onBlockForm(BlockFormEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry("Environment", DataType.BLOCK_FORM, event.getBlock().getLocation(), BlockUtil.getBlockString(event.getBlock()) + "-" + BlockUtil.getBlockString(event.getNewState()));
	}
	
	public void onBlockFade(BlockFadeEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry("Environment", DataType.BLOCK_FADE, event.getBlock().getLocation(), BlockUtil.getBlockString(event.getBlock()) + "-" + BlockUtil.getBlockString(event.getNewState()));
	}
	
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry("Environment", DataType.BLOCK_BURN, event.getBlock().getLocation(), Integer.toString(event.getBlock().getTypeId()));
	}
	
	public void onLeavesDecay(LeavesDecayEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry("Environment", DataType.LEAF_DECAY, event.getBlock().getLocation(), "18");
	}
	
	public void onBlockFromTo(BlockFromToEvent event) {
		if (event.isCancelled()) return;
		Block from = event.getBlock();
		Block to = event.getToBlock();
		if (from.getTypeId() == 10 || from.getTypeId() == 11)
			DataManager.addEntry("Environment", DataType.LAVA_FLOW, event.getToBlock().getLocation(), Integer.toString(to.getTypeId()));
		else if (from.getTypeId() == 8 || from.getTypeId() == 9)
			DataManager.addEntry("Environment", DataType.WATER_FLOW, event.getToBlock().getLocation(), Integer.toString(to.getTypeId()));
	}

}

package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;

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
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
			DataManager.addEntry(new SignEntry(event.getPlayer(), DataType.SIGN_BREAK, event.getBlock()));
		DataManager.addEntry(new BlockEntry(event.getPlayer(), DataType.BLOCK_BREAK, block));
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) return;
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) return;
		DataManager.addEntry(new BlockChangeEntry(event.getPlayer(), DataType.BLOCK_PLACE, block.getLocation(), event.getBlockReplacedState(), block.getState()));
	}
	
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled()) return;
        DataManager.addEntry(new SignEntry(event.getPlayer(), DataType.SIGN_PLACE, event.getBlock()));
	}
	
	public void onBlockForm(BlockFormEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry(new BlockChangeEntry("Environment", DataType.BLOCK_FORM, event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState()));
	}
	
	public void onBlockFade(BlockFadeEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry(new BlockChangeEntry("Environment", DataType.BLOCK_FADE, event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState()));
	}
	
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry(new BlockEntry("Environment", DataType.BLOCK_BURN, event.getBlock()));
	}
	
	public void onLeavesDecay(LeavesDecayEvent event) {
		if (event.isCancelled()) return;
		DataManager.addEntry(new SimpleRollbackEntry("Environment", DataType.LEAF_DECAY, event.getBlock().getLocation(), ""));
	}
	
	public void onBlockFromTo(BlockFromToEvent event) {
		if (event.isCancelled()) return;
		Block from = event.getBlock();
		Block to = event.getToBlock();
		if (from.getTypeId() == 10 || from.getTypeId() == 11)
			DataManager.addEntry(new BlockEntry("Environment", DataType.LAVA_FLOW, to));
		else if (from.getTypeId() == 8 || from.getTypeId() == 9)
			DataManager.addEntry(new BlockEntry("Environment", DataType.WATER_FLOW, to));
	}

}

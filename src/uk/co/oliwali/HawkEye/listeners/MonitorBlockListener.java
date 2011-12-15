package uk.co.oliwali.HawkEye.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.MaterialData;

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
		
		List<Integer> fluidBlocks = Arrays.asList(0, 27, 28, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 66, 69, 70, 75, 76, 78, 93, 94);
		
		Location loc = event.getToBlock().getLocation();
		BlockState from = event.getBlock().getState();
		BlockState to = event.getToBlock().getState();
		MaterialData data = from.getData();
		
		//Lava
		if (from.getTypeId() == 10 || from.getTypeId() == 11) {

			//Flowing into a normal block
			if (fluidBlocks.contains(to.getTypeId())) {
				data.setData((byte)(from.getRawData() + 1));
				from.setData(data);
			}
			
			//Flowing into water
			else if (to.getTypeId() == 8 || to.getTypeId() == 9) {
				from.setTypeId(event.getFace() == BlockFace.DOWN?10:4);
				data.setData((byte)0);
				from.setData(data);
			}
			DataManager.addEntry(new BlockChangeEntry("Environment", DataType.LAVA_FLOW, loc, to, from));
		
		}
		
		//Water
		else if (from.getTypeId() == 8 || from.getTypeId() == 9) {
			
			//Normal block
			if (fluidBlocks.contains(to.getTypeId())) {
				data.setData((byte)(from.getRawData() + 1));
				from.setData(data);
				DataManager.addEntry(new BlockChangeEntry("Environment", DataType.WATER_FLOW, loc, to, from));
			}
				
			//If we are flowing over lava, cobble or obsidian will form
			BlockState lower = event.getToBlock().getRelative(BlockFace.DOWN).getState();
			if (lower.getTypeId() == 10 || lower.getTypeId() == 11) {
				from.setTypeId(lower.getData().getData() == 0?49:4);
				loc.setY(loc.getY() - 1);
				DataManager.addEntry(new BlockChangeEntry("Environment", DataType.WATER_FLOW, loc, lower, from));
			}
			
		}
		
	}

}

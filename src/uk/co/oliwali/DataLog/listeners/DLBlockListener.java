package uk.co.oliwali.DataLog.listeners;

import java.util.ArrayList;
import java.util.List;

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
import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.SearchQuery.SearchType;
import uk.co.oliwali.DataLog.util.Config;

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
		DataManager.addEntry(player, DataType.BLOCK_BREAK, loc, Integer.toString(block.getTypeId()));
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		if (block.getTypeId() == Config.toolBlock && DataLog.toolEnabled.containsKey(player)) {
			List<Integer> actions = new ArrayList<Integer>();
			for (DataType type : DataType.values())
				if (type.canHere()) actions.add(type.getId());
			DataManager.search(SearchType.SEARCH, player, null, null, null, actions, loc.toVector(), 0, null, null, "desc");
			event.setCancelled(true);
		}
		else DataManager.addEntry(player, DataType.BLOCK_PLACE, loc, event.getBlockReplacedState().getTypeId() + "-" + block.getTypeId());
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
        DataManager.addEntry(player, DataType.SIGN_PLACE, loc, text);
	}
	
	public void onSnowForm(SnowFormEvent event) {
		if (event.isCancelled())
			return;
		DataManager.addEntry("Environment", DataType.SNOW_FORM, event.getBlock().getLocation(), "0");
	}
	
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled())
			return;
		DataManager.addEntry("Environment", DataType.BLOCK_BURN, event.getBlock().getLocation(), Integer.toString(event.getBlock().getTypeId()));
	}
	
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		if (event.getChangedTypeId() == 18)
			DataManager.addEntry("Environment", DataType.LEAF_DECAY, event.getBlock().getLocation(), "18");
	}

}

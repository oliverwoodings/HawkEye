package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;

public class MonitorWorldListener implements Listener {
	
	public HawkEye plugin;

	public MonitorWorldListener(HawkEye HawkEye) {
		plugin = HawkEye;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onStructureGrow(StructureGrowEvent event) {
		if (!Config.isLogged(DataType.TREE_GROW) && !Config.isLogged(DataType.MUSHROOM_GROW)) return;
		if (event.isCancelled()) return;
		
		//Sort out structure type
		DataType type = DataType.TREE_GROW;
		if (event.getSpecies().name().toLowerCase().contains("mushroom")) type = DataType.MUSHROOM_GROW;
		
		//Loop through blocks
		for (BlockState block : event.getBlocks()) {
			
			//Don't log the bottom block
			if (block.getType() == Material.MYCEL || block.getType() == Material.DIRT || block.getType() == Material.GRASS) continue;
			
			Location loc = new Location(event.getWorld(), block.getX(), block.getY(), block.getZ());
			//If a player did it
			if (event.getPlayer() != null) {
				DataManager.addEntry(new BlockChangeEntry(event.getPlayer(), type, loc, "0", BlockUtil.getBlockString(block)));
			}
			//If the environment did it
			else {
				DataManager.addEntry(new BlockChangeEntry("Environment", type, loc, "0", BlockUtil.getBlockString(block)));
			}
		}
		
	}

}

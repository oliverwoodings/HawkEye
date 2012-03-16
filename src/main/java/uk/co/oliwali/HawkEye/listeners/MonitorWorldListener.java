package uk.co.oliwali.HawkEye.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class MonitorWorldListener extends HawkEyeListener {

	public HawkEye plugin;

	public MonitorWorldListener(HawkEye HawkEye) {
		super(HawkEye);
	}

	@HawkEvent(dataType = {DataType.TREE_GROW, DataType.MUSHROOM_GROW})
	public void onStructureGrow(StructureGrowEvent event) {
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

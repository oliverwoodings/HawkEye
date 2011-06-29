package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import uk.co.oliwali.DataLog.database.DataEntry;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.database.DataType;
import uk.co.oliwali.DataLog.util.BlockUtil;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Util;

public class Rollback implements Runnable {
	
	public PlayerSession session = null;
	
	public Rollback(PlayerSession session) {
		this.session = session;
	}
	
	public void run() {
		List<DataEntry> results = session.getRollbackResults();
		if (results == null) {
			Util.sendMessage(session.getSender(), "&cNo results found to rollback");
			return;
		}
		Util.sendMessage(session.getSender(), "&cAttempting to rollback &7" + results.size() + "&c results");
		List<BlockState> undo = new ArrayList<BlockState>();
		for (DataEntry entry : results.toArray(new DataEntry[0])) {
			
			DataType type = DataType.fromId(entry.getAction());
			if (type == null || !type.canRollback())
				continue;
			
			World world = DataLog.server.getWorld(entry.getWorld());
			if (world == null)
				continue;
			
			Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
			Block block = world.getBlockAt(loc);
			undo.add(block.getState());
			switch (type) {
				case BLOCK_BREAK:
				case SNOW_FORM:
				case BLOCK_BURN:
				case LEAF_DECAY:
				case EXPLOSION:
					BlockUtil.setBlockString(block, entry.getData());
					break;
				case BLOCK_PLACE:
					if (entry.getData().indexOf("-") == -1)
						block.setType(Material.AIR);
					else
						BlockUtil.setBlockString(block, entry.getData().substring(0, entry.getData().indexOf("-")));
					break;
				case LAVA_BUCKET:
				case WATER_BUCKET:
					block.setType(Material.AIR);
					break;
			}
			
			if (Config.DeleteDataOnRollback)
				DataManager.deleteEntry(entry.getDataid());
			
		}
		session.setRollbackUndo(undo);
		Util.sendMessage(session.getSender(), "&cRollbacked &7" + undo.size() + "&c actions out of &7" + results.size() + "&c attempted");
		Util.sendMessage(session.getSender(), "&cUndo this rollback using &7/dl undo");
	}

}

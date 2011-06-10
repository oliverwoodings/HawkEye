package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import uk.co.oliwali.DataLog.database.DataEntry;
import uk.co.oliwali.DataLog.database.DataType;
import uk.co.oliwali.DataLog.util.Util;

public class Rollback {
	
	public static void rollback(PlayerSession session) {
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
					block.setTypeId(Integer.parseInt(entry.getData()));
					break;
				case BLOCK_PLACE:
					if (entry.getData().indexOf("-") == -1)
						block.setType(Material.AIR);
					else
						block.setTypeId(Integer.parseInt(entry.getData().substring(0, entry.getData().indexOf("-"))));
					break;
				case LAVA_BUCKET:
				case WATER_BUCKET:
					block.setType(Material.AIR);
					break;
			}
		
		}
		session.setRollbackUndo(undo);
		Util.sendMessage(session.getSender(), "&cRollbacked &7" + undo.size() + "&c actions out of &7" + results.size() + "&c attempted");
		Util.sendMessage(session.getSender(), "&cUndo this rollback using &7/dl undo");
	}
	
	public static void undo(PlayerSession session) {
		List<BlockState> results = session.getRollbackUndo();
		if (results == null || results.size() == 0) {
			Util.sendMessage(session.getSender(), "&cNo rollbacks to undo");
			return;
		}
		Util.sendMessage(session.getSender(), "&cUndoing rollback (&7" + results.size() + " actions&c)");
		for (BlockState block : results.toArray(new BlockState[0]))
			block.update();
		Util.sendMessage(session.getSender(), "&cUndo complete");
		session.setRollbackUndo(null);
		session.setRollbackResults(null);
	}

}

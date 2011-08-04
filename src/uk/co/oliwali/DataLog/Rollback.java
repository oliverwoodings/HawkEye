package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.DataLog.database.DataEntry;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.BlockUtil;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.InventoryUtil;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Runnable class for performing a data rollback.
 * This class should always be run in a separate thread to avoid impacting on server performance
 * @author oliverw92
 */
public class Rollback implements Runnable {
	
	public PlayerSession session = null;
	private Iterator<DataEntry> rollbackQueue;
	private List<BlockState> undo = new ArrayList<BlockState>();
	private int timerID;
	
	/**
	 * @param session {@link PlayerSession} to retrieve rollback results from
	 */
	public Rollback(PlayerSession session) {
		
		this.session = session;
		rollbackQueue = session.getRollbackResults().iterator();
		session.setRollbackUndo(null);
		
		//Check that we actually have results
		if (!rollbackQueue.hasNext()) {
			Util.sendMessage(session.getSender(), "&cNo results found to rollback");
			return;
		}
		
		//Start rollback
		Util.sendMessage(session.getSender(), "&cAttempting to rollback &7" + session.getRollbackResults().size() + "&c results");
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("DataLog"), this, 1, 2);
		
	}
	
	/**
	 * Run the rollback.
	 * Contains appropriate methods of catching errors and notifying the player
	 */
	public void run() {
		
		//Start rollback process
		int i = 0;
		while (i < 200 && rollbackQueue.hasNext()) {
			
			DataEntry entry = rollbackQueue.next();
			
			//If the action can't be rolled back, skip this entry
			DataType type = DataType.fromId(entry.getAction());
			if (type == null || !type.canRollback())
				continue;
			
			//If the world doesn't exist, skip this entry
			World world = DataLog.server.getWorld(entry.getWorld());
			if (world == null)
				continue;
			
			//Get some data from the entry, then switch through some of the actions, performing the rollback
			Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
			Block block = world.getBlockAt(loc);
			undo.add(block.getState());
			switch (type) {
				case BLOCK_BREAK:
				case BLOCK_BURN:
				case LEAF_DECAY:
				case LAVA_FLOW:
				case WATER_FLOW:
				case EXPLOSION:
					BlockUtil.setBlockString(block, entry.getData());
					break;
				case BLOCK_PLACE:
				case BLOCK_FORM:
				case BLOCK_FADE:
					if (entry.getData().indexOf("-") == -1)
						block.setType(Material.AIR);
					else
						BlockUtil.setBlockString(block, entry.getData().substring(0, entry.getData().indexOf("-")));
					break;
				case LAVA_BUCKET:
				case WATER_BUCKET:
					block.setType(Material.AIR);
					break;
				case CONTAINER_TRANSACTION:
					if (!(block instanceof ContainerBlock)) continue;
					Inventory inv = ((ContainerBlock)block).getInventory();
					List<HashMap<String,Integer>> ops = InventoryUtil.interpretDifferenceString(entry.getData());
					//Handle the additions
					for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(0)))
						inv.addItem(stack);
					//Handle subtractions
					for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(1)))
						inv.removeItem(stack);
					break;
			}
			
			//Delete data if told to
			if (Config.DeleteDataOnRollback)
				DataManager.deleteEntry(entry.getDataid());
			
		}
		
		//Check if rollback is finished
		if (!rollbackQueue.hasNext()) {
			
			//End timer
			Bukkit.getServer().getScheduler().cancelTask(timerID);
			
			//Store undo results and notify player
			session.setRollbackUndo(undo);
			session.setRollbackResults(null);
			Util.sendMessage(session.getSender(), "&cRollback complete");
			Util.sendMessage(session.getSender(), "&cUndo this rollback using &7/dl undo");
			
		}
		
	}

}

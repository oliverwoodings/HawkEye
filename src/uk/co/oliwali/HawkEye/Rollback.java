package uk.co.oliwali.HawkEye;

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

import uk.co.oliwali.HawkEye.database.DataEntry;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;
import uk.co.oliwali.HawkEye.util.Util;

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
	private int counter = 0;
	
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
		
		Util.debug("Starting rollback of " + session.getRollbackResults().size() + " results");
		
		//Start rollback
		session.setDoingRollback(true);
		Util.sendMessage(session.getSender(), "&cAttempting to rollback &7" + session.getRollbackResults().size() + "&c results");
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1, 2);
		
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
			if (entry.getType() == null || !entry.getType().canRollback())
				continue;
			
			//If the world doesn't exist, skip this entry
			World world = HawkEye.server.getWorld(entry.getWorld());
			if (world == null)
				continue;
			
			//Get some data from the entry, then switch through some of the actions, performing the rollback
			Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
			Block block = world.getBlockAt(loc);
			undo.add(block.getState());
			switch (entry.getType()) {
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
				case SIGN_PLACE:
				case LAVA_BUCKET:
				case WATER_BUCKET:
					block.setType(Material.AIR);
					break;
				case CONTAINER_TRANSACTION:
					if (block.getType() != Material.CHEST) continue;
					Inventory inv = ((ContainerBlock)block.getState()).getInventory();
					List<HashMap<String,Integer>> ops = InventoryUtil.interpretDifferenceString(entry.getData());
					//Handle the additions
					if (ops.size() > 0) {
						for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(0)))
							inv.removeItem(stack);
					}
					//Handle subtractions
					if (ops.size() > 1) {
						for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(1)))
							inv.addItem(stack);
					}
					break;
			}
			
			//Delete data if told to
			if (Config.DeleteDataOnRollback)
				DataManager.deleteEntry(entry.getDataid());
			
			counter++;
			
		}
		
		//Check if rollback is finished
		if (!rollbackQueue.hasNext()) {
			
			//End timer
			Bukkit.getServer().getScheduler().cancelTask(timerID);
			
			//Store undo results and notify player
			session.setRollbackUndo(undo);
			session.setDoingRollback(false);
			Util.sendMessage(session.getSender(), "&cRollback complete, &7" + counter + "&c edits performed");
			Util.sendMessage(session.getSender(), "&cUndo this rollback using &7/dl undo");
			
			Util.debug("Rollback complete, " + counter + " edits performed");
			
		}
		
	}

}

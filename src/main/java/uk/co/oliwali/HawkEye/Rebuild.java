package uk.co.oliwali.HawkEye;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Runnable class for performing a data rollback.
 * This class should always be run in a separate thread to avoid impacting on server performance
 * @author oliverw92
 */
public class Rebuild implements Runnable {

	private final PlayerSession session;
	private Iterator<DataEntry> rebuildQueue;
	private final List<DataEntry> undo = new ArrayList<DataEntry>();
	private int timerID;
	private int counter = 0;

	/**
	 * @param session {@link PlayerSession} to retrieve rebuild results from
	 */
	public Rebuild(PlayerSession session) {

		this.session = session;
		rebuildQueue = session.getRollbackResults().iterator();

		//Check that we actually have results
		if (!rebuildQueue.hasNext()) {
			Util.sendMessage(session.getSender(), "&cNo results found to rebuild");
			return;
		}

		Util.debug("Starting rebuild of " + session.getRollbackResults().size() + " results");

		//Start rollback
		session.setDoingRollback(true);
		Util.sendMessage(session.getSender(), "&cAttempting to rebuild &7" + session.getRollbackResults().size() + "&c results");
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1, 2);

	}

	/**
	 * Run the rollback.
	 * Contains appropriate methods of catching errors and notifying the player
	 */
	public void run() {

		//Start rollback process
		int i = 0;
		while (i < 200 && rebuildQueue.hasNext()) {
			i++;

			DataEntry entry = rebuildQueue.next();

			//If the action can't be rolled back, skip this entry
			if (entry.getType() == null || !entry.getType().canRollback())
				continue;

			//If the world doesn't exist, skip this entry
			World world = HawkEye.server.getWorld(entry.getWorld());
			if (world == null)
				continue;

			//Get some data from the entry
			Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
			Block block = world.getBlockAt(loc);

			//Rebuild it
			entry.rebuild(block);

			counter++;

		}

		//Check if rollback is finished
		if (!rebuildQueue.hasNext()) {

			//End timer
			Bukkit.getServer().getScheduler().cancelTask(timerID);

			session.setDoingRollback(false);
			session.setRollbackResults(undo);

			Util.sendMessage(session.getSender(), "&cRebuild complete, &7" + counter + "&c edits performed");
			Util.sendMessage(session.getSender(), "&cUndo this rebuild using &7/hawk undo");

			Util.debug("Rebuild complete, " + counter + " edits performed");

		}

	}

}

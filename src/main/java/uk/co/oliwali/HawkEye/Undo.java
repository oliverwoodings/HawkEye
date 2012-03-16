package uk.co.oliwali.HawkEye;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Runnable class for reversing a {@link Rollback}.
 * This class should always be run in a separate thread to avoid impacting on server performance
 * @author oliverw92
 */
public class Undo implements Runnable {

	private final PlayerSession session;
	private Iterator<DataEntry> undoQueue;
	private int timerID;
	private int counter = 0;
	private RollbackType undoType = RollbackType.GLOBAL;

	/**
	 * @param session {@link PlayerSession} to retrieve undo results from
	 */
	public Undo(RollbackType undoType, PlayerSession session) {

		this.undoType = undoType;
		this.session = session;
		undoQueue = session.getRollbackResults().iterator();

		//Check if already rolling back
		if (session.doingRollback()) {
			Util.sendMessage(session.getSender(), "&cYour previous rollback is still processing, please wait before performing an undo!");
			return;
		}

		//Check that we actually have results
		if (!undoQueue.hasNext()) {
			Util.sendMessage(session.getSender(), "&cNo results found to undo");
			return;
		}

		Util.debug("Starting undo of " + session.getRollbackResults().size() + " results");

		//Start undo
		session.setDoingRollback(true);
		Util.sendMessage(session.getSender(), "&cAttempting to undo &7" + session.getRollbackResults().size() + "&c rollback edits");
		timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1, 2);

	}

	/**
	 * Run the undo.
	 * Contains appropriate methods of catching errors and notifying the player
	 */
	public void run() {

		//Start rollback process
		int i = 0;
		while (i < 200 && undoQueue.hasNext()) {

			//If undo doesn't exist
			DataEntry entry = undoQueue.next();
			if (entry.getUndoState() == null) continue;

			//Global undo
			if (undoType == RollbackType.GLOBAL) {
				entry.getUndoState().update(true);
				//Add back into database if delete data is on
				if (Config.DeleteDataOnRollback)
					DataManager.addEntry(entry);
			}

			//Player undo
			else {
				Player player = (Player)session.getSender();
				Block block = entry.getUndoState().getBlock();
				player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
			}

			counter++;

		}

		//Check if undo is finished
		if (!undoQueue.hasNext()) {

			//End timer
			Bukkit.getServer().getScheduler().cancelTask(timerID);

			session.setDoingRollback(false);
			session.setRollbackResults(null);

			Util.sendMessage(session.getSender(), "&cUndo complete, &7" + counter + " &cedits performed");
			Util.debug("Undo complete, " + counter + " edits performed");

		}


	}

}
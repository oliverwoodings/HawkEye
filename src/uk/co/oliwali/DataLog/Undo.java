package uk.co.oliwali.DataLog;

import java.util.List;

import org.bukkit.block.BlockState;

import uk.co.oliwali.DataLog.database.DataEntry;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Runnable class for reversing a {@link Rollback}.
 * This class should always be run in a separate thread to avoid impacting on server performance
 * @author oliverw92
 */
public class Undo implements Runnable {
	
	public PlayerSession session = null;
	
	/**
	 * @param session {@link PlayerSession} to retrieve undo results from
	 */
	public Undo(PlayerSession session) {
		this.session = session;
	}
	
	/**
	 * Run the undo.
	 * Contains appropriate methods of catching errors and notifying the player
	 */
	public void run() {
		if (session.getRollbackResults() != null) {
			Util.sendMessage(session.getSender(), "&cYour previous rollback is still processing, please wait before performing an undo!");
			return;
		}
		List<BlockState> results = session.getRollbackUndo();
		if (results == null || results.size() == 0) {
			Util.sendMessage(session.getSender(), "&cNo rollbacks to undo");
			return;
		}
		Util.sendMessage(session.getSender(), "&cUndoing rollback (&7" + results.size() + " action(s)&c)");
		for (BlockState block : results.toArray(new BlockState[0]))
			block.update(true);
		for (DataEntry entry : session.getRollbackResults()) {
			DataManager.addEntry(entry);
		}
		Util.sendMessage(session.getSender(), "&cUndo complete");
		session.setRollbackUndo(null);
		session.setRollbackResults(null);
	}
	
}
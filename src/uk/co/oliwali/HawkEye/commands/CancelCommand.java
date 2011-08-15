package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;

import org.bukkit.block.Block;

import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Cancels a rollback preview.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class CancelCommand extends BaseCommand {

	public CancelCommand() {
		bePlayer = true;
		name = "cancel";
		argLength = 0;
		usage = "<parameters> <- cancel rollback preview";
	}
	
	public boolean execute() {
		
		//Check if player already has a rollback processing
		if (!session.doingRollback() || session.getLocalRollbackUndo().size() == 0) {
			Util.sendMessage(sender, "&cNo preview to cancel!");
			return true;
		}
		
		//Undo local changes to the player
		session.setDoingRollback(false);
		for (Block block : session.getLocalRollbackUndo()) player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
		session.setLocalRollbackUndo(new ArrayList<Block>());
		Util.sendMessage(sender, "&cPreview rollback cancelled");
		return true;
		
	}
	
	public boolean permission() {
		return Permission.preview(sender);
	}

}
package uk.co.oliwali.HawkEye.commands;

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
		
		//Undo local changes to the player
		session.setDoingRollback(false);
		for (Block block : session.getLocalRollbackUndo()) player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
		Util.sendMessage(sender, "&cPreview rollback cancelled");
		return true;
		
	}
	
	public boolean permission() {
		return Permission.preview(sender);
	}

}
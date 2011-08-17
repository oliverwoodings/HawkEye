package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;

import org.bukkit.block.Block;

import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Applies a local rollback to the world
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class ApplyCommand extends BaseCommand {

	public ApplyCommand() {
		name = "apply";
		argLength = 0;
		usage = "<- apply rollback preview";
	}
	
	public boolean execute() {
		
		//Check if player already has a rollback processing
		if (!session.doingRollback() || session.getLocalRollbackUndo().size() == 0) {
			Util.sendMessage(sender, "&cNo preview to cancel!");
			return true;
		}
		
		//Undo local changes to the player
		Util.sendMessage(sender, "&cAttempting to apply rollback to world...");
		new Rollback(RollbackType.GLOBAL, session);
		session.setLocalRollbackUndo(new ArrayList<Block>());
		return true;
		
	}
	
	public void moreHelp() {
		Util.sendMessage(sender, "&cApplies the results of a &7/hawk preview&c globally");
		Util.sendMessage(sender, "&cUntil this command is called, the preview is only visible to you");
	}
	
	public boolean permission() {
		return Permission.preview(sender);
	}

}
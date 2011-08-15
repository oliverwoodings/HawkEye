package uk.co.oliwali.HawkEye.commands;

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
		bePlayer = true;
		name = "apply";
		argLength = 0;
		usage = "<parameters> <- apply rollback preview";
	}
	
	public boolean execute() {
		
		//Undo local changes to the player]
		Util.sendMessage(sender, "&cAttempting to apply rollback to world...");
		new Rollback(RollbackType.GLOBAL, session);
		return true;
		
	}
	
	public boolean permission() {
		return Permission.preview(sender);
	}

}
package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.Undo;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Reverses the previous {@link RollbackCommand}
 * @author oliverw92
 */
public class UndoCommand extends BaseCommand {

	public UndoCommand() {
		name = "undo";
		usage = "<- reverses your previous rollback";
	}
	
	public boolean execute() {
		Thread thread = new Thread(new Undo(session));
		thread.start();
		return true;
	}
	
	public void moreHelp() {
		Util.sendMessage(sender, "&cReverses your previous rollback if you made a mistake with it");
	}

	public boolean permission() {
		return Permission.rollback(sender);
	}
	
}
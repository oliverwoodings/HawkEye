package uk.co.oliwali.DataLog.commands;

import uk.co.oliwali.DataLog.Rollback;
import uk.co.oliwali.DataLog.util.Permission;

public class UndoCommand extends BaseCommand {

	public UndoCommand() {
		name = "undo";
		bePlayer = true;
		usage = "<- reverses your previous rollback";
	}
	
	public boolean execute() {
		Rollback.undo(session);
		return true;
	}

	public boolean permission() {
		return Permission.rollback(sender);
	}
	
}
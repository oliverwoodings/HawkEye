package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.Undo;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Cancels a rollback preview.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class PreviewCancelCommand extends BaseCommand {

	public PreviewCancelCommand() {
		name = "preview cancel";
		argLength = 0;
		usage = "<- cancel rollback preview";
	}

	@Override
	public boolean execute() {

		//Check if player already has a rollback processing
		if (!session.isInPreview()) {
			Util.sendMessage(sender, "&cNo preview to cancel!");
			return true;
		}

		//Undo local changes to the player
		new Undo(RollbackType.LOCAL, session);

		Util.sendMessage(sender, "&cPreview rollback cancelled");
		session.setInPreview(false);
		return true;

	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cCancels results of a &7/hawk preview");
		Util.sendMessage(sender, "&cOnly affects you - no changes are seen by anyony else");
	}

	@Override
	public boolean permission() {
		return Permission.preview(sender);
	}

}

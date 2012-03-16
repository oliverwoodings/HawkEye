package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.RollbackCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Previews a rollback according to the player's specified input.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class PreviewCommand extends BaseCommand {

	public PreviewCommand() {
		name = "preview";
		argLength = 1;
		usage = "<parameters> <- preview rollback changes";
	}

	@Override
	public boolean execute() {

		//Check if player already has a rollback processing
		if (session.doingRollback()) {
			Util.sendMessage(sender, "&cYou already have a rollback command processing!");
			return true;
		}

		//Parse arguments
		SearchParser parser = null;
		try {

			parser = new SearchParser(player, args);
			parser.loc = null;

			//Check that supplied actions can rollback
			if (parser.actions.size() > 0) {
				for (DataType type : parser.actions)
					if (!type.canRollback()) throw new IllegalArgumentException("You cannot rollback that action type: &7" + type.getConfigName());
			}
			//If none supplied, add in all rollback types
			else {
				for (DataType type : DataType.values())
					if (type.canRollback()) parser.actions.add(type);
			}

		} catch (IllegalArgumentException e) {
			Util.sendMessage(sender, "&c" + e.getMessage());
			return true;
		}

		//Create new SearchQuery with data
		new SearchQuery(new RollbackCallback(session, RollbackType.LOCAL), parser, SearchDir.DESC);
		session.setInPreview(true);
		return true;

	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cPreviews a rollback to only you");
		Util.sendMessage(sender, "&cThis type of rollback does not affect the actual world in any way");
		Util.sendMessage(sender, "&cThe effects can be applied after using &7/hawk preview apply&c or cancelled using &7/hawk preview cancel");
		Util.sendMessage(sender, "&cThe parameters are the same as &7/hawk rollback");
	}

	@Override
	public boolean permission() {
		return Permission.preview(sender);
	}

}

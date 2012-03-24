package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteCommand extends BaseCommand {

	public DeleteCommand() {
		bePlayer = false;
		name = "delete";
		argLength = 1;
		usage = "<parameters> <- delete database entries";
	}

	@Override
	public boolean execute() {

		//Parse arguments
		SearchParser parser = null;
		try {
			parser = new SearchParser(sender, args);
		} catch (IllegalArgumentException e) {
			Util.sendMessage(sender, "&c" + e.getMessage());
			return true;
		}

		//Create new SeachQuery with data
		new SearchQuery(new DeleteCallback(session), parser, SearchDir.DESC);
		return true;

	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cDeletes specified entries from the database permanently");
		Util.sendMessage(sender, "&cUses the same parameters and format as /hawk search");
	}

	@Override
	public boolean permission() {
		return Permission.delete(sender);
	}

}

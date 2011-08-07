package uk.co.oliwali.DataLog.commands;

import uk.co.oliwali.DataLog.SearchParser;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchDir;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchType;
import uk.co.oliwali.DataLog.database.SearchQuery;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Searches for data according to the player's specified input.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class SearchCommand extends BaseCommand {

	public SearchCommand() {
		bePlayer = true;
		name = "search";
		argLength = 1;
		usage = "<parameters> <- search the DataLog database. Type &c/dl searchhelp&7 for more info";
	}
	
	public boolean execute() {
		
		//Parse arguments
		SearchParser parser = null;
		try {
			parser = new SearchParser(player, args);
		} catch (IllegalArgumentException e) {
			Util.sendMessage(sender, "&c" + e.getMessage());
			return true;
		}
		
		//Create new SeachQuery with data
		Thread thread = new SearchQuery(SearchType.SEARCH, parser, SearchDir.ASC);
		thread.start();
		return true;
		
	}
	
	public boolean permission() {
		return Permission.search(sender);
	}

}
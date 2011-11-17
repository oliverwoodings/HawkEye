package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Displays a page from the player's previous search results
 * @author oliverw92
 */
public class PageCommand extends BaseCommand {

	public PageCommand() {
		bePlayer = false;
		name = "page";
		argLength = 1;
		usage = "<page> <- display a page from your last search";
	}
	
	public boolean execute() {
		DisplayManager.displayPage(session, Integer.parseInt(args.get(0)));
		return true;
	}
	
	public void moreHelp() {
		Util.sendMessage(sender, "&cShows the specified page of results from your latest search");
	}
	
	public boolean permission() {
		return Permission.search(sender);
	}
	
}
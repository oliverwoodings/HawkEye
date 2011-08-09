package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.util.Permission;

/**
 * Displays a page from the player's previous search results
 * @author oliverw92
 */
public class PageCommand extends BaseCommand {

	public PageCommand() {
		name = "page";
		argLength = 1;
		usage = "<page> <- display a page from your last search";
	}
	
	public boolean execute() {
		DisplayManager.displayPage(session, Integer.parseInt(args.get(0)));
		return true;
	}
	
	public boolean permission() {
		return Permission.search(sender);
	}
	
}
package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;

import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Implementation of BaseCallback for use in search commands
 * @author oliverw92
 */
public class SearchCallback extends BaseCallback {
	
	private PlayerSession session;
	private CommandSender sender;
	
	public SearchCallback(PlayerSession session) {
		this.session = session;
		sender = session.getSender();
		Util.sendMessage(sender, "&cSearching for matching results...");
	}

	public void execute() {
		session.setSearchResults(results);
		DisplayManager.displayPage(session, 1);
	}
	
	public void error(SearchError error, String message) {
		Util.sendMessage(sender, message);
	}

}

package uk.co.oliwali.HawkEye.callbacks;

import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.util.Util;

public class SearchCallback extends BaseCallback {
	
	private PlayerSession session;
	
	public SearchCallback() { }
	
	public SearchCallback(PlayerSession session) {
		this.session = session;
	}

	public void execute() {
		session.setSearchResults(results);
		DisplayManager.displayPage(session, 1);
	}
	
	public void error(SearchError error, String message) {
		Util.sendMessage(session.getSender(), message);
	}

}

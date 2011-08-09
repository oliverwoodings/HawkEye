package uk.co.oliwali.HawkEye.callbacks;

import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.util.Util;

public class RollbackCallback extends BaseCallback {
	
	private PlayerSession session;
	
	public RollbackCallback() { }
	
	public RollbackCallback(PlayerSession session) {
		this.session = session;
	}

	public void execute() {
		session.setRollbackResults(results);
		new Rollback(session);
	}

	public void error(SearchError error, String message) {
		Util.sendMessage(session.getSender(), message);
	}

}

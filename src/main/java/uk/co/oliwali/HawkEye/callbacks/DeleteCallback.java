package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;

import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchError;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteCallback extends BaseCallback {

	private final CommandSender sender;
	public int deleted;

	public DeleteCallback(PlayerSession session) {
		sender = session.getSender();
		Util.sendMessage(sender, "&cDeleting matching results...");
	}

	@Override
	public void execute() {
		Util.sendMessage(sender, "&c" + deleted + " entries removed from database.");
	}

	@Override
	public void error(SearchError error, String message) {
		Util.sendMessage(sender, message);
	}

}

package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rebuild;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class RebuildCallback extends BaseCallback {

   private final PlayerSession session;
   private final CommandSender sender;


   public RebuildCallback(PlayerSession session) {
      this.session = session;
      this.sender = session.getSender();
      Util.sendMessage(this.sender, "&cSearching for matching results to rebuild...");
   }

   public void execute() {
      this.session.setRollbackResults(this.results);
      new Rebuild(this.session);
   }

   public void error(SearchQuery.SearchError error, String message) {
      Util.sendMessage(this.session.getSender(), message);
   }
}

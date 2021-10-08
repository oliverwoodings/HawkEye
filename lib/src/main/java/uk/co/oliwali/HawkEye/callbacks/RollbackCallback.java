package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class RollbackCallback extends BaseCallback {

   private final PlayerSession session;
   private final CommandSender sender;
   private final Rollback.RollbackType type;


   public RollbackCallback(PlayerSession session, Rollback.RollbackType type) {
      this.type = type;
      this.session = session;
      this.sender = session.getSender();
      Util.sendMessage(this.sender, "&cSearching for matching results to rollback...");
   }

   public void execute() {
      this.session.setRollbackResults(this.results);
      new Rollback(this.type, this.session);
   }

   public void error(SearchQuery.SearchError error, String message) {
      Util.sendMessage(this.session.getSender(), message);
   }
}

package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteCallback extends BaseCallback {

   private final CommandSender sender;
   public int deleted;


   public DeleteCallback(PlayerSession session) {
      this.sender = session.getSender();
      Util.sendMessage(this.sender, "&cDeleting matching results...");
   }

   public void execute() {
      Util.sendMessage(this.sender, "&c" + this.deleted + " entries removed from database.");
   }

   public void error(SearchQuery.SearchError error, String message) {
      Util.sendMessage(this.sender, message);
   }
}

package uk.co.oliwali.HawkEye.callbacks;

import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class SearchCallback extends BaseCallback {

   private final PlayerSession session;
   private final CommandSender sender;


   public SearchCallback(PlayerSession session) {
      this.session = session;
      this.sender = session.getSender();
      Util.sendMessage(this.sender, "&cSearching for matching results...");
   }

   public void execute() {
      this.session.setSearchResults(this.results);
      
      DisplayManager.displayPage(this.session, 1);
   }

   public void error(SearchQuery.SearchError error, String message) {
      Util.sendMessage(this.sender, message);
   }
}

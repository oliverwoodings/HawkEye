package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteCommand extends BaseCommand {

   public DeleteCommand() {
      this.bePlayer = false;
      this.name = "delete";
      this.permission = "delete";
      this.argLength = 1;
      this.usage = "<parameters> <- delete database entries";
   }

   public boolean execute() {
      SearchParser parser = null;

      try {
         parser = new SearchParser(this.sender, this.args);
      } catch (IllegalArgumentException var3) {
         Util.sendMessage(this.sender, "&c" + var3.getMessage());
         return true;
      }

      new SearchQuery(new DeleteCallback(this.session), parser, SearchQuery.SearchDir.DESC);
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cDeletes specified entries from the database permanently");
      Util.sendMessage(this.sender, "&cUses the same parameters and format as /hawk search");
   }
}

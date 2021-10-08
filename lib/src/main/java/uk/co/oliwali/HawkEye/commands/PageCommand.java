package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.DisplayManager;
import uk.co.oliwali.HawkEye.util.Util;

public class PageCommand extends BaseCommand {

   public PageCommand() {
      this.bePlayer = false;
      this.name = "page";
      this.permission = "page";
      this.argLength = 1;
      this.usage = "<page> <- display a page from your last search";
   }

   public boolean execute() {
      if(!Util.isInteger((String)this.args.get(0))) {
         Util.sendMessage(this.sender, "&cInvalid argument format: &7" + (String)this.args.get(0));
         return true;
      } else {
         DisplayManager.displayPage(this.session, Integer.parseInt((String)this.args.get(0)));
         return true;
      }
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cShows the specified page of results from your latest search");
   }
}

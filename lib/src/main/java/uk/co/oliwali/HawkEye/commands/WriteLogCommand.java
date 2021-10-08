package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.WriteLogCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class WriteLogCommand extends BaseCommand {

   public WriteLogCommand() {
      this.bePlayer = false;
      this.name = "writelog";
      this.argLength = 1;
      this.permission = "writelog";
      this.usage = "<parameters> <- Search for entries to log";
   }

   public boolean execute() {
      SearchParser parser = null;

      try {
         parser = new SearchParser(this.sender, this.args);
      } catch (IllegalArgumentException var3) {
         Util.sendMessage(this.sender, "&c" + var3.getMessage());
         return true;
      }

      new SearchQuery(new WriteLogCallback(this.session), parser, SearchQuery.SearchDir.DESC);
      return true;
   }

   public void moreHelp() {
      ArrayList acs = new ArrayList();
      DataType[] arr$ = DataType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         DataType type = arr$[i$];
         acs.add(type.getConfigName());
      }

      Util.sendMessage(this.sender, "&7There are 7 parameters you can use - &ca: p: w: r: f: t:");
      Util.sendMessage(this.sender, "&6Action &ca:&7 - list of actions separated by commas. Select from the following: &8" + Util.join(acs, " "));
      Util.sendMessage(this.sender, "&6Player &cp:&7 - list of players. &6World &cw:&7 - list of worlds");
      Util.sendMessage(this.sender, "&6Filter &cf:&7 - list of keywords. &6Location &cl:&7 - x,y,z location");
      Util.sendMessage(this.sender, "&6Radius &cr:&7 - radius to search around given location");
      Util.sendMessage(this.sender, "&6Time &ct:&7 - time bracket in the following format:");
      Util.sendMessage(this.sender, "&7  -&c Date format: yyyy-MM-dd");
      Util.sendMessage(this.sender, "&7  -&c t:10h45m10s &7-back specified amount of time");
      Util.sendMessage(this.sender, "&7  -&c t:2011-06-02,10:45:10 &7-from given date");
      Util.sendMessage(this.sender, "&7  -&c t:2011-06-02,10:45:10,2011-07-04,18:15:00 &7-between dates");
   }
}

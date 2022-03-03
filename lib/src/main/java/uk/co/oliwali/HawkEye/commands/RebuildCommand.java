package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;
import java.util.Iterator;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.RebuildCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class RebuildCommand extends BaseCommand {

   public RebuildCommand() {
      this.name = "rebuild";
      this.argLength = 1;
      this.permission = "rebuild";
      this.usage = "<parameters> <- re-applies changes";
   }

   public boolean execute() {
      if(this.session.doingRollback()) {
         Util.sendMessage(this.sender, "&cYou already have a query command processing!");
         return true;
      } else {
         SearchParser parser = null;

         try {
            parser = new SearchParser(this.player, this.args, true);
            parser.loc = null;
            if(parser.actions.size() > 0) {
               Iterator e = parser.actions.iterator();

               while(e.hasNext()) {
                  DataType len$ = (DataType)e.next();
                  if(!len$.canRollback()) {
                     throw new IllegalArgumentException("You cannot rebuild that action type: &7" + len$.getConfigName());
                  }
               }
            } else {
               DataType[] var7 = DataType.values();
               int var8 = var7.length;

               for(int i$ = 0; i$ < var8; ++i$) {
                  DataType type = var7[i$];
                  if(type.canRollback()) {
                     parser.actions.add(type);
                  }
               }
            }
         } catch (IllegalArgumentException var6) {
            Util.sendMessage(this.sender, "&c" + var6.getMessage());
            return true;
         }

         new SearchQuery(new RebuildCallback(this.session), parser, SearchQuery.SearchDir.ASC);
         return true;
      }
   }

   public void moreHelp() {
      ArrayList acs = new ArrayList();
      DataType[] arr$ = DataType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         DataType type = arr$[i$];
         if(type.canRollback()) {
            acs.add(type.getConfigName());
         }
      }

      Util.sendMessage(this.sender, "&7There are 6 parameters you can use - &ca: p: w: r: f: t:");
      Util.sendMessage(this.sender, "&6Action &ca:&7 - list of actions separated by commas. Select from the following: &8" + Util.join(acs, " "));
      Util.sendMessage(this.sender, "&6Player &cp:&7 - list of players. &6World &cw:&7 - list of worlds");
      Util.sendMessage(this.sender, "&6Filter &cf:&7 - list of keywords (e.g. block id)");
      Util.sendMessage(this.sender, "&6Radius &cr:&7 - radius to search around given location");
      Util.sendMessage(this.sender, "&6Time &ct:&7 - time bracket in the following format:");
      Util.sendMessage(this.sender, "&7  -&c t:10h45m10s &7-back specified amount of time");
      Util.sendMessage(this.sender, "&7  -&c t:2011-06-02,10:45:10 &7-from given date");
      Util.sendMessage(this.sender, "&7  -&c t:2011-06-02,10:45:10,2011-07-04,18:15:00 &7-between dates");
   }
}

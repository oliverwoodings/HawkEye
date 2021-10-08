package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.SearchCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class HereCommand extends BaseCommand {

   public HereCommand() {
      this.name = "here";
      this.argLength = 0;
      this.permission = "search";
      this.usage = "[radius] [player] <- search around you";
   }

   public boolean execute() {
      SearchParser parser = null;

      try {
         if(this.args.size() != 0 && !Util.isInteger((String)this.args.get(0))) {
            throw new IllegalArgumentException("Invalid integer supplied for radius!");
         }

         int e;
         if(this.args.size() > 0) {
            e = Integer.parseInt((String)this.args.get(0));
         } else {
            e = Config.DefaultHereRadius;
         }

         if(e > Config.MaxRadius && Config.MaxRadius > 0 || e < 0) {
            throw new IllegalArgumentException("Invalid radius supplied supplied!");
         }

         parser = new SearchParser(this.player, e);
         DataType[] arr$ = DataType.values();
         int len$ = arr$.length;

         int i$;
         DataType type;
         for(i$ = 0; i$ < len$; ++i$) {
            type = arr$[i$];
            if(type.canHere()) {
               parser.actions.add(type);
            }
         }

         if(this.args.size() > 1) {
            String[] var8 = ((String)this.args.get(1)).split(",");
            len$ = var8.length;

            for(i$ = 0; i$ < len$; ++i$) {
               String var9 = var8[i$];
               parser.players.add(var9);
            }
         }

         arr$ = DataType.values();
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            type = arr$[i$];
            if(type.canHere()) {
               parser.actions.add(type);
            }
         }
      } catch (IllegalArgumentException var7) {
         Util.sendMessage(this.sender, "&c" + var7.getMessage());
         return true;
      }

      new SearchQuery(new SearchCallback(this.session), parser, SearchQuery.SearchDir.DESC);
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cShows all changes in a radius around you");
      Util.sendMessage(this.sender, "&cRadius should be an integer");
   }
}

package uk.co.oliwali.HawkEye.commands;

import java.util.Iterator;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.Rollback;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.RollbackCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Util;

public class PreviewCommand extends BaseCommand {

   public PreviewCommand() {
      this.name = "preview";
      this.argLength = 1;
      this.permission = "preview";
      this.usage = "<parameters> <- preview rollback changes";
   }

   public boolean execute() {
      if(this.session.doingRollback()) {
         Util.sendMessage(this.sender, "&cYou already have a rollback command processing!");
         return true;
      } else {
         SearchParser parser = null;

         try {
            parser = new SearchParser(this.player, this.args, false);
            parser.loc = null;
            if(parser.actions.size() > 0) {
               Iterator e = parser.actions.iterator();

               while(e.hasNext()) {
                  DataType len$ = (DataType)e.next();
                  if(!len$.canRollback()) {
                     throw new IllegalArgumentException("You cannot rollback that action type: &7" + len$.getConfigName());
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

         new SearchQuery(new RollbackCallback(this.session, Rollback.RollbackType.LOCAL), parser, SearchQuery.SearchDir.DESC);
         this.session.setInPreview(true);
         return true;
      }
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cPreviews a rollback to only you");
      Util.sendMessage(this.sender, "&cThis type of rollback does not affect the actual world in any way");
      Util.sendMessage(this.sender, "&cThe effects can be applied after using &7/hawk preview apply&c or cancelled using &7/hawk preview cancel");
      Util.sendMessage(this.sender, "&cThe parameters are the same as &7/hawk rollback");
   }
}

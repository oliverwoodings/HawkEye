package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.Util;

public class InfoCommand extends BaseCommand {

   public InfoCommand() {
      this.name = "info";
      this.argLength = 0;
      this.permission = "info";
      this.usage = " <- displays hawkeye\'s details";
   }

   public boolean execute() {
      ArrayList acs = new ArrayList();
      DataType[] arr$ = DataType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         DataType type = arr$[i$];
         if(type.isLogged()) {
            acs.add(type.getConfigName());
         }
      }

      Util.sendMessage(this.sender, "&c---------------------&8[ &7HawkEye &8]&c---------------------");
      Util.sendMessage(this.sender, "&8  - &cQueue-load: &7" + DataManager.getQueue().size());
      Util.sendMessage(this.sender, "&8  - &cVersion: &7" + this.plugin.getDescription().getVersion());
      Util.sendMessage(this.sender, "&8  - &cLogged: &7" + Util.join(acs, " "));
      Util.sendMessage(this.session.getSender(), "&c----------------------------------------------------");
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cDisplays HawkEye\'s details");
   }
}

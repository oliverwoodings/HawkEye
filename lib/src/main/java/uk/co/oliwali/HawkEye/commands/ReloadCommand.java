package uk.co.oliwali.HawkEye.commands;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class ReloadCommand extends BaseCommand {

   public ReloadCommand() {
      this.name = "reload";
      this.argLength = 0;
      this.permission = "reload";
      this.usage = " <- reload hawkeye";
   }

   public boolean execute() {
      Util.sendMessage(this.sender, "&c-----------&8[ &7Reload Process Started &8]&c-----------");
      HawkEye hawk = HawkEye.instance;
      hawk.reloadConfig();
      hawk.config = new Config(hawk);
      DataType[] arr$ = DataType.values();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         DataType dt = arr$[i$];
         dt.reload();
      }

      Util.sendMessage(this.sender, "&8|  &7- &cConfig has been reloaded..");
      HandlerList.unregisterAll(hawk);
      hawk.registerListeners(Bukkit.getPluginManager());
      Util.sendMessage(this.sender, "&8|  &7- &cListeners have been reloaded..");
      Util.sendMessage(this.sender, "&c-----------&8[ &7Reload Process Finished &8]&c-----------");
      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cReloads Hawkeye\'s configuration");
   }
}

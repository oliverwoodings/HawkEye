package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.util.Util;

public class HelpCommand extends BaseCommand {

   public HelpCommand() {
      this.bePlayer = false;
      this.name = "help";
      this.argLength = 0;
      this.permission = "help";
      this.usage = "<- lists all HawkEye commands";
   }

   public boolean execute() {
      BaseCommand[] arr$;
      int len$;
      int i$;
      BaseCommand cmd;
      if(this.args.size() == 0) {
         Util.sendMessage(this.sender, "&c---------------------- &7HawkEye &c----------------------");
         Util.sendMessage(this.sender, "&7Type &8/hawk help <command>&7 for more info on that command");
         arr$ = (BaseCommand[])HawkEye.commands.toArray(new BaseCommand[0]);
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            cmd = arr$[i$];
            if(Util.hasPerm(this.sender, cmd.permission)) {
               Util.sendMessage(this.sender, "&8-&7 /" + this.usedCommand + " &c" + cmd.name + " &7" + cmd.usage);
            }
         }
      } else {
         arr$ = (BaseCommand[])HawkEye.commands.toArray(new BaseCommand[0]);
         len$ = arr$.length;

         for(i$ = 0; i$ < len$; ++i$) {
            cmd = arr$[i$];
            if(Util.hasPerm(this.sender, cmd.permission) && cmd.name.equalsIgnoreCase((String)this.args.get(0))) {
               Util.sendMessage(this.sender, "&c---------------------- &7HawkEye - " + cmd.name);
               Util.sendMessage(this.sender, "&8-&7 /" + this.usedCommand + " &c" + cmd.name + " &7" + cmd.usage);
               cmd.sender = this.sender;
               cmd.moreHelp();
               return true;
            }
         }

         Util.sendMessage(this.sender, "&cNo command found by that name");
      }

      return true;
   }

   public void moreHelp() {
      Util.sendMessage(this.sender, "&cShows all HawkEye commands");
      Util.sendMessage(this.sender, "&cType &7/hawk help <command>&c for help on that command");
   }
}

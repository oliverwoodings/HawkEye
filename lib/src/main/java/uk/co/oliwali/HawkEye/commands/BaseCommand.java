package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.SessionManager;
import uk.co.oliwali.HawkEye.util.Util;

public abstract class BaseCommand {

   public CommandSender sender;
   public List args = new ArrayList();
   public String name;
   public int argLength = 0;
   public String usage;
   public boolean bePlayer = true;
   public Player player;
   public String permission;
   public String usedCommand;
   public PlayerSession session;
   public HawkEye plugin;


   public boolean run(HawkEye instace, CommandSender csender, String[] preArgs, String cmd) {
      this.plugin = instace;
      this.sender = csender;
      this.session = SessionManager.getSession(this.sender);
      this.usedCommand = cmd;
      this.args.clear();
      String[] i = preArgs;
      int len$ = preArgs.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String arg = i[i$];
         this.args.add(arg);
      }

      for(int var9 = 0; var9 < this.name.split(" ").length && var9 < this.args.size(); ++var9) {
         this.args.remove(0);
      }

      if(this.argLength > this.args.size()) {
         this.sendUsage();
         return true;
      } else if(this.bePlayer && !(this.sender instanceof Player)) {
         return false;
      } else {
         if(this.sender instanceof Player) {
            this.player = (Player)this.sender;
         }

         if(!Util.hasPerm(this.sender, this.permission)) {
            Util.sendMessage(this.sender, "&cYou do not have permission to do that!");
            return true;
         } else {
            return this.execute();
         }
      }
   }

   public abstract boolean execute();

   public abstract void moreHelp();

   public void sendUsage() {
      Util.sendMessage(this.sender, "&c/" + this.usedCommand + " " + this.name + " " + this.usage);
   }
}

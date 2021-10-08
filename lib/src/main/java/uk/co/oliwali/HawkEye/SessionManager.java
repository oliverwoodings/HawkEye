package uk.co.oliwali.HawkEye;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SessionManager {

   private static final HashMap playerSessions = new HashMap();


   public SessionManager() {
      addSession(Bukkit.getServer().getConsoleSender());
      Player[] arr$ = Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Player player = arr$[i$];
         addSession(player);
      }

   }

   public static PlayerSession getSession(CommandSender player) {
      PlayerSession session = (PlayerSession)playerSessions.get(player.getName());
      if(session == null) {
         session = addSession(player);
      }

      session.setSender(player);
      return session;
   }

   public static PlayerSession addSession(CommandSender player) {
      PlayerSession session;
      if(playerSessions.containsKey(player.getName())) {
         session = (PlayerSession)playerSessions.get(player.getName());
         session.setSender(player);
      } else {
         session = new PlayerSession(player);
         playerSessions.put(player.getName(), session);
      }

      return session;
   }

}

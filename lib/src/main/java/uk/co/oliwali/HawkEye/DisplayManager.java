package uk.co.oliwali.HawkEye;

import java.util.List;
import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

public class DisplayManager {

   public static void displayPage(PlayerSession session, int page) {
      List results = session.getSearchResults();
      if(results != null && results.size() != 0) {
         byte maxLines = 6;
         int maxPages = (int)Math.ceil((double)results.size() / 6.0D);
         if(page <= maxPages && page >= 1) {
            int len = String.valueOf(maxPages).length();
            if(len > 3) {
               len = len - len / 2 + 1;
            }

            String l = "----------------------".substring(len);
            Util.sendMessage(session.getSender(), "&8" + l + " &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8" + l + (len > 1?"-":""));

            for(int i = (page - 1) * maxLines; i < (page - 1) * maxLines + maxLines && i != results.size(); ++i) {
               DataEntry entry = (DataEntry)results.get(i);
               String time = Util.getTime(entry.getTimestamp());
               sendLine(session.getSender(), "&cID:" + entry.getDataId() + " &7" + time + "&c" + entry.getPlayer() + " &7" + entry.getType().getConfigName());
               sendLine(session.getSender(), "&cLoc: &7" + entry.getWorld() + "-" + entry.getX() + "," + entry.getY() + "," + entry.getZ() + " &cData: &7" + entry.getStringData());
            }

            Util.sendMessage(session.getSender(), "&8-----------------------------------------------------");
         }
      } else {
         Util.sendMessage(session.getSender(), "&cNo results found");
      }
   }

   public static void sendLine(CommandSender sender, String input) {
      byte n = 65;
      String s = "";
      String[] splitInput = input.replaceAll("\\s+", " ").replaceAll(String.format(" *(.{1,%d})(?=$| ) *", new Object[]{Integer.valueOf(n)}), "$1\n").split("\n");
      String[] arr$ = splitInput;
      int len$ = splitInput.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String line = arr$[i$];
         Util.sendMessage(sender, "&8| " + Util.getLastColor(s) + line);
         s = line;
      }

   }
}

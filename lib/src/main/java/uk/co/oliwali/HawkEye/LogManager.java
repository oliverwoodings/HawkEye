package uk.co.oliwali.HawkEye;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.bukkit.command.CommandSender;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class LogManager {

   public static void log(PlayerSession session) {
      CommandSender sender = session.getSender();
      List results = session.getSearchResults();
      if(results != null && results.size() != 0) {
         if(results.size() > Config.MaxLog) {
            Util.sendMessage(sender, "&cMax log results: " + Config.MaxLog);
         } else {
            String t = (new SimpleDateFormat("MM-dd_HH-mm-ss")).format(Calendar.getInstance().getTime());
            String name = "Log-" + t + ".txt";
            Util.sendMessage(sender, "&7Attempting to write &c" + results.size() + " &7results to &c" + name + "&7!");
            BufferedWriter writer = null;
            int i = 0;

            try {
               writer = new BufferedWriter(new FileWriter(new File(HawkEye.instance.getDataFolder(), name)));
               writer.write("|------(Log By " + sender.getName() + ")------|" + "\n");

               for(Iterator e = results.iterator(); e.hasNext(); ++i) {
                  DataEntry e1 = (DataEntry)e.next();
                  writer.write("ID:" + e1.getDataId() + ", " + e1.getTimestamp() + ", " + e1.getPlayer() + ", " + e1.getType().getConfigName() + "\n");
                  writer.write("Loc: " + e1.getWorld() + "," + e1.getX() + "," + e1.getY() + "," + e1.getZ() + " Data: " + e1.getStringData() + "\n");
                  writer.write("--\n");
               }
            } catch (IOException var17) {
               Util.warning(var17.getMessage());
            } finally {
               if(writer != null) {
                  try {
                     writer.close();
                  } catch (IOException var16) {
                     Util.warning(var16.getMessage());
                  }
               }

            }

            Util.sendMessage(sender, "&7Successfully wrote &c" + i + " &7results to &c" + name + "&7!");
         }
      } else {
         Util.sendMessage(sender, "&cNo results found");
      }
   }
}

package uk.co.oliwali.HawkEye.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.entry.DataEntry;

public class HawkEyeAPI {

   public static boolean addCustomEntry(JavaPlugin plugin, String action, Player player, Location loc, String data) {
      return addCustomEntry(plugin, action, player.getName(), loc, data);
   }

   public static boolean addCustomEntry(JavaPlugin plugin, String action, String player, Location loc, String data) {
      if(plugin != null && action != null && player != null && loc != null && data != null) {
         DataEntry entry = new DataEntry(player, DataType.OTHER, loc, action + "-" + data);
         return addEntry(plugin, entry);
      } else {
         return false;
      }
   }

   public static boolean addEntry(JavaPlugin plugin, DataEntry entry) {
      if(entry.getClass() != entry.getType().getEntryClass()) {
         return false;
      } else if(entry.getPlayer() == null) {
         return false;
      } else {
         entry.setPlugin(plugin.getDescription().getName());
         DataManager.addEntry(entry);
         return true;
      }
   }

   public static void performSearch(BaseCallback callBack, SearchParser parser, SearchQuery.SearchDir dir) {
      new SearchQuery(callBack, parser, dir);
   }
}

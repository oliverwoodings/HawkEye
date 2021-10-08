package uk.co.oliwali.HawkEye.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Util {

   private static final Logger log = Logger.getLogger("Minecraft");


   public static void info(String msg) {
      log.info("[HawkEye] " + msg);
   }

   public static void warning(String msg) {
      log.warning("[HawkEye] " + msg);
   }

   public static void severe(String msg) {
      log.severe("[HawkEye] " + msg);
   }

   public static void debug(String msg) {
      if(Config.Debug) {
         debug(DebugLevel.LOW, msg);
      }

   }

   public static void debug(DebugLevel level, String msg) {
      if(Config.Debug && Config.DebugLevel.compareTo(level) >= 0) {
         info("DEBUG: " + msg);
      }

   }

   public static ChatColor getLastColor(String s) {
      int length = s.length();
      ChatColor color = ChatColor.GRAY;

      for(int i = length - 1; i > -1; --i) {
         char ch = s.charAt(i);
         if(ch == 38) {
            color = ChatColor.getByChar(s.charAt(i + 1));
            if(color != null) {
               return color;
            }
         }
      }

      return color;
   }

   public static void sendMessage(CommandSender player, String msg) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
   }

   public static Location getSimpleLocation(Location location) {
      location.setX((double)Math.round(location.getX() * 10.0D) / 10.0D);
      location.setY((double)Math.round(location.getY() * 10.0D) / 10.0D);
      location.setZ((double)Math.round(location.getZ() * 10.0D) / 10.0D);
      return location;
   }

   public static boolean isInteger(String str) {
      try {
         Integer.parseInt(str);
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public static String join(Collection s, String delimiter) {
      StringBuffer buffer = new StringBuffer();
      Iterator iter = s.iterator();

      while(iter.hasNext()) {
         buffer.append(iter.next());
         if(iter.hasNext()) {
            buffer.append(delimiter);
         }
      }

      return buffer.toString();
   }

   public static Object[] concat(Object[] first, Object[] ... rest) {
      int totalLength = first.length;
      Object[][] result = rest;
      int offset = rest.length;

      for(int arr$ = 0; arr$ < offset; ++arr$) {
         Object[] len$ = result[arr$];
         totalLength += len$.length;
      }

      Object[] var9 = Arrays.copyOf(first, totalLength);
      offset = first.length;
      Object[][] var10 = rest;
      int var11 = rest.length;

      for(int i$ = 0; i$ < var11; ++i$) {
         Object[] array = var10[i$];
         System.arraycopy(array, 0, var9, offset, array.length);
         offset += array.length;
      }

      return var9;
   }

   public static double distance(Location from, Location to) {
      return Math.sqrt(Math.pow(from.getX() - to.getX(), 2.0D) + Math.pow(from.getY() - to.getY(), 2.0D) + Math.pow(from.getZ() - to.getZ(), 2.0D));
   }

   public static String getEntityName(Entity entity) {
      return entity instanceof Player?((Player)entity).getName():entity.getType().getName();
   }

   public static boolean hasPerm(CommandSender sender, String perms) {
      if(!(sender instanceof Player)) {
         return true;
      } else {
         Player player = (Player)sender;
         boolean check = player.hasPermission("hawkeye." + perms) || perms.equals("help");
         if(player.isOp() && Config.OpPermissions) {
            check = true;
         }

         return check;
      }
   }

   public static String getTime(Date d1) {
      if(!Config.isSimpleTime) {
         return d1.toString();
      } else {
         String message = "";
         Date curdate = Calendar.getInstance().getTime();
         SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Date d2 = null;

         try {
            d2 = form.parse(form.format(curdate));
         } catch (ParseException var9) {
            warning(var9.getMessage());
         }

         long diff = d2.getTime() / 1000L - d1.getTime() / 1000L;
         int seconds = (int)diff;
         int min;
         if(seconds >= 86400) {
            min = seconds / 86400;
            seconds %= 86400;
            message = message + min + "d ";
         }

         if(seconds >= 3600) {
            min = seconds / 3600;
            seconds %= 3600;
            message = message + min + "h ";
         }

         if(seconds >= 60) {
            min = seconds / 60;
            seconds %= 60;
            message = message + min + "m ";
         } else {
            message = message + seconds + "s ";
         }

         return message;
      }
   }


   public static enum DebugLevel {

      NONE("NONE", 0),
      LOW("LOW", 1),
      HIGH("HIGH", 2);
      // $FF: synthetic field
      private static final DebugLevel[] $VALUES = new DebugLevel[]{NONE, LOW, HIGH};


      private DebugLevel(String var1, int var2) {}

   }
}

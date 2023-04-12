package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import uk.co.oliwali.HawkEye.HawkEye;

public class Config {

   public static List CommandFilter = new ArrayList();
   public static List IgnoreWorlds = new ArrayList();
   public static List BlockFilter = new ArrayList();
   public static int MaxLines = 0;
   public static int MaxRadius;
   public static int DefaultHereRadius;
   public static ItemStack ToolBlock;
   public static String[] DefaultToolCommand;
   public static String CleanseAge;
   public static String CleansePeriod;
   public static boolean SuperPick;
   public static boolean WEPlace;
   public static boolean WEBreak;
   public static boolean GiveTool;
   public static boolean CheckUpdates;
   public static boolean Debug;
   public static Util.DebugLevel DebugLevel;
   public static boolean LogIpAddresses;
   public static boolean DeleteDataOnRollback;
   public static boolean LogDeathDrops;
   public static boolean OpPermissions;
   public static boolean isSimpleTime;
   public static boolean logChest;
   public static boolean logDoubleChest;
   public static boolean logFurnace;
   public static boolean logDispenser;
   public static int LogDelay;
   public static int MaxLog;
   public static String DbUrl;
   public static String DbUser;
   public static String DbPassword;
   public static String DbDatabase;
   public static String DbHawkEyeTable;
   public static String DbPlayerTable;
   public static String DbEntityTable;
   public static String DbWorldTable;
   public static int PoolSize;
   private static Configuration config;


   public Config(HawkEye plugin) {
      config = plugin.getConfig().getRoot();
      config.options().copyDefaults(true);
      config.set("version", plugin.version);
      plugin.saveConfig();
      plugin.reloadConfig();
      config = plugin.getConfig();
      CommandFilter = config.getStringList("command-filter");
      BlockFilter = config.getIntegerList("block-filter");
      IgnoreWorlds = config.getStringList("ignore-worlds");
      MaxLines = config.getInt("general.max-lines");
      MaxRadius = config.getInt("general.max-radius");
      MaxLog = config.getInt("general.max-write-logs");
      DefaultHereRadius = config.getInt("general.default-here-radius");
      ToolBlock = BlockUtil.itemStringToStack(config.getString("general.tool-block"), Integer.valueOf(1));
      DefaultToolCommand = config.getString("general.default-tool-command").split(" ");
      CleanseAge = config.getString("general.cleanse-age");
      CleansePeriod = config.getString("general.cleanse-period");
      SuperPick = config.getBoolean("log.super-pickaxe");
      WEPlace = config.getBoolean("log.worldedit-place");
      WEBreak = config.getBoolean("log.worldedit-break");
      GiveTool = config.getBoolean("general.give-user-tool");
      CheckUpdates = config.getBoolean("general.check-for-updates");
      Debug = config.getBoolean("general.debug");
      LogIpAddresses = config.getBoolean("general.log-ip-addresses");
      DeleteDataOnRollback = config.getBoolean("general.delete-data-on-rollback");
      LogDeathDrops = config.getBoolean("general.log-item-drops-on-death");
      OpPermissions = config.getBoolean("general.op-permissions");
      isSimpleTime = config.getBoolean("general.simplify-time");
      LogDelay = config.getInt("general.log-delay");
      DbUser = config.getString("mysql.username");
      DbPassword = config.getString("mysql.password");
      DbUrl = "jdbc:mysql://" + config.getString("mysql.hostname") + ":" + config.getInt("mysql.port") + "/" + config.getString("mysql.database");
      DbDatabase = config.getString("mysql.database");
      DbHawkEyeTable = config.getString("mysql.hawkeye-table");
      DbPlayerTable = config.getString("mysql.player-table");
      DbEntityTable = config.getString("mysql.entity-table");
      DbWorldTable = config.getString("mysql.world-table");
      PoolSize = config.getInt("mysql.max-connections");
      logChest = config.getBoolean("containertransaction-filter.chest");
      logDoubleChest = config.getBoolean("containertransaction-filter.doublechest");
      logFurnace = config.getBoolean("containertransaction-filter.furnace");
      logDispenser = config.getBoolean("containertransaction-filter.dispenser");
      ItemMeta m = ToolBlock.getItemMeta();
      m.setDisplayName(ChatColor.GOLD + "HawkEye Tool");
      ToolBlock.setItemMeta(m);

      try {
         DebugLevel = Util.DebugLevel.valueOf(config.getString("general.debug-level").toUpperCase());
      } catch (Exception var4) {
         DebugLevel = Util.DebugLevel.NONE;
      }

   }

}

package uk.co.oliwali.HawkEye.database;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class DataManager extends TimerTask {

   private static final LinkedBlockingQueue queue = new LinkedBlockingQueue();
   private static ConnectionManager connections;
   public static Timer cleanseTimer = null;
   public static final HashMap dbPlayers = new HashMap();
   public static final HashMap dbWorlds = new HashMap();


   public DataManager(HawkEye instance) throws Exception {
      connections = new ConnectionManager(Config.DbUrl, Config.DbUser, Config.DbPassword);
      getConnection().close();
      if(!this.checkTables()) {
         throw new Exception();
      } else if(!this.updateDbLists()) {
         throw new Exception();
      } else {
         try {
            new CleanseUtil();
         } catch (Exception var3) {
            Util.severe(var3.getMessage());
            Util.severe("Unable to start cleansing utility - check your cleanse age");
         }

      }
   }

   public static LinkedBlockingQueue getQueue() {
      return queue;
   }

   public static void close() {
      connections.close();
      if(cleanseTimer != null) {
         cleanseTimer.cancel();
      }

   }

   public static void addEntry(DataEntry entry) {
      if(entry.getType().isLogged()) {
         if(!Config.IgnoreWorlds.contains(entry.getWorld())) {
            queue.add(entry);
         }
      }
   }

   public static DataEntry getEntry(int id) {
      JDCConnection conn = null;

      try {
         conn = getConnection();
         ResultSet ex = conn.createStatement().executeQuery("SELECT * FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = " + id);
         ex.next();
         DataEntry var3 = createEntryFromRes(ex);
         return var3;
      } catch (Exception var7) {
         Util.severe("Unable to retrieve data entry from MySQL Server: " + var7);
      } finally {
         conn.close();
      }

      return null;
   }

   public static void deleteEntry(int dataid) {
      Thread thread = new Thread(new DeleteEntry(Integer.valueOf(dataid)));
      thread.start();
   }

   public static void deleteEntries(List entries) {
      Thread thread = new Thread(new DeleteEntry(entries));
      thread.start();
   }

   public static String getPlayer(int id) {
      Iterator i$ = dbPlayers.entrySet().iterator();

      Entry entry;
      do {
         if(!i$.hasNext()) {
            return null;
         }

         entry = (Entry)i$.next();
      } while(((Integer)entry.getValue()).intValue() != id);

      return (String)entry.getKey();
   }

   public static String getWorld(int id) {
      Iterator i$ = dbWorlds.entrySet().iterator();

      Entry entry;
      do {
         if(!i$.hasNext()) {
            return null;
         }

         entry = (Entry)i$.next();
      } while(((Integer)entry.getValue()).intValue() != id);

      return (String)entry.getKey();
   }

   public static JDCConnection getConnection() {
      try {
         return connections.getConnection();
      } catch (SQLException var1) {
         Util.severe("Error whilst attempting to get connection: " + var1);
         return null;
      }
   }

   public static DataEntry createEntryFromRes(ResultSet res) throws Exception {
      DataType type = DataType.fromId(res.getInt(4));
      return (DataEntry)type.getEntryConstructor().newInstance(new Object[]{Integer.valueOf(res.getInt(3)), res.getTimestamp(2), Integer.valueOf(res.getInt(1)), Integer.valueOf(res.getInt(4)), res.getString(9), res.getString(10), Integer.valueOf(res.getInt(5)), Integer.valueOf(res.getInt(6)), Integer.valueOf(res.getInt(7)), Integer.valueOf(res.getInt(8))});
   }

   private boolean addPlayer(String name) {
      JDCConnection conn = null;

      boolean var4;
      try {
         Util.debug("Attempting to add player \'" + name + "\' to database");
         conn = getConnection();
         conn.createStatement().execute("INSERT INTO `" + Config.DbPlayerTable + "` (player) VALUES (\'" + name + "\') ON DUPLICATE KEY UPDATE player=\'" + name + "\';");
         return this.updateDbLists();
      } catch (SQLException var8) {
         Util.severe("Unable to add player to database: " + var8);
         var4 = false;
      } finally {
         conn.close();
      }

      return var4;
   }

   private boolean addWorld(String name) {
      JDCConnection conn = null;

      boolean var4;
      try {
         Util.debug("Attempting to add world \'" + name + "\' to database");
         conn = getConnection();
         conn.createStatement().execute("INSERT IGNORE INTO `" + Config.DbWorldTable + "` (world) VALUES (\'" + name + "\');");
         return this.updateDbLists();
      } catch (SQLException var8) {
         Util.severe("Unable to add world to database: " + var8);
         var4 = false;
      } finally {
         conn.close();
      }

      return var4;
   }

   private boolean updateDbLists() {
      JDCConnection conn = null;
      Statement stmnt = null;

      boolean var4;
      try {
         conn = getConnection();
         stmnt = conn.createStatement();
         ResultSet ex = stmnt.executeQuery("SELECT * FROM `" + Config.DbPlayerTable + "`;");

         while(ex.next()) {
            dbPlayers.put(ex.getString("player"), Integer.valueOf(ex.getInt("player_id")));
         }

         ex = stmnt.executeQuery("SELECT * FROM `" + Config.DbWorldTable + "`;");

         while(ex.next()) {
            dbWorlds.put(ex.getString("world"), Integer.valueOf(ex.getInt("world_id")));
         }

         return true;
      } catch (SQLException var14) {
         Util.severe("Unable to update local data lists from database: " + var14);
         var4 = false;
      } finally {
         try {
            if(stmnt != null) {
               stmnt.close();
            }

            conn.close();
         } catch (SQLException var13) {
            Util.severe("Unable to close SQL connection: " + var13);
         }

      }

      return var4;
   }

   private boolean checkTables() {
      JDCConnection conn = null;
      Statement stmnt = null;

      boolean var4;
      try {
         conn = getConnection();
         stmnt = conn.createStatement();
         DatabaseMetaData ex = conn.getMetaData();
         if(!JDBCUtil.tableExists(ex, Config.DbPlayerTable)) {
            Util.info("Table `" + Config.DbPlayerTable + "` not found, creating...");
            stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbPlayerTable + "` (" + "`player_id` int(11) NOT NULL AUTO_INCREMENT, " + "`player` varchar(255) NOT NULL, " + "PRIMARY KEY (`player_id`), " + "UNIQUE KEY `player` (`player`)" + ");");
         }

         if(!JDBCUtil.tableExists(ex, Config.DbWorldTable)) {
            Util.info("Table `" + Config.DbWorldTable + "` not found, creating...");
            stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbWorldTable + "` (" + "`world_id` int(11) NOT NULL AUTO_INCREMENT, " + "`world` varchar(255) NOT NULL, " + "PRIMARY KEY (`world_id`), " + "UNIQUE KEY `world` (`world`)" + ");");
         }

         if(!JDBCUtil.tableExists(ex, Config.DbHawkEyeTable)) {
            Util.info("Table `" + Config.DbHawkEyeTable + "` not found, creating...");
            stmnt.execute("CREATE TABLE `" + Config.DbHawkEyeTable + "` (" + "`data_id` int(11) NOT NULL AUTO_INCREMENT," + "`timestamp` datetime NOT NULL," + "`player_id` int(11) NOT NULL," + "`action` int(11) NOT NULL," + "`world_id` varchar(255) NOT NULL," + "`x` double NOT NULL," + "`y` double NOT NULL," + "`z` double NOT NULL," + "`data` varchar(500) DEFAULT NULL," + "`plugin` varchar(255) DEFAULT \'HawkEye\'," + "PRIMARY KEY (`data_id`)," + "KEY `timestamp` (`timestamp`)," + "KEY `player` (`player_id`)," + "KEY `action` (`action`)," + "KEY `world_id` (`world_id`)," + "KEY `x_y_z` (`x`,`y`,`z`)" + ");");
         }

         if(JDBCUtil.columnExists(ex, Config.DbHawkEyeTable, "date") && !JDBCUtil.columnExists(ex, Config.DbHawkEyeTable, "timestamp")) {
            Util.info("Attempting to update HawkEye\'s MySQL tables....");
            Util.info("This could take 1-30 minutes! Do not restart!");
            stmnt.execute("ALTER TABLE `" + Config.DbHawkEyeTable + "`" + " CHANGE COLUMN `date` `timestamp` TIMESTAMP NOT NULL" + ", ADD INDEX `timestamp` (`timestamp` DESC)" + ", ADD INDEX `player` (`player_id` ASC)" + ", ADD INDEX `action` (`action` ASC)" + ", ADD INDEX `world_id` (`world_id` ASC)" + ", DROP INDEX `player_action_world`;");
         }

         return true;
      } catch (SQLException var14) {
         Util.severe("Error checking HawkEye tables: " + var14);
         var4 = false;
      } finally {
         try {
            if(stmnt != null) {
               stmnt.close();
            }

            conn.close();
         } catch (SQLException var13) {
            Util.severe("Unable to close SQL connection: " + var13);
         }

      }

      return var4;
   }

   public void run() {
      if(!queue.isEmpty()) {
         if(queue.size() > 70000) {
            Util.info("The queue is almost overloaded! Queue: " + queue.size());
         }

         JDCConnection conn = getConnection();
         PreparedStatement stmnt = null;

         try {
            conn.setAutoCommit(false);
            stmnt = conn.prepareStatement("INSERT into `" + Config.DbHawkEyeTable + "` (timestamp, player_id, action, world_id, x, y, z, data, plugin, data_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            for(int ex = 0; ex < queue.size(); ++ex) {
               DataEntry entry = (DataEntry)queue.poll();
               if(!dbPlayers.containsKey(entry.getPlayer()) && !this.addPlayer(entry.getPlayer())) {
                  Util.debug("Player \'" + entry.getPlayer() + "\' not found, skipping entry");
               } else if(!dbWorlds.containsKey(entry.getWorld()) && !this.addWorld(entry.getWorld())) {
                  Util.debug("World \'" + entry.getWorld() + "\' not found, skipping entry");
               } else if(entry.getPlayer() != null && dbPlayers.get(entry.getPlayer()) != null) {
                  stmnt.setTimestamp(1, entry.getTimestamp());
                  stmnt.setInt(2, ((Integer)dbPlayers.get(entry.getPlayer())).intValue());
                  stmnt.setInt(3, entry.getType().getId());
                  stmnt.setInt(4, ((Integer)dbWorlds.get(entry.getWorld())).intValue());
                  stmnt.setDouble(5, entry.getX());
                  stmnt.setDouble(6, entry.getY());
                  stmnt.setDouble(7, entry.getZ());
                  stmnt.setString(8, entry.getSqlData());
                  stmnt.setString(9, entry.getPlugin());
                  if(entry.getDataId() > 0) {
                     stmnt.setInt(10, entry.getDataId());
                  } else {
                     stmnt.setInt(10, 0);
                  }

                  stmnt.addBatch();
                  if(ex % 1000 == 0) {
                     stmnt.executeBatch();
                  }
               } else {
                  Util.debug("No player found, skipping entry");
               }
            }

            stmnt.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);
         } catch (Exception var13) {
            Util.warning(var13.getMessage());
         } finally {
            try {
               if(stmnt != null) {
                  stmnt.close();
               }

               conn.close();
            } catch (Exception var12) {
               Util.severe("Unable to close SQL connection: " + var12);
            }

         }

      }
   }

}

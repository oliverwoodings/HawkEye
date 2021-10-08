package uk.co.oliwali.HawkEye.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class SearchQuery extends Thread {

   private final SearchParser parser;
   private final SearchDir dir;
   private final BaseCallback callBack;
   private final boolean delete;


   public SearchQuery(BaseCallback callBack, SearchParser parser, SearchDir dir) {
      this.callBack = callBack;
      this.parser = parser;
      this.dir = dir;
      this.delete = callBack instanceof DeleteCallback;
      this.start();
   }

   public void run() {
      Util.debug("Beginning search query");
      String sql;
      if(this.delete) {
         sql = "DELETE FROM ";
      } else {
         sql = "SELECT * FROM ";
      }

      sql = sql + "`" + Config.DbHawkEyeTable + "` WHERE ";
      LinkedList args = new LinkedList();
      LinkedList binds = new LinkedList();
      Util.debug("Building players");
      ArrayList res;
      ArrayList results;
      String stmnt;
      Entry ex;
      String ex1;
      if(this.parser.players.size() >= 1) {
         res = new ArrayList();
         results = new ArrayList();
         Iterator conn = this.parser.players.iterator();

         while(conn.hasNext()) {
            stmnt = (String)conn.next();
            Iterator deleted = DataManager.dbPlayers.entrySet().iterator();

            while(deleted.hasNext()) {
               ex = (Entry)deleted.next();
               ex1 = ((String)ex.getKey()).toLowerCase();
               if(ex1.equals(stmnt.replace("*", ""))) {
                  res.add(ex.getValue());
               } else if(ex1.contains(stmnt)) {
                  res.add(ex.getValue());
               } else if(ex1.contains(stmnt.replace("!", ""))) {
                  results.add(ex.getValue());
               }
            }
         }

         if(res.size() > 0) {
            args.add("player_id IN (" + Util.join(res, ",") + ")");
         }

         if(results.size() > 0) {
            args.add("player_id NOT IN (" + Util.join(results, ",") + ")");
         }

         if(results.size() + res.size() < 1) {
            this.callBack.error(SearchError.NO_PLAYERS, "No players found matching your specifications");
            return;
         }
      }

      Util.debug("Building worlds");
      int var32;
      if(this.parser.worlds != null) {
         res = new ArrayList();
         results = new ArrayList();
         String[] var26 = this.parser.worlds;
         int var29 = var26.length;

         for(var32 = 0; var32 < var29; ++var32) {
            String var33 = var26[var32];
            Iterator var35 = DataManager.dbWorlds.entrySet().iterator();

            while(var35.hasNext()) {
               Entry entry = (Entry)var35.next();
               if(((String)entry.getKey()).toLowerCase().contains(var33.toLowerCase())) {
                  res.add(entry.getValue());
               } else if(((String)entry.getKey()).toLowerCase().contains(var33.replace("!", "").toLowerCase())) {
                  results.add(entry.getValue());
               }
            }
         }

         if(res.size() > 0) {
            args.add("world_id IN (" + Util.join(res, ",") + ")");
         }

         if(results.size() > 0) {
            args.add("world_id NOT IN (" + Util.join(results, ",") + ")");
         }

         if(results.size() + res.size() < 1) {
            this.callBack.error(SearchError.NO_WORLDS, "No worlds found matching your specifications");
            return;
         }
      }

      Util.debug("Building actions");
      if(this.parser.actions != null && this.parser.actions.size() > 0) {
         res = new ArrayList();
         Iterator var24 = this.parser.actions.iterator();

         while(var24.hasNext()) {
            DataType var27 = (DataType)var24.next();
            res.add(Integer.valueOf(var27.getId()));
         }

         args.add("action IN (" + Util.join(res, ",") + ")");
      }

      Util.debug("Building dates");
      if(this.parser.dateFrom != null) {
         args.add("timestamp >= ?");
         binds.add(this.parser.dateFrom);
      }

      if(this.parser.dateTo != null) {
         args.add("timestamp <= ?");
         binds.add(this.parser.dateTo);
      }

      Util.debug("Building location");
      if(this.parser.minLoc != null) {
         args.add("(x BETWEEN " + this.parser.minLoc.getBlockX() + " AND " + this.parser.maxLoc.getBlockX() + ")");
         args.add("(y BETWEEN " + this.parser.minLoc.getBlockY() + " AND " + this.parser.maxLoc.getBlockY() + ")");
         args.add("(z BETWEEN " + this.parser.minLoc.getBlockZ() + " AND " + this.parser.maxLoc.getBlockZ() + ")");
      } else if(this.parser.loc != null) {
         args.add("x = " + this.parser.loc.getX());
         args.add("y = " + this.parser.loc.getY());
         args.add("z = " + this.parser.loc.getZ());
      }

      Util.debug("Building filters");
      if(this.parser.filters != null) {
         String[] var22 = this.parser.filters;
         int var25 = var22.length;

         for(int var28 = 0; var28 < var25; ++var28) {
            stmnt = var22[var28];
            args.add("data LIKE ?");
            binds.add("%" + stmnt + "%");
         }
      }

      sql = sql + Util.join(args, " AND ");
      Util.debug("Ordering by data_id");
      sql = sql + " ORDER BY `data_id` " + (this.dir == SearchDir.DESC?"DESC":"ASC");
      Util.debug("Building limits");
      if(Config.MaxLines > 0) {
         sql = sql + " LIMIT " + Config.MaxLines;
      }

      ResultSet var23 = null;
      results = new ArrayList();
      JDCConnection var30 = DataManager.getConnection();
      PreparedStatement var31 = null;
      var32 = 0;

      label365: {
         try {
            var30.setAutoCommit(false);
            var31 = var30.prepareStatement(sql);
            Util.debug("Preparing statement");

            for(int var34 = 0; var34 < binds.size(); ++var34) {
               var31.setObject(var34 + 1, binds.get(var34));
            }

            Util.debug("Searching: " + var31.toString());
            if(this.delete) {
               Util.debug("Deleting entries");
               var32 = var31.executeUpdate();
            } else {
               var23 = var31.executeQuery();
               Util.debug("Getting results");
               ex = null;
               ex1 = null;

               while(var23.next()) {
                  DataType var36 = DataType.fromId(var23.getInt(4));
                  DataEntry var37 = (DataEntry)var36.getEntryConstructor().newInstance(new Object[]{Integer.valueOf(var23.getInt(3)), var23.getTimestamp(2), Integer.valueOf(var23.getInt(1)), Integer.valueOf(var23.getInt(4)), var23.getString(9), var23.getString(10), Integer.valueOf(var23.getInt(5)), Integer.valueOf(var23.getInt(6)), Integer.valueOf(var23.getInt(7)), Integer.valueOf(var23.getInt(8))});
                  results.add(var37);
               }
            }

            var30.commit();
            var30.setAutoCommit(true);
            break label365;
         } catch (Exception var20) {
            Util.severe("Error executing MySQL query: " + var20);
            var20.printStackTrace();
            this.callBack.error(SearchError.MYSQL_ERROR, "Error executing MySQL query: " + var20);
         } finally {
            try {
               if(var23 != null) {
                  var23.close();
               }

               if(var31 != null) {
                  var31.close();
               }

               var30.close();
            } catch (SQLException var19) {
               Util.severe("Unable to close SQL connection: " + var19);
               this.callBack.error(SearchError.MYSQL_ERROR, "Unable to close SQL connection: " + var19);
            }

         }

         return;
      }

      Util.debug(results.size() + " results found");
      if(this.delete) {
         ((DeleteCallback)this.callBack).deleted = var32;
      } else {
         this.callBack.results = results;
      }

      this.callBack.execute();
      Util.debug("Search complete");
   }

   public static enum SearchError {

      NO_PLAYERS("NO_PLAYERS", 0),
      NO_WORLDS("NO_WORLDS", 1),
      MYSQL_ERROR("MYSQL_ERROR", 2);
      // $FF: synthetic field
      private static final SearchError[] $VALUES = new SearchError[]{NO_PLAYERS, NO_WORLDS, MYSQL_ERROR};


      private SearchError(String var1, int var2) {}

   }

   public static enum SearchDir {

      ASC("ASC", 0),
      DESC("DESC", 1);
      // $FF: synthetic field
      private static final SearchDir[] $VALUES = new SearchDir[]{ASC, DESC};


      private SearchDir(String var1, int var2) {}

   }
}

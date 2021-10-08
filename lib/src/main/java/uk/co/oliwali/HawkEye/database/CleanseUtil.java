package uk.co.oliwali.HawkEye.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class CleanseUtil extends TimerTask {

   private String date = null;
   private int interval = 1200;


   public CleanseUtil() throws Exception {
      List arr = Arrays.asList(new String[]{"0", "0s"});
      if(Config.CleanseAge != null && Config.CleansePeriod != null && !arr.contains(Config.CleanseAge) && !arr.contains(Config.CleansePeriod)) {
         this.ageToDate();
         int temp = 0;
         String nums = "";

         for(int i = 0; i < Config.CleansePeriod.length(); ++i) {
            String c = Config.CleansePeriod.substring(i, i + 1);
            if(Util.isInteger(c)) {
               nums = nums + c;
            } else {
               int num = Integer.parseInt(nums);
               if(c.equals("w")) {
                  temp += 604800 * num;
               } else if(c.equals("d")) {
                  temp += 86400 * num;
               } else if(c.equals("h")) {
                  temp += 3600 * num;
               } else if(c.equals("m")) {
                  temp += 60 * num;
               } else {
                  if(!c.equals("s")) {
                     throw new Exception();
                  }

                  temp += num;
               }

               nums = "";
            }
         }

         if(temp > 0) {
            this.interval = temp;
         }

         Util.info("Starting database cleanse thread with a period of " + this.interval + " seconds");
         DataManager.cleanseTimer = new Timer();
         DataManager.cleanseTimer.scheduleAtFixedRate(this, 0L, (long)(this.interval * 1000));
      }
   }

   public void run() {
      Util.info("Running cleanse utility for logs older than " + this.date);
      JDCConnection conn = null;
      PreparedStatement stmnt = null;
      String sql = "DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `timestamp` < \'" + this.date + "\'";

      try {
         this.ageToDate();
         conn = DataManager.getConnection();
         stmnt = conn.prepareStatement(sql);
         Util.debug("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `timestamp` < \'" + this.date + "\'");
         Util.info("Deleted " + stmnt.executeUpdate() + " row(s) from database");
      } catch (Exception var13) {
         Util.severe("Unable to execute cleanse utility: " + var13);
      } finally {
         try {
            if(stmnt != null) {
               stmnt.close();
            }

            conn.close();
         } catch (SQLException var12) {
            Util.warning(var12.getMessage());
         }

      }

   }

   private void ageToDate() throws Exception {
      int weeks = 0;
      int days = 0;
      int hours = 0;
      int mins = 0;
      int secs = 0;
      String nums = "";

      for(int cal = 0; cal < Config.CleanseAge.length(); ++cal) {
         String form = Config.CleanseAge.substring(cal, cal + 1);
         if(Util.isInteger(form)) {
            nums = nums + form;
         } else {
            int num = Integer.parseInt(nums);
            if(form.equals("w")) {
               weeks = num;
            } else if(form.equals("d")) {
               days = num;
            } else if(form.equals("h")) {
               hours = num;
            } else if(form.equals("m")) {
               mins = num;
            } else {
               if(!form.equals("s")) {
                  throw new Exception();
               }

               secs = num;
            }

            nums = "";
         }
      }

      Calendar var10 = Calendar.getInstance();
      var10.add(3, -1 * weeks);
      var10.add(5, -1 * days);
      var10.add(10, -1 * hours);
      var10.add(12, -1 * mins);
      var10.add(13, -1 * secs);
      SimpleDateFormat var11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      this.date = var11.format(var10.getTime());
   }
}

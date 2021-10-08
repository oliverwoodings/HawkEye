package uk.co.oliwali.HawkEye.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class DeleteEntry implements Runnable {

   private final List ids = new ArrayList();


   public DeleteEntry(Integer id) {
      this.ids.add(id);
   }

   public DeleteEntry(DataEntry entry) {
      this.ids.add(Integer.valueOf(entry.getDataId()));
   }

   public DeleteEntry(List entries) {
      for(int i = 0; i < entries.size(); ++i) {
         if(entries.get(i) instanceof DataEntry) {
            this.ids.add(Integer.valueOf(((DataEntry)((DataEntry)entries.get(i))).getDataId()));
         } else {
            this.ids.add((Integer)entries.get(i));
         }
      }

   }

   public void run() {
      JDCConnection conn = null;
      PreparedStatement stmnt = null;

      try {
         conn = DataManager.getConnection();
         conn.setAutoCommit(false);
         stmnt = conn.prepareStatement("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = ?");
         int ex = 0;
         Iterator i$ = this.ids.iterator();

         while(i$.hasNext()) {
            Integer id = (Integer)i$.next();
            stmnt.setInt(1, id.intValue());
            stmnt.addBatch();
            ++ex;
            if(ex % 1000 == 0) {
               stmnt.executeBatch();
            }
         }

         stmnt.executeBatch();
         conn.commit();
         conn.setAutoCommit(true);
      } catch (SQLException var9) {
         Util.severe("Unable to delete data entries from MySQL database: " + var9);
      } finally {
         conn.close();
      }

   }
}

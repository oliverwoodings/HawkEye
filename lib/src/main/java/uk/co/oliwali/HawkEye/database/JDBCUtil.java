package uk.co.oliwali.HawkEye.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import uk.co.oliwali.HawkEye.util.Util;

public abstract class JDBCUtil {

   protected abstract void delegatedLog(String var1);

   public static boolean tableExists(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
      return tableExistsCaseSensitive(dbMetaData, tableName) || tableExistsCaseSensitive(dbMetaData, tableName.toUpperCase(Locale.US)) || tableExistsCaseSensitive(dbMetaData, tableName.toLowerCase(Locale.US));
   }

   public static boolean tableExistsCaseSensitive(DatabaseMetaData dbMetaData, String tableName) throws SQLException {
      ResultSet rsTables = dbMetaData.getTables((String)null, (String)null, tableName, (String[])null);

      boolean var4;
      try {
         boolean found = rsTables.next();
         var4 = found;
      } finally {
         closeJDBCResultSet(rsTables);
      }

      return var4;
   }

   public static boolean columnExists(DatabaseMetaData dbMetaData, String tableName, String columnName) throws SQLException {
      return columnExistsCaseSensitive(dbMetaData, tableName, columnName) || columnExistsCaseSensitive(dbMetaData, tableName, columnName.toUpperCase(Locale.US)) || columnExistsCaseSensitive(dbMetaData, tableName, columnName.toLowerCase(Locale.US)) || columnExistsCaseSensitive(dbMetaData, tableName.toUpperCase(Locale.US), columnName) || columnExistsCaseSensitive(dbMetaData, tableName.toUpperCase(Locale.US), columnName.toUpperCase(Locale.US)) || columnExistsCaseSensitive(dbMetaData, tableName.toUpperCase(Locale.US), columnName.toLowerCase(Locale.US)) || columnExistsCaseSensitive(dbMetaData, tableName.toLowerCase(Locale.US), columnName) || columnExistsCaseSensitive(dbMetaData, tableName.toLowerCase(Locale.US), columnName.toUpperCase(Locale.US)) || columnExistsCaseSensitive(dbMetaData, tableName.toLowerCase(Locale.US), columnName.toLowerCase(Locale.US));
   }

   public static boolean columnExistsCaseSensitive(DatabaseMetaData dbMetaData, String tableName, String columnName) throws SQLException {
      ResultSet rsTables = dbMetaData.getColumns((String)null, (String)null, tableName, columnName);

      boolean var5;
      try {
         boolean found = rsTables.next();
         var5 = found;
      } finally {
         closeJDBCResultSet(rsTables);
      }

      return var5;
   }

   public static void closeJDBCResultSet(ResultSet aResultSet) {
      try {
         if(aResultSet != null) {
            aResultSet.close();
         }
      } catch (SQLException var2) {
         Util.severe("Unable to close JDBCResulset: " + var2);
      }

   }
}

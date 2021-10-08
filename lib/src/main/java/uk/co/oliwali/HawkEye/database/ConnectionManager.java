package uk.co.oliwali.HawkEye.database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class ConnectionManager implements Closeable {

   private static int poolsize = 10;
   private static long timeToLive = 300000L;
   private static Vector connections;
   private final ConnectionReaper reaper;
   private final String url;
   private final String user;
   private final String password;


   public ConnectionManager(String url, String user, String password) throws ClassNotFoundException {
      Class.forName("com.mysql.jdbc.Driver");
      Util.debug("Attempting to connecting to database at: " + url);
      this.url = url;
      this.user = user;
      this.password = password;
      poolsize = Config.PoolSize;
      connections = new Vector(poolsize);
      this.reaper = new ConnectionReaper((NamelessClass1725384373)null);
      this.reaper.start();
   }

   public synchronized void close() {
      Util.debug("Closing all MySQL connections");
      Enumeration conns = connections.elements();

      while(conns.hasMoreElements()) {
         JDCConnection conn = (JDCConnection)conns.nextElement();
         connections.remove(conn);
         conn.terminate();
      }

   }

   public synchronized JDCConnection getConnection() throws SQLException {
      JDCConnection conn;
      for(int i = 0; i < connections.size(); ++i) {
         conn = (JDCConnection)connections.get(i);
         if(conn.lease()) {
            if(conn.isValid()) {
               return conn;
            }

            Util.debug("Removing dead MySQL connection");
            connections.remove(conn);
            conn.terminate();
         }
      }

      Util.debug("No available MySQL connections, attempting to create new one");
      conn = new JDCConnection(DriverManager.getConnection(this.url, this.user, this.password));
      conn.lease();
      if(!conn.isValid()) {
         conn.terminate();
         throw new SQLException("Could not create new connection");
      } else {
         connections.add(conn);
         return conn;
      }
   }

   public static synchronized void removeConn(Connection conn) {
      connections.remove(conn);
   }

   public static boolean areConsOpen() {
      Enumeration conns = connections.elements();

      while(conns.hasMoreElements()) {
         JDCConnection conn = (JDCConnection)conns.nextElement();

         try {
            if(conn.isValid() && !conn.isClosed() && conn.lease()) {
               return true;
            }
         } catch (SQLException var3) {
            var3.printStackTrace();
         }
      }

      return false;
   }

   private synchronized void reapConnections() {
      Util.debug("Attempting to reap dead connections");
      long stale = System.currentTimeMillis() - timeToLive;
      Enumeration conns = connections.elements();
      int count = 0;

      for(int i = 1; conns.hasMoreElements(); ++i) {
         JDCConnection conn = (JDCConnection)conns.nextElement();
         if(conn.inUse() && stale > conn.getLastUse() && !conn.isValid()) {
            connections.remove(conn);
            ++count;
         }

         if(i > poolsize) {
            connections.remove(conn);
            ++count;
            conn.terminate();
         }
      }

      Util.debug(count + " connections reaped");
   }

   public static Vector getConnections() {
      return connections;
   }


   private class ConnectionReaper extends Thread {

      private ConnectionReaper() {}

      public void run() {
         while(true) {
            try {
               Thread.sleep(300000L);
            } catch (InterruptedException var2) {
               ;
            }

            ConnectionManager.this.reapConnections();
         }
      }

      // $FF: synthetic method
      ConnectionReaper(NamelessClass1725384373 x1) {
         this();
      }
   }

   // $FF: synthetic class
   static class NamelessClass1725384373 {
   }
}

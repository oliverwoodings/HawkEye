package uk.co.oliwali.HawkEye.database;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Manages the MySQL connection pool.
 * By default 10 connections are maintained at a time
 * @author oliverw92
 */
public class ConnectionManager implements Closeable {

	private static int poolsize = 10;
	private static long timeToLive = 300000;
	private static Vector<JDCConnection> connections;
	private final ConnectionReaper reaper;
	private final String url;
	private final String user;
	private final String password;

	/**
	 * Creates the connection manager and starts the reaper
	 * @param url url of the database
	 * @param user username to use
	 * @param password password for the database
	 * @throws ClassNotFoundException
	 */
	public ConnectionManager(String url, String user, String password) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Util.debug("Attempting to connecting to database at: " + url);
		this.url = url;
		this.user = user;
		this.password = password;
		poolsize = Config.PoolSize;
		connections = new Vector<JDCConnection>(poolsize);
		reaper = new ConnectionReaper();
		reaper.start();
	}

	/**
	 * Closes all connections
	 */
	@Override
	public synchronized void close() {
		Util.debug("Closing all MySQL connections");
		final Enumeration<JDCConnection> conns = connections.elements();
		while (conns.hasMoreElements()) {
			final JDCConnection conn = conns.nextElement();
			connections.remove(conn);
			conn.terminate();
		}
	}

	/**
	 * Returns a connection from the pool
	 * @return returns a {JDCConnection}
	 * @throws SQLException
	 */
	public synchronized JDCConnection getConnection() throws SQLException {
		JDCConnection conn;
		for (int i = 0; i < connections.size(); i++) {
			conn = connections.get(i);
			if (conn.lease()) {
				if (conn.isValid())
					return conn;
				Util.debug("Removing dead MySQL connection");
				connections.remove(conn);
				conn.terminate();
			}
		}
		Util.debug("No available MySQL connections, attempting to create new one");
		conn = new JDCConnection(DriverManager.getConnection(url, user, password));
		conn.lease();
		if (!conn.isValid()) {
			conn.terminate();
			throw new SQLException("Could not create new connection");
		}
		connections.add(conn);
		return conn;
	}

    /**
     * Removes a connection from the pool
     * @param {JDCConnection} to remove
     */
	public static synchronized void removeConn(Connection conn) {
		connections.remove(conn);
	}

	/**
	 * Loops through connections, reaping old ones
	 */
	private synchronized void reapConnections() {
		Util.debug("Attempting to reap dead connections");
		final long stale = System.currentTimeMillis() - timeToLive;
		final Enumeration<JDCConnection> conns = connections.elements();
		int count = 0;
		int i = 1;
		while (conns.hasMoreElements()) {
			final JDCConnection conn = conns.nextElement();

			if (conn.inUse() && stale > conn.getLastUse() && !conn.isValid()) {
				connections.remove(conn);
				count++;
			}

			if (i > poolsize) {
				connections.remove(conn);
				count++;
				conn.terminate();
			}
			i++;
		}
		Util.debug(count + " connections reaped");
	}

	/**
	 * Reaps connections
	 * @author oliverw92
	 */
	private class ConnectionReaper extends Thread
	{
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(300000);
				} catch (final InterruptedException e) {}
				reapConnections();
			}
		}
	}
}

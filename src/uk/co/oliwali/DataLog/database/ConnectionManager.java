package uk.co.oliwali.DataLog.database;

import java.io.Closeable;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;

import uk.co.oliwali.DataLog.util.Config;

public class ConnectionManager implements Closeable {
	
	private static int poolsize = 10;
	private static long timeToLive = 300000;
	private Vector<JDCConnection> connections;
	private ConnectionReaper reaper;
	private String url;
	private String user;
	private String password;

	public ConnectionManager(String url, String user, String password) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		this.url = url;
		this.user = user;
		this.password = password;
		poolsize = Config.PoolSize;
		connections = new Vector<JDCConnection>(poolsize);
		reaper = new ConnectionReaper();
		reaper.start();
	}

	@Override
	public synchronized void close() {
		final Enumeration<JDCConnection> conns = connections.elements();
		while (conns.hasMoreElements()) {
			final JDCConnection conn = conns.nextElement();
			connections.remove(conn);
			conn.terminate();
		}
	}

	public synchronized JDCConnection getConnection() throws SQLException {
		JDCConnection conn;
		for (int i = 0; i < connections.size(); i++) {
			conn = connections.get(i);
			if (conn.lease()) {
				if (conn.isValid())
					return conn;
				connections.remove(conn);
				conn.terminate();
			}
		}
		conn = new JDCConnection(DriverManager.getConnection(url, user, password));
		conn.lease();
		if (!conn.isValid()) {
			conn.terminate();
			throw new SQLException("Failed to validate a brand new connection");
		}
		connections.add(conn);
		return conn;
	}

	private synchronized void reapConnections() {
		final long stale = System.currentTimeMillis() - timeToLive;
		for (final JDCConnection conn : connections)
			if (conn.inUse() && stale > conn.getLastUse() && !conn.isValid())
				connections.remove(conn);
	}

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

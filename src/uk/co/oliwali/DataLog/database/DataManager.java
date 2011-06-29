package uk.co.oliwali.DataLog.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Util;
import uk.co.oliwali.DataLog.database.JDCConnection;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchType;

public class DataManager extends TimerTask {
	
	private static DataLog plugin;
	private static LinkedBlockingQueue<DataEntry> queue = new LinkedBlockingQueue<DataEntry>();
	private static ConnectionManager connections;
	public static HashMap<String, Integer> dbPlayers = new HashMap<String, Integer>();
	public static HashMap<String, Integer> dbWorlds = new HashMap<String, Integer>();
	
	public DataManager(DataLog instance) throws Exception {
		plugin = instance;
		connections = new ConnectionManager(Config.DbUrl, Config.DbUser, Config.DbPassword);
		getConnection().close();
		
		//Check tables and update player/world lists
		if (!checkTables())
			throw new Exception();
		if (!updateDbLists())
			throw new Exception();

		//Start cleansing utility
		try {
			CleanseUtil util = new CleanseUtil();
			if (util.date != null) {
				Timer cleanse = new Timer();
				cleanse.scheduleAtFixedRate(util, 0, 1200000);
			}
		} catch (Exception e) {
			Util.severe("Unable to start cleansing utility - check your cleanse age");
		}

		//Start logging timer
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 2000, 2000);
	}
	
	public static void addEntry(Player player, DataType dataType, Location loc, String data) {
		addEntry(player, plugin, dataType, loc, data);
	}
	public static void addEntry(String player, DataType dataType, Location loc, String data) {
		addEntry(player, plugin, dataType, loc, data);
	}
	public static void addEntry(Player player, JavaPlugin cplugin, DataType dataType, Location loc, String data) {
		addEntry(player.getName(), cplugin, dataType, loc, data);
	}
	public static void addEntry(String player, JavaPlugin cplugin, DataType dataType, Location loc, String data) {
		if (plugin.config.isLogged(dataType)) {
			DataEntry dataEntry = new DataEntry();
			loc = Util.getSimpleLocation(loc);
			dataEntry.setInfo(player, cplugin, dataType.getId(), loc, data);
			queue.add(dataEntry);
		}
	}
	
	public static DataEntry getEntry(int id) {
		JDCConnection conn = null;
		try {
			conn = getConnection();
			ResultSet res = conn.createStatement().executeQuery("SELECT * FROM `" + Config.DbDatalogTable + "` WHERE `data_id` = " + id);
			res.next();
			DataEntry entry = new DataEntry();
			entry.setDataid(res.getInt("data_id"));
			entry.setDate(res.getString("date"));
			entry.setPlayer(DataManager.getPlayer(res.getInt("player_id")));
			entry.setAction(res.getInt("action"));
			entry.setData(res.getString("data"));
			entry.setPlugin(res.getString("plugin"));
			entry.setWorld(DataManager.getWorld(res.getInt("world_id")));
			entry.setX(res.getInt("x"));
			entry.setY(res.getInt("y"));
			entry.setZ(res.getInt("z"));
			return entry;
		} catch (SQLException ex) {
			Util.severe("Unable to retrieve data entry from MySQL Server: " + ex);
		} finally {
			conn.close();
		}
		return null;
	}
	
	public static String getPlayer(int id) {
		for (Entry<String, Integer> entry : dbPlayers.entrySet())
			if (entry.getValue() == id)
				return entry.getKey();
		return null;
	}
	
	public static String getWorld(int id) {
		for (Entry<String, Integer> entry : dbWorlds.entrySet())
			if (entry.getValue() == id)
				return entry.getKey();
		return null;
	}
	
	public static void toolSearch(Player player, Location loc) {
		List<Integer> actions = new ArrayList<Integer>();
		for (DataType type : DataType.values())
			if (type.canHere()) actions.add(type.getId());
		loc = Util.getSimpleLocation(loc);
		Thread thread = new SearchQuery(SearchType.SEARCH, player, null, null, null, actions, loc.toVector(), 0, loc.getWorld().getName().split(","), null, "desc");
		thread.start();
	}
	
	public static JDCConnection getConnection() {
		try {
			return connections.getConnection();
		} catch (final SQLException ex) {
			Util.severe("Error whilst attempting to get connection: " + ex);
			return null;
		}
	}
	
	public static DataEntry createEntryFromRes(ResultSet res) throws SQLException {
		DataEntry entry = new DataEntry();
		entry.setPlayer(DataManager.getPlayer(res.getInt("player_id")));
		entry.setDate(res.getString("date"));
		entry.setDataid(res.getInt("data_id"));
		entry.setAction(res.getInt("action"));
		entry.setData(res.getString("data"));
		entry.setPlugin(res.getString("plugin"));
		entry.setWorld(DataManager.getWorld(res.getInt("world_id")));
		entry.setX(res.getInt("x"));
		entry.setY(res.getInt("y"));
		entry.setZ(res.getInt("z"));
		return entry;
	}
	
	private boolean addPlayer(String name) {
		JDCConnection conn = null;
		try {
			conn = getConnection();
			conn.createStatement().execute("INSERT IGNORE INTO `" + Config.DbPlayerTable + "` (player) VALUES ('" + name + "');");
		} catch (SQLException ex) {
			Util.severe("Unable to add player to database: " + ex);
			return false;
		} finally {
			conn.close();
		}
		if (!updateDbLists())
			return false;
		return true;
	}
	
	private boolean addWorld(String name) {
		JDCConnection conn = null;
		try {
			conn = getConnection();
			conn.createStatement().execute("INSERT IGNORE INTO `" + Config.DbWorldTable + "` (world) VALUES ('" + name + "');");
		} catch (SQLException ex) {
			Util.severe("Unable to add world to database: " + ex);
			return false;
		} finally {
			conn.close();
		}
		if (!updateDbLists())
			return false;
		return true;
	}
	
	private boolean updateDbLists() {
		JDCConnection conn = null;
		Statement stmnt = null;
		try {
			conn = getConnection();
			stmnt = conn.createStatement();
			ResultSet res = stmnt.executeQuery("SELECT * FROM `" + Config.DbPlayerTable + "`;");
			while (res.next())
				dbPlayers.put(res.getString("player"), res.getInt("player_id"));
			res = stmnt.executeQuery("SELECT * FROM `" + Config.DbWorldTable + "`;");
			while (res.next())
				dbWorlds.put(res.getString("world"), res.getInt("world_id"));
		} catch (SQLException ex) {
			Util.severe("Unable to update local data lists from database: " + ex);
			return false;
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}
				
		}
		return true;
	}
	
	private boolean checkTables() {
		JDCConnection conn = null;
		Statement stmnt = null;
		try {
			conn = getConnection();
			stmnt = conn.createStatement();
			DatabaseMetaData dbm = conn.getMetaData();
			//Check if tables exist
			if (!JDBCUtil.tableExists(dbm, Config.DbPlayerTable)) {
				Util.info("Table `" + Config.DbPlayerTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbPlayerTable + "` (`player_id` int(11) NOT NULL AUTO_INCREMENT, `player` varchar(255) NOT NULL, PRIMARY KEY (`player_id`), KEY `player` (`player`) ) ENGINE=MyISAM;");
			}
			if (!JDBCUtil.tableExists(dbm, Config.DbWorldTable)) {
				Util.info("Table `" + Config.DbWorldTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbWorldTable + "` (`world_id` int(11) NOT NULL AUTO_INCREMENT, `world` varchar(255) NOT NULL, PRIMARY KEY (`world_id`), KEY `world` (`world`) ) ENGINE=MyISAM;");
			}
			if (!JDBCUtil.tableExists(dbm, Config.DbDatalogTable)) {
				Util.info("Table `" + Config.DbDatalogTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbDatalogTable + "` (`data_id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `action` int(11) NOT NULL, `world_id` varchar(255) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `data` varchar(255) DEFAULT NULL, `plugin` varchar(255) DEFAULT 'DataLog', PRIMARY KEY (`data_id`), KEY `player_action_world` (`player_id`,`action`,`world_id`), KEY `x_y_z` (`x`,`y`,`z` )) ENGINE=MyISAM;");
			}
			else if (!JDBCUtil.columnExists(dbm, Config.DbDatalogTable, "player_id")) {
				Util.info("Pre-v1.1.0 database detected, performing legacy database update please wait...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbDatalogTable + "2` (`data_id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `action` int(11) NOT NULL, `world_id` varchar(255) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `data` varchar(255) DEFAULT NULL, `plugin` varchar(255) DEFAULT 'DataLog', PRIMARY KEY (`data_id`), KEY `player_action_world` (`player_id`,`action`,`world_id`), KEY `x_y_z` (`x`,`y`,`z` )) ENGINE=MyISAM;");
				stmnt.execute("INSERT INTO `" + Config.DbPlayerTable + "` (player) SELECT DISTINCT `player` FROM `" + Config.DbDatalogTable + "`");
				stmnt.execute("INSERT INTO `" + Config.DbWorldTable + "` (world) SELECT DISTINCT `world` FROM `" + Config.DbDatalogTable + "`");
				Util.info("Players and worlds imported into new structure. Importing main data please wait...");
				stmnt.execute("INSERT INTO `" + Config.DbDatalogTable + "2` (date, player_id, action, world_id, x, y, z, data, plugin) SELECT " + Config.DbDatalogTable + ".date, " + Config.DbPlayerTable + ".player_id, " + Config.DbDatalogTable + ".action, " + Config.DbWorldTable + ".world_id, " + Config.DbDatalogTable + ".x, " + Config.DbDatalogTable + ".y, " + Config.DbDatalogTable + ".z, " + Config.DbDatalogTable + ".data, " + Config.DbDatalogTable + ".plugin FROM `" + Config.DbDatalogTable + "`, `" + Config.DbPlayerTable + "`, `" + Config.DbWorldTable + "` WHERE " + Config.DbPlayerTable + ".player = " + Config.DbDatalogTable + ".player AND " + Config.DbWorldTable + ".world = " + Config.DbDatalogTable + ".world");
				Util.info("Import complete, cleaning up old table and renaming new...");
				stmnt.execute("DROP TABLE `" + Config.DbDatalogTable + "`;");
				stmnt.execute("RENAME TABLE `" + Config.DbDatalogTable + "2` TO `" + Config.DbDatalogTable + "`");
				Util.info("Legacy database update complete");
			}
		} catch (SQLException ex) {
			Util.severe("Error checking DataLog tables: " + ex);
			return false;
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}
				
		}
		return true;
	}

	public void run() {
		if (queue.isEmpty())
			return;
		JDCConnection conn = getConnection();
		PreparedStatement stmnt = null;
		try {
			while (!queue.isEmpty()) {
				DataEntry entry = queue.poll();
				if (!dbPlayers.containsKey(entry.getPlayer()))
					if (!addPlayer(entry.getPlayer()))
						continue;
				if (!dbWorlds.containsKey(entry.getWorld()))
					if (!addWorld(entry.getWorld()))
						continue;
				
				stmnt = conn.prepareStatement("INSERT into `" + Config.DbDatalogTable + "` (date, player_id, action, world_id, x, y, z, data, plugin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
				stmnt.setString(1, entry.getDate());
				stmnt.setInt(2, dbPlayers.get(entry.getPlayer()));
				stmnt.setInt(3, entry.getAction());
				stmnt.setInt(4, dbWorlds.get(entry.getWorld()));
				stmnt.setDouble(5, entry.getX());
				stmnt.setDouble(6, entry.getY());
				stmnt.setDouble(7, entry.getZ());
				stmnt.setString(8, entry.getData());
				stmnt.setString(9, entry.getPlugin());
				stmnt.executeUpdate();
			}
		} catch (SQLException ex) {
			Util.severe("SQL Exception: " + ex);
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}
				
		}
		
	}
	
}

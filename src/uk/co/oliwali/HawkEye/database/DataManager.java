package uk.co.oliwali.HawkEye.database;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;
import uk.co.oliwali.HawkEye.database.JDCConnection;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchType;

/**
 * Handler for everything to do with the database.
 * All queries except searching goes through this class.
 * @author oliverw92
 */
public class DataManager extends TimerTask {
	
	private static HawkEye plugin;
	private static LinkedBlockingQueue<DataEntry> queue = new LinkedBlockingQueue<DataEntry>();
	private static ConnectionManager connections;
	public static HashMap<String, Integer> dbPlayers = new HashMap<String, Integer>();
	public static HashMap<String, Integer> dbWorlds = new HashMap<String, Integer>();

	/**
	 * Initiates database connection pool, checks tables, starts cleansing utility
	 * Throws an exception if it is unable to complete setup
	 * @param instance
	 * @throws Exception
	 */
	public DataManager(HawkEye instance) throws Exception {
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
	
	/**
	 * Closes down all connections
	 */
	public static void close() {
		connections.close();
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
		DataEntry dataEntry = new DataEntry();
		loc = Util.getSimpleLocation(loc);
		dataEntry.setInfo(player, cplugin, dataType, loc, data);
		addEntry(dataEntry);
	}
	/**
	 * Adds a {@link DataEntry} to the database queue.
	 * {Rule}s are checked at this point
	 * @param entry {@link DataEntry} to be added
	 * @return
	 */
	public static void addEntry(DataEntry entry) {
		
		//Check block filter
		switch (entry.getType()) {
			case BLOCK_BREAK:
				if (Config.BlockFilter.contains(BlockUtil.getBlockStringName(entry.getData())))
					return;
				break;
			case BLOCK_PLACE:
				String txt = null;
				if (entry.getData().indexOf("-") == -1)
					txt = BlockUtil.getBlockStringName(entry.getData());
				else
					txt = BlockUtil.getBlockStringName(entry.getData().substring(entry.getData().indexOf("-") + 1));
				if (Config.BlockFilter.contains(txt))
					return;
		}
		
		if (Config.isLogged(entry.getType()))
			queue.add(entry);
	}
	
	/**
	 * Retrieves an entry from the database
	 * @param id id of entry to return
	 * @return
	 */
	public static DataEntry getEntry(int id) {
		JDCConnection conn = null;
		try {
			conn = getConnection();
			ResultSet res = conn.createStatement().executeQuery("SELECT * FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = " + id);
			res.next();
			DataEntry entry = new DataEntry();
			entry.setDataid(res.getInt("data_id"));
			entry.setDate(res.getString("date"));
			entry.setPlayer(DataManager.getPlayer(res.getInt("player_id")));
			entry.setType(DataType.fromId(res.getInt("action")));
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
	
	/**
	 * Deletes an entry from the database
	 * @param dataid id to delete
	 */
	public static void deleteEntry(int dataid) {
		JDCConnection conn = null;
		try {
			conn = getConnection();
			conn.createStatement().executeUpdate("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `data_id` = " + dataid);
		} catch (SQLException ex) {
			Util.severe("Unable to delete data entry from MySQL Server: " + ex);
		} finally {
			conn.close();
		}
	}
	
	/**
	 * Get a players name from the database player list
	 * @param id
	 * @return player name
	 */
	public static String getPlayer(int id) {
		for (Entry<String, Integer> entry : dbPlayers.entrySet())
			if (entry.getValue() == id)
				return entry.getKey();
		return null;
	}
	
	/**
	 * Get a world name from the database world list
	 * @param id
	 * @return world name
	 */
	public static String getWorld(int id) {
		for (Entry<String, Integer> entry : dbWorlds.entrySet())
			if (entry.getValue() == id)
				return entry.getKey();
		return null;
	}
	
	/**
	 * Performs a HawkEye tool search at the specified location
	 * @param player
	 * @param loc
	 */
	public static void toolSearch(Player player, Location loc) {
		SearchParser parser = new SearchParser(player);
		for (DataType type : DataType.values())
			if (type.canHere()) parser.actions.add(type);
		loc = Util.getSimpleLocation(loc);
		parser.loc = loc.toVector();
		parser.worlds = new String[]{ loc.getWorld().getName() };
		Thread thread = new SearchQuery(SearchType.SEARCH, parser, SearchDir.DESC);
		thread.start();
	}
	
	/**
	 * Returns a database connection from the pool
	 * @return {JDCConnection}
	 */
	public static JDCConnection getConnection() {
		try {
			return connections.getConnection();
		} catch (final SQLException ex) {
			Util.severe("Error whilst attempting to get connection: " + ex);
			return null;
		}
	}
	
	/**
	 * Creates a {@link DataEntry} from the inputted {ResultSet}
	 * @param res
	 * @return returns a {@link DataEntry}
	 * @throws SQLException
	 */
	public static DataEntry createEntryFromRes(ResultSet res) throws SQLException {
		DataEntry entry = new DataEntry();
		entry.setPlayer(DataManager.getPlayer(res.getInt("player_id")));
		entry.setDate(res.getString("date"));
		entry.setDataid(res.getInt("data_id"));
		entry.setType(DataType.fromId(res.getInt("action")));
		entry.setData(res.getString("data"));
		entry.setPlugin(res.getString("plugin"));
		entry.setWorld(DataManager.getWorld(res.getInt("world_id")));
		entry.setX(res.getInt("x"));
		entry.setY(res.getInt("y"));
		entry.setZ(res.getInt("z"));
		return entry;
	}
	
	/**
	 * Adds a player to the database
	 */
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
	
	/**
	 * Adds a world to the database
	 */
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
	
	/**
	 * Updates world and player local lists
	 * @return true on success, false on failure
	 */
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
	
	/**
	 * Checks that all tables are up to date and exist
	 * @return true on success, false on failure
	 */
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
			if (!JDBCUtil.tableExists(dbm, Config.DbHawkEyeTable)) {
				Util.info("Table `" + Config.DbHawkEyeTable + "` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbHawkEyeTable + "` (`data_id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `action` int(11) NOT NULL, `world_id` varchar(255) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `data` varchar(255) DEFAULT NULL, `plugin` varchar(255) DEFAULT 'HawkEye', PRIMARY KEY (`data_id`), KEY `player_action_world` (`player_id`,`action`,`world_id`), KEY `x_y_z` (`x`,`y`,`z` )) ENGINE=MyISAM;");
			}
			//Update to post v1.1.0 database
			else if (!JDBCUtil.columnExists(dbm, Config.DbHawkEyeTable, "player_id")) {
				Util.info("Pre-v1.1.0 database detected, performing legacy database update please wait...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `" + Config.DbHawkEyeTable + "2` (`data_id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `action` int(11) NOT NULL, `world_id` varchar(255) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `data` varchar(255) DEFAULT NULL, `plugin` varchar(255) DEFAULT 'HawkEye', PRIMARY KEY (`data_id`), KEY `player_action_world` (`player_id`,`action`,`world_id`), KEY `x_y_z` (`x`,`y`,`z` )) ENGINE=MyISAM;");
				stmnt.execute("INSERT INTO `" + Config.DbPlayerTable + "` (player) SELECT DISTINCT `player` FROM `" + Config.DbHawkEyeTable + "`");
				stmnt.execute("INSERT INTO `" + Config.DbWorldTable + "` (world) SELECT DISTINCT `world` FROM `" + Config.DbHawkEyeTable + "`");
				Util.info("Players and worlds imported into new structure. Importing main data please wait...");
				stmnt.execute("INSERT INTO `" + Config.DbHawkEyeTable + "2` (date, player_id, action, world_id, x, y, z, data, plugin) SELECT " + Config.DbHawkEyeTable + ".date, " + Config.DbPlayerTable + ".player_id, " + Config.DbHawkEyeTable + ".action, " + Config.DbWorldTable + ".world_id, " + Config.DbHawkEyeTable + ".x, " + Config.DbHawkEyeTable + ".y, " + Config.DbHawkEyeTable + ".z, " + Config.DbHawkEyeTable + ".data, " + Config.DbHawkEyeTable + ".plugin FROM `" + Config.DbHawkEyeTable + "`, `" + Config.DbPlayerTable + "`, `" + Config.DbWorldTable + "` WHERE " + Config.DbPlayerTable + ".player = " + Config.DbHawkEyeTable + ".player AND " + Config.DbWorldTable + ".world = " + Config.DbHawkEyeTable + ".world");
				Util.info("Import complete, cleaning up old table and renaming new...");
				stmnt.execute("DROP TABLE `" + Config.DbHawkEyeTable + "`;");
				stmnt.execute("RENAME TABLE `" + Config.DbHawkEyeTable + "2` TO `" + Config.DbHawkEyeTable + "`");
				Util.info("Legacy database update complete");
			}
		} catch (SQLException ex) {
			Util.severe("Error checking HawkEye tables: " + ex);
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
	
	/**
	 * Empty the {@link DataEntry} queue into the database
	 */
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
				
				if (entry.getDataid() > 0) {
					stmnt = conn.prepareStatement("INSERT into `" + Config.DbHawkEyeTable + "` (date, player_id, action, world_id, x, y, z, data, plugin, data_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
					stmnt.setInt(10, entry.getDataid());
				}
				else
					stmnt = conn.prepareStatement("INSERT into `" + Config.DbHawkEyeTable + "` (date, player_id, action, world_id, x, y, z, data, plugin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
				stmnt.setString(1, entry.getDate());
				stmnt.setInt(2, dbPlayers.get(entry.getPlayer()));
				stmnt.setInt(3, entry.getType().getId());
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

package uk.co.oliwali.DataLog.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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
		connections = new ConnectionManager(Config.url, Config.user, Config.password);
		getConnection().close();
		if (!checkTables())
			throw new Exception();
		if (!updateDbLists())
			throw new Exception();
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
		try {
			ResultSet res = getConnection().createStatement().executeQuery("SELECT * FROM `datalog` WHERE `dataid` = " + id);
			DataEntry entry = new DataEntry();
			entry.setPlayer(DataManager.getPlayer(res.getInt("player_id")));
			entry.setDate(res.getString("player"));
			entry.setDataid(res.getInt("dataid"));
			entry.setAction(res.getInt("action"));
			entry.setData(res.getString("data"));
			entry.setPlugin(res.getString("plugin"));
			entry.setWorld(DataManager.getWorld(res.getInt("world")));
			entry.setX(res.getInt("x"));
			entry.setY(res.getInt("y"));
			entry.setZ(res.getInt("z"));
			return entry;
		} catch (SQLException ex) {
			Util.severe("Unable to retrieve data entry from MySQL Server: " + ex);
		}
		return null;
	}
	
	public static String getPlayer(int id) {
		for (Entry<String, Integer> entry : dbPlayers.entrySet())
			if (entry.getValue().equals(id))
				return entry.getKey();
		return null;
	}
	
	public static String getWorld(int id) {
		for (Entry<String, Integer> entry : dbWorlds.entrySet())
			if (entry.getValue().equals(id))
				return entry.getKey();
		return null;
	}
	
	public static void toolSearch(Player player, Location loc) {
		List<Integer> actions = new ArrayList<Integer>();
		for (DataType type : DataType.values())
			if (type.canHere()) actions.add(type.getId());
		Thread thread = new SearchQuery(SearchType.SEARCH, player, null, null, null, actions, loc.toVector(), 0, null, null, "desc");
		thread.start();
	}
	
	public static JDCConnection getConnection() {
		try {
			return connections.getConnection();
		} catch (final SQLException ex) {
			Util.severe("Error whilst attempting to get connection");
			return null;
		}
	}
	
	private boolean addPlayer(String name) {
		try {
			JDCConnection conn = getConnection();
			conn.createStatement().execute("INSERT IGNORE INTO `dl_players` (player) VALUES ('" + name + "');");
			conn.commit();
		} catch (SQLException e) {
			Util.severe("Unable to add player to database");
			return false;
		}
		if (!updateDbLists())
			return false;
		return true;
	}
	
	private boolean addWorld(String name) {
		try {
			JDCConnection conn = getConnection();
			conn.createStatement().execute("INSERT IGNORE INTO `dl_worlds` (world) VALUES ('" + name + "');");
			conn.commit();
		} catch (SQLException e) {
			Util.severe("Unable to add world to database");
			return false;
		}
		if (!updateDbLists())
			return false;
		return true;
	}
	
	private boolean updateDbLists() {
		try {
			JDCConnection conn = getConnection();
			Statement stmnt = conn.createStatement();
			ResultSet res = stmnt.executeQuery("SELECT * FROM `dl_players`;");
			while (res.next())
				dbPlayers.put(res.getString(1), res.getInt(0));
			res = stmnt.executeQuery("SELECT * FROM `dl_worlds`;");
			while (res.next())
				dbWorlds.put(res.getString(1), res.getInt(0));
		} catch (SQLException e) {
			Util.severe("Unable to update local data lists from database");
			return false;
		}
		return true;
	}
	
	private boolean checkTables() {
		try {
			JDCConnection conn = getConnection();
			Statement stmnt = conn.createStatement();
			
			//Check if tables exist
			if (!stmnt.execute("SHOW TABLES LIKE 'dl_players'")) {
				Util.info("Table `dl_players` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `dl_players` (`player_id` int(11) NOT NULL AUTO_INCREMENT, `player` varchar(255) NOT NULL, PRIMARY KEY (`player_id`), KEY `player` (`player`) ) ENGINE=MyISAM;");
			}
			if (!stmnt.execute("SHOW TABLES LIKE 'dl_worlds'")) {
				Util.info("Table `dl_worlds` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `dl_worlds` (`world_id` int(11) NOT NULL AUTO_INCREMENT, `world` varchar(255) NOT NULL, PRIMARY KEY (`world_id`), KEY `world` (`world`) ) ENGINE=MyISAM;");
			}
			if (!stmnt.execute("SHOW TABLES LIKE 'datalog'")) {
				Util.info("Table `datalog` not found, creating...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `datalog` (`data_id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `action` int(11) NOT NULL, `world_id` varchar(255) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `data` varchar(255) DEFAULT NULL, `plugin` varchar(255) DEFAULT 'DataLog', PRIMARY KEY (`data_id`), KEY `player_action_world` (`player_id`,`action`,`world_id`) ) ENGINE=MyISAM;");
			}
			else if (!stmnt.execute("SELECT * FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '" + Config.database + "' AND TABLE_NAME = 'datalog' AND COLUMN_NAME = 'player_id'")) {
				Util.info("Pre-v1.1.0 database detected, performing legacy database update please wait...");
				stmnt.execute("CREATE TABLE IF NOT EXISTS `datalog2` (`data_id` int(11) NOT NULL AUTO_INCREMENT, `date` varchar(255) NOT NULL, `player_id` int(11) NOT NULL, `action` int(11) NOT NULL, `world_id` varchar(255) NOT NULL, `x` double NOT NULL, `y` double NOT NULL, `z` double NOT NULL, `data` varchar(255) DEFAULT NULL, `plugin` varchar(255) DEFAULT 'DataLog', PRIMARY KEY (`data_id`), KEY `player_action_world` (`player_id`,`action`,`world_id`) ) ENGINE=MyISAM;");
				stmnt.execute("INSERT INTO `dl_players` (player) SELECT DISTINCT `player` FROM `datalog`");
				stmnt.execute("INSERT INTO `dl_worlds` (world) SELECT DISTINCT `world` FROM `datalog`");
				Util.info("Players and worlds imported into new structure. Importing main data please wait...");
				stmnt.execute("INSERT INTO `datalog2` (date, player_id, action, world_id, x, y, z, data, plugin) SELECT datalog.date, dl_players.player_id, datalog.action, dl_worlds.world_id, datalog.x, datalog.y, datalog.z, datalog.data, datalog.plugin FROM `datalog`, `dl_players`, `dl_worlds` WHERE dl_players.player = datalog.player AND dl_worlds.world = datalog.world");
				Util.info("Import complete, cleaning up old table and renaming new...");
				stmnt.execute("DROP TABLE `datalog`;");
				stmnt.execute("RENAME TABLE `datalog2` TO `datalog`");
				Util.info("Legacy database update complete");
			}
			conn.commit();
		} catch (SQLException ex) {
			Util.severe("Error checking DataLog tables: " + ex);
			return false;
		}
		return true;
	}

	public void run() {
		if (queue.isEmpty())
			return;
		JDCConnection conn = getConnection();
		Statement stmnt = null;
		try {
			stmnt = conn.createStatement();
			int rows = 1;
			while (!queue.isEmpty()) {
				DataEntry entry = queue.poll();
				if (!dbPlayers.containsKey(entry.getPlayer()))
					if (!addPlayer(entry.getPlayer()))
						continue;
				if (!dbPlayers.containsKey(entry.getWorld()))
					if (!addWorld(entry.getWorld()))
						continue;
				stmnt.execute("INSERT into `datalog` (date, player_id, action, world_id, x, y, z, data, plugin) VALUES ('" + entry.getDate() + "', '" + dbPlayers.get(entry.getPlayer()) + "', '" + entry.getAction() + "', '" + dbWorlds.get(entry.getWorld()) + "', '" + entry.getX() + "', '" + entry.getY() + "', '" + entry.getZ() + "', '" + entry.getData() + "', '" + entry.getPlugin() + "');");
				if (rows % 50 == 0)
					conn.commit();
			}
			conn.commit();
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

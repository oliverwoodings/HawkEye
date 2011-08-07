package uk.co.oliwali.DataLog.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.config.Configuration;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;

/**
 * Configuration manager for DataLog.
 * Any field with the first letter capitalised is a config option
 * @author oliverw92
 */
public class Config {
	
	public static List<String> CommandFilter = new ArrayList<String>();
	public static List<Integer> BlockFilter = new ArrayList<Integer>();
	public static int MaxLines = 0;
	public static int MaxRadius;
	public static int ToolBlock;
	public static String CleanseAge;
	public static boolean Debug;
	public static boolean LogIpAddresses;
	public static boolean DeleteDataOnRollback;
	public static boolean LogDeathDrops;
	public static String DbUrl;
	public static String DbUser;
	public static String DbPassword;
	public static String DbDatabase;
	public static String DbDatalogTable;
	public static String DbPlayerTable;
	public static String DbWorldTable;
	public static int PoolSize;
	
	private static Configuration config;
	
	/**
	 * Loads the config from file and validates the data
	 * @param plugin
	 */
	public Config (DataLog plugin) {
		
		config = plugin.getConfiguration();
		List<String> keys = config.getKeys(null);
		
		//If there is no config file
		if (keys.size() ==  0) {
			Util.info("No config.yml detected, creating default file");
			keys = new ArrayList<String>();
		}
		else {

			//Version checks
			//v0.1 - remove everything
			if (keys.contains("driver")) {
				Util.info("DataLog v0.1 config detected, deleting unused config. MySQL details are now configured in bukkit.yml");
				for (String key : keys.toArray(new String[0])) {
					config.removeProperty(key);
				}
			}
			//pre v1.1 - warn about MySQL settings
			if (!keys.contains("mysql")) {
				Util.info("Updating config file to v1.1");
				Util.info("IMPORTANT: After server has rebooted, stop server and configure plugins/DataLog/config.yml with new info");
			}
			//pre v1.2 - move settings around
			if (!keys.contains("general")) {
				Util.info("Updating config file to v1.2");
				config.setProperty("general.max-lines", config.getInt("max-lines", 0));
				config.removeProperty("max-lines");
				config.setProperty("general.max-radius", config.getInt("max-radius", 0));
				config.removeProperty("max-radius");
				config.setProperty("general.tool-block", config.getInt("tool-block", 17));
				config.removeProperty("tool-block");
				config.setProperty("general.cleanse-age", config.getString("cleanse-age", "0d0h0s"));
				config.removeProperty("cleanse-age");
				config.setProperty("mysql.max-connections", config.getInt("max-connections", 10));
				config.removeProperty("max-connections");
				config.setProperty("general.debug", config.getBoolean("debug", false));
				config.removeProperty("debug");
			}
			
		}
		
		//Check DataType settings
		for (DataType type : DataType.values()) {
			if (config.getProperty(getNode(type)) == null)
				config.setProperty(getNode(type), true);
		}
		
		//Update version
		config.setProperty("version", plugin.version);

		//Load values
		CommandFilter = config.getStringList("command-filter", Arrays.asList(new String[]{"/login", "/restartsrv", "/register"}));
		BlockFilter = config.getIntList("block-filter", Arrays.asList(new Integer[]{97,98}));
		MaxLines = config.getInt("general.max-lines", 0);
		MaxRadius = config.getInt("general.max-radius", 0);
		ToolBlock = config.getInt("general.tool-block", 17);
		CleanseAge = config.getString("general.cleanse-age");
		Debug = config.getBoolean("general.debug", false);
		LogIpAddresses = config.getBoolean("general.log-ip-addresses", true);
		DeleteDataOnRollback = config.getBoolean("general.delete-data-on-rollback", false);
		LogDeathDrops = config.getBoolean("general.log-item-drops-on-death", false);
		DbUser = config.getString("mysql.username", "root");
		DbPassword = config.getString("mysql.password", "");
		DbUrl = "jdbc:mysql://" + config.getString("mysql.hostname", "root") + ":" + config.getInt("mysql.port", 3306) + "/" + config.getString("mysql.database", "minecraft");
		DbDatabase = config.getString("mysql.database", "minecraft");
		DbDatalogTable = config.getString("mysql.datalog-table", "datalog");
		DbPlayerTable = config.getString("mysql.player-table", "dl_players");
		DbWorldTable = config.getString("mysql.world-table", "dl_worlds");
		PoolSize = config.getInt("mysql.max-connections", 10);
		
		//Attempt a save
		if (!config.save())
			Util.severe("Error while writing to config.yml");
		
	}
	
	/**
	 * Check if a {@link DataType} is logged or not
	 * @param dataType
	 * @return true or false
	 */
	public static boolean isLogged(DataType dataType) {
		if (config.getBoolean(getNode(dataType), false) == true)
			return true;
		return false;
	}
	
	/**
	 * Returns a log node
	 * @param type
	 * @return string node
	 */
	private static String getNode(DataType type) {
		return "log." + type.getConfigName();
	}
}
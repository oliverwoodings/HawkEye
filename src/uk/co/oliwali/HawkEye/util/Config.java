package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.config.Configuration;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.DataType;

/**
 * Configuration manager for HawkEye.
 * Any field with the first letter capitalised is a config option
 * @author oliverw92
 */
public class Config {
	
	public static List<String> CommandFilter = new ArrayList<String>();
	public static List<Integer> BlockFilter = new ArrayList<Integer>();
	public static int MaxLines = 0;
	public static int MaxRadius;
	public static int DefaultHereRadius;
	public static String ToolBlock;
	public static String[] DefaultToolCommand;
	public static String CleanseAge;
	public static boolean CheckUpdates;
	public static boolean Debug;
	public static boolean LogIpAddresses;
	public static boolean DeleteDataOnRollback;
	public static boolean LogDeathDrops;
	public static String DbUrl;
	public static String DbUser;
	public static String DbPassword;
	public static String DbDatabase;
	public static String DbHawkEyeTable;
	public static String DbPlayerTable;
	public static String DbWorldTable;
	public static int PoolSize;
	
	private static Configuration config;
	
	/**
	 * Loads the config from file and validates the data
	 * @param plugin
	 */
	public Config(HawkEye plugin) {
		
		config = plugin.getConfiguration();
		List<String> keys = config.getKeys(null);
		
		//If there is no config file
		if (keys.size() ==  0) {
			Util.info("No config.yml detected, creating default file");
			keys = new ArrayList<String>();
		}
		else {

			//Version checks
			
		}
		
		//Check DataType settings
		for (DataType type : DataType.values()) {
			if (config.getProperty(getNode(type)) == null)
				config.setProperty(getNode(type), true);
		}
		
		//Check filters
		if (config.getProperty("command-filter") == null)
			config.setProperty("command-filter", Arrays.asList(new String[]{"/login", "/restartsrv", "/register"}));
		if (config.getProperty("block-filter") == null)
			config.setProperty("block-filter", Arrays.asList(new Integer[]{97,98}));
		
		//Update version
		config.setProperty("version", plugin.version);

		//Load values
		CommandFilter = config.getStringList("command-filter", Arrays.asList(new String[]{"/login", "/restartsrv", "/register"}));
		BlockFilter = config.getIntList("block-filter", Arrays.asList(new Integer[]{97,98}));
		MaxLines = config.getInt("general.max-lines", 0);
		MaxRadius = config.getInt("general.max-radius", 0);
		DefaultHereRadius = config.getInt("general.default-here-radius", 5);
		ToolBlock = config.getString("general.tool-block", "17");
		DefaultToolCommand = config.getString("general.default-tool-command", "").split(" ");
		CleanseAge = config.getString("general.cleanse-age", "0");
		CheckUpdates = config.getBoolean("general.check-for-updates", true);
		Debug = config.getBoolean("general.debug", false);
		LogIpAddresses = config.getBoolean("general.log-ip-addresses", true);
		DeleteDataOnRollback = config.getBoolean("general.delete-data-on-rollback", false);
		LogDeathDrops = config.getBoolean("general.log-item-drops-on-death", false);
		DbUser = config.getString("mysql.username", "root");
		DbPassword = config.getString("mysql.password", "");
		DbUrl = "jdbc:mysql://" + config.getString("mysql.hostname", "localhost") + ":" + config.getInt("mysql.port", 3306) + "/" + config.getString("mysql.database", "minecraft");
		DbDatabase = config.getString("mysql.database", "minecraft");
		DbHawkEyeTable = config.getString("mysql.hawkeye-table", "hawkeye");
		DbPlayerTable = config.getString("mysql.player-table", "hawk_players");
		DbWorldTable = config.getString("mysql.world-table", "hawk_worlds");
		PoolSize = config.getInt("mysql.max-connections", 10);
		
		//Attempt a save
		if (!config.save()) Util.severe("Error while writing to config.yml");
		
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
	
	/**
	 * Imports an old configuration file 
	 * @param oldConf DataLog config file to update from
	 */
	public static void importOldConfig(Configuration oldConf) {
		
		//Import type settings
		for (DataType type : DataType.values())
			config.setProperty(getNode(type), oldConf.getBoolean(getNode(type), true));
		
		//Import old settings
		config.setProperty("command-filter", oldConf.getStringList("command-filter", Arrays.asList(new String[]{"/login", "/restartsrv", "/register"})));
		config.setProperty("block-filter", oldConf.getIntList("block-filter", Arrays.asList(new Integer[]{97,98})));
		config.setProperty("general.max-lines", oldConf.getInt("general.max-lines", 0));
		config.setProperty("general.max-radius", oldConf.getInt("general.max-radius", 0));
		config.setProperty("general.tool-block", oldConf.getInt("general.tool-block", 17));
		config.setProperty("general.cleanse-age", oldConf.getString("general.cleanse-age", "0"));
		config.setProperty("general.debug", oldConf.getBoolean("general.debug", false));
		config.setProperty("general.log-ip-addresses", oldConf.getBoolean("general.log-ip-addresses", true));
		config.setProperty("general.delete-data-on-rollback", oldConf.getBoolean("general.delete-data-on-rollback", false));
		config.setProperty("general.log-item-drops-on-death", oldConf.getBoolean("general.log-item-drops-on-death", false));
		config.setProperty("mysql.username", oldConf.getString("mysql.username", "root"));
		config.setProperty("mysql.password", oldConf.getString("mysql.password", ""));
		config.setProperty("mysql.hostname", oldConf.getString("mysql.hostname", "localhost"));
		config.setProperty("mysql.port", oldConf.getInt("mysql.port", 3306));
		config.setProperty("mysql.database", oldConf.getString("mysql.database", "minecraft"));
		config.setProperty("mysql.hawkeye-table", oldConf.getString("mysql.datalog-table", "hawkeye"));
		config.setProperty("mysql.player-table", oldConf.getString("mysql.player-table", "dl_players"));
		config.setProperty("mysql.world-table", oldConf.getString("mysql.world-table", "dl_worlds"));
		config.setProperty("mysql.max-connections", oldConf.getInt("mysql.max-connections", 10));
		
		//Attempt a save
		if (!config.save()) Util.severe("Error while writing to config.yml");
		
	}
}

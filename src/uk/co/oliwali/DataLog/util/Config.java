package uk.co.oliwali.DataLog.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.util.config.Configuration;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.Rule;

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
	public static List<Rule> Rules = new ArrayList<Rule>();
	public static String DbUrl;
	public static String DbUser;
	public static String DbPassword;
	public static String DbDatabase;
	public static String DbDatalogTable;
	public static String DbPlayerTable;
	public static String DbWorldTable;
	public static int PoolSize;
	
	private Configuration config;
	
	/**
	 * Loads the config from file and validates the data
	 * @param plugin
	 */
	public Config (DataLog plugin) {
		
		config = plugin.getConfiguration();
		config.load();
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
			//pre v1.3 - add rules in
			if (!keys.contains("rules")) {
				Util.info("Updating config file to v1.3");
				config.setProperty("rules.fireblock.events", Arrays.asList(new String[]{"block-place"}));
				config.setProperty("rules.fireblock.pattern", "\\b51\\b");
				config.setProperty("rules.fireblock.worlds", Arrays.asList(new String[]{"pvp"}));
				config.setProperty("rules.fireblock.notify-message", "%PLAYER% placed illegal fire block on %WORLD%");
				config.setProperty("rules.fireblock.warn-message", "You are not allowed to place illegal fire blocks on %WORLD%!");
				config.setProperty("rules.fireblock.action.notify", true);
				config.setProperty("rules.fireblock.action.warn", true);
				config.setProperty("rules.fireblock.action.kick", true);
				config.setProperty("rules.fireblock.action.deny", true);
				config.setProperty("rules.fireblock.exclude-groups", Arrays.asList(new String[]{"admins"}));
			}
		}
		
		//Check filters
		if (!keys.contains("command-filter"))
			config.setProperty("command-filter", Arrays.asList(new String[]{"/login", "/restartsrv", "/register"}));
		if (!keys.contains("block-filter"))
			config.setProperty("block-filter", Arrays.asList(new Integer[]{33,34}));
		//Check general settings
		keys = config.getKeys("general");
		if (!keys.contains("max-lines"))
			config.setProperty("general.max-lines", 0);
		if (!keys.contains("max-radius"))
			config.setProperty("general.max-radius", 100);
		if (!keys.contains("tool-block"))
			config.setProperty("general.tool-block", 17);
		if (!keys.contains("cleanse-age"))
			config.setProperty("general.cleanse-age", "0d0h0s");
		if (!keys.contains("debug"))
			config.setProperty("general.debug", false);
		if (!keys.contains("log-ip-addresses"))
			config.setProperty("general.log-ip-addresses", true);
		if (!keys.contains("delete-data-on-rollback"))
			config.setProperty("general.delete-data-on-rollback", false);
		//Check MySQL settings
		keys = config.getKeys("mysql");
		if (keys == null)
			keys = new ArrayList<String>();
		if (!keys.contains("username"))
			config.setProperty("mysql.username", "root");
		if (!keys.contains("password"))
			config.setProperty("mysql.password", "");
		if (!keys.contains("hostname"))
			config.setProperty("mysql.hostname", "localhost");
		if (!keys.contains("database"))
			config.setProperty("mysql.database", "minecraft");
		if (!keys.contains("port"))
			config.setProperty("mysql.port", 3306);
		if (!keys.contains("datalog-table"))
			config.setProperty("mysql.datalog-table", "datalog");
		if (!keys.contains("player-table"))
			config.setProperty("mysql.player-table", "dl_players");
		if (!keys.contains("world-table"))
			config.setProperty("mysql.world-table", "dl_worlds");
		if (!keys.contains("max-connections"))
			config.setProperty("mysql.max-connections", 10);
		for (DataType type : DataType.values()) {
			if (config.getProperty(getNode(type)) == null)
				config.setProperty(getNode(type), true);
		}
		
		//Update version
		config.setProperty("version", plugin.version);
		
		//Attempt a save
		if (!config.save())
			Util.severe("Error while writing to config.yml");

		//Load values
		CommandFilter = config.getStringList("command-filter", null);
		BlockFilter = config.getIntList("block-filter", null);
		MaxLines = config.getInt("general.max-lines", 0);
		MaxRadius = config.getInt("general.max-radius", 0);
		ToolBlock = config.getInt("general.tool-block", 17);
		CleanseAge = config.getString("general.cleanse-age");
		Debug = config.getBoolean("general.debug", false);
		LogIpAddresses = config.getBoolean("general.log-ip-addresses", true);
		DeleteDataOnRollback = config.getBoolean("general.delete-data-on-rollback", false);
		DbUser = config.getString("mysql.username", "root");
		DbPassword = config.getString("mysql.password", "");
		DbUrl = "jdbc:mysql://" + config.getString("mysql.hostname") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database");
		DbDatabase = config.getString("mysql.database");
		DbDatalogTable = config.getString("mysql.datalog-table");
		DbPlayerTable = config.getString("mysql.player-table");
		DbWorldTable = config.getString("mysql.world-table");
		PoolSize = config.getInt("mysql.max-connections", 10);
		
		//Load rules
		keys = config.getKeys("rules");
		outer:
		for (String name : keys) {
			List<DataType> events = new ArrayList<DataType>();
			for (String event : config.getStringList("rules." + name + ".events", null)) {
				if (DataType.fromName(event) == null) {
					Util.severe("Invalid event name found in rule '" + name + "': " + event);
					continue outer;
				}
				events.add(DataType.fromName(event));
			}
			if (events.size() == 0) {
				Util.severe("No valid events supplied in rule '" + name + "'");
				continue outer;
			}
			Rule rule = new Rule(name, events, config.getString("rules." + name + ".pattern", ""), config.getStringList("rules." + name + ".worlds", null), config.getStringList("rules." + name + ".exclude-groups", null), config.getString("rules." + name + ".notify-message", ""), config.getString("rules." + name + ".warn-message", ""), config.getBoolean("rules." + name + ".action.notify", false), config.getBoolean("rules." + name + ".action.warn", false), config.getBoolean("rules." + name + ".action.kick", false), config.getBoolean("rules." + name + ".action.deny", false));
			Rules.add(rule);
		}
		Util.info(Rules.size() + " rule(s) out of " + keys.size() + " loaded from config file");
	}
	
	/**
	 * Check if a {@link DataType} is logged or not
	 * @param dataType
	 * @return true or false
	 */
	public boolean isLogged(DataType dataType) {
		if (config.getBoolean(getNode(dataType), false) == true)
			return true;
		return false;
	}
	
	/**
	 * Returns a log node
	 * @param type
	 * @return string node
	 */
	private String getNode(DataType type) {
		return "log." + type.getConfigName();
	}
}
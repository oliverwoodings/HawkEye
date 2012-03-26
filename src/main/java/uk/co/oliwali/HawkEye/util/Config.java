package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.Configuration;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;

/**
 * Configuration manager for HawkEye.
 * Any field with the first letter capitalised is a config option
 * @author oliverw92
 */
public class Config {

	public static List<String> CommandFilter = new ArrayList<String>();
	public static List<String> IgnoreWorlds = new ArrayList<String>();
	public static List<Integer> BlockFilter = new ArrayList<Integer>();
	public static int MaxLines = 0;
	public static int MaxRadius;
	public static int DefaultHereRadius;
	public static String ToolBlock;
	public static String[] DefaultToolCommand;
	public static String CleanseAge;
	public static String CleansePeriod;
	public static boolean GiveTool;
	public static boolean CheckUpdates;
	public static boolean Debug;
	public static Util.DebugLevel DebugLevel;
	public static boolean LogIpAddresses;
	public static boolean DeleteDataOnRollback;
	public static boolean LogDeathDrops;
	public static boolean OpPermissions;
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

		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		config.set("version", plugin.version);
        plugin.saveConfig();

		//Load values
		CommandFilter = config.getStringList("command-filter");
		BlockFilter = config.getIntegerList("block-filter");
		IgnoreWorlds = config.getStringList("ignore-worlds");
		MaxLines = config.getInt("general.max-lines");
		MaxRadius = config.getInt("general.max-radius");
		DefaultHereRadius = config.getInt("general.default-here-radius");
		ToolBlock = config.getString("general.tool-block");
		DefaultToolCommand = config.getString("general.default-tool-command").split(" ");
		CleanseAge = config.getString("general.cleanse-age");
		CleansePeriod = config.getString("general.cleanse-period");
		GiveTool = config.getBoolean("general.give-user-tool");
		CheckUpdates = config.getBoolean("general.check-for-updates");
		Debug = config.getBoolean("general.debug");
		LogIpAddresses = config.getBoolean("general.log-ip-addresses");
		DeleteDataOnRollback = config.getBoolean("general.delete-data-on-rollback");
		LogDeathDrops = config.getBoolean("general.log-item-drops-on-death");
		OpPermissions = config.getBoolean("general.op-permissions");
		DbUser = config.getString("mysql.username");
		DbPassword = config.getString("mysql.password");
		DbUrl = "jdbc:mysql://" + config.getString("mysql.hostname") + ":" + config.getInt("mysql.port") + "/" + config.getString("mysql.database");
		DbDatabase = config.getString("mysql.database");
		DbHawkEyeTable = config.getString("mysql.hawkeye-table");
		DbPlayerTable = config.getString("mysql.player-table");
		DbWorldTable = config.getString("mysql.world-table");
		PoolSize = config.getInt("mysql.max-connections");

		try {
			DebugLevel = Util.DebugLevel.valueOf(config.getString("general.debug-level").toUpperCase());
		} catch (Exception ex) {
			DebugLevel = Util.DebugLevel.NONE;
		}

	}

	/**
	 * Check if a {@link DataType} is logged or not
	 * @param dataType
	 * @return true or false
	 */
	public static boolean isLogged(DataType dataType) {
		if (config.getBoolean("log." + dataType.getConfigName(), false) == true)
			return true;
		return false;
	}

}

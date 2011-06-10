package uk.co.oliwali.DataLog.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.Configuration;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.database.DataType;

public class Config {
	
	public static List<String> commandFilter = new ArrayList<String>();
	public static int maxLines = 0;
	public static int maxRadius;
	public static int toolBlock;
	public static String url;
	public static String user;
	public static String password;
	public static String database;
	
	private Configuration config;
	
	public Config (DataLog plugin) {
		
		config = plugin.getConfiguration();
		config.load();
		List<String> keys = config.getKeys(null);
		
		//If there is no config file
		if (keys.size() == 0)
			Util.info("No config.yml detected, creating default file. Please make sure bukkit.yml is configured with your MySQL details");


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
		
		//Check if any keys are missing
		if (!keys.contains("max-lines"))
			config.setProperty("max-lines", 0);
		if (!keys.contains("max-radius"))
			config.setProperty("max-radius", 100);
		if (!keys.contains("tool-block"))
			config.setProperty("tool-block", 17);
		if (!keys.contains("command-filter")) {
			List<String> cmds = new ArrayList<String>();
			cmds.add("/login");
			cmds.add("/restartsrv");
			cmds.add("/register");
			config.setProperty("command-filter", cmds);
		}
		//Check MySQL settings
		keys = config.getKeys("mysql");
		if (!keys.contains("username"))
			config.setProperty("mysql.username", "root");
		if (!keys.contains("password"))
			config.setProperty("mysql.password", "");
		if (!keys.contains("hostname"))
			config.setProperty("mysql.hostname", "localhost");
		if (!keys.contains("database"))
			config.setProperty("mysql.database", "minecraft");
		if (!keys.contains("port"))
			config.setProperty("port", 3306);
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
		commandFilter = config.getStringList("command-filter", null);
		maxLines = config.getInt("max-lines", 0);
		maxRadius = config.getInt("max-radius", 0);
		toolBlock = config.getInt("tool-block", 17);
		user = config.getString("mysql.username", "root");
		password = config.getString("mysql.password", "");
		url = "jdbc:mysql://" + config.getString("mysql.hostname") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database");
		database = config.getString("mysql.database");
	}
	
	//Check if a type is logged or not
	public boolean isLogged(DataType dataType) {
		if (config.getBoolean(getNode(dataType), false) == true)
			return true;
		return false;
	}
	
	private String getNode(DataType type) {
		return "log." + type.getConfigName();
	}
}
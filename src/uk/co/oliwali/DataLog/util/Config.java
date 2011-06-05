package uk.co.oliwali.DataLog.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.Configuration;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;

public class Config {
	
	public static List<String> commandFilter = new ArrayList<String>();
	public static int maxLines = 0;
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
		
		//Check if any keys are missing
		if (!keys.contains("max-lines"))
			config.setProperty("max-lines", 0);
		if (!keys.contains("command-filter")) {
			List<String> cmds = new ArrayList<String>();
			cmds.add("/login");
			cmds.add("/restartsrv");
			cmds.add("/register");
			config.setProperty("command-filter", cmds);
		}
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
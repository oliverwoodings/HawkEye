package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.Configuration;

public class Config {
	
	public List<String> commandFilter = new ArrayList<String>();
	private Configuration config;
	
	public Config (DataLog plugin) {
		
		config = plugin.getConfiguration();
		config.load();
		List<String> keys = config.getKeys(null);
		
		//If there is no config file
		if (keys.size() == 0)
			Util.info("No config.yml detected, creating default file. Please make sure bukkit.yml is configured with your MySQL details");

		//If we are on the old config, remove everything and 
		if (keys.contains("driver")) {
			Util.info("DataLog v0.1 config detected, deleting unused config. MySQL details are now configured in bukkit.yml");
			for (String key : keys.toArray(new String[0])) {
				config.removeProperty(key);
			}
		}
		
		//Check if any keys are missing
		if (!keys.contains("version"))
			config.setProperty("version", plugin.version);
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
		
		//Attempt a save
		if (!config.save())
			Util.severe("Error while writing to config.yml");

		//Load command-filter
		commandFilter = config.getStringList("command-filter", null);

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
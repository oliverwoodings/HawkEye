package uk.co.oliwali.DataLog.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;

public class API {
	
	private DataLog datalog;

	public API(DataLog instance) {
		datalog = instance;
	}

	public boolean addEntry(JavaPlugin plugin, String type, Player player, Location loc, String data) {
		if (type.contains(" "))
			return false;
		datalog.addDataEntry(player, plugin, DataType.OTHER, loc, type + "-" + data);
		return true;
	}

}

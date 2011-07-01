package uk.co.oliwali.DataLog.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;

public class DataLogAPI {

	public static boolean addEntry(JavaPlugin plugin, String action, Player player, Location loc, String data) {
		if (DataManager.addEntry(player, plugin, DataType.OTHER, loc, action + "-" + data))
			return false;
		return true;
	}

}

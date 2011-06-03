package uk.co.oliwali.DataLog.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.DataType;

public class API {

	public boolean addEntry(JavaPlugin plugin, String action, Player player, Location loc, String data) {
		if (action.contains(" "))
			return false;
		DataManager.addEntry(player, plugin, DataType.OTHER, loc, action + "-" + data);
		return true;
	}

}

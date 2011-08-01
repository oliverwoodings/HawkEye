package uk.co.oliwali.DataLog.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;

/**
 * API for other plugins.
 * See the wiki for information on how to correctly access this API: https://github.com/oliverw92/DataLog/wiki/
 * @author oliverw92
 */
public class DataLogAPI {
	
	/**
	 * Add a custom entry to the DataLog database
	 * @param plugin instance of your plugin (i.e. just pass 'this' from your main class)
	 * @param action action that has been performed (e.g. Go Home)
	 * @param player player that performed the action
	 * @param loc location of the event
	 * @param data data relevant to the event
	 * @return true if accepted, false if not
	 */
	public static boolean addEntry(JavaPlugin plugin, String action, Player player, Location loc, String data) {
		DataManager.addEntry(player, plugin, DataType.OTHER, loc, action + "-" + data);
		return true;
	}

}

package uk.co.oliwali.HawkEye.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;

/**
 * API for other plugins.
 * See the wiki for information on how to correctly access this API: https://github.com/oliverw92/HawkEye/wiki/
 * @author oliverw92
 */
public class HawkEyeAPI {
	
	/**
	 * Add a custom entry to the HawkEye database
	 * @param plugin instance of your plugin (i.e. just pass 'this' from your main class)
	 * @param action action that has been performed (e.g. Go Home)
	 * @param player player that performed the action
	 * @param loc location of the event
	 * @param data data relevant to the event
	 */
	public static void addCustomEntry(JavaPlugin plugin, String action, Player player, Location loc, String data) {
		DataManager.addEntry(player, plugin, DataType.OTHER, loc, action + "-" + data);
	}
	
	/**
	 * Add a standard entry to the HawkEye database
	 * @param plugin instance of your plugin (i.e. just pass 'this' from your main class)
	 * @param type {@DataType} of the entry
	 * @param player player that performed the action
	 * @param loc location of the event
	 * @param data data relevant to the event
	 */
	public static void addEntry(JavaPlugin plugin, DataType type, Player player, Location loc, String data) {
		DataManager.addEntry(player, plugin, type, loc, data);
	}
	
	/**
	 * Performs a search of the HawkEye database
	 * @param callBack implementation of {@BaseCallback} for {@SearchQuery} to call
	 * @param parser instance of {@SearchParser} to retrieve search information
	 * @param dir direction to list results as specified in {@SearchDir}
	 */
	public static void performSearch(BaseCallback callBack, SearchParser parser, SearchDir dir) {
		new SearchQuery(callBack, parser, dir);
	}

}

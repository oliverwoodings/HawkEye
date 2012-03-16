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
import uk.co.oliwali.HawkEye.entry.DataEntry;

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
	public static boolean addCustomEntry(JavaPlugin plugin, String action, Player player, Location loc, String data) {
		return addCustomEntry(plugin, action, player.getName(), loc, data);
	}
	public static boolean addCustomEntry(JavaPlugin plugin, String action, String player, Location loc, String data) {
		if (plugin == null || action == null || player == null || loc == null || data == null) return false;
		DataEntry entry = new DataEntry(player, DataType.OTHER, loc, action + "-" + data);
		return addEntry(plugin, entry);
	}

	/**
	 * Add a standard entry to the HawkEye database
	 * @param plugin instance of your plugin (i.e. just pass 'this' from your main class)
	 * @param type {@DataType} of the entry
	 * @param player player that performed the action
	 * @param loc location of the event
	 * @param data data relevant to the event
	 * @return true if accepted, false if not
	 */
	public static boolean addEntry(JavaPlugin plugin, DataEntry entry) {

		if (entry.getClass() != entry.getType().getEntryClass()) return false;
		if (entry.getPlayer() == null) return false;

		entry.setPlugin(plugin.getDescription().getName());
		DataManager.addEntry(entry);
		return true;

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

package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import uk.co.oliwali.DataLog.util.Util;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlRow;

public class DataManager {
	
	private static EbeanServer db;
	private static DataLog plugin;
	private static HashMap<CommandSender, List<SqlRow>> searchResults;
	
	public DataManager(DataLog instance) {
		plugin = instance;
		db = plugin.getDatabase();
	}
	
	public static void addEntry(Player player, DataType dataType, Location loc, String data) {
		addEntry(player, plugin, dataType, loc, data);
	}
	public static void addEntry(Player player, JavaPlugin cplugin, DataType dataType, Location loc, String data) {
		if (plugin.config.isLogged(dataType)) {
			DataEntry dataEntry = new DataEntry();
			loc = Util.getSimpleLocation(loc);
			dataEntry.setInfo(player, cplugin, dataType.getId(), loc, data);
			db.save(dataEntry);
		}
	}
	
	public static List<SqlRow> search(CommandSender sender, String dateFrom, String dateTo, String[] players, String[] actions, Vector loc, Integer radius, String[] worlds, String[] filters) {
		String sql = "SELECT * FROM `datalog` WHERE ";
		List<String> args = new ArrayList<String>();
		if (dateFrom != null)
			args.add("`date` >= '" + dateFrom + "'");
		if (dateTo != null)
			args.add("`date` <= '" + dateTo + "'");
		if (players != null)
			args.add("LOWER(`player`) = (" + Util.join(Arrays.asList(players), " OR ") + ")");
		if (actions != null)
			args.add("`action` = (" + Util.join(Arrays.asList(actions), " OR ") + ")");
		if (loc != null) {
			int range = 5;
			if (radius != null)
				range = radius;
			args.add("(`x` BETWEEN " + (loc.getX() - range) + " AND " + (loc.getX() + range) + ")");
			args.add("(`y` BETWEEN " + (loc.getY() - range) + " AND " + (loc.getY() + range) + ")");
			args.add("(`z` BETWEEN " + (loc.getZ() - range) + " AND " + (loc.getZ() + range) + ")");
		}
		if (worlds != null)
			args.add("`world` = (" + Util.join(Arrays.asList(worlds), " OR ") + ")");
		if (filters != null) {
			for (int i = 0; i < filters.length; i++)
				filters[i] = "%" + filters[i] + "%";
			args.add("`data` LIKE (" + Util.join(Arrays.asList(filters), " OR ") + ")");
		}
		sql += Util.join(args, " AND ");
		Util.info("QUERY: " + sql);
		List<SqlRow> results = db.createSqlQuery(sql).findList();
		searchResults.put(sender, results);
		return null;
	}

}

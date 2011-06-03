package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
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
	private static HashMap<CommandSender, List<SqlRow>> searchResults = new HashMap<CommandSender, List<SqlRow>>();
	
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
	
	public static boolean search(CommandSender sender, String dateFrom, String dateTo, String[] players, Integer[] actions, Vector loc, Integer radius, String[] worlds, String[] filters) {
		String sql = "SELECT * FROM `datalog` WHERE ";
		List<String> args = new ArrayList<String>();
		if (dateFrom != null)
			args.add("`date` >= '" + dateFrom + "'");
		if (dateTo != null)
			args.add("`date` <= '" + dateTo + "'");
		if (players != null) {
			for (int i = 0; i < players.length; i++)
				players[i] = "'" + players[i].toLowerCase() + "'";
			args.add("LOWER(`player`) = (" + Util.join(Arrays.asList(players), " OR ") + ")");
		}
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
		if (worlds != null) {
			for (int i = 0; i < worlds.length; i++)
				worlds[i] = "'" + worlds[i].toLowerCase() + "'";
			args.add("LOWER(`world`) = (" + Util.join(Arrays.asList(worlds), " OR ") + ")");
		}
		if (filters != null) {
			for (int i = 0; i < filters.length; i++)
				filters[i] = "'%" + filters[i] + "%'";
			args.add("`data` LIKE (" + Util.join(Arrays.asList(filters), " OR ") + ")");
		}
		
		if (args.size() == 0)
			return false;
		sql += Util.join(args, " AND ");
		List<SqlRow> results = db.createSqlQuery(sql).findList();
		if (results == null || results.size() == 0)
			return false;
		searchResults.put(sender, results);
		displayPage(sender, 1);
		return true;
	}
	
	public static boolean displayPage(CommandSender sender, int page) {
		List<SqlRow> results = searchResults.get(sender);
		
		if (results == null || results.size() == 0)
			return false;
		int maxLines = 6;
		int maxPages = (int)Math.ceil((double)results.size() / 6);
		if (page > maxPages || page < 1)
			return false;
		
		Util.sendMessage(sender, "&8--------------------- &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8--------------------" + (maxPages < 9?"-":""));

		for (int i = (page-1) * maxLines; i < ((page-1) * maxLines) + maxLines; i++) {
			if (i == results.size())
				break;
			SqlRow row = results.get(i);
			String data = row.getString("data");
			if (row.getInteger("action") == 0 || row.getInteger("action") == 1)
				data = Material.getMaterial(row.getInteger("data")).name();
			sendLine(sender, "&7" + row.getString("date").substring(5) + " &c" + row.getString("player") + " &7" + DataType.fromId(row.getInteger("action")).getConfigName() + " &c" + row.getString("world") + ":" + row.getInteger("x") + "," + row.getInteger("y")+ "," + row.getInteger("z"));
			sendLine(sender, "   &7Data: &c" + data);
		}
		Util.sendMessage(sender, "&8-----------------------------------------------------");
		return true;
	}
	
	public static void sendLine(CommandSender sender, String line) {
		int len = 65;
		if (line.length() < len)
			Util.sendMessage(sender, "&8| " + line);
		else
			for (int i = 0; i < line.length(); i+=len)
				Util.sendMessage(sender, "&8| &c" + (i+len>line.length()?line.substring(i):line.substring(i, i+len)));
	}

}
package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import com.avaje.ebean.SqlRow;

import uk.co.oliwali.DataLog.util.Util;

public class SearchQuery implements Runnable {
	
	private String[] players;
	private Vector loc;
	private Integer radius;
	private Integer[] actions;
	private String[] worlds;
	private String dateFrom;
	private String dateTo;
	private String[] filters;
	private CommandSender sender;
	
	public SearchQuery(CommandSender sender, String dateFrom, String dateTo, String[] players, Integer[] actions, Vector loc, Integer radius, String[] worlds, String[] filters) {
		this.sender = sender;
		this.players = players;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.actions = actions;
		this.loc = loc;
		this.radius = radius;
		this.worlds = worlds;
		this.filters = filters;
	}
	
	public void run() {
		String sql = "SELECT * FROM `datalog` WHERE ";
		List<String> args = new ArrayList<String>();
		if (dateFrom != null)
			args.add("`date` >= '" + dateFrom + "'");
		if (dateTo != null)
			args.add("`date` <= '" + dateTo + "'");
		if (players != null) {
			for (int i = 0; i < players.length; i++)
				players[i] = "'" + players[i].toLowerCase() + "'";
			args.add("LOWER(`player`) LIKE (" + Util.join(Arrays.asList(players), " OR ") + ")");
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
			Util.sendMessage(sender, "&cInvalid search format!");
		sql += Util.join(args, " AND ");
		List<SqlRow> results = DataManager.db.createSqlQuery(sql).findList();
		if (results == null || results.size() == 0)
			Util.sendMessage(sender, "&cNo results found matching those criteria");
		DataManager.searchResults.put(sender, results);
		DataManager.displayPage(sender, 1);
	}
	
}
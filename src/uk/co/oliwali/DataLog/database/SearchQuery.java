package uk.co.oliwali.DataLog.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DisplayManager;
import uk.co.oliwali.DataLog.PlayerSession;
import uk.co.oliwali.DataLog.Rollback;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.database.DataType;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class SearchQuery extends Thread {
	
	private SearchType searchType;
	private String[] players;
	private Vector loc;
	private Integer radius;
	private List<Integer> actions;
	private String[] worlds;
	private String dateFrom;
	private String dateTo;
	private String[] filters;
	private CommandSender sender;
	private String order;
	
	public SearchQuery(SearchType searchType, CommandSender sender, String dateFrom, String dateTo, String[] players, List<Integer> actions, Vector loc, Integer radius, String[] worlds, String[] filters, String order) {
		this.searchType = searchType;
		this.sender = sender;
		this.players = players;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.actions = actions;
		this.loc = loc;
		this.radius = radius;
		this.worlds = worlds;
		this.filters = filters;
		this.order = order;
	}
	
	public void run() {
		String sql = "SELECT * FROM `datalog` WHERE ";
		List<String> args = new ArrayList<String>();
		if (players != null) {
			for (int i = 0; i < players.length; i++)
				players[i] = "'" + players[i].toLowerCase() + "'";
			args.add("LOWER(`player`) IN (" + Util.join(Arrays.asList(players), ",") + ")");
		}
		if (worlds != null) {
			for (int i = 0; i < worlds.length; i++)
				worlds[i] = "'" + worlds[i].toLowerCase() + "'";
			args.add("LOWER(`world`) IN (" + Util.join(Arrays.asList(worlds), ",") + ")");
		}
		if (actions == null || actions.size() == 0) {
			actions = new ArrayList<Integer>();
			for (DataType type : DataType.values())
				actions.add(type.getId());
		}
		
		List<Integer> acs = new ArrayList<Integer>();
		for (int act : actions.toArray(new Integer[actions.size()]))
			if (Permission.searchType(sender, DataType.fromId(act).getConfigName()))
				acs.add(act);
		args.add("`action` IN (" + Util.join(acs, ",") + ")");
		
		if (dateFrom != null)
			args.add("`date` >= '" + dateFrom + "'");
		if (dateTo != null)
			args.add("`date` <= '" + dateTo + "'");
		
		if (loc != null) {
			if (radius == 0) {
				args.add("`x` = " + loc.getX());
				args.add("`y` = " + loc.getY());
				args.add("`z` = " + loc.getZ());
			}
			else {
				int range = 5;
				if (radius != null)
					range = radius;
				args.add("(`x` BETWEEN " + (loc.getX() - range) + " AND " + (loc.getX() + range) + ")");
				args.add("(`y` BETWEEN " + (loc.getY() - range) + " AND " + (loc.getY() + range) + ")");
				args.add("(`z` BETWEEN " + (loc.getZ() - range) + " AND " + (loc.getZ() + range) + ")");
			}
		}
		if (filters != null) {
			for (int i = 0; i < filters.length; i++)
				filters[i] = "'%" + filters[i] + "%'";
			args.add("`data` LIKE " + Util.join(Arrays.asList(filters), " OR `data` LIKE "));
		}
		
		sql += Util.join(args, " AND ");
		if (order != null) {
			sql += " ORDER BY `dataid` ";
			if (order.equalsIgnoreCase("desc"))
				sql += "DESC";
			if (order.equalsIgnoreCase("asc"))
				sql += "DESC";
		}
		if (Config.maxLines > 0)
			sql += " LIMIT " + Config.maxLines;
		
		ResultSet res;
		PlayerSession session = DataLog.playerSessions.get(sender);
		Util.sendMessage(session.getSender(), "&cSearching for matching logs...");
		List<DataEntry> results = new ArrayList<DataEntry>();
		try {
			res = DataManager.getConnection().createStatement().executeQuery(sql);
			while (res.next()) {
				DataEntry entry = new DataEntry();
				entry.setPlayer(DataManager.getPlayer(res.getInt("player_id")));
				entry.setDate(res.getString("player"));
				entry.setDataid(res.getInt("dataid"));
				entry.setAction(res.getInt("action"));
				entry.setData(res.getString("data"));
				entry.setPlugin(res.getString("plugin"));
				entry.setWorld(DataManager.getWorld(res.getInt("world")));
				entry.setX(res.getInt("x"));
				entry.setY(res.getInt("y"));
				entry.setZ(res.getInt("z"));
				results.add(entry);
			}
		} catch (SQLException ex) {
			Util.severe("Error executing MySQL query: " + ex);
			Util.sendMessage(sender, "&cError executing MySQL query, search aborted");
			return;
		}
		
		switch (searchType) {
			case ROLLBACK:
				session.setRollbackResults(results);
				Rollback.rollback(session);
				break;
			case SEARCH:
				session.setSearchResults(results);
				DisplayManager.displayPage(session, 1);
				break;
		}
		
	}
	
	public enum SearchType {
		ROLLBACK,
		SEARCH
	}
	
}
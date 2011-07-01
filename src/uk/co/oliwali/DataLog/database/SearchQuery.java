package uk.co.oliwali.DataLog.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.DisplayManager;
import uk.co.oliwali.DataLog.PlayerSession;
import uk.co.oliwali.DataLog.Rollback;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Threadable class for performing a search query
 * Used for in-game searches and rollbacks
 * @author oliverw92
 */
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
	
	/**
	 * Run the search query
	 */
	public void run() {
		
		Util.debug("Beginning search query");
		String sql = "SELECT * FROM `" + Config.DbDatalogTable + "` WHERE ";
		List<String> args = new ArrayList<String>();
		
		//Match players from database list
		Util.debug("Building players");
		if (players != null) {
			List<Integer> pids = new ArrayList<Integer>();
			for (String player : players) {
				for (Map.Entry<String, Integer> entry : DataManager.dbPlayers.entrySet()) {
					if (entry.getKey().toLowerCase().contains(player.toLowerCase()))
							pids.add(entry.getValue());
				}
			}
			if (pids.size() > 0)
				args.add("player_id IN (" + Util.join(pids, ",") + ")");
			else {
				Util.sendMessage(sender, "&cNo players found matching your specifications");
				return;
			}
		}
		
		//Match worlds from database list
		Util.debug("Building worlds");
		if (worlds != null) {
			List<Integer> wids = new ArrayList<Integer>();
			for (String world : worlds) {
				for (Map.Entry<String, Integer> entry : DataManager.dbWorlds.entrySet()) {
					if (entry.getKey().toLowerCase().contains(world.toLowerCase()))
							wids.add(entry.getValue());
				}
			}
			if (wids.size() > 0)
				args.add("world_id IN (" + Util.join(wids, ",") + ")");
			else {
				Util.sendMessage(sender, "&cNo worlds found matching your specifications");
				return;
			}
		}
		
		//Compile actions into SQL form
		Util.debug("Building actions");
		if (actions == null || actions.size() == 0) {
			actions = new ArrayList<Integer>();
			for (DataType type : DataType.values())
				actions.add(type.getId());
		}
		List<Integer> acs = new ArrayList<Integer>();
		for (int act : actions.toArray(new Integer[actions.size()]))
			if (Permission.searchType(sender, DataType.fromId(act).getConfigName()))
				acs.add(act);
		args.add("action IN (" + Util.join(acs, ",") + ")");
		
		//Add dates
		Util.debug("Building dates");
		if (dateFrom != null)
			args.add("date >= '" + dateFrom + "'");
		if (dateTo != null)
			args.add("date <= '" + dateTo + "'");
		
		//Check if location is exact or a range
		Util.debug("Building location");
		if (loc != null) {
			if (radius == null || radius == 0) {
				args.add("x = " + loc.getX());
				args.add("y = " + loc.getY());
				args.add("z = " + loc.getZ());
			}
			else {
				int range = 5;
				if (radius != null)
					range = radius;
				args.add("(x BETWEEN " + (loc.getX() - range) + " AND " + (loc.getX() + range) + ")");
				args.add("(y BETWEEN " + (loc.getY() - range) + " AND " + (loc.getY() + range) + ")");
				args.add("(z BETWEEN " + (loc.getZ() - range) + " AND " + (loc.getZ() + range) + ")");
			}
		}
		
		//Build the filters into SQL form
		Util.debug("Building filters");
		if (filters != null) {
			for (int i = 0; i < filters.length; i++)
				filters[i] = "'%" + filters[i] + "%'";
			args.add("data LIKE " + Util.join(Arrays.asList(filters), " OR datalog.data LIKE "));
		}
		
		//Check the limits
		Util.debug("Building limits");
		sql += Util.join(args, " AND ");
		if (Config.MaxLines > 0)
			sql += " LIMIT " + Config.MaxLines;
		
		Util.debug("Searching: " + sql);
		
		//Set up some stuff for the search
		ResultSet res;
		PlayerSession session = DataLog.playerSessions.get(sender);
		Util.sendMessage(session.getSender(), "&cSearching for matching logs...");
		List<DataEntry> results = new ArrayList<DataEntry>();
		JDCConnection conn = DataManager.getConnection();
		Statement stmnt = null;
		
		try {
			//Execute query
			stmnt = conn.createStatement();
			res = stmnt.executeQuery(sql);
			Util.debug("Getting results");
			//Retrieve results in specified order
			if (order == "desc") {
				res.afterLast();
				while (res.previous())
					results.add(DataManager.createEntryFromRes(res));
			}
			else {
				while (res.next())
					results.add(DataManager.createEntryFromRes(res));
			}
		} catch (SQLException ex) {
			Util.severe("Error executing MySQL query: " + ex);
			Util.sendMessage(sender, "&cError executing MySQL query, search aborted");
			return;
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
			}
				
		}
		
		//Perform actions dependent on the type of search
		switch (searchType) {
			case ROLLBACK:
				session.setRollbackResults(results);
				Thread thread = new Thread(new Rollback(session));
				thread.start();
				break;
			case SEARCH:
				session.setSearchResults(results);
				DisplayManager.displayPage(session, 1);
				break;
		}
		Util.debug("Search complete");
		
	}
	
	/**
	 * Enumeration for the different types of searching
	 * @author oliverw92
	 */
	public enum SearchType {
		ROLLBACK,
		SEARCH
	}
	
}
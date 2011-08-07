package uk.co.oliwali.DataLog.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.DisplayManager;
import uk.co.oliwali.DataLog.PlayerSession;
import uk.co.oliwali.DataLog.Rollback;
import uk.co.oliwali.DataLog.SearchParser;
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
	
	private SearchParser parser;
	private CommandSender sender;
	private SearchDir dir;
	private SearchType searchType;
	
	public SearchQuery(SearchType type, SearchParser parser, SearchDir dire) {
		sender = parser.player;
		dir = dire;
		searchType = type;
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
		if (parser.players != null) {
			List<Integer> pids = new ArrayList<Integer>();
			List<Integer> npids = new ArrayList<Integer>();
			for (String player : parser.players) {
				for (Map.Entry<String, Integer> entry : DataManager.dbPlayers.entrySet()) {
					if (entry.getKey().toLowerCase().contains(player.toLowerCase()))
							pids.add(entry.getValue());
					else if (entry.getKey().toLowerCase().contains(player.replace("!", "").toLowerCase()))
						npids.add(entry.getValue());
				}
			}
			//Include players
			if (pids.size() > 0)
				args.add("player_id IN (" + Util.join(pids, ",") + ")");
			//Exclude players
			if (npids.size() > 0)
				args.add("player_id NOT IN (" + Util.join(npids, ",") + ")");
			else {
				Util.sendMessage(sender, "&cNo players found matching your specifications");
				return;
			}
		}
		
		//Match worlds from database list
		Util.debug("Building worlds");
		if (parser.worlds != null) {
			List<Integer> wids = new ArrayList<Integer>();
			List<Integer> nwids = new ArrayList<Integer>();
			for (String world : parser.worlds) {
				for (Map.Entry<String, Integer> entry : DataManager.dbWorlds.entrySet()) {
					if (entry.getKey().toLowerCase().contains(world.toLowerCase()))
						wids.add(entry.getValue());
					else if (entry.getKey().toLowerCase().contains(world.replace("!", "").toLowerCase()))
						nwids.add(entry.getValue());
				}
			}
			//Include worlds
			if (wids.size() > 0)
				args.add("world_id IN (" + Util.join(wids, ",") + ")");
			//Exclude worlds
			if (nwids.size() > 0)
				args.add("world_id NOT IN (" + Util.join(nwids, ",") + ")");
			else {
				Util.sendMessage(sender, "&cNo worlds found matching your specifications");
				return;
			}
		}
		
		//Compile actions into SQL form
		Util.debug("Building actions");
		if (parser.actions == null || parser.actions.size() == 0) {
			parser.actions = new ArrayList<DataType>();
			for (DataType type : DataType.values())
				parser.actions.add(type);
		}
		List<Integer> acs = new ArrayList<Integer>();
		for (DataType act : parser.actions)
			if (Permission.searchType(sender, act.getConfigName()))
				acs.add(act.getId());
		args.add("action IN (" + Util.join(acs, ",") + ")");
		
		//Add dates
		Util.debug("Building dates");
		if (parser.dateFrom != null)
			args.add("date >= '" + parser.dateFrom + "'");
		if (parser.dateTo != null)
			args.add("date <= '" + parser.dateTo + "'");
		
		//Check if location is exact or a range
		Util.debug("Building location");
		if (parser.minLoc != null) {
			args.add("(x BETWEEN " + parser.minLoc.getX() + " AND " + parser.maxLoc.getX() + ")");
			args.add("(y BETWEEN " + parser.minLoc.getY() + " AND " + parser.maxLoc.getY() + ")");
			args.add("(z BETWEEN " + parser.minLoc.getZ() + " AND " + parser.maxLoc.getZ() + ")");
		}
		else if (parser.loc != null) {
			args.add("x = " + parser.loc.getX());
			args.add("y = " + parser.loc.getY());
			args.add("z = " + parser.loc.getZ());
		}
		
		//Build the filters into SQL form
		Util.debug("Building filters");
		if (parser.filters != null) {
			for (int i = 0; i < parser.filters.length; i++)
				parser.filters[i] = "'%" + parser.filters[i] + "%'";
			args.add("data LIKE " + Util.join(Arrays.asList(parser.filters), " OR datalog.data LIKE "));
		}
		
		//Check the limits
		Util.debug("Building limits");
		sql += Util.join(args, " AND ");
		if (Config.MaxLines > 0)
			sql += " LIMIT " + Config.MaxLines;
		
		Util.debug("Searching: " + sql);
		
		//Set up some stuff for the search
		ResultSet res;
		PlayerSession session = DataLog.getSession(sender);
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
			if (dir == SearchDir.DESC) {
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
				new Rollback(session);
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
	
	/**
	 * Enumeration for result sorting directions
	 * @author oliverw92
	 */
	public enum SearchDir {
		ASC,
		DESC
	}
	
}
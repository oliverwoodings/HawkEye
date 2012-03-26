package uk.co.oliwali.HawkEye.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.BaseCallback;
import uk.co.oliwali.HawkEye.callbacks.DeleteCallback;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Threadable class for performing a search query
 * Used for in-game searches and rollbacks
 * @author oliverw92
 */
public class SearchQuery extends Thread {

	private final SearchParser parser;
	private final SearchDir dir;
	private final BaseCallback callBack;
	private final boolean delete;

	public SearchQuery(BaseCallback callBack, SearchParser parser, SearchDir dir) {
		this.callBack = callBack;
		this.parser = parser;
		this.dir = dir;
		this.delete = (callBack instanceof DeleteCallback);

		//Start thread
		this.start();
	}

	/**
	 * Run the search query
	 */
	@Override
	public void run() {

		Util.debug("Beginning search query");
		String sql;

		if (delete)
			sql = "DELETE FROM ";
		else
			sql = "SELECT * FROM ";

		sql += "`" + Config.DbHawkEyeTable + "` WHERE ";
		List<String> args = new LinkedList<String>();
		List<Object> binds = new LinkedList<Object>();

		//Match players from database list
		Util.debug("Building players");
		if (parser.players.size() >= 1) {
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
			if (npids.size() + pids.size() < 1) {
				callBack.error(SearchError.NO_PLAYERS, "No players found matching your specifications");
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
			if (nwids.size() + wids.size() < 1) {
				callBack.error(SearchError.NO_WORLDS, "No worlds found matching your specifications");
				return;
			}
		}

		//Compile actions into SQL form
		Util.debug("Building actions");
		if (parser.actions != null && parser.actions.size() > 0) {
			List<Integer> acs = new ArrayList<Integer>();
			for (DataType act : parser.actions)
				acs.add(act.getId());
					args.add("action IN (" + Util.join(acs, ",") + ")");
		}

		//Add dates
		Util.debug("Building dates");
		if (parser.dateFrom != null) {
			args.add("date >= ?");
			binds.add(parser.dateFrom);
		}
		if (parser.dateTo != null) {
			args.add("date <= ?");
			binds.add(parser.dateTo);
		}

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
			for (String filter : parser.filters) {
				args.add("data LIKE ?");
				binds.add("%" + filter + "%");
			}
		}

		//Build WHERE clause
		sql += Util.join(args, " AND ");

		//Add order by
		Util.debug("Ordering by data_id");
		sql += " ORDER BY `data_id` DESC";

		//Check the limits
		Util.debug("Building limits");
		if (Config.MaxLines > 0)
			sql += " LIMIT " + Config.MaxLines;

		//Util.debug("Searching: " + sql);

		//Set up some stuff for the search
		ResultSet res;
		List<DataEntry> results = new ArrayList<DataEntry>();
		JDCConnection conn = DataManager.getConnection();
		PreparedStatement stmnt = null;
		int deleted = 0;

		try {

			//Execute query
			stmnt = conn.prepareStatement(sql);

			Util.debug("Preparing statement");
			for (int i = 0; i < binds.size(); i++)
				stmnt.setObject(i + 1, binds.get(i));

			Util.debug("Searching: " + stmnt.toString());

			if (delete) {
				Util.debug("Deleting entries");
				deleted = stmnt.executeUpdate();
			} else {
				res = stmnt.executeQuery();

				Util.debug("Getting results");

				//Retrieve results
				while (res.next())
					results.add(DataManager.createEntryFromRes(res));

				//If ascending, reverse results
				if (dir == SearchDir.ASC)
					Collections.reverse(results);
			}
		} catch (Exception ex) {
			Util.severe("Error executing MySQL query: " + ex);
			ex.printStackTrace();
			callBack.error(SearchError.MYSQL_ERROR, "Error executing MySQL query: " + ex);
			return;
		} finally {
			try {
				if (stmnt != null)
					stmnt.close();
				conn.close();
			} catch (SQLException ex) {
				Util.severe("Unable to close SQL connection: " + ex);
				callBack.error(SearchError.MYSQL_ERROR, "Unable to close SQL connection: " + ex);
			}

		}

		Util.debug(results.size() + " results found");

		//Run callback
		if (delete)
			((DeleteCallback) callBack).deleted = deleted;
		else
			callBack.results = results;

		callBack.execute();

		Util.debug("Search complete");

	}

	/**
	 * Enumeration for result sorting directions
	 * @author oliverw92
	 */
	public enum SearchDir {
		ASC,
		DESC
	}

	/**
	 * Enumeration for query errors
	 */
	public enum SearchError {
		NO_PLAYERS,
		NO_WORLDS,
		MYSQL_ERROR
	}

}

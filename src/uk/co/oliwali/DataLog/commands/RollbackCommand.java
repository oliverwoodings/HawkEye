package uk.co.oliwali.DataLog.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bukkit.util.Vector;

import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchType;
import uk.co.oliwali.DataLog.database.SearchQuery;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Rolls back actions according to the player's specified input.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class RollbackCommand extends BaseCommand {

	public RollbackCommand() {
		name = "rollback";
		argLength = 1;
		bePlayer = true;
		usage = "<parameters> <- rollback actions. Type &c/dl rollbackhelp&7 for more info";
	}
	
	public boolean execute() {
		
		//Set up placeholders
		String[] players = null;
		Vector loc = null;
		Integer radius = null;
		List<Integer> actions = new ArrayList<Integer>();
		String[] worlds = null;
		String dateFrom = null;
		String dateTo = null;
		String[] filters = null;
		
		try {
			//Loop through all the arguments
			for (String arg : args) {
				
				//Check if argument has a valid prefix
				String param = arg.substring(0,1).toLowerCase();
				if (!arg.substring(1,2).equals(":"))
					throw new Exception();
				String[] values = arg.substring(2).split(",");
				
				//Check different parameters
				if (param.equals("p")) players = values;
				else if (param.equals("w")) worlds = values;
				else if (param.equals("f")) filters = values;
				else if (param.equals("a")) {
					for (String value : values) {
						DataType type = DataType.fromName(value);
						if (type == null || !type.canRollback())
							throw new Exception();
						actions.add(DataType.fromName(value).getId());
					}
				}
				else if (param.equals("r"))
					radius = Integer.parseInt(values[0]);
				else if (param.equals("t")) {
					//Handler for different time formats
					int type = 2;
					for (int i = 0; i < arg.length(); i++) {
						String c = arg.substring(i, i+1);
						if (!Util.isInteger(c)) {
							if (c.equals("w") || c.equals("d") || c.equals("m") || c .equals("s") || c.equals("h"))
								type = 0;
							if (c.equals("-") || c.equals(":"))
								type = 1;
						}
					}
					
					//If the time is in the format '0w0d0h0m0s'
					if (type == 0) {
						
						int weeks = 0;
						int days = 0;
						int hours = 0;
						int mins = 0;
						int secs = 0;
						
						String nums = "";
						for (int i = 0; i < values[0].length(); i++) {
							String c = values[0].substring(i, i+1);
							if (Util.isInteger(c)) {
								nums += c;
								continue;
							}
							int num = Integer.parseInt(nums);
							if (c.equals("w")) weeks = num;
							else if (c.equals("d")) days = num;
							else if (c.equals("h")) hours = num;
							else if (c.equals("m")) mins = num;
							else if (c.equals("s")) secs = num;
							else throw new Exception();
							nums = "";
						}
						
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.WEEK_OF_YEAR, -1 * weeks);
						cal.add(Calendar.DAY_OF_MONTH, -1 * days);
						cal.add(Calendar.HOUR, -1 * hours);
						cal.add(Calendar.MINUTE, -1 * mins);
						cal.add(Calendar.SECOND, -1 * secs);
						SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateFrom = form.format(cal.getTime());
						
					}
					//If the time is in the format 'yyyy-MM-dd HH:mm:ss'
					else if (type == 1) {
						if (values.length == 1) {
							SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
							dateFrom = form.format(Calendar.getInstance().getTime()) + " " + values[0];
						}
						if (values.length >= 2)
							dateFrom = values[0] + " " + values[1];
						if (values.length == 4)
							dateTo = values[2] + " " + values[3];
					}
					//Invalid time format
					else if (type == 2)
						throw new Exception();
					
				}
				else throw new Exception();
				
			}
		} catch (Throwable t) {
			Util.sendMessage(sender, "&cInvalid rollback parameters/format!");
			return true;
		}
		
		if (worlds == null) {
			worlds = new String[1];
			worlds[0] = player.getWorld().getName();
		}
		loc = Util.getSimpleLocation(player.getLocation()).toVector();
		//If no action is specified, add all actions that can be rolled back
		if (actions.size() == 0) {
			for (DataType type : DataType.values())
				if (type.canRollback())
					actions.add(type.getId());
		}
		
		//Create new SeachQuery with data
		Thread thread = new SearchQuery(SearchType.ROLLBACK, sender, dateFrom, dateTo, players, actions, loc, radius, worlds, filters, "desc");
		thread.start();
		return true;
	}
	
	public boolean permission() {
		return Permission.rollback(sender);
	}

}
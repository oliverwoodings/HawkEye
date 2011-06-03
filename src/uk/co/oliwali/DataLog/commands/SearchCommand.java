package uk.co.oliwali.DataLog.commands;

import org.bukkit.util.Vector;

import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class SearchCommand extends BaseCommand {

	public SearchCommand() {
		name = "search";
		argLength = -1;
		usage = "p:player1,player2 l:x,y,z r:50 a:chat,break w:world1,world2 t:yy-mm-dd,hh:mm:ss,yy-mm-dd,hh:mm:ss f:hack <- search the DataLog database";
	}
	
	public boolean execute() {
		
		String[] players = null;
		Vector loc = null;
		Integer radius = null;
		Integer[] actions = null;
		String[] worlds = null;
		String dateFrom = null;
		String dateTo = null;
		String[] filters = null;
		
		try {
			for (String arg : args) {
				
				String param = arg.substring(0,1).toLowerCase();
				if (!arg.substring(1,2).equals(":"))
					throw new Exception();
				String[] values = arg.substring(2).split(",");
				
				if (param.equals("p")) players = values;
				if (param.equals("w")) worlds = values;
				if (param.equals("f")) filters = values;
				if (param.equals("a")) {
					Integer[] ints = new Integer[values.length];
					for (int i = 0; i < values.length; i++)
						ints[i] = DataType.fromName(values[i]).getId();
					actions = ints;
				}
				if (param.equals("l")) {
					loc = new Vector();
					loc.setX(Integer.parseInt(values[0]));
					loc.setY(Integer.parseInt(values[1]));
					loc.setZ(Integer.parseInt(values[2]));
				}
				if (param.equals("r"))
					radius = Integer.parseInt(values[0]);
				if (param.equals("t")) {
					
				}
				
			}
		} catch (Throwable t) {
			Util.sendMessage(sender, "&cInvalid search format!");
			return true;
		}
		
		if (!DataManager.search(sender, dateFrom, dateTo, players, actions, loc, radius, worlds, filters))
			Util.sendMessage(sender, "&cNo results found matching those criteria");
		
		return true;
	}
	
	public boolean permission() {
		return Permission.search(sender);
	}

}
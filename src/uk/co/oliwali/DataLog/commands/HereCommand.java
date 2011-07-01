package uk.co.oliwali.DataLog.commands;

import java.util.ArrayList;
import java.util.List;

import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchType;
import uk.co.oliwali.DataLog.database.SearchQuery;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Searches around the player for 'here' {@link DataType}s
 * @author oliverw92
 */
public class HereCommand extends BaseCommand {

	public HereCommand() {
		name = "here";
		argLength = 0;
		bePlayer = true;
		usage = "[radius] [player] <- search around you for data";
	}
	
	public boolean execute() {
		try {
			int integer = Integer.parseInt(args.get(0));
			if (integer > Config.MaxRadius || integer < 0)
				throw new Exception();
			List<Integer> actions = new ArrayList<Integer>();
			for (DataType type : DataType.values())
				if (type.canHere()) actions.add(type.getId());
			String[] players = null;
			if (args.size() > 1)
				players = args.get(1).split(",");
			Thread thread = new SearchQuery(SearchType.SEARCH, sender, null, null, players, actions, player.getLocation().toVector(), Integer.parseInt(args.get(0)), player.getWorld().getName().split(","), null, "desc");
			thread.start();
		} catch (Throwable t) {
			Util.sendMessage(sender, "&cInvalid radius!");
		}
		return true;
	}

	public boolean permission() {
		return Permission.search(sender);
	}
	
}
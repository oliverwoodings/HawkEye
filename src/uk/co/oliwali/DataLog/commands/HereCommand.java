package uk.co.oliwali.DataLog.commands;

import java.util.ArrayList;
import java.util.List;

import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.SearchQuery.SearchType;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class HereCommand extends BaseCommand {

	public HereCommand() {
		name = "here";
		argLength = 1;
		bePlayer = true;
		usage = "<radius> <- radius to search around you";
	}
	
	public boolean execute() {
		try {
			int integer = Integer.parseInt(args.get(0));
			if (integer > Config.maxRadius || integer < 0)
				throw new Exception();
			List<Integer> actions = new ArrayList<Integer>();
			for (DataType type : DataType.values())
				if (type.canHere()) actions.add(type.getId());
			DataManager.search(SearchType.SEARCH, sender, null, null, null, actions, player.getLocation().toVector(), Integer.parseInt(args.get(0)), null, null, "desc");
		} catch (Throwable t) {
			Util.sendMessage(sender, "Invalid radius!");
		}
		return true;
	}

	public boolean permission() {
		return Permission.search(sender);
	}
	
}
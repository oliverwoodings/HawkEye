package uk.co.oliwali.DataLog.commands;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.SearchQuery;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Util;

public class HereCommand extends BaseCommand implements Runnable {

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
			SearchQuery search = new SearchQuery(this, sender, null, null, null, null, player.getLocation().toVector(), Integer.parseInt(args.get(0)), null, null);
			DataLog.server.getScheduler().scheduleAsyncDelayedTask(DataLog.server.getPluginManager().getPlugin("DataLog"), search);
		} catch (Throwable t) {
			Util.sendMessage(sender, "Invalid radius!");
		}
		return true;
	}

	public boolean permission() {
		return false;
	}
	
	public void run() {
		DataManager.displayPage(sender, 1);
	}
	
}
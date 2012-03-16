package uk.co.oliwali.HawkEye.commands;

import java.util.ArrayList;
import java.util.List;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.RebuildCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

public class RebuildCommand extends BaseCommand {

	public RebuildCommand() {
		name = "rebuild";
		argLength = 1;
		usage = "<parameters> <- re-applies changes";
	}

	@Override
	public boolean execute() {

		//Check if player already has a rollback processing
		if (session.doingRollback()) {
			Util.sendMessage(sender, "&cYou already have a query command processing!");
			return true;
		}

		//Parse arguments
		SearchParser parser = null;
		try {

			parser = new SearchParser(player, args);
			parser.loc = null;

			//Check that supplied actions can rollback
			if (parser.actions.size() > 0) {
				for (DataType type : parser.actions)
					if (!type.canRollback()) throw new IllegalArgumentException("You cannot rebuild that action type: &7" + type.getConfigName());
			}
			//If none supplied, add in all rollback types
			else {
				for (DataType type : DataType.values())
					if (type.canRollback()) parser.actions.add(type);
			}

		} catch (IllegalArgumentException e) {
			Util.sendMessage(sender, "&c" + e.getMessage());
			return true;
		}

		//Create new SearchQuery with data
		new SearchQuery(new RebuildCallback(session), parser, SearchDir.ASC);
		return true;

	}

	@Override
	public void moreHelp() {
		List<String> acs = new ArrayList<String>();
		for (DataType type : DataType.values()) if (type.canRollback()) acs.add(type.getConfigName());
		Util.sendMessage(sender, "&7There are 6 parameters you can use - &ca: p: w: r: f: t:");
		Util.sendMessage(sender, "&6Action &ca:&7 - list of actions separated by commas. Select from the following: &8" + Util.join(acs, " "));
		Util.sendMessage(sender, "&6Player &cp:&7 - list of players. &6World &cw:&7 - list of worlds");
		Util.sendMessage(sender, "&6Filter &cf:&7 - list of keywords (e.g. block id)");
		Util.sendMessage(sender, "&6Radius &cr:&7 - radius to search around given location");
		Util.sendMessage(sender, "&6Time &ct:&7 - time bracket in the following format:");
		Util.sendMessage(sender, "&7  -&c t:10h45m10s &7-back specified amount of time");
		Util.sendMessage(sender, "&7  -&c t:2011-06-02,10:45:10 &7-from given date");
		Util.sendMessage(sender, "&7  -&c t:2011-06-02,10:45:10,2011-07-04,18:15:00 &7-between dates");
	}

	@Override
	public boolean permission() {
		return Permission.rebuild(sender);
	}

}
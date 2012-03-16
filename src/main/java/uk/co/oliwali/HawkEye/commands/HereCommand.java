package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.SearchCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Searches around the player for 'here' {@link DataType}s
 * @author oliverw92
 */
public class HereCommand extends BaseCommand {

	public HereCommand() {
		name = "here";
		argLength = 0;
		usage = "[radius] [player] <- search around you";
	}

	@Override
	public boolean execute() {

		//Create new parser
		SearchParser parser = null;
		try {

			//Check for valid integer
			if (args.size() != 0 && !Util.isInteger(args.get(0))) throw new IllegalArgumentException("Invalid integer supplied for radius!");
			int integer;
			if (args.size() > 0) integer = Integer.parseInt(args.get(0));
			else integer = Config.DefaultHereRadius;
			if ((integer > Config.MaxRadius && Config.MaxRadius > 0) || integer < 0)
				throw new IllegalArgumentException("Invalid radius supplied supplied!");

			//New search parser
			parser = new SearchParser(player, integer);

			//Add in DataTypes
			for (DataType type : DataType.values())
				if (type.canHere()) parser.actions.add(type);

			//Check if players were supplied
			if (args.size() > 1)
				for (String p : args.get(1).split(",")) parser.players.add(p);

			//Add in 'here' actions
			for (DataType type : DataType.values())
				if (type.canHere()) parser.actions.add(type);

		} catch (IllegalArgumentException e) {
			Util.sendMessage(sender, "&c" + e.getMessage());
			return true;
		}

		//Run the search query
		new SearchQuery(new SearchCallback(session), parser, SearchDir.DESC);
		return true;

	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cShows all changes in a radius around you");
		Util.sendMessage(sender, "&cRadius should be an integer");
	}

	@Override
	public boolean permission() {
		return Permission.search(sender);
	}

}

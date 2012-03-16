package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Displays the help data for all commands
 * @author oliverw92
 */
public class HelpCommand extends BaseCommand {

	public HelpCommand() {
		bePlayer = false;
		name = "help";
		argLength = 0;
		usage = "<- lists all HawkEye commands";
	}

	@Override
	public boolean execute() {
		//General help
		if (args.size() == 0) {
			Util.sendMessage(sender, "&c---------------------- &7HawkEye &c----------------------");
			Util.sendMessage(sender, "&7Type &8/hawk help <command>&7 for more info on that command");
			for (BaseCommand cmd : HawkEye.commands.toArray(new BaseCommand[0]))
				if (cmd.permission())
					Util.sendMessage(sender, "&8-&7 /"+usedCommand+" &c" + cmd.name + " &7" + cmd.usage);
		}
		//Command-specific help
		else {
			for (BaseCommand cmd : HawkEye.commands.toArray(new BaseCommand[0])) {
				if (cmd.permission() && cmd.name.equalsIgnoreCase(args.get(0))) {
					Util.sendMessage(sender, "&c---------------------- &7HawkEye - " + cmd.name);
					Util.sendMessage(sender, "&8-&7 /"+usedCommand+" &c" + cmd.name + " &7" + cmd.usage);
					cmd.sender = sender;
					cmd.moreHelp();
					return true;
				}
			}
			Util.sendMessage(sender, "&cNo command found by that name");
		}
		return true;
	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cShows all HawkEye commands");
		Util.sendMessage(sender, "&cType &7/hawk help <command>&c for help on that command");
	}

	@Override
	public boolean permission() {
		return true;
	}

}
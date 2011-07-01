package uk.co.oliwali.DataLog.commands;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Displays the help data for all commands
 * @author oliverw92
 */
public class HelpCommand extends BaseCommand {

	public HelpCommand() {
		name = "help";
		argLength = 0;
		usage = "<- lists all DataLog commands";
	}
	
	public boolean execute() {
		Util.sendMessage(sender, "&c---------------------- &7DataLog &c----------------------");
		for (BaseCommand cmd : DataLog.commands.toArray(new BaseCommand[0]))
			if (cmd.permission())
				Util.sendMessage(sender, "&7- /"+usedCommand+" &c" + cmd.name + " &7" + cmd.usage);
		return true;
	}
	
	public boolean permission() {
		return true;
	}

}
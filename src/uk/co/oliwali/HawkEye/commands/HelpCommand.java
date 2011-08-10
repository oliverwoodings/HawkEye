package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Displays the help data for all commands
 * @author oliverw92
 */
public class HelpCommand extends BaseCommand {

	public HelpCommand() {
		name = "help";
		argLength = 0;
		usage = "<- lists all HawkEye commands";
	}
	
	public boolean execute() {
		Util.sendMessage(sender, "&c---------------------- &7HawkEye &c----------------------");
		for (BaseCommand cmd : HawkEye.commands.toArray(new BaseCommand[0]))
			if (cmd.permission())
				Util.sendMessage(sender, "&8-&7 /"+usedCommand+" &c" + cmd.name + " &7" + cmd.usage);
		return true;
	}
	
	public boolean permission() {
		return true;
	}

}
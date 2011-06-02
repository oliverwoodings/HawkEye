package uk.co.oliwali.DataLog.commands;

import org.bukkit.entity.Player;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.util.Util;

public class HelpCommand extends BaseCommand {

	public HelpCommand() {
		name = "help";
		argLength = 0;
		usage = "<- lists all DataLog commands";
	}
	
	public boolean execute() {
		Util.sendMessage(player, "&c---------------------- &7DataLog &c----------------------");
		for (BaseCommand cmd : DataLog.commands.toArray(new BaseCommand[0]))
			if (cmd.permission(player))
				Util.sendMessage(player, "&7- /"+usedCommand+" &c" + cmd.name + " &7" + cmd.usage);
		return true;
	}
	
	public boolean permission(Player player) {
		return true;
	}

}
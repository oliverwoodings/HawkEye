package uk.co.oliwali.DataLog.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.oliwali.DataLog.util.Util;

public abstract class BaseCommand {
	
	public CommandSender sender;
	public List<String> args = new ArrayList<String>();
	public String name;
	public int argLength;
	public String usage;
	public boolean bePlayer = true;
	public Player player;
	public String usedCommand;
	
	public boolean run(CommandSender sender, String[] preArgs, String cmd) {
		this.sender = sender;
		args.clear();
		for (String arg : preArgs)
			args.add(arg);
		args.remove(0);
		
		if (argLength != args.size()) {
			sendUsage();
			return true;
		}
		if (bePlayer && !(sender instanceof Player))
			return false;
		player = (Player)sender;
		usedCommand = cmd;
		if (!permission(player))
			return false;
		return execute();
	}
	
	public abstract boolean execute();
	public abstract boolean permission(Player player);
	
	public void sendUsage() {
		Util.sendMessage(sender, "&c/"+usedCommand+" " + name + " " + usage);
	}

}

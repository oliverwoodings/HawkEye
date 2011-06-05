package uk.co.oliwali.DataLog.commands;

import org.bukkit.Location;
import org.bukkit.World;

import uk.co.oliwali.DataLog.DataEntry;
import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class TptoCommand extends BaseCommand {

	public TptoCommand() {
		name = "tpto";
		argLength = 1;
		bePlayer = true;
		usage = "<id> <- teleport to location of inputted data entry";
	}
	
	public boolean execute() {
		if (!Util.isInteger(args.get(0))) {
			Util.sendMessage(sender, "&cPlease supply a entry id!");
			return true;
		}
		DataEntry entry = DataManager.getEntry(Integer.parseInt(args.get(0)));
		World world = DataLog.server.getWorld(entry.getWorld());
		if (world == null) {
			Util.sendMessage(sender, "&cWorld &7" + entry.getWorld() + "&c does not exist!");
			return true;
		}
		Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
		player.teleport(loc);
		Util.sendMessage(sender, "&7Teleported to location of data entry &c" + args.get(0));
		return true;
	}
	
	public boolean permission() {
		return Permission.tpTo(sender);
	}
	
}
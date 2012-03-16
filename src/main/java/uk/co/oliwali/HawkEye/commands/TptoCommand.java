package uk.co.oliwali.HawkEye.commands;

import org.bukkit.Location;
import org.bukkit.World;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Teleports player to location of specified data entry
 * @author oliverw92
 */
public class TptoCommand extends BaseCommand {

	public TptoCommand() {
		name = "tpto";
		argLength = 1;
		usage = "<id> <- teleport to location of the data entry";
	}

	@Override
	public boolean execute() {
		if (!Util.isInteger(args.get(0))) {
			Util.sendMessage(sender, "&cPlease supply a entry id!");
			return true;
		}
		DataEntry entry = DataManager.getEntry(Integer.parseInt(args.get(0)));
		if (entry == null) {
			Util.sendMessage(sender, "&cEntry not found");
			return true;
		}
		World world = HawkEye.server.getWorld(entry.getWorld());
		if (world == null) {
			Util.sendMessage(sender, "&cWorld &7" + entry.getWorld() + "&c does not exist!");
			return true;
		}
		Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
		player.teleport(loc);
		Util.sendMessage(sender, "&7Teleported to location of data entry &c" + args.get(0));
		return true;
	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cTakes you to the location of the data entry with the specified ID");
		Util.sendMessage(sender, "&cThe ID can be found in either the DataLog interface or when you do a search command");
	}

	@Override
	public boolean permission() {
		return Permission.tpTo(sender);
	}

}
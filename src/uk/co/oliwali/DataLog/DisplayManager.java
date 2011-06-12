package uk.co.oliwali.DataLog;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import uk.co.oliwali.DataLog.database.DataEntry;
import uk.co.oliwali.DataLog.database.DataType;
import uk.co.oliwali.DataLog.util.Util;

public class DisplayManager {

	public static void displayPage(PlayerSession session, int page) {
		
		List<DataEntry> results = session.getSearchResults();
		if (results == null || results.size() == 0) {
			Util.sendMessage(session.getSender(), "&cNo results found, type &7/dl searchhelp");
			return;
		}
			
		int maxLines = 6;
		int maxPages = (int)Math.ceil((double)results.size() / 6);
		if (page > maxPages || page < 1)
			return;
		
		Util.sendMessage(session.getSender(), "&8--------------------- &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8--------------------" + (maxPages < 9?"-":""));

		for (int i = (page-1) * maxLines; i < ((page-1) * maxLines) + maxLines; i++) {
			if (i == results.size())
				break;
			DataEntry entry = results.get(i);
			String data = entry.getData();
			if (entry.getAction() == 0)
				data = Material.getMaterial(Integer.parseInt(data)).name();
			if (entry.getAction() == 1) {
				if (data.indexOf("-") == -1)
					data = Material.getMaterial(Integer.parseInt(data)).name();
				else
					data = Material.getMaterial(Integer.parseInt(data.substring(0, data.indexOf("-")))).name() + " changed to " + Material.getMaterial(Integer.parseInt(data.substring(data.indexOf("-") + 1))).name();
			}
			String action = DataType.fromId(entry.getAction()).getConfigName();
			if (entry.getAction() == 16) {
				action = data.substring(0, data.indexOf("-"));
				data = data.substring(data.indexOf("-") + 1);
			}
			sendLine(session.getSender(), "&7" + entry.getDataid() + " &c" + entry.getDate().substring(5) + " &7" + entry.getPlayer() + " &c" + entry.getWorld() + ":" + entry.getX() + "," + entry.getY() + "," + entry.getZ());
			sendLine(session.getSender(), "   &7Action: &c" + action + " &7Data: &c" + data);
		}
		Util.sendMessage(session.getSender(), "&8-----------------------------------------------------");
		return;
	}
	
	public static void sendLine(CommandSender sender, String line) {
		int len = 70;
		if (line.length() < len)
			Util.sendMessage(sender, "&8| " + line);
		else
			for (int i = 0; i < line.length(); i+=len)
				Util.sendMessage(sender, "&8| &c" + (i+len>line.length()?line.substring(i):line.substring(i, i+len)));
	}
	
}

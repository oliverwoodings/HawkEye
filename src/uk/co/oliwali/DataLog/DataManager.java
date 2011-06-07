package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.util.Util;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlRow;

public class DataManager {
	
	public static EbeanServer db;
	private static DataLog plugin;
	public static HashMap<CommandSender, List<SqlRow>> searchResults = new HashMap<CommandSender, List<SqlRow>>();
	public static HashMap<CommandSender, List<SqlRow>> undoResults = new HashMap<CommandSender, List<SqlRow>>();
	
	public DataManager(DataLog instance) {
		plugin = instance;
		db = plugin.getDatabase();
	}
	
	public static void addEntry(Player player, DataType dataType, Location loc, String data) {
		addEntry(player, plugin, dataType, loc, data);
	}
	public static void addEntry(Player player, JavaPlugin cplugin, DataType dataType, Location loc, String data) {
		if (plugin.config.isLogged(dataType)) {
			DataEntry dataEntry = new DataEntry();
			loc = Util.getSimpleLocation(loc);
			dataEntry.setInfo(player, cplugin, dataType.getId(), loc, data);
			db.save(dataEntry);
		}
	}
	
	public static boolean displayPage(CommandSender sender, int page) {
		if (!hasResults(sender))
			return false;
		List<SqlRow> results = searchResults.get(sender);
		int maxLines = 6;
		int maxPages = (int)Math.ceil((double)results.size() / 6);
		if (page > maxPages || page < 1)
			return false;
		
		Util.sendMessage(sender, "&8--------------------- &7Page (&c" + page + "&7/&c" + maxPages + "&7) &8--------------------" + (maxPages < 9?"-":""));

		for (int i = (page-1) * maxLines; i < ((page-1) * maxLines) + maxLines; i++) {
			if (i == results.size())
				break;
			SqlRow row = results.get(i);
			String data = row.getString("data");
			if (row.getInteger("action") == 0)
				data = Material.getMaterial(Integer.parseInt(data)).name();
			if (row.getInteger("action") == 1) {
				if (data.indexOf("-") == -1)
					data = Material.getMaterial(Integer.parseInt(data)).name();
				else
					data = Material.getMaterial(Integer.parseInt(data.substring(0, data.indexOf("-")))).name() + " changed to " + Material.getMaterial(Integer.parseInt(data.substring(data.indexOf("-") + 1))).name();
			}
			String action = DataType.fromId(row.getInteger("action")).getConfigName();
			if (row.getInteger("action") == 16) {
				action = data.substring(0, data.indexOf("-"));
				data = data.substring(data.indexOf("-") + 1);
			}
			sendLine(sender, "&7" + row.getInteger("dataid") + " &c" + row.getString("date").substring(5) + " &7" + row.getString("player") + " &c" + action + " &7" + row.getString("world") + ":" + row.getInteger("x") + "," + row.getInteger("y")+ "," + row.getInteger("z"));
			sendLine(sender, "   &7Data: &c" + data);
		}
		Util.sendMessage(sender, "&8-----------------------------------------------------");
		return true;
	}
	
	public static void sendLine(CommandSender sender, String line) {
		int len = 70;
		if (line.length() < len)
			Util.sendMessage(sender, "&8| " + line);
		else
			for (int i = 0; i < line.length(); i+=len)
				Util.sendMessage(sender, "&8| &c" + (i+len>line.length()?line.substring(i):line.substring(i, i+len)));
	}
	
	public static boolean hasResults(CommandSender sender) {
		List<SqlRow> results = searchResults.get(sender);
		if (results == null || results.size() == 0)
			return false;
		return true;
	}
	
	public static DataEntry getEntry(int id) {
		return db.find(DataEntry.class).where().eq("dataid", id).findUnique();
	}
	
	public static List<SqlRow> rollback(List<SqlRow> results) {
		List<SqlRow> undo = new ArrayList<SqlRow>();
		for (SqlRow row : results.toArray(new SqlRow[0])) {
			
			DataType type = DataType.fromId(row.getInteger("action"));
			if (type == null || !type.canRollback())
				continue;
			
			World world = DataLog.server.getWorld(row.getString("world"));
			if (world == null)
				continue;
			
			Location loc = new Location(world, row.getInteger("x"), row.getInteger("y"), row.getInteger("z"));
			Block block = world.getBlockAt(loc);
			int id = block.getTypeId();
			switch (type) {
				case BLOCK_BREAK:
					block.setTypeId(Integer.parseInt(row.getString("data")));
					row.set("data", id);
					break;
				case BLOCK_PLACE:
					if (row.getString("data").indexOf("-") == -1) {
						block.setType(Material.AIR);
						row.set("data", id);
					}
					else {
						block.setTypeId(Integer.parseInt(row.getString("data").substring(0, row.getString("data").indexOf("-"))));
						row.set("data", block.getTypeId() + "-" + row.getString("data").substring(row.getString("data").indexOf("-") + 1));
					}
					break;
				case LAVA_BUCKET:
				case WATER_BUCKET:
					block.setType(Material.AIR);
					row.set("data", id);
					break;
			}
			undo.add(row);
			
		}
		return undo;
	}

}
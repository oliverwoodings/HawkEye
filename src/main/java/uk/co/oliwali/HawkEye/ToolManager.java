package uk.co.oliwali.HawkEye;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import uk.co.oliwali.HawkEye.callbacks.SearchCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Contains methods for controlling the HawkEye tool
 * @author oliverw92
 */
public class ToolManager {

	/**
	 * Enables the HawkEye tool
	 * @param session
	 * @param player
	 */
	public static void enableTool(PlayerSession session, Player player) {

		Inventory inv = player.getInventory();
		session.setUsingTool(true);
		ItemStack stack = BlockUtil.itemStringToStack(Config.ToolBlock, 1);

		//If player doesn't have a tool, give them one if enabled in config
		if (!inv.contains(stack) && Config.GiveTool) {
			int first = inv.firstEmpty();
			if (first == -1)
				player.getWorld().dropItem(player.getLocation(), stack);
			else inv.setItem(first, stack);
		}

		//If they aren't holding a tool, move the tool to their hand
		int first = inv.first(BlockUtil.getIdFromString(Config.ToolBlock));
		if (!BlockUtil.getItemString(player.getItemInHand()).equals(Config.ToolBlock) && first != -1){
			ItemStack back = player.getItemInHand().clone();
			player.setItemInHand(inv.getItem(first));
			if (back.getAmount() == 0) inv.clear(first);
			else inv.setItem(first, back);
		}

		Util.sendMessage(player, "&cHawkEye tool enabled! &7Left click a block or place the tool to get information");

	}

	/**
	 * Disables the HawkEye tool
	 * @param session
	 * @param player
	 */
	public static void disableTool(PlayerSession session, Player player) {
		session.setUsingTool(false);
		Util.sendMessage(player, "&cHawkEye tool disabled");
	}

	/**
	 * Performs a HawkEye tool search at the specified location
	 * @param player
	 * @param loc
	 */
	public static void toolSearch(Player player, Location loc) {

		PlayerSession session = SessionManager.getSession(player);
		SearchParser parser;

		//If parameters aren't bound, do some default
		if (session.getToolCommand().length == 0 || session.getToolCommand()[0] == "") {
			parser = new SearchParser(player);
			for (DataType type : DataType.values())
				if (type.canHere()) parser.actions.add(type);
		}
		//Else use the default ones
		else {
			parser = new SearchParser(player, Arrays.asList(session.getToolCommand()));
		}

		Vector vec = Util.getSimpleLocation(loc).toVector();

		parser.loc = vec;
		parser.minLoc = null;
		parser.maxLoc = null;
		parser.worlds = new String[]{ loc.getWorld().getName() };
		new SearchQuery(new SearchCallback(SessionManager.getSession(player)), parser, SearchDir.DESC);

	}

	/**
	 * Binds arguments to the HawkEye tool
	 * @param player player issueing the command
	 * @param session session to save args to
	 * @param args parameters
	 */
	public static void bindTool(Player player, PlayerSession session, List<String> args) {

		try {
			new SearchParser(player, args);
		} catch (IllegalArgumentException e) {
			Util.sendMessage(player, "&c" + e.getMessage());
			return;
		}

		Util.sendMessage(player, "&cParameters bound to tool: &7" + Util.join(args, " "));
		session.setToolCommand(args.toArray(new String[0]));
		if (!session.isUsingTool()) enableTool(session, player);

	}

	/**
	 * Reset tool to default parameters
	 * @param session
	 */
	public static void resetTool(PlayerSession session) {
		session.setToolCommand(Config.DefaultToolCommand);
	}

}

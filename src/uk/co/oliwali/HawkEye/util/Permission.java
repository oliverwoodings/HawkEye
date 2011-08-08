package uk.co.oliwali.HawkEye.util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import uk.co.oliwali.HawkEye.HawkEye;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * Permissions handler for HawkEye
 * Supports multiple permissions systems
 * @author oliverw92
 */
public class Permission {
	
	private HawkEye plugin;
	private static PermissionPlugin handler = PermissionPlugin.OP;
	private static PermissionHandler permissionPlugin;
	
	/**
	 * Check permissions plugins, deciding which one to use
	 * @param instance
	 */
	public Permission(HawkEye instance) {
		plugin = instance;
        Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
        
        if (permissions != null) {
        	permissionPlugin = ((Permissions)permissions).getHandler();
        	handler = PermissionPlugin.PERMISSIONS;
        	Util.info("Using Permissions for user permissions");
        }
        else {
        	Util.info("No permission handler detected, only ops can use commands");
        }
	}
	
	/**
	 * Private method for checking a users permission level.
	 * Permission checks from other classes should go through a separate method for each node.
	 * @param sender
	 * @param node
	 * @return true if the user has permission, false if not
	 */
	private static boolean hasPermission(CommandSender sender, String node) {
		if (!(sender instanceof Player))
			return true;
		Player player = (Player)sender;
		switch (handler) {
			case PERMISSIONS:
				return permissionPlugin.has(player, node);
			case OP:
				return player.isOp();
		}
		return false;
	}
	
	/**
	 * Permission to search the logs
	 * @param player
	 * @return
	 */
	public static boolean search(CommandSender player) {
		return hasPermission(player, "HawkEye.search");
	}
	
	/**
	 * Permission to search a specific data type
	 * @param player
	 * @return
	 */
	public static boolean searchType(CommandSender player, String type) {
		return hasPermission(player, "HawkEye.search." + type.toLowerCase());
	}
	
	/**
	 * Permission to teleport to the location of a result
	 * @param player
	 * @return
	 */
	public static boolean tpTo(CommandSender player) {
		return hasPermission(player, "HawkEye.tpto");
	}
	
	/**
	 * Permission to use the rollback command
	 * @param player
	 * @return
	 */
	public static boolean rollback(CommandSender player) {
		return hasPermission(player, "HawkEye.rollback");
	}
	
	/**
	 * Permission to the HawkEye tool
	 * @param player
	 * @return
	 */
	public static boolean tool(CommandSender player) {
		return hasPermission(player, "HawkEye.tool");
	}
	
	/**
	 * Permission to be notified of rule breaks
	 * @param player
	 * @return
	 */
	public static boolean notify(CommandSender player) {
		return hasPermission(player, "HawkEye.notify");
	}
	
	/**
	 * Check if a player is in a group
	 * @param world
	 * @param player
	 * @param group
	 * @return
	 */
	public static boolean inSingleGroup(String world, String player, String group) {
		switch (handler) {
			case PERMISSIONS:
				return permissionPlugin.inSingleGroup(world, player, group);
		}
		return false;
	}
	
	/**
	 * Enumeration containing supported permission systems
	 * @author oliverw92
	 */
	private enum PermissionPlugin {
		PERMISSIONS,
		OP
	}

}

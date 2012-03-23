package uk.co.oliwali.HawkEye.util;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import uk.co.oliwali.HawkEye.HawkEye;

/**
 * Permissions handler for HawkEye
 * Supports multiple permissions systems
 * @author oliverw92
 */
public class Permission {

	private final HawkEye plugin;
	private static PermissionPlugin handler = PermissionPlugin.BUKKITPERMS;
	private static net.milkbowl.vault.permission.Permission vaultPermissions;
	private static PermissionHandler permissionPlugin;
	private static PermissionManager permissionsEx;

	/**
	 * Check permissions plugins, deciding which one to use
	 * @param instance
	 */
	public Permission(HawkEye instance) {
		plugin = instance;
		PluginManager pm = Bukkit.getServer().getPluginManager();

		if (pm.isPluginEnabled("Vault")) {
			RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
			vaultPermissions = rsp.getProvider();
			if (vaultPermissions != null) {
				handler = PermissionPlugin.VAULT;
				Util.info("Using " + vaultPermissions.getName() + " for user permissions");
				return;
			}
		}

		if (pm.isPluginEnabled("PermissionsEx")) {
        	handler = PermissionPlugin.PERMISSIONSEX;
        	permissionsEx = PermissionsEx.getPermissionManager();
        	Util.info("Using PermissionsEx for user permissions");
		}
        else if (pm.isPluginEnabled("Permissions")) {
        	permissionPlugin = ((Permissions)plugin.getServer().getPluginManager().getPlugin("Permissions")).getHandler();
        	handler = PermissionPlugin.PERMISSIONS;
        	Util.info("Using Permissions for user permissions");
        }
        else {
        	Util.info("No permission handler detected, defaulting to superperms");
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
		if (Config.OpPermissions && player.isOp())
			return true;
		switch (handler) {
			case VAULT:
				return vaultPermissions.has(player, node);
			case PERMISSIONSEX:
				return permissionsEx.has(player, node);
			case PERMISSIONS:
				return permissionPlugin.has(player, node);
			case BUKKITPERMS:
				return player.hasPermission(node);
		}
		return false;
	}

	/**
	 * Permission to view different pages
	 * @param player
	 * @return
	 */
	public static boolean page(CommandSender player) {
		return hasPermission(player, "hawkeye.page");
	}

	/**
	 * Permission to search the logs
	 * @param player
	 * @return
	 */
	public static boolean search(CommandSender player) {
		return hasPermission(player, "hawkeye.search");
	}

	/**
	 * Permission to search a specific data type
	 * @param player
	 * @return
	 */
	public static boolean searchType(CommandSender player, String type) {
		return hasPermission(player, "hawkeye.search." + type.toLowerCase());
	}

	/**
	 * Permission to teleport to the location of a result
	 * @param player
	 * @return
	 */
	public static boolean tpTo(CommandSender player) {
		return hasPermission(player, "hawkeye.tpto");
	}

	/**
	 * Permission to use the rollback command
	 * @param player
	 * @return
	 */
	public static boolean rollback(CommandSender player) {
		return hasPermission(player, "hawkeye.rollback");
	}

	/**
	 * Permission to the hawkeye tool
	 * @param player
	 * @return
	 */
	public static boolean tool(CommandSender player) {
		return hasPermission(player, "hawkeye.tool");
	}

	/**
	 * Permission to be notified of rule breaks
	 * @param player
	 * @return
	 */
	public static boolean notify(CommandSender player) {
		return hasPermission(player, "hawkeye.notify");
	}

	/**
	 * Permission to preview rollbacks
	 * @param player
	 * @return
	 */
	public static boolean preview(CommandSender player) {
		return hasPermission(player, "hawkeye.preview");
	}

    /**
     * Permission to bind a tool
     * @param player
     * @return
     */
	public static boolean toolBind(CommandSender player) {
		return hasPermission(player, "hawkeye.tool.bind");
	}

	/**
	 * Permission to rebuild
	 * @param player
	 * @return
	 */
	public static boolean rebuild(CommandSender player) {
		return hasPermission(player, "hawkeye.rebuild");
	}

	/**
	 * Permission to delete entires
	 * @param player
	 * @return
	 */
	public static boolean delete(CommandSender player) {
		return hasPermission(player, "hawkeye.delete");
	}

	/**
	 * Check if a player is in a group
	 * @param world
	 * @param player
	 * @param group
	 * @return
	 */
	public static boolean inGroup(World world, Player player, String group) {
		return inGroup(world.getName(), player.getName(), group);
	}
	public static boolean inGroup(String world, String player, String group) {
		switch (handler) {
			case VAULT:
				return vaultPermissions.playerInGroup(world, player, group);
			case PERMISSIONSEX:
				return permissionsEx.getUser(player).inGroup(group);
			case PERMISSIONS:
				return permissionPlugin.inGroup(world, player, group);
		}
		return false;
	}

	/**
	 * Enumeration containing supported permission systems
	 * @author oliverw92
	 */
	private enum PermissionPlugin {
		VAULT,
		PERMISSIONSEX,
		PERMISSIONS,
		BUKKITPERMS
	}

}

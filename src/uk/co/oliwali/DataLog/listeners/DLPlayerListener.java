package uk.co.oliwali.DataLog.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Config;

/**
 * Player listener class for DataLog
 * @author oliverw92
 */
public class DLPlayerListener extends PlayerListener {
	
	public DataLog plugin;

	public DLPlayerListener(DataLog dataLog) {
		plugin = dataLog;	
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		if (DataManager.addEntry(player, DataType.CHAT, loc, event.getMessage()))
			event.setCancelled(true);
	}
	
	/**
	 * Contains processing of the command filter in {@link Config}
	 */
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		String message = event.getMessage();
		String command = message.split(" ")[0];
		//Check if command is in filter list or not
		if (!Config.CommandFilter.contains(command))
			if (DataManager.addEntry(player, DataType.COMMAND, loc, message))
				event.setCancelled(true);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		DataLog.addSession(player);
		Location loc  = player.getLocation();
		DataManager.addEntry(player, DataType.JOIN, loc, Config.LogIpAddresses?player.getAddress().getAddress().getHostAddress().toString():"");
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		DataManager.addEntry(player, DataType.QUIT, loc, Config.LogIpAddresses?player.getAddress().getAddress().getHostAddress().toString():"");
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to   = event.getTo();
		if (distance(from, to) > 5)
			if (DataManager.addEntry(player, DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ()))
				event.setCancelled(true);
	}
	
	/**
	 * Handles several actions: 
	 * OPEN_CHEST, DOOR_INTERACT, LEVER, STONE_BUTTON, FLINT_AND_STEEL, LAVA_BUCKET, WATER_BUCKET
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Location loc = null;
		if (block != null) loc = block.getLocation();
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && player.getItemInHand().getTypeId() == Config.ToolBlock && DataLog.getSession(player).isUsingTool()) {
			DataManager.toolSearch(player, loc);
			event.setCancelled(true);
		}

		if (event.isCancelled())
			return;

		switch (block.getType()) {
			case CHEST:
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
					if (DataManager.addEntry(player, DataType.OPEN_CHEST, loc, ""))
						event.setCancelled(true);
				break;
			case WOODEN_DOOR:
				if (DataManager.addEntry(player, DataType.DOOR_INTERACT, loc, ""))
					event.setCancelled(true);
				break;
			case LEVER:
				if (DataManager.addEntry(player, DataType.LEVER, loc, ""))
					event.setCancelled(true);
				break;
			case STONE_BUTTON:
				if (DataManager.addEntry(player, DataType.STONE_BUTTON, loc, ""))
					event.setCancelled(true);
				break;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			switch (player.getItemInHand().getType()) {
				case FLINT_AND_STEEL:
					if (DataManager.addEntry(player, DataType.FLINT_AND_STEEL, loc, ""))
						event.setCancelled(true);
					break;
				case LAVA_BUCKET:
					if (DataManager.addEntry(player, DataType.LAVA_BUCKET, loc, ""))
						event.setCancelled(true);
					break;
				case WATER_BUCKET:
					if (DataManager.addEntry(player, DataType.WATER_BUCKET, loc, ""))
						event.setCancelled(true);
					break;
			}
		}
			
		
	}
	
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItemDrop().getItemStack();
		String data = null;
		if (stack.getData() != null)
			data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
		else
			data = stack.getAmount() + "x " + stack.getTypeId();
		if (DataManager.addEntry(player, DataType.ITEM_DROP, player.getLocation(), data))
			event.setCancelled(true);
	}
	
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItem().getItemStack();
		String data = null;
		if (stack.getData() != null)
			data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
		else
			data = stack.getAmount() + "x " + stack.getTypeId();
		if (DataManager.addEntry(player, DataType.ITEM_PICKUP, player.getLocation(), data))
			event.setCancelled(true);
	}
	
	/**
	 * Returns the distance between two {Location}s
	 * @param from
	 * @param to
	 * @return double
	 */
	private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }

}

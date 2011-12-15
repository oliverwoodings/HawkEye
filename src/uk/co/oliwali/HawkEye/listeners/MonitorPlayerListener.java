package uk.co.oliwali.HawkEye.listeners;

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

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Player listener class for HawkEye
 * @author oliverw92
 */
public class MonitorPlayerListener extends PlayerListener {
	
	public HawkEye plugin;

	public MonitorPlayerListener(HawkEye HawkEye) {
		plugin = HawkEye;	
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(event.getPlayer());
		DataManager.addEntry(new DataEntry(player, DataType.CHAT, player.getLocation(), event.getMessage()));
	}

	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

		if (event.isCancelled()) return;
		Player player = event.getPlayer();
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(player);
		//Check command filter
		if (Config.CommandFilter.contains(event.getMessage().split(" ")[0])) return;
		DataManager.addEntry(new DataEntry(player, DataType.COMMAND, player.getLocation(), event.getMessage()));
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		DataManager.addEntry(new DataEntry(player, DataType.JOIN, loc, Config.LogIpAddresses?player.getAddress().getAddress().getHostAddress().toString():""));
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(player);
		
		//Hackish workaround for random NPE given off by the address system
		String ip = "";
		try {
			ip = player.getAddress().getAddress().getHostAddress().toString();
		} catch (Exception e) { }
		
		DataManager.addEntry(new DataEntry(player, DataType.QUIT, loc, Config.LogIpAddresses?ip:""));
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled()) return;
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(event.getPlayer());
		Location from = event.getFrom();
		Location to   = event.getTo();
		if (Util.distance(from, to) > 5)
			DataManager.addEntry(new DataEntry(event.getPlayer(), DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ()));
	}
	
	/**
	 * Handles several actions: 
	 * OPEN_CHEST, DOOR_INTERACT, LEVER, STONE_BUTTON, FLINT_AND_STEEL, LAVA_BUCKET, WATER_BUCKET
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {

		if (event.isCancelled()) return;
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		//Check for inventory close
		HawkEye.containerManager.checkInventoryClose(player);
		
		if (block != null) {
			
			Location loc = block.getLocation();

			switch (block.getType()) {
				case FURNACE:
				case DISPENSER:
				case CHEST:
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						//Call container manager for inventory open
						HawkEye.containerManager.checkInventoryOpen(player, block);
						DataManager.addEntry(new DataEntry(player, DataType.OPEN_CONTAINER, loc, Integer.toString(block.getTypeId())));
					}
					break;
				case WOODEN_DOOR:
					DataManager.addEntry(new DataEntry(player, DataType.DOOR_INTERACT, loc, ""));
					break;
				case LEVER:
					DataManager.addEntry(new DataEntry(player, DataType.LEVER, loc, ""));
					break;
				case STONE_BUTTON:
					DataManager.addEntry(new DataEntry(player, DataType.STONE_BUTTON, loc, ""));
					break;
			}
			
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				loc = block.getRelative(event.getBlockFace()).getLocation();
				switch (player.getItemInHand().getType()) {
					case FLINT_AND_STEEL:
						DataManager.addEntry(new SimpleRollbackEntry(player, DataType.FLINT_AND_STEEL, loc, ""));
						break;
					case LAVA_BUCKET:
						DataManager.addEntry(new SimpleRollbackEntry(player, DataType.LAVA_BUCKET, loc, ""));
						break;
					case WATER_BUCKET:
						DataManager.addEntry(new SimpleRollbackEntry(player, DataType.WATER_BUCKET, loc, ""));
						break;
				}
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
		DataManager.addEntry(new DataEntry(player, DataType.ITEM_DROP, player.getLocation(), data));
	}
	
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItem().getItemStack();
		String data = null;
		if (stack.getData() != null)
			data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
		else
			data = stack.getAmount() + "x " + stack.getTypeId();
		DataManager.addEntry(new DataEntry(player, DataType.ITEM_PICKUP, player.getLocation(), data));
	}

}

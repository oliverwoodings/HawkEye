package uk.co.oliwali.DataLog;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DLPlayerListener extends PlayerListener {
	
	public DataLog plugin;

	public DLPlayerListener(DataLog dataLog) {
		plugin = dataLog;	
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		plugin.addDataEntry(player, DataType.CHAT, loc, event.getMessage());
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		String message = event.getMessage();
		String command = message.split(" ")[0];
		//Check if command is in filter list or not
		if (!plugin.config.commandFilter.contains(command))
			plugin.addDataEntry(player, DataType.COMMAND, loc, message);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		plugin.addDataEntry(player, DataType.JOIN, loc, player.getAddress().getAddress().getHostAddress().toString());
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		plugin.addDataEntry(player, DataType.QUIT, loc, player.getAddress().getAddress().getHostAddress().toString());
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Location from = event.getFrom();
		Location to   = event.getTo();
		if (distance(from, to) > 5)
			plugin.addDataEntry(player, DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ());
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		Location loc = block.getLocation();

		switch (block.getType()) {
			case CHEST:
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
					plugin.addDataEntry(player, DataType.OPEN_CHEST, loc, "");
				break;
			case WOODEN_DOOR:
				plugin.addDataEntry(player, DataType.DOOR_INTERACT, loc, "");
				break;
			case LEVER:
				plugin.addDataEntry(player, DataType.LEVER, loc, "");
				break;
			case STONE_BUTTON:
				plugin.addDataEntry(player, DataType.STONE_BUTTON, loc, "");
				break;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			switch (player.getItemInHand().getType()) {
				case FLINT_AND_STEEL:
					plugin.addDataEntry(player, DataType.FLINT_AND_STEEL, loc, "");
					break;
				case LAVA_BUCKET:
					plugin.addDataEntry(player, DataType.LAVA_BUCKET, loc, "");
					break;
				case WATER_BUCKET:
					plugin.addDataEntry(player, DataType.WATER_BUCKET, loc, "");
					break;
			}
		}
			
		
	}
	
	private double distance(Location from, Location to) {
        return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
    }

}

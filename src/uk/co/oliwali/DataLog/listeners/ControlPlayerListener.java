package uk.co.oliwali.DataLog.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Config;

public class ControlPlayerListener extends PlayerListener {
	
	public void onPlayerChat(PlayerChatEvent event) {
		if (DataLog.checkRules(event.getPlayer(), DataType.CHAT, event.getPlayer().getLocation(), event.getMessage()))
			event.setCancelled(true);
	}
	
	/**
	 * Contains processing of the command filter in {@link Config}
	 */
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		Location loc  = player.getLocation();
		String command = event.getMessage().split(" ")[0];
		//Check if command is in filter list or not
		if (!Config.CommandFilter.contains(command) && DataLog.checkRules(player, DataType.COMMAND, loc, event.getMessage()))
			event.setCancelled(true);
	}
	
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Location from = event.getFrom();
		Location to   = event.getTo();
		if (DataLog.checkRules(event.getPlayer(), DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ()))
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

		switch (block.getType()) {
			case CHEST:
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK && DataLog.checkRules(player, DataType.OPEN_CHEST, loc, ""))
					event.setCancelled(true);
				break;
			case WOODEN_DOOR:
				if (DataLog.checkRules(player, DataType.DOOR_INTERACT, loc, ""))
					event.setCancelled(true);
				break;
			case LEVER:
				if (DataLog.checkRules(player, DataType.LEVER, loc, ""))
					event.setCancelled(true);
				break;
			case STONE_BUTTON:
				if (DataLog.checkRules(player, DataType.STONE_BUTTON, loc, ""))
					event.setCancelled(true);
				break;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			switch (player.getItemInHand().getType()) {
				case FLINT_AND_STEEL:
					if (DataLog.checkRules(player, DataType.FLINT_AND_STEEL, loc, ""))
						event.setCancelled(true);
					break;
				case LAVA_BUCKET:
					if (DataLog.checkRules(player, DataType.LAVA_BUCKET, loc, ""))
						event.setCancelled(true);
					break;
				case WATER_BUCKET:
					if (DataLog.checkRules(player, DataType.WATER_BUCKET, loc, ""))
						event.setCancelled(true);
					break;
			}
		}
		
	}

}

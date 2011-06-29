package uk.co.oliwali.DataLog.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.block.SnowFormEvent;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.database.DataType;
import uk.co.oliwali.DataLog.util.BlockUtil;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;

public class DLBlockListener extends BlockListener {
	
	public DataLog plugin;

	public DLBlockListener(DataLog dataLog) {
		plugin = dataLog;
	}
	
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		if (!Config.blockFilter.contains(block.getTypeId()))
			DataManager.addEntry(player, DataType.BLOCK_BREAK, loc, BlockUtil.getBlockString(block));
		
		//If this block is one we have set to report about, lets report it!
		if (Config.reportBlocks.contains(block.getTypeId()) && !Config.reportGroups.isEmpty()) {
			String message = Config.reportMessage;
			message = message.replace("%PLAYER%", player.getName());
			message = message.replace("%BLOCK%", block.getType().name().toLowerCase());
			message = message.replace("%LOC%", "x: " + block.getX() + " y: " + block.getY() + " z: " + block.getZ());
			message = message.replace("%WORLD%", loc.getWorld().getName());
			for (Player p : plugin.getServer().getOnlinePlayers()) 
				for (String group : Config.reportGroups) 
					if (Permission.hasGroup(loc.getWorld().getName(), p, group)) {
						p.sendMessage(message);
						//Only report once
						break;
					}
		}
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		Location loc  = block.getLocation();
		if (block.getTypeId() == Config.toolBlock && DataLog.playerSessions.get(player).isUsingTool()) {
			DataManager.toolSearch(player, loc);
			event.setCancelled(true);
		}
		else if (!Config.blockFilter.contains(block.getTypeId()))
			DataManager.addEntry(player, DataType.BLOCK_PLACE, loc, BlockUtil.getBlockString(event.getBlockReplacedState()) + "-" + BlockUtil.getBlockString(block));
	}
	
	public void onSignChange(SignChangeEvent event) {
		if (event.isCancelled())
			return;
        Player player = event.getPlayer();
    	Location loc  = event.getBlock().getLocation();
        String text = "";
        for (String line : event.getLines()) {
            text = text + "|" + line;
        }
        DataManager.addEntry(player, DataType.SIGN_PLACE, loc, text);
	}
	
	public void onSnowForm(SnowFormEvent event) {
		if (event.isCancelled())
			return;
		DataManager.addEntry("Environment", DataType.SNOW_FORM, event.getBlock().getLocation(), "0");
	}
	
	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled())
			return;
		DataManager.addEntry("Environment", DataType.BLOCK_BURN, event.getBlock().getLocation(), Integer.toString(event.getBlock().getTypeId()));
	}
	
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		if (event.getChangedTypeId() == 18)
			DataManager.addEntry("Environment", DataType.LEAF_DECAY, event.getBlock().getLocation(), "18");
	}

}

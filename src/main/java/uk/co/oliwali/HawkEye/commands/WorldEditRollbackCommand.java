package uk.co.oliwali.HawkEye.commands;

import org.bukkit.util.Vector;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.Rollback.RollbackType;
import uk.co.oliwali.HawkEye.SearchParser;
import uk.co.oliwali.HawkEye.callbacks.RollbackCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery.SearchDir;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Rolls back actions inside a WorldEdit selection according to the player's specified input.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class WorldEditRollbackCommand extends BaseCommand {

	public WorldEditRollbackCommand() {
		name = "werollback";
		argLength = 1;
		usage = "<parameters> <- rollback in WorldEdit area";
	}
	
	public boolean execute() {
		
		//Check if player already has a rollback processing
		if (session.doingRollback()) {
			Util.sendMessage(sender, "&cYou already have a rollback command processing!");
			return true;
		}
		
		//Check if WorldEdit is enabled
		if (plugin.worldEdit == null) {
			Util.sendMessage(sender, "&7WorldEdit&c is not enabled, unable to perform rollbacks in selected region");
			return true;
		}
		
		//Check if the WorldEdit selection is complete
		Region region = null;
		try {
			LocalPlayer lp = new BukkitPlayer(plugin.worldEdit, plugin.worldEdit.getWorldEdit().getServer(), player);
			region = plugin.worldEdit.getWorldEdit().getSession(lp).getRegionSelector().getRegion();
		} catch (IncompleteRegionException e) {
			Util.sendMessage(sender, "&cPlease complete your selection before doing a &7WorldEdit&c rollback!");
			return true;
		}
		
		//Parse arguments
		SearchParser parser = null;
		try {
			
			parser = new SearchParser(player, args);
			
			//Check that supplied actions can rollback
			if (parser.actions.size() > 0) {
				for (DataType type : parser.actions)
					if (!type.canRollback()) throw new IllegalArgumentException("You cannot rollback that action type: &7" + type.getConfigName());
			}
			//If none supplied, add in all rollback types
			else {
				for (DataType type : DataType.values())
					if (type.canRollback()) parser.actions.add(type);
			}
			
		} catch (IllegalArgumentException e) {
			Util.sendMessage(sender, "&c" + e.getMessage());
			return true;
		}
		
		//Set WorldEdit locations
		parser.minLoc = new Vector(region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
		parser.maxLoc = new Vector(region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
		
		//Create new SearchQuery with data
		new SearchQuery(new RollbackCallback(session, RollbackType.GLOBAL), parser, SearchDir.DESC);
		return true;
		
	}
	
	public void moreHelp() {
		Util.sendMessage(sender, "&cRolls back all changes inside a WorldEdit selection");
		Util.sendMessage(sender, "&cParameters are the same as a normal rollback command");
		Util.sendMessage(sender, "&cDoes not support polygon selections.");
	}
	
	public boolean permission() {
		return Permission.rollback(sender);
	}

}
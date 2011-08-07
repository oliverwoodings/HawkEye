package uk.co.oliwali.DataLog.commands;

import org.bukkit.util.Vector;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.regions.Region;

import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.SearchParser;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchDir;
import uk.co.oliwali.DataLog.database.SearchQuery.SearchType;
import uk.co.oliwali.DataLog.database.SearchQuery;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Rolls back actions inside a WorldEdit selection according to the player's specified input.
 * Error handling for user input is done using exceptions to keep code neat.
 * @author oliverw92
 */
public class WorldEditRollbackCommand extends BaseCommand {

	public WorldEditRollbackCommand() {
		name = "werollback";
		argLength = 1;
		bePlayer = true;
		usage = "<parameters> <- rollback actions. Type &c/dl rollbackhelp&7 for more info";
	}
	
	public boolean execute() {
		
		//Check if player already has a rollback processing
		if (session.getRollbackResults() != null) {
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
			region = plugin.worldEdit.getSession((LocalPlayer)player).getRegionSelector().getRegion();
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
		Thread thread = new SearchQuery(SearchType.SEARCH, parser, SearchDir.ASC);
		thread.start();
		return true;
		
	}
	
	public boolean permission() {
		return Permission.rollback(sender);
	}

}
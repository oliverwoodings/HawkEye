package uk.co.oliwali.DataLog;

import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import uk.co.oliwali.DataLog.database.DataEntry;

public class PlayerSession {

	private CommandSender sender;
	private List<DataEntry> searchResults = null;
	private List<DataEntry> rollbackResults  = null;
	private List<BlockState> rollbackUndo = null;
	private boolean usingTool = false;
	private DamageCause lastDamageCause = null;
	private Entity lastAttacker = null;
	
	public PlayerSession(CommandSender sender) {
		this.sender = sender;
	}

	public CommandSender getSender() {
		return sender;
	}
	public void setSender(CommandSender sender) {
		this.sender = sender;
	}
	
	public List<DataEntry> getSearchResults() {
		return searchResults;
	}
	public void setSearchResults(List<DataEntry> searchResults) {
		this.searchResults = searchResults;
	}

	public List<DataEntry> getRollbackResults() {
		return rollbackResults;
	}
	public void setRollbackResults(List<DataEntry> rollbackResults) {
		this.rollbackResults = rollbackResults;
	}

	public List<BlockState> getRollbackUndo() {
		return rollbackUndo;
	}
	public void setRollbackUndo(List<BlockState> rollbackUndo) {
		this.rollbackUndo = rollbackUndo;
	}

	public boolean isUsingTool() {
		return usingTool;
	}
	public void setUsingTool(boolean usingTool) {
		this.usingTool = usingTool;
	}
	
	public DamageCause getLastDamageCause() {
		return lastDamageCause;
	}
	public void setLastDamageCause(DamageCause lastDamageCause) {
		this.lastDamageCause = lastDamageCause;
	}
	
	public Entity getLastAttacker() {
		return lastAttacker;
	}
	public void setLastAttacker(Entity lastAttacker) {
		this.lastAttacker = lastAttacker;
	}

}

package uk.co.oliwali.DataLog;

import java.util.List;

import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;

import uk.co.oliwali.DataLog.database.DataEntry;

public class PlayerSession {

	private CommandSender sender;
	private List<DataEntry> searchResults = null;
	private List<DataEntry> rollbackResults  = null;
	private List<BlockState> rollbackUndo = null;
	private boolean usingTool = false;
	
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

}

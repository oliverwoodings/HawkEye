package uk.co.oliwali.HawkEye;

import java.util.List;

import org.bukkit.command.CommandSender;

import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;

/**
 * Stores data specific to each player on the server.
 * This class is persistent over play quit and rejoins, but not over server reboots
 * @author oliverw92
 */
public class PlayerSession {

	private CommandSender sender;
	private List<DataEntry> searchResults = null;
	private List<DataEntry> rollbackResults = null;
	private boolean usingTool = false;
	private boolean doingRollback = false;
	private String[] toolCommand = Config.DefaultToolCommand;
	private boolean inPreview = false;

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

	public boolean isUsingTool() {
		return usingTool;
	}
	public void setUsingTool(boolean usingTool) {
		this.usingTool = usingTool;
	}

	public boolean doingRollback() {
		return doingRollback;
	}
	public void setDoingRollback(boolean doingRollback) {
		this.doingRollback = doingRollback;
	}

	public String[] getToolCommand() {
		return toolCommand;
	}
	public void setToolCommand(String[] toolCommand) {
		this.toolCommand = toolCommand;
	}

	public boolean isInPreview() {
		return inPreview;
	}
	public void setInPreview(boolean inPreview) {
		this.inPreview = inPreview;
	}

}

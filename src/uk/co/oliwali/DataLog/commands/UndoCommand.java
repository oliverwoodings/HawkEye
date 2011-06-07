package uk.co.oliwali.DataLog.commands;


import java.util.List;

import org.bukkit.block.BlockState;

import uk.co.oliwali.DataLog.DataManager;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class UndoCommand extends BaseCommand {

	public UndoCommand() {
		name = "undo";
		bePlayer = true;
		usage = "<- reverses your previous rollback";
	}
	
	public boolean execute() {
		List<BlockState> undoList = DataManager.undoResults.get(sender);
		if (undoList == null)
			Util.sendMessage(sender, "&cNo previous rollback to undo!");
		else {
			Util.sendMessage(sender, "&cUndoing rollback (&7" +  undoList.size() + " edits&7)...");
			for (BlockState block : undoList.toArray(new BlockState[0]))
				block.update();
			Util.sendMessage(sender, "&cUndo complete");
		}
		return true;
	}

	public boolean permission() {
		return Permission.rollback(sender);
	}
	
}
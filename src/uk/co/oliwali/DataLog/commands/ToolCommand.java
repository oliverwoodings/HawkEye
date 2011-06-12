package uk.co.oliwali.DataLog.commands;

import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class ToolCommand extends BaseCommand {

	public ToolCommand() {
		name = "tool";
		argLength = 0;
		bePlayer = true;
		usage = " <- enables/disables the searching tool";
	}
	
	public boolean execute() {
		if (!session.isUsingTool()) {
			session.setUsingTool(true);
			if (!player.getInventory().contains(Config.toolBlock)) {
				ItemStack stack = new ItemStack(Config.toolBlock, 1);
				if (player.getInventory().firstEmpty() == -1)
					player.getWorld().dropItem(player.getLocation(), stack);
				else
					player.getInventory().addItem(stack);
			}
			Util.sendMessage(sender, "&cDataLog tool enabled! &7Left click a block or place the tool to get infomation");
		}
		else {
			session.setUsingTool(false);
			Util.sendMessage(sender, "&cDataLog tool disabled");
		}
		return true;
	}

	public boolean permission() {
		return Permission.tool(sender);
	}
	
}
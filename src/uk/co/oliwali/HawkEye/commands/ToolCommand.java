package uk.co.oliwali.HawkEye.commands;

import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Enables or disables search tool for players
 * @author oliverw92
 */
public class ToolCommand extends BaseCommand {

	public ToolCommand() {
		name = "tool";
		argLength = 0;
		usage = " <- enables/disables the searching tool";
	}
	
	public boolean execute() {
		if (!session.isUsingTool()) {
			session.setUsingTool(true);
			if (!player.getInventory().contains(BlockUtil.itemStringToStack(Config.ToolBlock, 1))) {
				ItemStack stack = BlockUtil.itemStringToStack(Config.ToolBlock, 1);
				int first = player.getInventory().firstEmpty();
				if (first == -1)
					player.getWorld().dropItem(player.getLocation(), stack);
				else {
					player.getInventory().setItem(first, player.getInventory().getItemInHand());
					player.getInventory().setItemInHand(stack);
				}
			}
			Util.sendMessage(sender, "&cHawkEye tool enabled! &7Left click a block or place the tool to get information");
		}
		else {
			session.setUsingTool(false);
			Util.sendMessage(sender, "&cHawkEye tool disabled");
		}
		return true;
	}
	
	public void moreHelp() {
		Util.sendMessage(sender, "&cGives you the HawkEye tool. You can use this to see changes at specific places");
		Util.sendMessage(sender, "&cLeft click a block or place the tool to get information");
	}

	public boolean permission() {
		return Permission.tool(sender);
	}
	
}
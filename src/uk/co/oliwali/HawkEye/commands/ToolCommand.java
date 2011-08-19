package uk.co.oliwali.HawkEye.commands;

import org.bukkit.inventory.Inventory;
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
		bePlayer = true;
		usage = " <- enables/disables the searching tool";
	}
	
	public boolean execute() {
		if (!session.isUsingTool()) {
			Inventory inv = player.getInventory();
			session.setUsingTool(true);
			ItemStack stack = BlockUtil.itemStringToStack(Config.ToolBlock, 1);
			if (!inv.contains(stack)) {
				int first = inv.firstEmpty();
				if (first == -1)
					player.getWorld().dropItem(player.getLocation(), stack);
				else inv.setItem(first, stack);
			}
			ItemStack back = player.getItemInHand().clone();
			int slot = inv.first(stack);
			player.setItemInHand(inv.getItem(inv.first(stack)));
			if (back.getAmount() == 0) inv.clear(slot);
			else inv.setItem(slot, back);
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
package uk.co.oliwali.actions;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ContainerBlock;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

public class ContainerAction extends BaseAction {
	
	private String diff;
	
	public ContainerAction(String diff, DataType type) {
		this.diff = diff;
		this.type = type;
	}
	
	public String getStringData() {
		return InventoryUtil.createChangeString(InventoryUtil.interpretDifferenceString(diff));
	}

	public String getSqlData() {
		return diff;
	}

	public boolean rollback(Block block) {
		if (block.getType() != Material.CHEST) return false;
		Inventory inv = ((ContainerBlock)block.getState()).getInventory();
		List<HashMap<String,Integer>> ops = InventoryUtil.interpretDifferenceString(diff);
		//Handle the additions
		if (ops.size() > 0) {
			for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(0)))
				inv.removeItem(stack);
		}
		//Handle subtractions
		if (ops.size() > 1) {
			for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(1)))
				inv.addItem(stack);
		}
		return true;
	}

	public void interpretSqlData(String data) {
		this.diff = data;
	}

}

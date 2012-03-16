package uk.co.oliwali.HawkEye.entry;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

/**
 * Represents a container transaction as created in {@MonitorInventoryListener}
 * @author oliverw92
 */
public class ContainerEntry extends DataEntry {

	public ContainerEntry() { }

	public ContainerEntry(Player player, Location location, String diff) {
		data = diff;
		setInfo(player, DataType.CONTAINER_TRANSACTION, location);
	}
	public ContainerEntry(String player, Location location, String diff) {
		data = diff;
		setInfo(player, DataType.CONTAINER_TRANSACTION, location);
	}

	@Override
	public String getStringData() {
		return InventoryUtil.createChangeString(InventoryUtil.interpretDifferenceString(data));
	}

	@Override
	public boolean rollback(Block block) {
		if (!(block instanceof InventoryHolder)) return false;
		Inventory inv = ((InventoryHolder) block.getState()).getInventory();
		List<HashMap<String,Integer>> ops = InventoryUtil.interpretDifferenceString(data);
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

	@Override
	public boolean rebuild(Block block) {
		if (!(block instanceof InventoryHolder)) return false;
		Inventory inv = ((InventoryHolder) block.getState()).getInventory();
		List<HashMap<String,Integer>> ops = InventoryUtil.interpretDifferenceString(data);
		//Handle the additions
		if (ops.size() > 0) {
			for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(0)))
				inv.addItem(stack);
		}
		//Handle subtractions
		if (ops.size() > 1) {
			for (ItemStack stack : InventoryUtil.uncompressInventory(ops.get(1)))
				inv.removeItem(stack);
		}
		return true;
	}

}

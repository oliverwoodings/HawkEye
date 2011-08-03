package uk.co.oliwali.DataLog.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkitcontrib.event.inventory.InventoryCloseEvent;
import org.bukkitcontrib.event.inventory.InventoryListener;
import org.bukkitcontrib.event.inventory.InventoryOpenEvent;

import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.InventoryUtil;

/**
 * Inventory listener class for HawkEye
 * Requires Spout to be present
 * @author oliverw92
 */
public class MonitorInventoryListener extends InventoryListener {
	
	private HashMap<Player, HashMap<String,Integer>> containers = new HashMap<Player, HashMap<String,Integer>>();

	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.isCancelled() || event.getLocation() == null) return;
		containers.put(event.getPlayer(), InventoryUtil.compressInventory(event.getInventory().getContents()));
	}
	
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = event.getPlayer();
		if (event.isCancelled() || !containers.containsKey(player)) return;
		HashMap<String,Integer> after = InventoryUtil.compressInventory(event.getInventory().getContents());
		String diff = InventoryUtil.createDifferenceString(containers.get(player), after);
		DataManager.addEntry(player, DataType.CONTAINER_TRANSACTION, event.getLocation(), diff);
		containers.remove(player);
	}
	
}

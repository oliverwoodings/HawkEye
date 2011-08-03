package uk.co.oliwali.DataLog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
	
	/**
	 * Compress an ItemStack[] into a HashMap of the item string and the total amount of that item
	 * Uses {@BlockUtil} to get the item string
	 * @param inventory ItemStack[] to compress
	 * @return HashMap<String,Integer>
	 */
	public static HashMap<String,Integer> compressInventory(ItemStack[] inventory) {
		HashMap<String,Integer> items = new HashMap<String,Integer>();
		for (ItemStack item : inventory) {
			String iString = BlockUtil.getItemString(item);
			if (items.containsKey(iString)) items.put(iString, items.get(iString) + item.getAmount());
			else items.put(iString, item.getAmount());
		}
		return items;
	}
	
	/**
	 * Takes two compressed inventories and returns a string representation of the difference
	 * @param before HashMap<String,Integer> of inventory before changes
	 * @param after HashMap<String,Integer> of inventory after changes
	 * @return String in the form item:data,amount&item:data,amount@item:data,amount&item:data,amount
	 */
	public static String getDifferenceString(HashMap<String,Integer> before, HashMap<String,Integer> after) {
		List<String> add = new ArrayList<String>();
		List<String> sub = new ArrayList<String>();
		for (Entry<String, Integer> item : before.entrySet()) {
			//If the item does not appear after changes
		    if (!after.containsKey(item.getKey())) sub.add(item.getKey() + "," + item.getValue());
		    //If the item is smaller after changes
		    else if (item.getValue() > after.get(item.getKey())) sub.add(item.getKey() + "," + (item.getValue() - after.get(item.getKey())));
		    //If the item is larger after changes
		    else if (item.getValue() < after.get(item.getKey())) add.add(item.getKey() + "," + (after.get(item.getKey()) - item.getValue()));
		}
		for (Entry<String, Integer> item : before.entrySet()) {
			//If the item does not appear before changes
			if (!before.containsKey(item.getKey())) add.add(item.getKey() + "," + item.getValue());
		}
		return Util.join(add, "&") + "@" + Util.join(sub, "&");
	}

}

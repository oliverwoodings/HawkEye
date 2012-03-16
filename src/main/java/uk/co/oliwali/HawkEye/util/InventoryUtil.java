package uk.co.oliwali.HawkEye.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.InventoryHolder;
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
			if (item == null) continue;
			String iString = BlockUtil.getItemString(item);
			if (items.containsKey(iString)) items.put(iString, items.get(iString) + item.getAmount());
			else items.put(iString, item.getAmount());
		}
		return items;
	}

	/**
	 * Uncompress an inventory back into proper ItemStacks
	 * @param comp Compressed HashMap inventory
	 * @return ItemStack array
	 */
	public static ItemStack[] uncompressInventory(HashMap<String,Integer> comp) {
		List<ItemStack> inv = new ArrayList<ItemStack>();
		for (Entry<String, Integer> item : comp.entrySet()) {
			int i = item.getValue();
			while (i > 0) {
				if (i < 64)	inv.add(BlockUtil.itemStringToStack(item.getKey(), i));
				else inv.add(BlockUtil.itemStringToStack(item.getKey(), 64));
				i = i-64;
			}
		}
		return inv.toArray(new ItemStack[0]);
	}

	/**
	 * Takes two compressed inventories and returns a string representation of the difference
	 * @param before HashMap<String,Integer> of inventory before changes
	 * @param after HashMap<String,Integer> of inventory after changes
	 * @return String in the form item:data,amount&item:data,amount@item:data,amount&item:data,amount where the first part is additions and second is subtractions
	 */
	public static String createDifferenceString(HashMap<String,Integer> before, HashMap<String,Integer> after) {
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
		for (Entry<String, Integer> item : after.entrySet()) {
			//If the item does not appear before changes
			if (!before.containsKey(item.getKey())) add.add(item.getKey() + "," + item.getValue());
		}
		return Util.join(add, "&") + "@" + Util.join(sub, "&");
	}

	/**
	 * Takes an inventory difference string and forms two HashMaps containing the compressed inventory forms of the additions and subtractions
	 * @param diff The difference string to be processed
	 * @return a List of two HashMaps containing the additions and subtractions. First list element is adds, second is subs.
	 */
	public static List<HashMap<String,Integer>> interpretDifferenceString(String diff) {
		List<HashMap<String,Integer>> ops = new ArrayList<HashMap<String,Integer>>();
		for (String changes : diff.split("@")) {
			HashMap<String,Integer> op = new HashMap<String,Integer>();
			for (String change : changes.split("&")) {
				if (change.length() == 0) continue;
				String[] item = change.split(",");
				op.put(item[0], Integer.parseInt(item[1]));
			}
			ops.add(op);
		}
		if (ops.size() == 1) ops.add(new HashMap<String,Integer>());
		return ops;
	}

	/**
	 * Creates a readable string representing the changes of a difference string
	 * @param ops additions and subtractions as supplied by interpretDifferenceString
	 * @return
	 */
	public static String createChangeString(List<HashMap<String,Integer>> ops) {

		if (ops.size() == 0) return "";
		String changeString = "";

		//Loop through ops
		List<String> add = new ArrayList<String>();
		for (Entry<String, Integer> item : ops.get(0).entrySet())
			add.add(item.getValue() + "x " + BlockUtil.getBlockStringName(item.getKey()));
		List<String> sub = new ArrayList<String>();
		for (Entry<String, Integer> item : ops.get(1).entrySet())
			sub.add(item.getValue() + "x " + BlockUtil.getBlockStringName(item.getKey()));

		//Build string
		if (add.size() > 0) changeString += "&a+(" + Util.join(add, ", ") + ")";
		if (sub.size() > 0) changeString += "&4-(" + Util.join(sub, ", ") + ")";

		return changeString;

	}

	/**
	 * Method for getting complete inventory from a ContainerBlock
	 * Works around a bug in Minecraft that sometimes returns only half the chest
	 * Thanks to N3X15 and the BigBrother team for letting me use this
	 * @param container block to check
	 * @return ItemStack[] of both inventories merged
	 * @author N3X15
	 */
    public static ItemStack[] getContainerContents(InventoryHolder container) {

    	//If it isn't a chest, there is no issue!
    	if (!(container instanceof Chest)) return container.getInventory().getContents();

    	Chest chest = (Chest)container;
        Chest second = null;

        //Iterate through nearby blocks to find any other chests
        if (chest.getBlock().getRelative(BlockFace.NORTH).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.NORTH).getState();
        else if (chest.getBlock().getRelative(BlockFace.SOUTH).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.SOUTH).getState();
        else if (chest.getBlock().getRelative(BlockFace.EAST).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.EAST).getState();
        else if (chest.getBlock().getRelative(BlockFace.WEST).getType() == Material.CHEST)
            second = (Chest) chest.getBlock().getRelative(BlockFace.WEST).getState();

        //If we can't find a second chest, just return this one
        if (second == null) {
            return chest.getInventory().getContents();
        }
        else {

            //I think it would be good, to consistently return same chest
            //contents, regardless of what
            //block was clicked on. That means, we must determine, which part
            //of chest comes first, and which second.
            //I choose the one, which has lower X coordinate. If they are same,
            //than it's the one with lower Z coordinate.
            //I believe it can be easily checked with this trick:
            ItemStack[] result = new ItemStack[54];
            ItemStack[] firstHalf;
            ItemStack[] secondHalf;

            if ((chest.getX() + chest.getZ()) < (second.getX() + second.getZ())) {
                firstHalf = chest.getInventory().getContents();
                secondHalf = second.getInventory().getContents();
            } else {
                firstHalf = second.getInventory().getContents();
                secondHalf = chest.getInventory().getContents();
            }

            //Merge them
            for (int i = 0; i < 27; i++) {
                result[i] = firstHalf[i];
                result[i + 27] = secondHalf[i];
            }

            return result;
        }

    }

}

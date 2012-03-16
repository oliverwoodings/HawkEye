package uk.co.oliwali.HawkEye.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Contains utilities for manipulating blocks without losing data
 * @author oliverw92
 */
public class BlockUtil {

	/**
	 * Gets the block in 'string form'.
	 * e.g. blockid:datavalue
	 * @param block BlockState of the block you wish to convert
	 * @return string representing the block
	 */
	public static String getBlockString(Block block) {
		return getBlockString(block.getState());
	}
	public static String getBlockString(BlockState block) {
		if (block.getRawData() != 0)
			return block.getTypeId() + ":" + block.getRawData();
		return Integer.toString(block.getTypeId());
	}

	/**
	 * Same as getBlockString() except for ItemStack
	 * @param stack ItemStack you wish to convert
	 * @return string representing the item
	 */
	public static String getItemString(ItemStack stack) {
		if (stack.getData() != null && stack.getData().getData() != 0)
			return stack.getTypeId() + ":" + stack.getData().getData();
		return Integer.toString(stack.getTypeId());
	}

	/**
	 * Converts an item string into an ItemStack
	 * @param item item string representing the material and data
	 * @param amount
	 * @return an ItemStack
	 */
	public static ItemStack itemStringToStack(String item, Integer amount) {
		String[] itemArr = item.split(":");
		ItemStack stack = new ItemStack(Integer.parseInt(itemArr[0]), amount);
		if (itemArr.length > 1)
			stack.setData(new MaterialData(Integer.parseInt(itemArr[0]), Byte.parseByte(itemArr[1])));
		return stack;
	}

	/**
	 * Returns the name of the block, with its data if applicable
	 * @param blockData
	 * @return
	 */
	public static String getBlockStringName(String blockData) {
		String[] blockArr = blockData.split(":");
		if (!Util.isInteger(blockArr[0])) return blockData;
		if (blockArr.length > 1)
			return Material.getMaterial(Integer.parseInt(blockArr[0])).name() + ":" + blockArr[1];
		else return Material.getMaterial(Integer.parseInt(blockArr[0])).name();
	}

	/**
	 * Sets the block type and data to the inputted block string
	 * @param block Block to be changed
	 * @param blockData string form of a block
	 */
	public static void setBlockString(Block block, String blockData) {
		String[] blockArr = blockData.split(":");
		if (!Util.isInteger(blockArr[0])) return;
		block.setTypeId(Integer.parseInt(blockArr[0]));
		if (blockArr.length > 1)
			block.setData((byte) Integer.parseInt(blockArr[1]));
	}

	/**
	 * Returns ID section of a block string
	 * @param string
	 * @return int ID
	 */
	public static int getIdFromString(String string) {
		if (!Util.isInteger(string.split(":")[0])) return 0;
		return Integer.parseInt(string.split(":")[0]);
	}

	/**
	 * Returns data section of a block string
	 * @param string
	 * @return int data
	 */
	public static byte getDataFromString(String string) {
		if (string.split(":").length == 1) return 0;
		return (byte)Integer.parseInt(string.split(":")[1]);
	}

}

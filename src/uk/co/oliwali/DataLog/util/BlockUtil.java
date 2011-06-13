package uk.co.oliwali.DataLog.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class BlockUtil {
	
	public static String getBlockString(Block block) {
		return getBlockString(block.getState());
	}
	public static String getBlockString(BlockState block) {
		if (block.getData().getData() != 0)
			return block.getTypeId() + ":" + block.getData();
		return Integer.toString(block.getTypeId());
	}
	
	public static String getBlockStringName(String blockData) {
		String[] blockArr = blockData.split(":");
		Material.getMaterial(Integer.parseInt(blockArr[0]));
		if (blockArr.length > 1)
			return Material.getMaterial(Integer.parseInt(blockArr[0])).name() + ":" + blockArr[1];
		else return Material.getMaterial(Integer.parseInt(blockArr[0])).name();
	}
	
	public static void setBlockString(Block block, String blockData) {
		String[] blockArr = blockData.split(":");
		block.setTypeId(Integer.parseInt(blockArr[0]));
		if (blockArr.length > 1)
			block.setData((byte) Integer.parseInt(blockArr[1]));
	}

}

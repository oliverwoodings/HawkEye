package uk.co.oliwali.HawkEye.entry;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

/**
 * Represents a block-type entry in the database
 * Rollbacks will set the block to the data value
 * @author oliverw92
 */
public class BlockEntry extends DataEntry {

	public BlockEntry() { }

	public BlockEntry(String player, DataType type, Block block) {
		setInfo(player, type, block.getLocation());
		data = BlockUtil.getBlockString(block);
	}
	public BlockEntry(Player player, DataType type, Block block) {
		setInfo(player, type, block.getLocation());
		data = BlockUtil.getBlockString(block);
	}

	@Override
	public String getStringData() {
		return BlockUtil.getBlockStringName(data);
	}

	@Override
	public boolean rollback(Block block) {
		BlockUtil.setBlockString(block, data);
		return true;
	}

	@Override
	public boolean rollbackPlayer(Block block, Player player) {
		player.sendBlockChange(block.getLocation(), BlockUtil.getIdFromString(data), BlockUtil.getDataFromString(data));
		return true;
	}

	@Override
	public boolean rebuild(Block block) {
		if (data == null) return false;
		else block.setTypeId(0);
		return true;
	}

}

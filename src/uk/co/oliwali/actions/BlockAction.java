package uk.co.oliwali.actions;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class BlockAction extends BaseAction {
	
	private String data;
	
	public BlockAction(BlockState block, DataType type) {
		this.data = BlockUtil.getBlockString(block);
		this.type = type;
	}
	
	public String getStringData() {
		return BlockUtil.getBlockStringName(data);
	}

	public String getSqlData() {
		return data;
	}

	public boolean rollback(Block block) {
		BlockUtil.setBlockString(block, data);
		return true;
	}

	public void interpretSqlData(String data) {
		this.data = data;
	}

}

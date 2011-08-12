package uk.co.oliwali.actions;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class BlockChangeAction extends BaseAction {
	
	private String from = null;
	private String to = null;
	
	public BlockChangeAction(BlockState from, BlockState to, DataType type) {
		this.from = BlockUtil.getBlockString(from);
		this.to = BlockUtil.getBlockString(to);
		this.type = type;
	}
	
	public String getStringData() {
		return BlockUtil.getBlockStringName(from) + " changed to " + BlockUtil.getBlockStringName(to);
	}

	public String getSqlData() {
		return from + "-" + to;
	}

	public boolean rollback(Block block) {
		if (from == null)
			block.setType(Material.AIR);
		else
			BlockUtil.setBlockString(block, from);
		return true;
	}

	public void interpretSqlData(String data) {
		if (data.indexOf("-") == -1) {
			from = null;
			to = data;
		}
		else {
			from = data.substring(0, data.indexOf("-"));
			to = data.substring(data.indexOf("-") + 1);
		}
	}

}

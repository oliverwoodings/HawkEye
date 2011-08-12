package uk.co.oliwali.actions;

import org.bukkit.block.Block;

import uk.co.oliwali.HawkEye.DataType;

public abstract class BaseAction {
	
	protected DataType type;
	
	public DataType getDataType() {
		return type;
	}

	public abstract String getStringData();
	
	public abstract String getSqlData();
	
	public abstract boolean rollback(Block block);
	
	public abstract void interpretSqlData(String data);

}

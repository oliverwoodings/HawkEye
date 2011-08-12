package uk.co.oliwali.HawkEye.actions;

import org.bukkit.block.Block;

import uk.co.oliwali.HawkEye.DataType;

public class SimpleAction extends BaseAction {
	
	private String data;
	
	public SimpleAction(String data, DataType type) {
		this.data = data;
		this.type = type;
	}
	
	public String getStringData() {
		return data;
	}

	public String getSqlData() {
		return data;
	}

	public boolean rollback(Block block) {
		return false;
	}

	public void interpretSqlData(String data) {
		this.data = data;
	}

}

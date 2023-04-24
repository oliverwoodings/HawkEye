package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

public class MinecartEntry extends DataEntry {
	public MinecartEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
		super(playerId, timestamp, dataId, typeId, data, plugin, worldId, x, y, z);
	}

	public MinecartEntry(String player, DataType type, Location loc, String[] entityInfo) {
		this.setInfo(player, type, loc);
		this.setEntity(entityInfo);
		this.data = entityInfo[1];
	}
	
	public MinecartEntry(String player, DataType type, Location loc, String[] entityInfo, String diff) {
		this.setInfo(player, type, loc);
		this.setEntity(entityInfo);
		this.data = diff;
	}
	
	public String getStringData() {
		if(this.getType() == DataType.MINECART_TRANSACTION) {
			if(this.data.contains("&")) {
				this.data = InventoryUtil.updateInv(this.data);
			}
			return InventoryUtil.dataToString(this.data);
		}
		return this.data;
	}

	public boolean rollback(Block block) {
		return true;
	}

	public boolean rollbackPlayer(Block block, Player player) {
		return true;
	}

	public boolean rebuild(Block block) {
		return true;
	}
}

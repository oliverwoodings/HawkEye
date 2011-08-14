package uk.co.oliwali.HawkEye.entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import uk.co.oliwali.HawkEye.DataType;

public class SimpleRollbackEntry extends DataEntry {
	
	public SimpleRollbackEntry() { }
	
	public SimpleRollbackEntry(Player player, DataType type, Location loc, String data) {
		setInfo(player, type, loc);
		this.data = data;
	}
	public SimpleRollbackEntry(String player, DataType type, Location loc, String data) {
		setInfo(player, type, loc);
		this.data = data;
	}

	@Override
	public boolean rollback(Block block) {
		block.setType(Material.AIR);
		return true;
	}
	
}

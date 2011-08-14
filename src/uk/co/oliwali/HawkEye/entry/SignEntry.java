package uk.co.oliwali.HawkEye.entry;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.Util;

public class SignEntry extends DataEntry {
	
	private BlockFace facing;
	private boolean wallSign;
	private String[] lines = new String[4];
	
	public SignEntry(Player player, DataType type, Block block) {
		setInfo(player, type, block.getLocation());
		this.facing = ((org.bukkit.material.Sign)block).getFacing();
		this.wallSign = ((org.bukkit.material.Sign)block).isWallSign();
		this.lines = ((Sign)block).getLines();
	}
	
	@Override
	public String getStringData() {
		return Util.join(Arrays.asList(lines), " | ");
	}

	@Override
	public String getSqlData() {
		BASE64Encoder encoder = new BASE64Encoder();
		String[] encoded = new String[4];
		for (int i = 0; i < 4; i++) encoded[i] = encoder.encode(lines[i].getBytes());
		return wallSign + "@" + facing + "@" + Util.join(Arrays.asList(encoded), ",");
	}

	@Override
	public boolean rollback(Block block) {
		
		//If it is a sign place
		if (type == DataType.SIGN_PLACE) block.setTypeId(0);
		
		//if it is a sign break
		else {
			if (wallSign) block.setType(Material.WALL_SIGN);
			else block.setType(Material.SIGN_POST);
			for (int i = 0; i < 4; i++) ((org.bukkit.block.Sign)block).setLine(i, lines[i]);
			((org.bukkit.material.Sign)block).setFacingDirection(facing);
		}
		
		return true;
		
	}

	@Override
	public void interpretSqlData(String data) {
		
		String[] arr = data.split("@");
		//Parse wall sign or not
		if (arr[0].equals("true")) wallSign = true;
		else wallSign = false;
		
		//Parse sign direction
		for (BlockFace face : BlockFace.values())
			if (face.toString().equalsIgnoreCase(arr[1])) facing = face;
		
		//Parse lines
		BASE64Decoder decoder = new BASE64Decoder();
		String[] encLines = arr[2].split(",");
		for (int i = 0; i < 4; i++) {
			try {
				lines[i] = new String(decoder.decodeBuffer(encLines[i]));
			} catch (IOException e) {
				Util.severe("Unable to decode sign data from database");
			}
		}
		
	}

}

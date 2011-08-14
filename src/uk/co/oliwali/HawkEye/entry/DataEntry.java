package uk.co.oliwali.HawkEye.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.HawkEye.DataType;

/**
 * Represents a HawkEye entry entity
 * @author oliverw92
 */
public class DataEntry {

	private String plugin = null;
    
    private int dataId;

    private String date;

    private String player;

    private String world;

    private double x;

    private double y;

    private double z;

    protected DataType type;
    
    protected String data;
    
    public DataEntry() { }
    public DataEntry(Player player, DataType type, Location loc, String data) {
    	setInfo(player, type, loc);
    	this.data = data;
    }
    public DataEntry(String player, DataType type, Location loc, String data) {
    	setInfo(player, type, loc);
    	this.data = data;
    }

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
    public String getPlugin() {
		return plugin;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
    public int getDataId() {
		return dataId;
	}

	public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }

    public void setPlayer(String player) {
        this.player = player;
    }
    public String getPlayer() {
        return player;
    }
    
    public void setType(DataType type) {
    	this.type = type;
    }
    public DataType getType() {
    	return type;
    }

    public void setWorld(String world) {
        this.world = world;
    }
    public String getWorld() {
        return world;
    }

    public void setX(double x) {
        this.x = x;
    }
    public double getX() {
        return x;
    }

    public void setY(double y) {
        this.y = y;
    }
    public double getY() {
        return y;
    }

    public void setZ(double z) {
        this.z = z;
    }
    public double getZ() {
        return z;
    }
    
    public void setData(String data) {
    	this.data = data;
    }
	public String getStringData() {
		return data;
	}
	
	public void interpretSqlData(String data) {
		this.data = data;
	}
	public String getSqlData() {
		return data;
	}
	
	public boolean rollback(Block block) {
		return false;
	}
    
    /**
     * Parses the inputted action into the DataEntry instance
     * @param player
     * @param instance
     * @param type
     * @param loc
     * @param action
     */

	public void setInfo(Player player, DataType type, Location loc) {
		setInfo(player.getName(), type, loc);
	}
	public void setInfo(String player, DataType type, Location loc) {
		setInfo(player, "HawkEye", type, loc);
	}
	public void setInfo(String player, JavaPlugin instance, DataType type, Location loc) {
		setInfo(player, instance.getDescription().getName(), type, loc);
	}
	public void setInfo(String player, String instance, DataType type, Location loc) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    setDate(sdf.format(Calendar.getInstance().getTime()));
	    setPlugin(instance);
		setPlayer(player);
		setType(type);
		setWorld(loc.getWorld().getName());
		setX(loc.getX());
		setY(loc.getY());
		setZ(loc.getZ());
	}

}
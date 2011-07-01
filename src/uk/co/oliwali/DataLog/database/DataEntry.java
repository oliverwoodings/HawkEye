package uk.co.oliwali.DataLog.database;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a DataLog entry entity
 * @author oliverw92
 */
public class DataEntry {

    private int dataid;

    private String plugin;

    private String date;

    private String player;

    private int action;

    private String world;

    private double x;

    private double y;

    private double z;

    private String data;

	public void setDataid(int dataid) {
		this.dataid = dataid;
	}

    public int getDataid() {
		return dataid;
	}

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

    public String getPlugin() {
		return plugin;
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
    
    public void setAction(int action) {
    	this.action = action;
    }
    
    public int getAction() {
    	return action;
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
    
    public String getData() {
    	return data;
    }
    
    /**
     * Parses the inputted data into the DataEntry instance
     * @param player
     * @param instance
     * @param action
     * @param loc
     * @param data
     */
	public void setInfo(String player, JavaPlugin instance, int action, Location loc, String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    setDate(sdf.format(Calendar.getInstance().getTime()));
	    setPlugin(instance.getDescription().getName());
		setPlayer(player);
		setAction(action);
		setWorld(loc.getWorld().getName());
		setX(loc.getX());
		setY(loc.getY());
		setZ(loc.getZ());
		setData(data);
	}

}
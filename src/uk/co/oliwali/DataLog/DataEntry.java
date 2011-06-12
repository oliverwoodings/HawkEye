package uk.co.oliwali.DataLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.avaje.ebean.validation.NotNull;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Entity()
@Table(name="datalog")
public class DataEntry {

    @Id
    @GeneratedValue
    private int dataid;
    
    @NotNull
    private String plugin;

    @NotNull
    private String date;

    @NotNull
    private String player;

	@NotNull
    private int action;
    
    @NotNull
    private String world;

    @NotNull
    private double x;

    @NotNull
    private double y;

    @NotNull
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
    
	public void setInfo(Player player, JavaPlugin instance, int action, Location loc, String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    setDate(sdf.format(Calendar.getInstance().getTime()));
	    setPlugin(instance.getDescription().getName());
		setPlayer(player.getName());
		setAction(action);
		setWorld(loc.getWorld().getName());
		setX(loc.getX());
		setY(loc.getY());
		setZ(loc.getZ());
		setData(data);
	}

}
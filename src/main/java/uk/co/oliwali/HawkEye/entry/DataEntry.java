package uk.co.oliwali.HawkEye.entry;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Represents a HawkEye database entry
 * This class can be extended and overriden by sub-entry classes to allow customisation of rollbacks etc
 * @author oliverw92
 */
public class DataEntry {

	private String plugin = null;

    private int dataId;

    private String date;

    private String player = null;

    private String world;

    private double x;

    private double y;

    private double z;

    private BlockState undoState;

    protected DataType type = null;

    protected String data = null;

    public DataEntry() { }
    public DataEntry(Player player, DataType type, Location loc, String data) {
    	setInfo(player, type, loc);
    	setData(data);
    }
    public DataEntry(String player, DataType type, Location loc, String data) {
    	setInfo(player, type, loc);
    	setData(data);
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

	public BlockState getUndoState() {
		return undoState;
	}
	public void setUndoState(BlockState undoState) {
		this.undoState = undoState;
	}

    /**
     * Returns the entry data in a visually attractive and readable way for an in-game user to read
     * Extending classes can add colours, customise layout etc.
     * @return
     */
	public String getStringData() {
		return data;
	}

	/**
	 * Converts the raw data from the database into the actual data required by the entry
	 * Extending classes can override this to support custom storage methods (e.g. sign data etc)
	 * @param data string to be interpreted
	 */
	public void interpretSqlData(String data) {
		this.data = data;
	}

	/**
	 * Returns the entry data ready for storage in the database
	 * Extending classes can override this method and format the data as they wish
	 * @return string containing data to be stored
	 */
	public String getSqlData() {
		return data;
	}

	/**
	 * Rolls back the data entry on the specified block
	 * Default is to return false, however extending classes can override this and do their own thing
	 * @param block
	 * @return true if rollback is performed, false if it isn't
	 */
	public boolean rollback(Block block) {
		return false;
	}

	/**
	 * Performs a local rollback for the specified player only
	 * Default is to return false, and most extending classes will not override this
	 * If overriding, the method should use Player.sendBlockChange() for sending fake changes
	 * @param block
	 * @param player
	 * @return true if rollback is performed, false if it isn't
	 */
	public boolean rollbackPlayer(Block block, Player player) {
		return false;
	}

	/**
	 * Rebuilds the entry (reapplies it)
	 * Extending classes can implement this method to do custom things
	 * @param block
	 * @return true if rebuild is performed, false it if isn't
	 */
	public boolean rebuild(Block block) {
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
		loc = Util.getSimpleLocation(loc);
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

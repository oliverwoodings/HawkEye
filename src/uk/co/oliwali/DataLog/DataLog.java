package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.listeners.DLBlockListener;
import uk.co.oliwali.DataLog.listeners.DLEntityListener;
import uk.co.oliwali.DataLog.listeners.DLPlayerListener;

public class DataLog extends JavaPlugin {
	
	public String name;
	public String version;
	public Config config;
	public static final Logger log = Logger.getLogger("Minecraft");
	public DLBlockListener blockListener = new DLBlockListener(this);
	public DLEntityListener entityListener = new DLEntityListener(this);
	public DLPlayerListener playerListener = new DLPlayerListener(this);
	
	public void onDisable() {
		Util.info("Version " + version + " disabled!");
	}
	
	public void onEnable() {

		//Set up config and database
		name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        config = new Config(this);
        
        // Register events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_PLACE, blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.SIGN_CHANGE, blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_CHAT, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_DEATH, entityListener, Event.Priority.Monitor, this);
        
        setupDatabase();
        
        Util.info("Version " + version + " enabled!");
        
	}
	
	public void addDataEntry(Player player, DataType dataType, Location loc, String data) {
		if (config.isLogged(dataType)) {
			DataEntry dataEntry = new DataEntry();
			dataEntry.setInfo(player, dataType.getId(), loc, data);
			getDatabase().save(dataEntry);
		}
	}
	
	private void setupDatabase() {
        try {
            getDatabase().find(DataEntry.class).findRowCount();
        } catch (PersistenceException ex) {
            Util.info("Installing database due to first time usage");
            installDDL();
        }
	}
	
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(DataEntry.class);
        return list;
    }

}

package uk.co.oliwali.DataLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.commands.BaseCommand;
import uk.co.oliwali.DataLog.commands.HelpCommand;
import uk.co.oliwali.DataLog.commands.PageCommand;
import uk.co.oliwali.DataLog.commands.SearchCommand;
import uk.co.oliwali.DataLog.commands.TptoCommand;
import uk.co.oliwali.DataLog.listeners.DLBlockListener;
import uk.co.oliwali.DataLog.listeners.DLEntityListener;
import uk.co.oliwali.DataLog.listeners.DLPlayerListener;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Util;

public class DataLog extends JavaPlugin {
	
	public String name;
	public String version;
	public Config config;
	public static Server server;
	public static final Logger log = Logger.getLogger("Minecraft");
	public DLBlockListener blockListener = new DLBlockListener(this);
	public DLEntityListener entityListener = new DLEntityListener(this);
	public DLPlayerListener playerListener = new DLPlayerListener(this);
	public static List<BaseCommand> commands = new ArrayList<BaseCommand>();
	
	public void onDisable() {
		Util.info("Version " + version + " disabled!");
	}
	
	public void onEnable() {

		//Set up config and database
		server = getServer();
		name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        config = new Config(this);
        new DataManager(this);
        
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
        
        //Add commands
        commands.add(new HelpCommand());
        commands.add(new SearchCommand());
        commands.add(new PageCommand());
        commands.add(new TptoCommand());
        
        Util.info("Version " + version + " enabled!");
        
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		if (cmd.getName().equalsIgnoreCase("datalog")) {
			if (args.length == 0)
				args = new String[]{"help"};
			BaseCommand help = null;
			for (BaseCommand command : commands.toArray(new BaseCommand[0])) {
				if (command.name.equalsIgnoreCase("help"))
					help = command;
				if (command.name.equalsIgnoreCase(args[0]))
					return command.run(sender, args, commandLabel);
			}
			return help.run(sender, args, commandLabel);
		}
		return false;
	}
	
	private void setupDatabase() {
		//Check if ebean.properties exists, if not create empty file to hide severe error
		try {
			File props = new File("ebean.properties");
			if (!props.exists())
				props.createNewFile();
		} catch (IOException e) {
			Util.info("Unable to create ebean.properties file");
		}
		
        try {
            getDatabase().find(DataEntry.class).findRowCount();
        } catch (PersistenceException ex) {
            Util.info("Installing database due to first time usage");
            installDDL();
        }
        
        //Update for legacy v0.2 databases
        if (getDatabase().createSqlQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'datalog' AND COLUMN_NAME = 'plugin'").findUnique() == null) {
        	Util.info("v0.2 database detected - updating to v0.3...");
        	getDatabase().createSqlUpdate("ALTER TABLE datalog ADD plugin varchar(255)").execute();
        	getDatabase().createSqlUpdate("UPDATE datalog SET plugin='" + getDescription().getName() + "' WHERE plugin IS NULL").execute();
        	getDatabase().createSqlUpdate("UPDATE datalog SET x=round(x,1), y=round(y,1), z=round(z,1)").execute();
        	Util.info("Database updated to v0.3");
        }
	}
	
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(DataEntry.class);
        return list;
    }

}

package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.commands.BaseCommand;
import uk.co.oliwali.DataLog.commands.HelpCommand;
import uk.co.oliwali.DataLog.commands.HereCommand;
import uk.co.oliwali.DataLog.commands.PageCommand;
import uk.co.oliwali.DataLog.commands.RollbackCommand;
import uk.co.oliwali.DataLog.commands.RollbackHelpCommand;
import uk.co.oliwali.DataLog.commands.SearchCommand;
import uk.co.oliwali.DataLog.commands.SearchHelpCommand;
import uk.co.oliwali.DataLog.commands.ToolCommand;
import uk.co.oliwali.DataLog.commands.TptoCommand;
import uk.co.oliwali.DataLog.commands.UndoCommand;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.listeners.DLBlockListener;
import uk.co.oliwali.DataLog.listeners.DLEntityListener;
import uk.co.oliwali.DataLog.listeners.DLPlayerListener;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
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
	public static HashMap<CommandSender, PlayerSession> playerSessions = new HashMap<CommandSender, PlayerSession>();
	
	/**
	 * Safely shuts down DataLog
	 */
	public void onDisable() {
		DataManager.close();
		Util.info("Version " + version + " disabled!");
	}
	
	/**
	 * Starts up DataLog initiation process
	 */
	public void onEnable() {
		
		Util.info("Starting DataLog initiation process...");

		//Set up config and permissions
        PluginManager pm = getServer().getPluginManager();
		server = getServer();
		name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        config = new Config(this);
        new Permission(this);
        
        //Create player sessions
        for (Player player : server.getOnlinePlayers())
        	playerSessions.put(player, new PlayerSession(player));
        
        //Initiate database connection
        try {
			new DataManager(this);
		} catch (Exception e) {
			Util.severe("Error initiating DataLog database connection, disabling plugin");
			pm.disablePlugin(this);
			return;
		}
		
		//Add console session
		ConsoleCommandSender sender = new ConsoleCommandSender(getServer());
		playerSessions.put(sender, new PlayerSession(sender));
        
        // Register events
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.BLOCK_PLACE, blockListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.BLOCK_BURN, blockListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.BLOCK_PHYSICS, blockListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.SNOW_FORM, blockListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.SIGN_CHANGE, blockListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_CHAT, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_JOIN, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_DEATH, entityListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, entityListener, Event.Priority.Lowest, this);
        
        //Add commands
        commands.add(new HelpCommand());
        commands.add(new SearchCommand());
        commands.add(new PageCommand());
        commands.add(new TptoCommand());
        commands.add(new SearchHelpCommand());
        commands.add(new HereCommand());
        commands.add(new RollbackCommand());
        commands.add(new UndoCommand());
        commands.add(new ToolCommand());
        commands.add(new RollbackHelpCommand());
        
        Util.info("Version " + version + " enabled!");
        
	}
	
	/**
	 * Command manager for DataLog
	 * @param sender - {@link CommandSender}
	 * @param cmd - {@link Command}
	 * @param commandLabel - String
	 * @param args[] - String[]
	 */
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

}

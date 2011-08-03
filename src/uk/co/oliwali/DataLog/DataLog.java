package uk.co.oliwali.DataLog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkitcontrib.BukkitContrib;

import com.sk89q.worldedit.WorldEdit;

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
import uk.co.oliwali.DataLog.commands.WorldEditRollbackCommand;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.listeners.MonitorBlockListener;
import uk.co.oliwali.DataLog.listeners.MonitorContainerAccessListener;
import uk.co.oliwali.DataLog.listeners.MonitorEntityListener;
import uk.co.oliwali.DataLog.listeners.MonitorPlayerListener;
import uk.co.oliwali.DataLog.listeners.ToolBlockListener;
import uk.co.oliwali.DataLog.listeners.ToolPlayerListener;
import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Permission;
import uk.co.oliwali.DataLog.util.Util;

public class DataLog extends JavaPlugin {
	
	public String name;
	public String version;
	public Config config;
	public static Server server;
	public MonitorBlockListener monitorBlockListener = new MonitorBlockListener(this);
	public MonitorEntityListener monitorEntityListener = new MonitorEntityListener(this);
	public MonitorPlayerListener monitorPlayerListener = new MonitorPlayerListener(this);
	public ToolBlockListener toolBlockListener = new ToolBlockListener();
	public ToolPlayerListener toolPlayerListener = new ToolPlayerListener();
	public static List<BaseCommand> commands = new ArrayList<BaseCommand>();
	public WorldEdit worldEdit = null;
	public BukkitContrib bukkitContrib = null;
	private static HashMap<CommandSender, PlayerSession> playerSessions = new HashMap<CommandSender, PlayerSession>();
	
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
		
        //Check if WorldEdit is loaded
        Plugin we = pm.getPlugin("WorldEdit");
        if (we != null) {
        	worldEdit = (WorldEdit)we;
        	Util.info("WorldEdit found, selection rollbacks enabled");
        }
        
        //Check if BukkitContrib is loaded. If not, download and enable it
	    if (Config.isLogged(DataType.CHEST_TRANSACTION) && pm.getPlugin("BukkitContrib") == null) {
	        try {
	            download(new URL("http://bit.ly/autoupdateBukkitContrib"), new File("plugins" + File.separator + "BukkitContrib.jar"));
	            pm.loadPlugin(new File("plugins" + File.separator + "BukkitContrib.jar"));
	            pm.enablePlugin(pm.getPlugin("BukkitContrib"));
	        } catch (final Exception ex) {
	            Util.info("WARNING! Unable to download and install BukkitContrib. Container logging disabled until BukkitContrib is available");
	        }
		}
	    Plugin bc = pm.getPlugin("BukkitContrib");
	    if (bc != null) {
	    	bukkitContrib = (BukkitContrib)bc;
	    	Util.info("BukkitContrib found, container loggin enabled");
	    }
        
        // Register monitor events
        if (Config.isLogged(DataType.BLOCK_BREAK)) pm.registerEvent(Type.BLOCK_BREAK, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.BLOCK_PLACE)) pm.registerEvent(Type.BLOCK_PLACE, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.BLOCK_BURN)) pm.registerEvent(Type.BLOCK_BURN, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.LEAF_DECAY)) pm.registerEvent(Type.LEAVES_DECAY, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.BLOCK_FORM)) pm.registerEvent(Type.BLOCK_FORM, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.SIGN_PLACE)) pm.registerEvent(Type.SIGN_CHANGE, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.BLOCK_FADE)) pm.registerEvent(Type.BLOCK_FADE, monitorBlockListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.COMMAND)) pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.CHAT)) pm.registerEvent(Type.PLAYER_CHAT, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.JOIN)) pm.registerEvent(Type.PLAYER_JOIN, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.QUIT)) pm.registerEvent(Type.PLAYER_QUIT, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.TELEPORT)) pm.registerEvent(Type.PLAYER_TELEPORT, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_INTERACT, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.ITEM_DROP)) pm.registerEvent(Type.PLAYER_DROP_ITEM, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.ITEM_PICKUP)) pm.registerEvent(Type.PLAYER_PICKUP_ITEM, monitorPlayerListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.PVP_DEATH) || Config.isLogged(DataType.OTHER_DEATH)) pm.registerEvent(Type.ENTITY_DAMAGE, monitorEntityListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.PVP_DEATH) || Config.isLogged(DataType.OTHER_DEATH)) pm.registerEvent(Type.ENTITY_DEATH, monitorEntityListener, Event.Priority.Monitor, this);
        if (Config.isLogged(DataType.EXPLOSION)) pm.registerEvent(Type.ENTITY_EXPLODE, monitorEntityListener, Event.Priority.Monitor, this);
        
        //Register tool events
        pm.registerEvent(Type.BLOCK_PLACE, toolBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_INTERACT, toolPlayerListener, Event.Priority.Highest, this);
        
        //Register BukkitContrib events
        if (bukkitContrib != null) {
        	pm.registerEvent(Type.CUSTOM_EVENT, new MonitorContainerAccessListener(), Event.Priority.Monitor, this);
        }
        
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
        commands.add(new WorldEditRollbackCommand());
        
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
					return command.run(this, sender, args, commandLabel);
			}
			return help.run(this, sender, args, commandLabel);
		}
		return false;
	}
	
	/**
	 * Get a PlayerSession from the list
	 */
	public static PlayerSession getSession(CommandSender player) {
		PlayerSession session = playerSessions.get(player);
		if (session == null)
			session = addSession(player);
		return session;
	}
	
	/**
	 * Adds a PlayerSession to the list
	 */
	public static PlayerSession addSession(CommandSender player) {
		PlayerSession session = new PlayerSession(player);
		playerSessions.put(player, session);
		return session;
	}
	
	/**
	 * Downloads a file from the internet
	 * @param url URL of the file to download
	 * @param file location where the file should be downloaded to
	 * @throws IOException
	 */
	public static void download(URL url, File file) throws IOException {
	    if (!file.getParentFile().exists())
	        file.getParentFile().mkdir();
	    if (file.exists())
	        file.delete();
	    file.createNewFile();
	    int size = url.openConnection().getContentLength();
	    Util.info("Downloading " + file.getName() + " (" + size / 1024 + "kb) ...");
	    InputStream in = url.openStream();
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
	    byte[] buffer = new byte[1024];
	    int len, downloaded = 0, msgs = 0;
	    final long start = System.currentTimeMillis();
	    while ((len = in.read(buffer)) >= 0) {
	        out.write(buffer, 0, len);
	        downloaded += len;
	        if ((int)((System.currentTimeMillis() - start) / 500) > msgs) {
	            Util.info((int)((double)downloaded / (double)size * 100d) + "%");
	            msgs++;
	        }
	    }
	    in.close();
	    out.close();
	    Util.info("Download finished");
	}

}

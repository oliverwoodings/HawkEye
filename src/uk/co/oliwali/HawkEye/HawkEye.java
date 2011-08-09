package uk.co.oliwali.HawkEye;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.getspout.spout.Spout;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import uk.co.oliwali.HawkEye.commands.BaseCommand;
import uk.co.oliwali.HawkEye.commands.HelpCommand;
import uk.co.oliwali.HawkEye.commands.HereCommand;
import uk.co.oliwali.HawkEye.commands.PageCommand;
import uk.co.oliwali.HawkEye.commands.RollbackCommand;
import uk.co.oliwali.HawkEye.commands.RollbackHelpCommand;
import uk.co.oliwali.HawkEye.commands.SearchCommand;
import uk.co.oliwali.HawkEye.commands.SearchHelpCommand;
import uk.co.oliwali.HawkEye.commands.ToolCommand;
import uk.co.oliwali.HawkEye.commands.TptoCommand;
import uk.co.oliwali.HawkEye.commands.UndoCommand;
import uk.co.oliwali.HawkEye.commands.WorldEditRollbackCommand;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.listeners.MonitorBlockListener;
import uk.co.oliwali.HawkEye.listeners.MonitorInventoryListener;
import uk.co.oliwali.HawkEye.listeners.MonitorEntityListener;
import uk.co.oliwali.HawkEye.listeners.MonitorPlayerListener;
import uk.co.oliwali.HawkEye.listeners.ToolBlockListener;
import uk.co.oliwali.HawkEye.listeners.ToolPlayerListener;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

public class HawkEye extends JavaPlugin {
	
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
	public WorldEditPlugin worldEdit = null;
	public Spout spout = null;
	private static HashMap<CommandSender, PlayerSession> playerSessions = new HashMap<CommandSender, PlayerSession>();
	
	/**
	 * Safely shuts down HawkEye
	 */
	public void onDisable() {
		DataManager.close();
		Util.info("Version " + version + " disabled!");
	}
	
	/**
	 * Starts up HawkEye initiation process
	 */
	public void onEnable() {
		
		Util.info("Starting HawkEye initiation process...");

		//Set up config and permissions
        PluginManager pm = getServer().getPluginManager();
		server = getServer();
		name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        config = new Config(this);
        new Permission(this);
        
        datalogCheck(pm);
        
        versionCheck();
        
        //Create player sessions
        for (Player player : server.getOnlinePlayers())
        	playerSessions.put(player, new PlayerSession(player));
        
        //Initiate database connection
        try {
			new DataManager(this);
		} catch (Exception e) {
			Util.severe("Error initiating HawkEye database connection, disabling plugin");
			pm.disablePlugin(this);
			return;
		}
		
		//Add console session
		ConsoleCommandSender sender = new ConsoleCommandSender(getServer());
		playerSessions.put(sender, new PlayerSession(sender));
		
		checkDependencies(pm);
        
	    registerListeners(pm);
        
	    registerCommands();
        
        Util.info("Version " + version + " enabled!");
        
	}
	
	/**
	 * Checks if HawkEye needs to update config files from existing DataLog installation
	 * @param pm PluginManager
	 */
	private void datalogCheck(PluginManager pm) {
		
        //Check if we need to update from DataLog
        Plugin dl = pm.getPlugin("DataLog");
        if (dl != null) {
        	Util.warning("DataLog found, transferring configuration and removing");
        	Config.importOldConfig(dl.getConfiguration());
        	pm.disablePlugin(dl);
        	File dataLog = new File("plugins" + File.separator + "DataLog.jar");
        	if (dataLog.exists()) dataLog.delete();
        	File dataLogFolder = new File("plugins" + File.separator + "DataLog" + File.separator);
        	if (dataLogFolder.exists()) dataLogFolder.delete();
        	config = new Config(this);
        	Util.warning("DataLog removed, config files updated");
        }
        
	}
	
	/**
	 * Checks if any updates are available for HawkEye
	 * Outputs console warning if updates are needed
	 */
	private void versionCheck() {
		
        //Perform version check
        Util.info("Performing update check...");
        try {
        	
        	//Get version file
        	URLConnection yc = new URL("https://raw.github.com/oliverw92/HawkEye/master/version.txt").openConnection();
    		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
    		
    		//Sort out version numbers
    		String updateVersion = in.readLine();
    		int updateVer = Integer.parseInt(updateVersion.replace(".", ""));
    		int curVer = Integer.parseInt(version.replace(".", ""));
    		
    		//Extract Bukkit build from server versions
    		Pattern pattern = Pattern.compile("-b(\\d*?)jnks", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(server.getVersion());
			if (!matcher.find() || matcher.group(1) == null) throw new Exception();
			int curBuild = Integer.parseInt(matcher.group(1));
    		int updateBuild = Integer.parseInt(in.readLine());
    		
    		//Check versions
    		if (updateVer > curVer) {
				Util.warning("New version of HawkEye available: " + updateVersion);
    			if (updateBuild > curBuild)	Util.warning("Update recommended of CraftBukkit from build " + curBuild + " to " + updateBuild + " to ensure compatibility");
    			else Util.warning("Compatible with your current version of CraftBukkit");
    		}
    		else Util.info("No updates available for HawkEye");
    		in.close();
    		
		} catch (Exception e) {
			Util.warning("Unable to perform update check!");
		}
	}
	
	/**
	 * Checks if required plugins are loaded
	 * @param pm PluginManager
	 */
	private void checkDependencies(PluginManager pm) {
		
        //Check if WorldEdit is loaded
        Plugin we = pm.getPlugin("WorldEdit");
        if (we != null) {
        	worldEdit = (WorldEditPlugin)we;
        	Util.info("WorldEdit found, selection rollbacks enabled");
        }
        
        //Check if Spout is loaded. If not, download and enable it
	    if (Config.isLogged(DataType.CONTAINER_TRANSACTION) && pm.getPlugin("Spout") == null) {
	        try {
	            download(new URL("http://dl.dropbox.com/u/49805/Spout.jar"), new File("plugins" + File.separator + "Spout.jar"));
	            pm.loadPlugin(new File("plugins" + File.separator + "Spout.jar"));
	            pm.enablePlugin(pm.getPlugin("Spout"));
	        } catch (final Exception ex) {
	            Util.info("WARNING! Unable to download and install Spout. Container logging disabled until Spout is available");
	        }
		}
	    Plugin bc = pm.getPlugin("Spout");
	    if (bc != null) {
	    	spout = (Spout)bc;
	    	Util.info("Spout found, container logging enabled");
	    }
	    
	}
	
	/**
	 * Registers event listeners
	 * @param pm PluginManager
	 */
	private void registerListeners(PluginManager pm) {
		
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
        
        //Register Spout events
        if (spout != null) {
        	pm.registerEvent(Type.CUSTOM_EVENT, new MonitorInventoryListener(), Event.Priority.Monitor, this);
        }
		
	}
	
	/**
	 * Registers commands for use by the command manager
	 */
	private void registerCommands() {
		
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
        
	}
	
	/**
	 * Command manager for HawkEye
	 * @param sender - {@link CommandSender}
	 * @param cmd - {@link Command}
	 * @param commandLabel - String
	 * @param args[] - String[]
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		if (cmd.getName().equalsIgnoreCase("HawkEye")) {
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

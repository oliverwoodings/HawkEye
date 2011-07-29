package uk.co.oliwali.DataLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
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
import uk.co.oliwali.DataLog.listeners.ControlBlockListener;
import uk.co.oliwali.DataLog.listeners.ControlEntityListener;
import uk.co.oliwali.DataLog.listeners.ControlPlayerListener;
import uk.co.oliwali.DataLog.listeners.MonitorBlockListener;
import uk.co.oliwali.DataLog.listeners.MonitorEntityListener;
import uk.co.oliwali.DataLog.listeners.MonitorPlayerListener;
import uk.co.oliwali.DataLog.util.BlockUtil;
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
	public ControlBlockListener controlBlockListener = new ControlBlockListener();
	public ControlEntityListener controlEntityListener = new ControlEntityListener();
	public ControlPlayerListener controlPlayerListener = new ControlPlayerListener();
	public static List<BaseCommand> commands = new ArrayList<BaseCommand>();
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
        
        // Register monitor events
        pm.registerEvent(Type.BLOCK_BREAK, monitorBlockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_PLACE, monitorBlockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_BURN, monitorBlockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_PHYSICS, monitorBlockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_FORM, monitorBlockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.SIGN_CHANGE, monitorBlockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_CHAT, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_JOIN, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_QUIT, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_INTERACT, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_DROP_ITEM, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_PICKUP_ITEM, monitorPlayerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_DAMAGE, monitorEntityListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_DEATH, monitorEntityListener, Event.Priority.Monitor, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, monitorEntityListener, Event.Priority.Monitor, this);
        
        //Register control events
        pm.registerEvent(Type.BLOCK_BREAK, controlBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_PLACE, controlBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_BURN, controlBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_PHYSICS, controlBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.BLOCK_FORM, controlBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.SIGN_CHANGE, controlBlockListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, controlPlayerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_CHAT, controlPlayerListener, Event.Priority.Lowest, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, controlPlayerListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_INTERACT, controlPlayerListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_DROP_ITEM, controlPlayerListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_PICKUP_ITEM, controlPlayerListener, Event.Priority.Highest, this);
        pm.registerEvent(Type.ENTITY_EXPLODE, controlEntityListener, Event.Priority.Highest, this);
        
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
	 * Checks the event against currently loaded rules
	 * @return true if event is to be cancelled
	 */
	public static boolean checkRules(Player player, DataType type, Location loc, String data) {
		return checkRules(player.getName(), type, loc, data);
	}
	public static boolean checkRules(String player, DataType type, Location loc, String data) {
		
		//Check rules
		for (Rule rule : Config.Rules) {
			
			String matchText = "";
			String notification = rule.notificationMsg;
			String warning = rule.warningMsg;
			
			//Check events and worlds
			if (!rule.events.contains(type)) continue;
			if (rule.worlds != null && rule.worlds.size() > 0 && !rule.worlds.contains(loc.getWorld().getName())) continue;
			
			//Check groups
			boolean inGroup = false;
			for (String group : rule.excludeGroups)
				if (Permission.inSingleGroup(loc.getWorld().getName(), player, group)) inGroup = true;
			if (inGroup) continue;
			
			//Check pattern
			if (!rule.pattern.equals("")) {
				Pattern pattern = Pattern.compile(rule.pattern, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(data);
				if (!matcher.find()) continue;
				matchText = data.substring(matcher.start(), matcher.end());
			}
			
			//Replace text
			notification = notification.replaceAll("%PLAYER%", player);
			notification = notification.replaceAll("%WORLD%", loc.getWorld().getName());
			notification = notification.replaceAll("%MATCH%", matchText);
			warning = warning.replaceAll("%PLAYER%", player);
			warning = warning.replaceAll("%WORLD%", loc.getWorld().getName());
			warning = warning.replaceAll("%MATCH%", matchText);
			
			//Replace match text for certain items
			switch (type) {
				case BLOCK_BREAK:
					matchText = BlockUtil.getBlockStringName(matchText);
					break;
				case BLOCK_PLACE:
					if (matchText.indexOf("-") == -1)
						matchText = BlockUtil.getBlockStringName(matchText);
					else
						matchText = BlockUtil.getBlockStringName(matchText.substring(matchText.indexOf("-") + 1));
					break;
				case ITEM_DROP:
				case ITEM_PICKUP:
					matchText = BlockUtil.getBlockStringName(matchText.substring(matchText.indexOf("x") + 2));
					break;
			}
			
			//Execute actions
			if (rule.notify) {
				for (Player p : DataLog.server.getOnlinePlayers()) {
					if (Permission.notify(p))
						Util.sendMessage(p, notification);
				}
			}
			Player offender = DataLog.server.getPlayer(player);
			if (offender != null) {
				if (rule.kick)
					offender.kickPlayer(warning);
				else if (rule.warn)
					Util.sendMessage(offender, warning);
			}
			if (rule.deny)
				return true;
				
		}
		
		return false;
	}

}

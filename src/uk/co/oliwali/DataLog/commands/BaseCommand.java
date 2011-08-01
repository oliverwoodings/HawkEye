package uk.co.oliwali.DataLog.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.PlayerSession;
import uk.co.oliwali.DataLog.util.Util;

/**
 * Abstract class representing a command.
 * When run by the command manager ({@link DataLog}), it pre-processes all the data into more useful forms.
 * Extending classes should adjust required fields in their constructor
 * @author Oli
 *
 */
public abstract class BaseCommand {
	
	public CommandSender sender;
	public List<String> args = new ArrayList<String>();
	public String name;
	public int argLength = 0;
	public String usage;
	public boolean bePlayer = false;
	public Player player;
	public String usedCommand;
	public PlayerSession session;
	public DataLog plugin;
	
	/**
	 * Method called by the command manager in {@link DataLog} to run the command.
	 * Arguments are processed into a list for easier manipulating.
	 * Argument lengths, permissions and sender types are all handled.
	 * @param csender {@link CommandSender} to send data to
	 * @param preArgs arguments to be processed
	 * @param cmd command being executed
	 * @return true on success, false if there is an error in the checks or if the extending command returns false
	 */
	public boolean run(DataLog instace, CommandSender csender, String[] preArgs, String cmd) {
		plugin = instace;
		sender = csender;
		session = DataLog.getSession(sender);
		args.clear();
		for (String arg : preArgs)
			args.add(arg);
		args.remove(0);
		usedCommand = cmd;
		
		if (argLength > args.size()) {
			sendUsage();
			return true;
		}
		if (bePlayer && !(sender instanceof Player))
			return false;
		if (sender instanceof Player)
			player = (Player)sender;
		if (!permission())
			return false;
		return execute();
	}
	
	/**
	 * Runs the extending command.
	 * Should only be run by the BaseCommand after all pre-processing is done
	 * @return true on success, false otherwise
	 */
	public abstract boolean execute();
	
	/**
	 * Performs the extending command's permission check.
	 * @return true if the user has permission, false if not
	 */
	public abstract boolean permission();
	
	/**
	 * Displays the help information for this command
	 */
	public void sendUsage() {
		Util.sendMessage(sender, "&c/"+usedCommand+" " + name + " " + usage);
	}

}

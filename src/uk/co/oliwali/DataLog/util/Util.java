package uk.co.oliwali.DataLog.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * Utility class for DataLog.
 * All logging and messages should go through this class.
 * Contains methods for parsing strings, colours etc.
 * @author oliverw92
 */
public class Util {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	private static int maxLength = 105;
	
	/**
	 * Send an info level log message to console
	 * @param msg message to send
	 */
	public static void info(String msg) {
		log.info("[DataLog] " + msg);
	}
	/**
	 * Send a severe level log message to console
	 * @param msg message to send
	 */
	public static void severe(String msg) {
		log.severe("[DataLog] " + msg);
	}
	
	/**
	 * Send an debug message to console if debug is enabled
	 * @param msg message to send
	 */
	public static void debug(String msg) {
		if (Config.Debug)
			Util.info("DEBUG: " + msg);
	}
	
	/**
	 * Send a message to a CommandSender (can be a player or console).
	 * Has parsing built in for &a colours, as well as `n for new line
	 * @param player sender to send to
	 * @param msg message to send
	 */
	public static void sendMessage(CommandSender player, String msg) {
		int i;
		String part;
		CustomColor lastColor = CustomColor.WHITE;
		for (String line : msg.split("`n")) {
			i = 0;
			while (i < line.length()) {
				part = getMaxString(line.substring(i));
				if (i+part.length() < line.length() && part.contains(" "))
					part = part.substring(0, part.lastIndexOf(" "));
				part = lastColor.getCustom() + part;
				player.sendMessage(replaceColors(part));
				lastColor = getLastColor(part);
				i = i + part.length() -1;
			}
		}
	}
	
	/**
	 * Turns supplied location into a simplified (1 decimal point) version
	 * @param location location to simplify
	 * @return Location
	 */
	public static Location getSimpleLocation(Location location) {
		location.setX((double)Math.round(location.getX() * 10) / 10);
		location.setY((double)Math.round(location.getY() * 10) / 10);
		location.setZ((double)Math.round(location.getZ() * 10) / 10);
		return location;
	}
	
	/**
	 * Checks if inputted string is an integer
	 * @param str string to check
	 * @return true if an integer, false if not
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	/**
	 * Java version of PHP's join(array, delimiter)
	 * Takes any kind of collection (List, HashMap etc)
	 * @param s collection to be joined
	 * @param delimiter string delimiter
	 * @return String
	 */
	public static String join(Collection<?> s, String delimiter) {
	    StringBuffer buffer = new StringBuffer();
	    Iterator<?> iter = s.iterator();
	    while (iter.hasNext()) {
	        buffer.append(iter.next());
	        if (iter.hasNext())
	            buffer.append(delimiter);
	    }
	    return buffer.toString();
	}
	
	/**
	* Returns the distance between two {Location}s
	* @param from
	* @param to
	* @return double
	**/
	public static double distance(Location from, Location to) {
	    return Math.sqrt(Math.pow(from.getX() - to.getX(), 2) + Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getZ() - to.getZ(), 2));
	} 
	
	/**
	 * Strips colours from inputted string
	 * @param str
	 * @return string without colours
	 */
	public static String stripColors(String str) {
		str = str.replaceAll("(?i)\u00A7[0-F]", "");
		str = str.replaceAll("(?i)&[0-F]", "");
		return str;
	}
	
	/**
	 * Finds the last colour in the string
	 * @param str
	 * @return {@link CustomColor}
	 */
	public static CustomColor getLastColor(String str) {
		int i = 0;
		CustomColor lastColor = CustomColor.WHITE;
		while (i < str.length()-2) {
			for (CustomColor color: CustomColor.values()) {
				if (str.substring(i, i+2).equalsIgnoreCase(color.getCustom()))
					lastColor = color;
			}
			i = i+2;
		}
		return lastColor;
	}
	
	/**
	 * Replaces custom colours with actual colour values
	 * @param str input
	 * @return inputted string with proper colour values
	 */
    public static String replaceColors(String str) {
    	for (CustomColor color : CustomColor.values())
    		str = str.replace(color.getCustom(), color.getString());
        return str;
    }
    
    /**
     * Finds the max length of the inputted string for outputting
     * @param str
     * @return the string in its longest possible form
     */
    private static String getMaxString(String str) {
    	for (int i = 0; i < str.length(); i++) {
    		if (stripColors(str.substring(0, i)).length() == maxLength) {
    			if (stripColors(str.substring(i, i+1)) == "")
    				return str.substring(0, i-1);
    			else
    				return str.substring(0, i);
    		}
    	}
    	return str;
    }
    
    /**
     * Custom colour class.
     * Created to allow for easier colouring of text
     * @author oliverw92
     */
    public enum CustomColor {
    	
    	RED("c", 0xC),
    	DARK_RED("4", 0x4),
    	YELLOW("e", 0xE),
    	GOLD("6", 0x6),
    	GREEN("a", 0xA),
    	DARK_GREEN("2", 0x2),
    	TURQOISE("3", 0x3),
    	AQUA("b", 0xB),
    	DARK_AQUA("8", 0x8),
    	BLUE("9", 0x9),
    	DARK_BLUE("1", 0x1),
    	LIGHT_PURPLE("d", 0xD),
    	DARK_PURPLE("5", 0x5),
    	BLACK("0", 0x0),
    	DARK_GRAY("8", 0x8),
    	GRAY("7", 0x7),
    	WHITE("f", 0xf);
    	
    	private String custom;
    	private int code;
    	
    	private CustomColor(String custom, int code) {
    		this.custom = custom;
    		this.code = code;
    	}
    	public String getCustom() {
    		return "&" + custom;
    	}
    	public String getString() {
    		return String.format("\u00A7%x", code);
    	}
    	
    }

}

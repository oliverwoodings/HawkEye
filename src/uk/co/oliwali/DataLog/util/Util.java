package uk.co.oliwali.DataLog.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public class Util {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	private static int maxLength = 105;
	
	public static void info(String msg) {
		log.info("[DataLog] " + msg);
	}
	public static void severe(String msg) {
		log.severe("[DataLog] " + msg);
	}
	
	public static void debug(String msg) {
		if (Config.Debug)
			Util.info("DEBUG: " + msg);
	}
	
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
	
	public static Location getSimpleLocation(Location location) {
		location.setX((double)Math.round(location.getX() * 10) / 10);
		location.setY((double)Math.round(location.getY() * 10) / 10);
		location.setZ((double)Math.round(location.getZ() * 10) / 10);
		return location;
	}
	
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
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
	
	public static String stripColors(String str) {
		str = str.replaceAll("(?i)\u00A7[0-F]", "");
		str = str.replaceAll("(?i)&[0-F]", "");
		return str;
	}
	
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
	
    public static String replaceColors(String str) {
    	for (CustomColor color : CustomColor.values())
    		str = str.replace(color.getCustom(), color.getString());
        return str;
    }
    
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
    
    private enum CustomColor {
    	
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

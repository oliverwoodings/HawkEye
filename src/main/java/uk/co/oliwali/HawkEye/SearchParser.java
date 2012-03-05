package uk.co.oliwali.HawkEye;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Class for parsing HawkEye arguments ready to be used by an instance of {@SearchQuery}
 * @author oliverw92
 */
public class SearchParser {
	
	public Player player = null;
	public String[] players = null;
	public Vector loc = null;
	public Vector minLoc = null;
	public Vector maxLoc = null;
	public Integer radius = null;
	public List<DataType> actions = new ArrayList<DataType>();
	public String[] worlds = null;
	public String dateFrom = null;
	public String dateTo = null;
	public String[] filters = null;
	
	public SearchParser() { }
	
	public SearchParser(Player player) {
		this.player = player;
	}
	
	public SearchParser(Player player, int radius) {
		this.player = player;
		this.radius = radius;
		parseLocations();
	}
	
	public SearchParser(Player player, List<String> args) throws IllegalArgumentException {
		this.player = player;
		
		String lastParam = "";
		boolean paramSet = false;
		
		for (int i = 0; i < args.size(); i++) {
			String arg = args.get(i);
			if (arg.isEmpty()) continue;
			
			if (!paramSet) {
				if (arg.length() < 2)
					throw new IllegalArgumentException("Invalid argument format: &7" + arg);
				if (!arg.substring(1,2).equals(":"))
					throw new IllegalArgumentException("Invalid argument format: &7" + arg);
				
				lastParam = arg.substring(0,1).toLowerCase();
				paramSet = true;
				
				if (arg.length() == 2) {
					if (i == (args.size() - 1)) // No values specified
						throw new IllegalArgumentException("Invalid argument format: &7" + arg);
					else // User put a space between the colon and value
						continue;
				}
				
				// Get values out of argument
				arg = arg.substring(2);
			}
			
			if (paramSet) {
				if (arg.isEmpty()) {
					throw new IllegalArgumentException("Invalid argument format: &7" + lastParam + ":");
				}
				
				String[] values = arg.split(",");
				
				// Players
				if (lastParam.equals("p")) players = values;
				// Worlds
				else if (lastParam.equals("w")) worlds = values;
				// Filters
				else if (lastParam.equals("f")) {
					if (filters != null) filters = Util.concat(filters, values);
					else filters = values;
				}
				// Blocks
				else if (lastParam.equals("b")) {
					for (int j = 0; j < values.length; j++) {
						if (Material.getMaterial(values[j]) != null)
							values[j] = Integer.toString(Material.getMaterial(values[j]).getId());
					}
				}
				// Actions
				else if (lastParam.equals("a")) {
					for (String value : values) {
						DataType type = DataType.fromName(value);
						if (type == null) throw new IllegalArgumentException("Invalid action supplied: &7" + value);
						if (!Permission.searchType(player, type.getConfigName())) throw new IllegalArgumentException("You do not have permission to search for: &7" + type.getConfigName());
						actions.add(type);
					}
				}
				// Location
				else if (lastParam.equals("l")) {
					if (values[0].equalsIgnoreCase("here")) 
						loc = player.getLocation().toVector();
					else {
						loc = new Vector();
						loc.setX(Integer.parseInt(values[0]));
						loc.setY(Integer.parseInt(values[1]));
						loc.setZ(Integer.parseInt(values[2]));
					}
				}
				// Radius
				else if (lastParam.equals("r")) {
					if (!Util.isInteger(values[0])) throw new IllegalArgumentException("Invalid radius supplied: &7" + values[0]);
					radius = Integer.parseInt(values[0]);
				}
				//Time
				else if (lastParam.equals("t")) {
					
					int type = 2;
					for (int j = 0; j < arg.length(); j++) {
						String c = arg.substring(j, j+1);
						if (!Util.isInteger(c)) {
							if (c.equals("m") || c .equals("s") || c.equals("h") || c.equals("d") || c.equals("w"))
								type = 0;
							if (c.equals("-") || c.equals(":"))
								type = 1;
						}
					}
					
					//If the time is in the format '0w0d0h0m0s'
					if (type == 0) {
						
						int weeks = 0;
						int days = 0;
						int hours = 0;
						int mins = 0;
						int secs = 0;
						
						String nums = "";
						for (int j = 0; j < values[0].length(); j++) {
							String c = values[0].substring(j, j+1);
							if (Util.isInteger(c)) {
								nums += c;
								continue;
							}
							int num = Integer.parseInt(nums);
							if (c.equals("w")) weeks = num;
							else if (c.equals("d")) days = num;
							else if (c.equals("h")) hours = num;
							else if (c.equals("m")) mins = num;
							else if (c.equals("s")) secs = num;
							else throw new IllegalArgumentException("Invalid time measurement: &7" + c);
							nums = "";
						}
						
						Calendar cal = Calendar.getInstance();
						cal.add(Calendar.WEEK_OF_YEAR, -1 * weeks);
						cal.add(Calendar.DAY_OF_MONTH, -1 * days);
						cal.add(Calendar.HOUR, -1 * hours);
						cal.add(Calendar.MINUTE, -1 * mins);
						cal.add(Calendar.SECOND, -1 * secs);
						SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dateFrom = form.format(cal.getTime());
						
					}
					//If the time is in the format 'yyyy-MM-dd HH:mm:ss'
					else if (type == 1) {
						if (values.length == 1) {
							SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
							dateFrom = form.format(Calendar.getInstance().getTime()) + " " + values[0];
						}
						if (values.length >= 2)
							dateFrom = values[0] + " " + values[1];
						if (values.length == 4)
							dateTo = values[2] + " " + values[3];
					}
					//Invalid time format
					else if (type == 2)
						throw new IllegalArgumentException("Invalid time format!");
					
				}
				else throw new IllegalArgumentException("Invalid parameter supplied: &7" + lastParam);
				
				paramSet = false;
			}
		}
		
		//Sort out locations
		parseLocations();
		
	}
	
	/**
	 * Formats min and max locations if the radius is set
	 */
	public void parseLocations() {
		
		//If the radius is set we need to format the min and max locations
		if (radius != null) {
			
			//Check if location and world are supplied
			if (loc == null) loc = player.getLocation().toVector();
			if (worlds == null) worlds = new String[]{ player.getWorld().getName() };
			
			//Format min and max
			minLoc = new Vector(loc.getX() - radius, loc.getY() - radius, loc.getZ() - radius);
			maxLoc = new Vector(loc.getX() + radius, loc.getY() + radius, loc.getZ() + radius);
			
		}
		
	}

}

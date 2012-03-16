package uk.co.oliwali.HawkEye.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * DataBase cleansing utility.
 * Deletes data older than date specified in config.
 * This class should be run on a {Timer} in a separate thread
 * @author oliverw92
 */
public class CleanseUtil extends TimerTask {

	private String date = null;
	private int interval = 1200;

	/**
	 * Initiates utility.
	 * Throws exception if there are any errors processing the config time value
	 * @throws Exception
	 */
	public CleanseUtil() throws Exception {

		//Check for invalid ages/periods
		List<String> arr = Arrays.asList(new String[]{"0", "0s"});
		if (Config.CleanseAge == null || Config.CleansePeriod == null || arr.contains(Config.CleanseAge) || arr.contains(Config.CleansePeriod)) {
			return;
		}

		//Parse cleanse age
		ageToDate();

		//Parse interval
        int temp = 0;
		String nums = "";
		for (int i = 0; i < Config.CleansePeriod.length(); i++) {
			String c = Config.CleansePeriod.substring(i, i+1);
			if (Util.isInteger(c)) {
				nums += c;
				continue;
			}
			int num = Integer.parseInt(nums);
			if (c.equals("w")) temp += 604800*num;
			else if (c.equals("d")) temp += 86400*num;
			else if (c.equals("h")) temp += 3600*num;
			else if (c.equals("m")) temp += 60*num;
			else if (c.equals("s")) temp += num;
			else throw new Exception();
			nums = "";
		}
		if (temp > 0) interval = temp;

		//Start timer
		Util.info("Starting database cleanse thread with a period of " + interval + " seconds");
		DataManager.cleanseTimer = new Timer();
		DataManager.cleanseTimer.scheduleAtFixedRate(this, 0, interval * 1000);

	}

	/**
	 * Runs the cleansing utility
	 */
	@Override
	public void run() {

		Util.info("Running cleanse utility for logs older than " + date);
		JDCConnection conn = null;
		Statement stmnt = null;
		try {
			ageToDate();
			conn = DataManager.getConnection();
			stmnt = conn.createStatement();
			Util.debug("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `date` < '" + date + "'");
			int affected = stmnt.executeUpdate("DELETE FROM `" + Config.DbHawkEyeTable + "` WHERE `date` < '" + date + "'");
			Util.info("Deleted " + affected + " row(s) from database");
		} catch (Exception ex) {
			Util.severe("Unable to execute cleanse utility: " + ex);
		}
		finally {
			try {
				stmnt.close();
			} catch (SQLException e) {
				Util.severe("Error closing SQL statement!");
			}
			conn.close();
		}

	}

	/**
	 * Converts the cleanse age into date string
	 */
	private void ageToDate() throws Exception {

		int weeks = 0;
		int days = 0;
		int hours = 0;
		int mins = 0;
		int secs = 0;

		String nums = "";
		for (int i = 0; i < Config.CleanseAge.length(); i++) {
			String c = Config.CleanseAge.substring(i, i+1);
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
			else throw new Exception();
			nums = "";
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -1 * weeks);
		cal.add(Calendar.DAY_OF_MONTH, -1 * days);
		cal.add(Calendar.HOUR, -1 * hours);
		cal.add(Calendar.MINUTE, -1 * mins);
		cal.add(Calendar.SECOND, -1 * secs);
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date = form.format(cal.getTime());

	}

}

package uk.co.oliwali.DataLog.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

import uk.co.oliwali.DataLog.util.Config;
import uk.co.oliwali.DataLog.util.Util;

public class CleanseUtil extends TimerTask {
	
	public String date = null;
	
	public CleanseUtil() throws Exception {
		
		if (Config.cleanseAge.equalsIgnoreCase("0") || Config.cleanseAge.equalsIgnoreCase("0d0h0s"))
			return;
		
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int mins = 0;
		int secs = 0;
		
		String nums = "";
		for (int i = 0; i < Config.cleanseAge.length(); i++) {
			String c = Config.cleanseAge.substring(i, i+1);
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
		
		Util.info("Started cleanse thread for logs older than " + date);
	}

	public void run() {
		
		Util.info("Running cleanse utility...");
		try {
			JDCConnection conn = DataManager.getConnection();
			Statement stmnt = conn.createStatement();
			int affected = stmnt.executeUpdate("DELETE FROM `datalog` WHERE `date` < '" + date + "'");
			Util.info("Deleted " + affected + " row(s) from database");
		} catch (SQLException ex) {
			Util.severe("Unable to execute cleanse utility: " + ex);
		}
		
	}

}

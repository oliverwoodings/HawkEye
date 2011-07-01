package uk.co.oliwali.DataLog;

import java.util.List;

/**
 * Represents a rule to checked for the specified events.
 * Rules are stored in config.yml and loaded in the {@link Config} class
 * @author oliverw92
 */

public class Rule {
	
	public final String name;
	public final List<DataType> events;
	public final String pattern;
	public final List<String> worlds;
	public final List<String> excludeGroups;
	public final String notificationMsg;
	public final String warningMsg;
	public final boolean notify;
	public final boolean warn;
	public final boolean kick;
	public final boolean deny;
	
	/**
	 * Creates a new representation of a rule
	 * 
	 * @param name name of the rule
	 * @param events list of {@link DataType} to check against
	 * @param pattern regex pattern to check data against
	 * @param worlds worlds this rule is to be applied to
	 * @param excludeGroups groups that do not obey this rule
	 * @param notificationMsg message to send to mods/admins
	 * @param warningMsg warning message to send to offender
	 * @param notify whether or not to notify mods/admins
	 * @param warn whether or not to warn the offender
	 * @param kick whether or not to kick the offender
	 * @param deny whether or not to deny the action
	 */
	public Rule(String name, List<DataType> events, String pattern, List<String> worlds, List<String> excludeGroups, String notificationMsg, String warningMsg, boolean notify, boolean warn, boolean kick, boolean deny) {
		this.name = name;
		this.events = events;
		this.pattern = pattern;
		this.worlds = worlds;
		this.notificationMsg = notificationMsg;
		this.warningMsg = warningMsg;
		this.excludeGroups = excludeGroups;
		this.notify = notify;
		this.warn = warn;
		this.kick = kick;
		this.deny = deny;
	}

}

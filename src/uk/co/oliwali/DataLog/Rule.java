package uk.co.oliwali.DataLog;

import java.util.List;

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

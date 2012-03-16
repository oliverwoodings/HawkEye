package uk.co.oliwali.HawkEye.commands;

import uk.co.oliwali.HawkEye.ToolManager;
import uk.co.oliwali.HawkEye.util.Permission;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolResetCommand extends BaseCommand {

	public ToolResetCommand() {
		name = "tool reset";
		argLength = 0;
		usage = " <- resets tool to default properties";
	}

	@Override
	public boolean execute() {
		ToolManager.resetTool(session);
		Util.sendMessage(player, "&cTool reset to default parameters");
		return true;
	}

	@Override
	public void moreHelp() {
		Util.sendMessage(sender, "&cReset HawkEye tool to default properties");
		Util.sendMessage(sender, "&cSee &7/hawk tool bind help");
	}

	@Override
	public boolean permission() {
		return Permission.toolBind(sender);
	}

}
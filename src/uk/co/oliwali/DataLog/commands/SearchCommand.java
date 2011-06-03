package uk.co.oliwali.DataLog.commands;

import uk.co.oliwali.DataLog.util.Permission;

public class SearchCommand extends BaseCommand {

	public SearchCommand() {
		name = "search";
		argLength = -1;
		usage = "p:player1,player2 l:x,y,z r:50 a:chat,break w:world1,world2 t:yy-mm-dd,hh:mm:ss,yy-mm-dd,hh:mm:ss f:hack <- search the DataLog database";
	}
	
	public boolean execute() {
		
		return true;
	}
	
	public boolean permission() {
		return Permission.search(sender);
	}

}
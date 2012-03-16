package uk.co.oliwali.HawkEye;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class for parsing managing player's {@PlayerSession}s
 * @author oliverw92
 */
public class SessionManager {

	private static final HashMap<String, PlayerSession> playerSessions = new HashMap<String, PlayerSession>();

	public SessionManager() {

		//Add console session
		addSession(Bukkit.getServer().getConsoleSender());

        //Create player sessions
        for (Player player : Bukkit.getServer().getOnlinePlayers()) addSession(player);

	}

	/**
	 * Get a PlayerSession from the list
	 */
	public static PlayerSession getSession(CommandSender player) {
		PlayerSession session = playerSessions.get(player.getName());
		if (session == null)
			session = addSession(player);
		session.setSender(player);
		return session;
	}

	/**
	 * Adds a PlayerSession to the list
	 */
	public static PlayerSession addSession(CommandSender player) {
		PlayerSession session;
		if (playerSessions.containsKey(player.getName())) {
			session = playerSessions.get(player.getName());
			session.setSender(player);
		}
		else {
			session = new PlayerSession(player);
			playerSessions.put(player.getName(), session);
		}
		return session;
	}

}

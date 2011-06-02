package uk.co.oliwali.DataLog;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class DLEntityListener extends EntityListener {
	
	public DataLog plugin;
	private HashMap<String, String> damageList = new HashMap<String, String>();

	public DLEntityListener(DataLog dataLog) {
		plugin = dataLog;
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;
		if(event.getEntity() instanceof Player) {
			Player victim = (Player) event.getEntity();
			String attacker = null;
			switch (event.getCause()) {
				case ENTITY_ATTACK:
					EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) event;
					Entity attackEntity = attackEvent.getDamager();
					if (attackEntity instanceof Player) {
						Player attackPlayer = (Player) attackEntity;
						attacker = attackPlayer.getName();
					}
					else
						attacker = "default";
					break;
				default:
					attacker = "default";
					break;
			}
			if (damageList.get(victim.getName()) != null)
				damageList.remove(victim.getName());
			damageList.put(victim.getName(), attacker);
		}
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player victim   = (Player) entity;
			Location loc    = victim.getLocation();
			String attacker = damageList.get(victim.getName());
			if (attacker != "default")
				plugin.addDataEntry(victim, DataType.PVP_DEATH, loc, attacker);
		}
	}

}

package uk.co.oliwali.DataLog.listeners;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;
import uk.co.oliwali.DataLog.PlayerSession;
import uk.co.oliwali.DataLog.database.DataManager;
import uk.co.oliwali.DataLog.util.Util;
public class DLEntityListener extends EntityListener {
	
	public DataLog plugin;

	public DLEntityListener(DataLog dataLog) {
		plugin = dataLog;
	}
	
	public void onEntityDamage(EntityDamageEvent event) {
		
		if (event.isCancelled())
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player victim = (Player) event.getEntity();
		
		PlayerSession session = DataLog.playerSessions.get(victim);
		session.setLastDamageCause(event.getCause());
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) event;
			session.setLastAttacker(attackEvent.getDamager());				
		}
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player victim   = (Player) entity;
			PlayerSession session = DataLog.playerSessions.get(victim);
			Location loc    = victim.getLocation();
			if (session.getLastDamageCause() == DamageCause.ENTITY_ATTACK) {
				String attacker = null;
				Entity attackEntity = session.getLastAttacker();
				if (attackEntity instanceof Player) {
					DataManager.addEntry(victim, DataType.PVP_DEATH, loc, ((Player)attackEntity).getName());
					return;
				}
				else if (attackEntity instanceof PigZombie)
					attacker = "PigZombie";
				else if (attackEntity instanceof Giant)
					attacker = "Giant";
				else if (attackEntity instanceof Zombie)
					attacker = "Zombie";
				else if (attackEntity instanceof Skeleton)
					attacker = "Skeleton";
				else if (attackEntity instanceof Spider)
					attacker = "Spider";
				else if (attackEntity instanceof Creeper)
					attacker = "Creeper";
				else if (attackEntity instanceof Ghast)
					attacker = "Ghast";
				else if (attackEntity instanceof Slime)
					attacker = "Slime";
				else if (attackEntity instanceof Wolf)
					attacker = "Wolf";
				else
					attacker = "Unknown";
				DataManager.addEntry(victim, DataType.MOB_DEATH, loc, attacker);
			}
			else {
				String cause = session.getLastDamageCause() == null?"Unknown":session.getLastDamageCause().name();
				String[] words = cause.split("_");
				for (int i = 0; i < words.length; i++)
					words[i] = words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();
				cause = Util.join(Arrays.asList(words), " ");
				DataManager.addEntry(victim, DataType.OTHER_DEATH, loc, cause);
			}
		}
	}
	
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled())
			return;
		for (Block b : event.blockList().toArray(new Block[0]))
			if (DataManager.addEntry("Environment", DataType.EXPLOSION, b.getLocation(), Integer.toString(b.getTypeId())))
				event.setCancelled(true);
	}

}

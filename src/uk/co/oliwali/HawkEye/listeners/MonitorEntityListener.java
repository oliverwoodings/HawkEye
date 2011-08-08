package uk.co.oliwali.HawkEye.listeners;

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
import org.bukkit.inventory.ItemStack;

import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.PlayerSession;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

/**
 * Entity listener class for HawkEye
 * Contains system for managing player deaths
 * @author oliverw92
 */
public class MonitorEntityListener extends EntityListener {
	
	public HawkEye plugin;

	public MonitorEntityListener(HawkEye HawkEye) {
		plugin = HawkEye;
	}
	
	/**
	 * Stores last attacks in the {@link PlayerSession} for the player
	 */
	public void onEntityDamage(EntityDamageEvent event) {
		
		if (event.isCancelled())
			return;
		if (!(event.getEntity() instanceof Player))
			return;
		
		//Store damage details in the PlayerSession
		Player victim = (Player) event.getEntity();
		PlayerSession session = HawkEye.getSession(victim);
		session.setLastDamageCause(event.getCause());
		if (event.getCause() == DamageCause.ENTITY_ATTACK) {
			EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) event;
			session.setLastAttacker(attackEvent.getDamager());				
		}
	}
	
	/**
	 * Uses the lastAttacker field in the players {@link PlayerSession} to log the death and cause
	 */
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		//Only interested if it is a player death
		if (entity instanceof Player) {
			Player victim   = (Player) entity;
			PlayerSession session = HawkEye.getSession(victim);
			Location loc    = victim.getLocation();
			//Entity attack
			if (session.getLastDamageCause() == DamageCause.ENTITY_ATTACK) {
				String attacker = null;
				Entity attackEntity = session.getLastAttacker();
				//Player attack
				if (attackEntity instanceof Player) {
					DataManager.addEntry(victim, DataType.PVP_DEATH, loc, ((Player)attackEntity).getName());
					return;
				}
				//Creature attack
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
			//Other death
			else {
				String cause = session.getLastDamageCause() == null?"Unknown":session.getLastDamageCause().name();
				String[] words = cause.split("_");
				for (int i = 0; i < words.length; i++)
					words[i] = words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase();
				cause = Util.join(Arrays.asList(words), " ");
				DataManager.addEntry(victim, DataType.OTHER_DEATH, loc, cause);
			}
            
			//Log item drops - thanks Nibato!
			if (Config.LogDeathDrops) {
				String data = null;
				for (ItemStack stack : event.getDrops()) {
					if (stack.getData() != null)
						data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
				    else
				    	data = stack.getAmount() + "x " + stack.getTypeId();
				    DataManager.addEntry(victim, DataType.ITEM_DROP, loc, data);                           
				}
			}
	
		}
	}
	
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) return;
		for (Block b : event.blockList().toArray(new Block[0]))
			DataManager.addEntry("Environment", DataType.EXPLOSION, b.getLocation(), Integer.toString(b.getTypeId()));
	}

}

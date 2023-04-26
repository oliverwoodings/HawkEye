package uk.co.oliwali.HawkEye.listeners;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.MinecartEntry;

public class MonitorMinecartListener extends HawkEyeListener {
	public HawkEye plugin;
	
	public MonitorMinecartListener(HawkEye HawkEye) {
		super(HawkEye);
		this.plugin = HawkEye;
	}
	
	//Pose du minecart
	@HawkEvent(
            dataType = {DataType.MINECART_PLACE}
    )
	public void onVehicleCreate(VehicleCreateEvent event) {
		Boolean isMinecart = false;
		
		//on verif quel minecart c'est
		switch(event.getVehicle().getType()) {
			case MINECART:
			case MINECART_FURNACE:
			case MINECART_TNT:
			case MINECART_HOPPER:
			case MINECART_CHEST:
				isMinecart = true;
				break;
			default:
				break;
		}
		
		if(isMinecart) {
			Boolean found = false;
			Location loc = event.getVehicle().getLocation().getBlock().getLocation();
			Iterator<Entry<String, Location>> entrys = this.plugin.minecartLocation.entrySet().iterator();
			
			Entry<String, Location> entry = null;
			while(!found & entrys.hasNext()) {
				entry = entrys.next();
				String[] typeAndPlayer = entry.getKey().split(":");
				
				if(typeAndPlayer[0].equals(event.getVehicle().getType().toString())) {
					if(entry.getValue().distance(loc) == 0.0) {
						String[] uuidAndType = new String[] {event.getVehicle().getUniqueId().toString(), event.getVehicle().getType().toString()};
						
						DataManager.addEntry(new MinecartEntry(typeAndPlayer[1], DataType.MINECART_PLACE, loc, uuidAndType));
					}
				}
			}
			this.plugin.minecartLocation.remove(entry.getKey());
		}
	}
	
	//Destruction du minecart
	@HawkEvent(
            dataType = {DataType.MINECART_BREAK}
    )
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		Boolean isMinecart = false;
		
		//on verif quel minecart c'est
		switch(event.getVehicle().getType()) {
			case MINECART:
			case MINECART_FURNACE:
			case MINECART_TNT:
			case MINECART_HOPPER:
			case MINECART_CHEST:
				isMinecart = true;
				break;
			default:
				break;
		}
		
		if(isMinecart) {
			String player = "Unknown";
			if((event.getAttacker() instanceof Player)) {
				player = ((Player) event.getAttacker()).getName();
			}
			String[] uuidAndType = new String[] {
					event.getVehicle().getUniqueId().toString(),
					event.getVehicle().getType().toString()
			};
			DataManager.addEntry(
				new MinecartEntry(
					player,
					DataType.MINECART_BREAK,
					event.getVehicle().getLocation().getBlock().getLocation(),
					uuidAndType
				)
			);
		}
	}
	
	//Explosion de minecart
	@HawkEvent(
            dataType = {DataType.MINECART_EXPLOSION}
    )
	public void onEntityExplosion(EntityExplodeEvent event) {
		if(event.getEntity().getType() == EntityType.MINECART_TNT) {
			String[] uuidAndType = new String[] {
					event.getEntity().getUniqueId().toString(),
					event.getEntityType().toString()
			};
			DataManager.addEntry(new MinecartEntry(
					"Unknown",
					DataType.MINECART_EXPLOSION,
					event.getEntity().getLocation().getBlock().getLocation(),
					uuidAndType
			));
		}
	}
}

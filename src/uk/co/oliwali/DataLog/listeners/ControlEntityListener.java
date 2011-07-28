package uk.co.oliwali.DataLog.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import uk.co.oliwali.DataLog.DataLog;
import uk.co.oliwali.DataLog.DataType;

public class ControlEntityListener extends EntityListener {

	public void onEntityExplode(EntityExplodeEvent event) {
		for (Block b : event.blockList().toArray(new Block[0]))
			if (DataLog.checkRules("Environment", DataType.EXPLOSION, b.getLocation(), Integer.toString(b.getTypeId())))
				event.setCancelled(true);
	}
	
}

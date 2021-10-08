package uk.co.oliwali.HawkEye.listeners;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class MonitorWorldListener extends HawkEyeListener {

   public MonitorWorldListener(HawkEye HawkEye) {
      super(HawkEye);
   }

   @HawkEvent(
      dataType = {DataType.TREE_GROW, DataType.MUSHROOM_GROW}
   )
   public void onStructureGrow(StructureGrowEvent event) {
      DataType type = DataType.TREE_GROW;
      if(event.getSpecies().name().toLowerCase().contains("mushroom")) {
         type = DataType.MUSHROOM_GROW;
      }

      Iterator i$ = event.getBlocks().iterator();

      while(i$.hasNext()) {
         BlockState block = (BlockState)i$.next();
         if(block.getType() != Material.MYCEL && block.getType() != Material.DIRT && block.getType() != Material.GRASS) {
            Location loc = new Location(event.getWorld(), (double)block.getX(), (double)block.getY(), (double)block.getZ());
            if(event.getPlayer() != null) {
               DataManager.addEntry(new BlockChangeEntry(event.getPlayer(), type, loc, "0", BlockUtil.getBlockString(block)));
            } else {
               DataManager.addEntry(new BlockChangeEntry("Environment", type, loc, "0", BlockUtil.getBlockString(block)));
            }
         }
      }

   }
}

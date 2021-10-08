package uk.co.oliwali.HawkEye.listeners;

import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;

public class MonitorFallingBlockListener extends HawkEyeListener {

   private HashMap blocks = new HashMap();


   public MonitorFallingBlockListener(HawkEye HawkEye) {
      super(HawkEye);
   }

   @HawkEvent(
      dataType = {DataType.FALLING_BLOCK}
   )
   public void onBlockPlace(final BlockPlaceEvent event) {
      Material type = event.getBlock().getType();
      if((type.equals(Material.SAND) || type.equals(Material.GRAVEL) || type.equals(Material.ANVIL)) && event.getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
         HawkEye.server.getScheduler().scheduleSyncDelayedTask(HawkEye.instance, new Runnable() {
            public void run() {
               Location l = event.getBlock().getLocation();
               Iterator i$ = l.getWorld().getEntitiesByClass(FallingBlock.class).iterator();

               FallingBlock e;
               do {
                  if(!i$.hasNext()) {
                     return;
                  }

                  e = (FallingBlock)i$.next();
               } while(l.distanceSquared(e.getLocation()) > 0.8D);

               MonitorFallingBlockListener.this.blocks.put(e, event.getPlayer().getName());
            }
         }, 6L);
      }
   }

   @HawkEvent(
      dataType = {DataType.FALLING_BLOCK}
   )
   public void onEntityModifyBlock(EntityChangeBlockEvent event) {
      Entity en = event.getEntity();
      if(en instanceof FallingBlock && this.blocks.containsKey(en)) {
         FallingBlock fb = (FallingBlock)en;
         Block b = event.getBlock();
         String data = "" + (fb.getBlockData() == 0?Integer.valueOf(fb.getBlockId()):fb.getBlockId() + ":" + fb.getBlockData());
         DataManager.addEntry(new BlockChangeEntry((String)this.blocks.get(en), DataType.FALLING_BLOCK, b.getLocation(), event.getBlock().getState(), data));
      }

   }
}

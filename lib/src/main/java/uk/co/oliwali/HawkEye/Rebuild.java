package uk.co.oliwali.HawkEye;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

public class Rebuild implements Runnable {

   private final PlayerSession session;
   private Iterator rebuildQueue;
   private final List undo = new ArrayList();
   private int timerID;
   private int counter = 0;


   public Rebuild(PlayerSession session) {
      this.session = session;
      session.setRollbackType(Rollback.RollbackType.REBUILD);
      this.rebuildQueue = session.getRollbackResults().iterator();
      if(!this.rebuildQueue.hasNext()) {
         Util.sendMessage(session.getSender(), "&cNo results found to rebuild");
      } else {
         Util.debug("Starting rebuild of " + session.getRollbackResults().size() + " results");
         session.setDoingRollback(true);
         Util.sendMessage(session.getSender(), "&cAttempting to rebuild &7" + session.getRollbackResults().size() + "&c results");
         this.timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1L, 2L);
      }
   }

   public void run() {
      int i = 0;

      while(i < 200 && this.rebuildQueue.hasNext()) {
         ++i;
         DataEntry entry = (DataEntry)this.rebuildQueue.next();
         if(entry.getType() != null && entry.getType().canRollback()) {
            World world = HawkEye.server.getWorld(entry.getWorld());
            if(world != null) {
               Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
               Block block = world.getBlockAt(loc);
               BlockState state = block.getState();
               this.undo.add(entry);
               entry.rebuild(block);
               entry.setUndoState(state);
               ++this.counter;
            }
         }
      }

      if(!this.rebuildQueue.hasNext()) {
         Bukkit.getServer().getScheduler().cancelTask(this.timerID);
         Collections.reverse(this.undo);
         this.session.setDoingRollback(false);
         this.session.setRollbackResults(this.undo);
         Util.sendMessage(this.session.getSender(), "&cRebuild complete, &7" + this.counter + "&c edits performed");
         Util.sendMessage(this.session.getSender(), "&cUndo this rebuild using &7/hawk undo");
         Util.debug("Rebuild complete, " + this.counter + " edits performed");
      }

   }
}

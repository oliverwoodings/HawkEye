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
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class Rollback implements Runnable {

   private final PlayerSession session;
   private Iterator rollbackQueue;
   private final List undo = new ArrayList();
   private int timerID;
   private RollbackType rollbackType;


   public Rollback(RollbackType rollbackType, PlayerSession session) {
      this.rollbackType = RollbackType.GLOBAL;
      this.rollbackType = rollbackType;
      this.session = session;
      session.setRollbackType(rollbackType);
      this.rollbackQueue = session.getRollbackResults().iterator();
      if(!this.rollbackQueue.hasNext()) {
         Util.sendMessage(session.getSender(), "&cNo results found to rollback");
      } else {
         Util.debug("Starting rollback of " + session.getRollbackResults().size() + " results");
         session.setDoingRollback(true);
         Util.sendMessage(session.getSender(), "&cAttempting to rollback &7" + session.getRollbackResults().size() + "&c results");
         this.timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1L, 2L);
      }
   }

   public void run() {
      int i = 0;

      while(i < 200 && this.rollbackQueue.hasNext()) {
         ++i;
         DataEntry entry = (DataEntry)this.rollbackQueue.next();
         if(entry.getType() != null && entry.getType().canRollback()) {
            World world = HawkEye.server.getWorld(entry.getWorld());
            if(world != null) {
               Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
               Block block = world.getBlockAt(loc);
               BlockState state = block.getState();
               entry.setUndoState(state);
               if(this.rollbackType == RollbackType.GLOBAL && entry.rollback(world.getBlockAt(loc))) {
                  this.undo.add(entry);
               } else if(this.rollbackType == RollbackType.LOCAL && entry.rollbackPlayer(block, (Player)this.session.getSender())) {
                  this.undo.add(entry);
               }
            }
         }
      }

      if(!this.rollbackQueue.hasNext()) {
         Bukkit.getServer().getScheduler().cancelTask(this.timerID);
         Collections.reverse(this.undo);
         this.session.setDoingRollback(false);
         this.session.setRollbackResults(this.undo);
         if(this.rollbackType == RollbackType.GLOBAL) {
            Util.sendMessage(this.session.getSender(), "&cRollback complete, &7" + this.undo.size() + "&c edits performed");
            Util.sendMessage(this.session.getSender(), "&cUndo this rollback using &7/hawk undo");
            if(Config.DeleteDataOnRollback) {
               DataManager.deleteEntries(this.undo);
            }
         } else {
            Util.sendMessage(this.session.getSender(), "&cRollback preview complete, &7" + this.undo.size() + "&c edits performed to you");
            Util.sendMessage(this.session.getSender(), "&cType &7/hawk preview apply&c to make these changes permanent or &7/hawk preview cancel&c to cancel");
         }

         Util.debug("Rollback complete, " + this.undo.size() + " edits performed");
      }

   }

   public static enum RollbackType {

      GLOBAL("GLOBAL", 0),
      REBUILD("REBUILD", 1),
      LOCAL("LOCAL", 2);
      // $FF: synthetic field
      private static final RollbackType[] $VALUES = new RollbackType[]{GLOBAL, REBUILD, LOCAL};


      private RollbackType(String var1, int var2) {}

   }
}

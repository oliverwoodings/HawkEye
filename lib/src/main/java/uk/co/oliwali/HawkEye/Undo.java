package uk.co.oliwali.HawkEye;

import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class Undo implements Runnable {

   private final PlayerSession session;
   private Iterator undoQueue;
   private int timerID;
   private int counter = 0;
   private Rollback.RollbackType undoType;


   public Undo(PlayerSession session) {
      this.undoType = Rollback.RollbackType.GLOBAL;
      this.session = session;
      this.undoType = session.getRollbackType();
      if(this.undoType == null) {
         Util.sendMessage(session.getSender(), "&cNo results found to undo");
      } else {
         this.undoQueue = session.getRollbackResults().iterator();
         if(session.doingRollback()) {
            Util.sendMessage(session.getSender(), "&cYour previous rollback is still processing, please wait before performing an undo!");
         } else if(!this.undoQueue.hasNext()) {
            Util.sendMessage(session.getSender(), "&cNo results found to undo");
         } else {
            if(this.undoType == Rollback.RollbackType.GLOBAL && Config.DeleteDataOnRollback) {
               DataManager.getQueue().addAll(session.getRollbackResults());
            }

            Util.debug("Starting undo of " + session.getRollbackResults().size() + " results");
            session.setDoingRollback(true);
            Util.sendMessage(session.getSender(), "&cAttempting to undo &7" + session.getRollbackResults().size() + "&c rollback edits");
            this.timerID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Bukkit.getServer().getPluginManager().getPlugin("HawkEye"), this, 1L, 2L);
         }
      }
   }

   public void run() {
      for(byte i = 0; i < 200 && this.undoQueue.hasNext(); ++this.counter) {
         DataEntry entry = (DataEntry)this.undoQueue.next();
         if(this.undoType != Rollback.RollbackType.LOCAL) {
            entry.undo();
         } else if(entry.getUndo() != null) {
            Player player = (Player)this.session.getSender();
            BlockState state = entry.getUndo().getState();
            player.sendBlockChange(state.getLocation(), state.getType(), state.getData().getData());
         }
      }

      if(!this.undoQueue.hasNext()) {
         Bukkit.getServer().getScheduler().cancelTask(this.timerID);
         this.session.setRollbackType((Rollback.RollbackType)null);
         this.session.setDoingRollback(false);
         this.session.setRollbackResults((List)null);
         Util.sendMessage(this.session.getSender(), "&cUndo complete, &7" + this.counter + " &cedits performed");
         Util.debug("Undo complete, " + this.counter + " edits performed");
      }

   }
}

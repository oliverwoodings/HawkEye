package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class UndoSign extends UndoBlock {

   private String[] lines;


   public UndoSign(BlockState state) {
      super(state);
      this.lines = ((Sign)state).getLines();
   }

   public void undo() {
      if(this.state != null) {
         this.state.update(true);
         Sign s2 = (Sign)this.state.getBlock().getState();

         for(int i = 0; i < this.lines.length; ++i) {
            s2.setLine(i, this.lines[i]);
         }

         s2.update();
      }

   }
}

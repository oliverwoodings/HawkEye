package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.block.BlockState;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;

public class UndoBlock {

   protected BlockState state;


   public UndoBlock(BlockState state) {
      this.state = state;
   }

   public void undo() {
      if(this.state != null) {
         int id = this.state.getTypeId();
         byte data = this.state.getData().getData();
         HawkBlockType.getHawkBlock(id).Restore(this.state.getBlock(), id, data);
      }

   }

   public BlockState getState() {
      return this.state;
   }
}

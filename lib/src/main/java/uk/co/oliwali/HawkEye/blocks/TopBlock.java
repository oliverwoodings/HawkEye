package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;

public class TopBlock implements HawkBlock {

   public void Restore(Block b, int id, int data) {
      b.setTypeIdAndData(id, (byte)data, false);
   }

   public void logAttachedBlocks(Block b, Player p, DataType type) {}

   public Block getCorrectBlock(Block b) {
      return b;
   }

   public boolean isTopBlock() {
      return true;
   }

   public boolean isAttached() {
      return true;
   }
}

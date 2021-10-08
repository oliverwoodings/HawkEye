package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;

public class DoorBlock implements HawkBlock {

   public void Restore(Block b, int id, int data) {
      if(data != 8 && data != 9) {
         b.setTypeIdAndData(id, (byte)data, false);
         Block block = b.getRelative(BlockFace.UP);
         Block side = null;
         Block oside = null;
         if(data == 0) {
            side = b.getRelative(BlockFace.NORTH);
            oside = b.getRelative(BlockFace.SOUTH);
         } else if(data == 1) {
            side = b.getRelative(BlockFace.EAST);
            oside = b.getRelative(BlockFace.WEST);
         } else if(data == 2) {
            side = b.getRelative(BlockFace.SOUTH);
            oside = b.getRelative(BlockFace.NORTH);
         } else {
            side = b.getRelative(BlockFace.WEST);
            oside = b.getRelative(BlockFace.EAST);
         }

         int id2 = side.getTypeId();
         int oid = oside.getTypeId();
         if(id2 != 64 && id2 != 71) {
            if(oid != 64 && oid != 71) {
               block.setTypeIdAndData(id, (byte)8, false);
            } else {
               oside.getRelative(BlockFace.UP).setTypeIdAndData(id, (byte)9, false);
               block.setTypeIdAndData(id, (byte)8, false);
            }
         } else {
            block.setTypeIdAndData(id, (byte)9, false);
         }

      }
   }

   public void logAttachedBlocks(Block b, Player p, DataType type) {}

   public Block getCorrectBlock(Block b) {
      return b.getData() != 8 && b.getData() != 9?b:b.getRelative(BlockFace.DOWN);
   }

   public boolean isTopBlock() {
      return true;
   }

   public boolean isAttached() {
      return false;
   }
}

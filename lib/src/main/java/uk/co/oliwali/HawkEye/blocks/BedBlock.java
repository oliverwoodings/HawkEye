package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;

public class BedBlock implements HawkBlock {

   public void Restore(Block b, int id, int data) {
      if(data <= 7) {
         b.setTypeIdAndData(id, (byte)data, false);
         byte beddata = 0;
         Block bed = null;
         if(data == 0) {
            bed = b.getRelative(BlockFace.SOUTH);
            beddata = 8;
         }

         if(data == 1) {
            bed = b.getRelative(BlockFace.WEST);
            beddata = 9;
         }

         if(data == 2) {
            bed = b.getRelative(BlockFace.NORTH);
            beddata = 10;
         }

         if(data == 3) {
            bed = b.getRelative(BlockFace.EAST);
            beddata = 11;
         }

         if(bed != null) {
            bed.setTypeIdAndData(id, (byte)beddata, false);
         }

      }
   }

   public void logAttachedBlocks(Block b, Player p, DataType type) {}

   public Block getCorrectBlock(Block b) {
      return b.getData() > 7?b.getRelative(getBedFace(b)):b;
   }

   public static BlockFace getBedFace(Block block) {
      byte Data = block.getData();
      switch(Data) {
      case 8:
         return BlockFace.NORTH;
      case 9:
         return BlockFace.EAST;
      case 10:
         return BlockFace.SOUTH;
      case 11:
         return BlockFace.WEST;
      default:
         return null;
      }
   }

   public boolean isTopBlock() {
      return false;
   }

   public boolean isAttached() {
      return false;
   }
}

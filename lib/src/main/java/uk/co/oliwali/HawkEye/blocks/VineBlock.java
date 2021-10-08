package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

public class VineBlock implements HawkBlock {

   public void logAttachedBlocks(Block b, Player p, DataType type) {
      for(b = b.getRelative(BlockFace.DOWN); b.getType() == Material.VINE; b = b.getRelative(BlockFace.DOWN)) {
         Material b2 = b.getRelative(this.getVineFace(b.getData())).getType();
         if(b2.isSolid()) {
            break;
         }

         DataManager.addEntry(new BlockEntry(p, type, b));
      }

   }

   public boolean isAttached() {
      return true;
   }

   public BlockFace getVineFace(int data) {
      switch(data) {
      case 1:
         return BlockFace.SOUTH;
      case 2:
         return BlockFace.WEST;
      case 3:
      case 5:
      case 6:
      case 7:
      default:
         return BlockFace.NORTH;
      case 4:
         return BlockFace.NORTH;
      case 8:
         return BlockFace.EAST;
      }
   }

   public void Restore(Block b, int id, int data) {
      b.setTypeIdAndData(id, (byte)data, false);
   }

   public Block getCorrectBlock(Block b) {
      return b;
   }

   public boolean isTopBlock() {
      return false;
   }
}

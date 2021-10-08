package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;

public class PistonBlock implements HawkBlock {

   public void Restore(Block b, int id, int data) {
      if(id == 34) {
         b = b.getRelative(this.getPistonFromExtension(data));
      }

      switch(data) {
      case 0:
      case 8:
         b.setTypeIdAndData(id, (byte)0, true);
         break;
      case 1:
      case 9:
         b.setTypeIdAndData(id, (byte)1, true);
         break;
      case 2:
      case 10:
         b.setTypeIdAndData(id, (byte)2, true);
         break;
      case 3:
      case 11:
         b.setTypeIdAndData(id, (byte)3, true);
         break;
      case 4:
      case 12:
         b.setTypeIdAndData(id, (byte)4, true);
         break;
      case 5:
      case 13:
         b.setTypeIdAndData(id, (byte)5, true);
         break;
      case 6:
      case 7:
      default:
         return;
      }

   }

   public void logAttachedBlocks(Block b, Player p, DataType type) {}

   public Block getCorrectBlock(Block b) {
      return b.getType() == Material.PISTON_EXTENSION?b.getRelative(this.getPistonFromExtension(b.getData())):b;
   }

   public boolean isTopBlock() {
      return false;
   }

   public boolean isAttached() {
      return false;
   }

   public BlockFace getPistonFromExtension(int data) {
      switch(data) {
      case 0:
      case 8:
         return BlockFace.UP;
      case 1:
      case 9:
         return BlockFace.DOWN;
      case 2:
      case 10:
         return BlockFace.SOUTH;
      case 3:
      case 11:
         return BlockFace.NORTH;
      case 4:
      case 12:
         return BlockFace.EAST;
      case 5:
      case 13:
         return BlockFace.WEST;
      case 6:
      case 7:
      default:
         return BlockFace.EAST;
      }
   }
}

package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class SignBlock implements HawkBlock {

   public void Restore(Block b, int id, int data) {
      b.setTypeIdAndData(id, (byte)data, false);
   }

   public void logAttachedBlocks(Block b, Player p, DataType type) {
      Block topb = b.getRelative(BlockFace.UP);
      HawkBlock hb = HawkBlockType.getHawkBlock(topb.getTypeId());
      if(hb.isTopBlock()) {
         hb.logAttachedBlocks(topb, p, type);
         if(hb instanceof SignBlock && DataType.SIGN_BREAK.isLogged()) {
            DataManager.addEntry(new SignEntry(p, DataType.SIGN_BREAK, hb.getCorrectBlock(topb)));
         }
      }

      BlockFace[] arr$ = BlockUtil.faces;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         BlockFace face = arr$[i$];
         Block attch = b.getRelative(face);
         if(attch.getType() == Material.WALL_SIGN && DataType.SIGN_BREAK.isLogged()) {
            DataManager.addEntry(new SignEntry(p, DataType.SIGN_BREAK, attch));
         }
      }

   }

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

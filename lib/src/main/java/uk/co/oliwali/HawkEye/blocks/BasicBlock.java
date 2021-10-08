package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class BasicBlock extends Default {

   public void logAttachedBlocks(Block b, Player p, DataType type) {
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
}
package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockEntry;

public class TallPlant extends Default {

   public void logAttachedBlocks(Block b, Player p, DataType type) {
      for(b = b.getRelative(BlockFace.UP); HawkBlockType.getHawkBlock(b.getTypeId()).equals(this); b = b.getRelative(BlockFace.UP)) {
         DataManager.addEntry(new BlockEntry(p, type, b));
      }

   }

   public boolean isTopBlock() {
      return true;
   }
}

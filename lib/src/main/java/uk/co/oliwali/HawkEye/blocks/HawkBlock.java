package uk.co.oliwali.HawkEye.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;

public interface HawkBlock {

   void Restore(Block var1, int var2, int var3);

   Block getCorrectBlock(Block var1);

   void logAttachedBlocks(Block var1, Player var2, DataType var3);

   boolean isTopBlock();

   boolean isAttached();
}

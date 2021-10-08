package uk.co.oliwali.HawkEye.WorldEdit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bags.BlockBag;

public class WESessionFactory extends EditSessionFactory {

   public static void enableWELogging() {
      WorldEdit.getInstance().setEditSessionFactory(new WESessionFactory());
   }

   public EditSession getEditSession(LocalWorld world, int maxBlocks, LocalPlayer player) {
      return new HawkSession(world, maxBlocks, player);
   }

   public EditSession getEditSession(LocalWorld world, int maxBlocks, BlockBag blockBag, LocalPlayer player) {
      return new HawkSession(world, maxBlocks, blockBag, player);
   }
}

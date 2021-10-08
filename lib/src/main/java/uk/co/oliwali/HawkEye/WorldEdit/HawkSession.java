package uk.co.oliwali.HawkEye.WorldEdit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bags.BlockBag;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;

public class HawkSession extends EditSession {

   private LocalPlayer player;


   public HawkSession(LocalWorld world, int maxBlocks, LocalPlayer player) {
      super(world, maxBlocks);
      this.player = player;
   }

   public HawkSession(LocalWorld world, int maxBlocks, BlockBag blockBag, LocalPlayer player) {
      super(world, maxBlocks, blockBag);
      this.player = player;
   }

   public boolean rawSetBlock(Vector v, BaseBlock block) {
      World world = ((BukkitWorld)this.player.getWorld()).getWorld();
      BlockState bs = null;
      int b = world.getBlockTypeIdAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
      byte bdata = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ()).getData();
      if(b == 63 || b == 68) {
         bs = world.getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ()).getState();
      }

      if(!super.rawSetBlock(v, block)) {
         return false;
      } else {
         Location loc = new Location(world, (double)v.getBlockX(), (double)v.getBlockY(), (double)v.getBlockZ());
         if(block.getType() != 0) {
            DataManager.addEntry(new BlockChangeEntry(this.player.getName(), DataType.WORLDEDIT_PLACE, loc, b, bdata, block.getType(), block.getData()));
         } else if((b == 63 || b == 68) && DataType.SIGN_BREAK.isLogged()) {
            DataManager.addEntry(new SignEntry(this.player.getName(), DataType.SIGN_BREAK, bs));
         } else {
            DataManager.addEntry(new BlockEntry(this.player.getName(), DataType.WORLDEDIT_BREAK, b, bdata, loc));
         }

         return true;
      }
   }
}

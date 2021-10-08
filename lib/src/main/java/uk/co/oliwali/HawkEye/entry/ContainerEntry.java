package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.InventoryUtil;

public class ContainerEntry extends DataEntry {

   public ContainerEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      super(playerId, timestamp, dataId, typeId, plugin, worldId, x, y, z);
      this.interpretSqlData(data);
   }

   public ContainerEntry() {}

   public ContainerEntry(Player player, Location location, String diff) {
      this.data = diff;
      this.setInfo(player, DataType.CONTAINER_TRANSACTION, location);
   }

   public ContainerEntry(String player, Location location, String diff) {
      this.data = diff;
      this.setInfo(player, DataType.CONTAINER_TRANSACTION, location);
   }

   public String getStringData() {
      if(this.data.contains("&")) {
         this.data = InventoryUtil.updateInv(this.data);
      }

      return InventoryUtil.dataToString(this.data);
   }

   public boolean rollback(Block block) {
      BlockState blockState = block.getState();
      if(!(blockState instanceof InventoryHolder)) {
         return false;
      } else {
         Inventory inv = ((InventoryHolder)blockState).getInventory();
         if(this.data.contains("&")) {
            this.data = InventoryUtil.updateInv(this.data);
         }

         String[] arr$ = this.data.split("@");
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            if(s.startsWith("+")) {
               inv.removeItem(new ItemStack[]{InventoryUtil.uncompressItem(s)});
            } else {
               if(!s.startsWith("-")) {
                  return false;
               }

               inv.addItem(new ItemStack[]{InventoryUtil.uncompressItem(s)});
            }
         }

         return true;
      }
   }

   public boolean rebuild(Block block) {
      BlockState blockState = block.getState();
      if(!(blockState instanceof InventoryHolder)) {
         return false;
      } else {
         Inventory inv = ((InventoryHolder)blockState).getInventory();
         if(this.data.contains("&")) {
            this.data = InventoryUtil.updateInv(this.data);
         }

         String[] arr$ = this.data.split("@");
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            String s = arr$[i$];
            if(s.startsWith("+")) {
               inv.addItem(new ItemStack[]{InventoryUtil.uncompressItem(s)});
            } else {
               if(!s.startsWith("-")) {
                  return false;
               }

               inv.removeItem(new ItemStack[]{InventoryUtil.uncompressItem(s)});
            }
         }

         return true;
      }
   }
}

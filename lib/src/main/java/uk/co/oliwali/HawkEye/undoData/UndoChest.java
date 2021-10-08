package uk.co.oliwali.HawkEye.undoData;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class UndoChest extends UndoBlock {

   private ItemStack[] is;


   public UndoChest(BlockState state) {
      super(state);
      ItemStack[] tmp = ((InventoryHolder)state).getInventory().getContents();
      int len = tmp.length;
      this.is = new ItemStack[len];

      for(int i = 0; i < len; ++i) {
         this.is[i] = tmp[i] == null?null:tmp[i].clone();
      }

   }

   public void undo() {
      if(this.is != null && this.state != null) {
         this.state.update(true);
         Inventory inv2 = ((InventoryHolder)this.state.getBlock().getState()).getInventory();
         inv2.setContents(this.is);
      }

   }
}

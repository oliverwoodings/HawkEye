package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class BlockEntry extends DataEntry {

   public BlockEntry() {}

   public BlockEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      super(playerId, timestamp, dataId, typeId, data, plugin, worldId, x, y, z);
   }

   public BlockEntry(String player, DataType type, Block block) {
      this.setInfo(player, type, block.getLocation());
      this.data = BlockUtil.getBlockString(block);
   }

   public BlockEntry(Player player, DataType type, Block block) {
      this.setInfo(player, type, block.getLocation());
      this.data = BlockUtil.getBlockString(block);
   }

   public BlockEntry(Player player, DataType type, Block block, Location loc) {
      this.setInfo(player, type, loc);
      this.data = BlockUtil.getBlockString(block);
   }

   public BlockEntry(String player, DataType type, int block, int blockdata, Location loc) {
      this.setInfo(player, type, loc);
      if(blockdata != 0) {
         this.data = block + ":" + blockdata;
      } else {
         this.data = Integer.toString(block);
      }

   }

   public String getStringData() {
      return BlockUtil.getBlockStringName(this.data);
   }

   public boolean rollback(Block block) {
      BlockUtil.setBlockString(block, this.data);
      return true;
   }

   public boolean rollbackPlayer(Block block, Player player) {
      player.sendBlockChange(block.getLocation(), BlockUtil.getIdFromString(this.data), BlockUtil.getDataFromString(this.data));
      return true;
   }

   public boolean rebuild(Block block) {
      if(this.data == null) {
         return false;
      } else {
         block.setTypeId(0);
         return true;
      }
   }
}

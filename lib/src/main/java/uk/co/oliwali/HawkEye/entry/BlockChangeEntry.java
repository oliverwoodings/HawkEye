package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.BlockUtil;

public class BlockChangeEntry extends DataEntry {

   private String from = null;
   private String to = null;


   public BlockChangeEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      super(playerId, timestamp, dataId, typeId, plugin, worldId, x, y, z);
      this.interpretSqlData(data);
   }

   public BlockChangeEntry(Player player, DataType type, Location loc, BlockState from, BlockState to) {
      this.setInfo(player, type, loc);
      this.from = BlockUtil.getBlockString(from);
      this.to = BlockUtil.getBlockString(to);
   }

   public BlockChangeEntry(Player player, DataType type, Location loc, BlockState from, int id) {
      this.setInfo(player, type, loc);
      this.from = BlockUtil.getBlockString(from);
      this.to = String.valueOf(id);
   }

   public BlockChangeEntry(String string, DataType type, Location loc, Block block, BlockState to) {
      this.setInfo(string, type, loc);
      this.from = BlockUtil.getBlockString(block);
      this.to = BlockUtil.getBlockString(to);
   }

   public BlockChangeEntry(String player, DataType type, Location loc, BlockState from, BlockState to) {
      this.setInfo(player, type, loc);
      this.from = BlockUtil.getBlockString(from);
      this.to = BlockUtil.getBlockString(to);
   }

   public BlockChangeEntry(Player player, DataType type, Location loc, String from, String to) {
      this.setInfo(player, type, loc);
      this.from = from;
      this.to = to;
   }

   public BlockChangeEntry(String player, DataType type, Location loc, String from, String to) {
      this.setInfo(player, type, loc);
      this.from = from;
      this.to = to;
   }

   public BlockChangeEntry(Player player, DataType type, Location loc, String from, BlockState to) {
      this.setInfo(player, type, loc);
      this.from = from;
      this.to = BlockUtil.getBlockString(to);
   }

   public BlockChangeEntry(String player, DataType type, Location loc, int blockfrom, int blockfromdata, int blockto, int blockdatato) {
      this.setInfo(player, type, loc);
      if(blockfromdata != 0) {
         this.from = blockfrom + ":" + blockfromdata;
      } else {
         this.from = Integer.toString(blockfrom);
      }

      if(blockdatato != 0) {
         this.to = blockto + ":" + blockdatato;
      } else {
         this.to = Integer.toString(blockto);
      }

   }

   public BlockChangeEntry(String player, DataType type, Location loc, BlockState from, String to) {
      this.setInfo(player, type, loc);
      this.from = BlockUtil.getBlockString(from);
      this.to = to;
   }

   public String getStringData() {
      return this.from != null && !this.from.equals("0")?BlockUtil.getBlockStringName(this.from) + " changed to " + BlockUtil.getBlockStringName(this.to):BlockUtil.getBlockStringName(this.to);
   }

   public String getSqlData() {
      return this.from + "-" + this.to;
   }

   public boolean rollback(Block block) {
      if(this.from == null) {
         block.setType(Material.AIR);
      } else {
         BlockUtil.setBlockString(block, this.from);
      }

      return true;
   }

   public boolean rollbackPlayer(Block block, Player player) {
      if(this.from == null) {
         player.sendBlockChange(block.getLocation(), 0, (byte)0);
      } else {
         player.sendBlockChange(block.getLocation(), BlockUtil.getIdFromString(this.from), BlockUtil.getDataFromString(this.from));
      }

      return true;
   }

   public boolean rebuild(Block block) {
      if(this.to == null) {
         return false;
      } else {
         BlockUtil.setBlockString(block, this.to);
         return true;
      }
   }

   public void interpretSqlData(String data) {
      if(data.indexOf("-") == -1) {
         this.from = null;
         this.to = data;
      } else {
         this.from = data.substring(0, data.indexOf("-"));
         this.to = data.substring(data.indexOf("-") + 1);
      }

   }
}

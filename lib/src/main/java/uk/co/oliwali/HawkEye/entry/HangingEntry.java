package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.EntityUtil;

public class HangingEntry extends DataEntry {

   public HangingEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      super(playerId, timestamp, dataId, typeId, data, plugin, worldId, x, y, z);
   }

   public HangingEntry() {}

   public HangingEntry(Player player, DataType type, Location loc, int en, int da, int extra) {
      this.setInfo(player, type, loc);
      this.data = en + ":" + da + ":" + extra;
   }

   public HangingEntry(String player, DataType type, Location loc, int en, int da, int extra) {
      this.setInfo(player, type, loc);
      this.data = en + ":" + da + ":" + extra;
   }

   public String getStringData() {
      return EntityUtil.getStringName(this.data);
   }

   public boolean rollback(Block block) {
      EntityUtil.setBlockString(block, this.data);
      return true;
   }

   public boolean rollbackPlayer(Block block, Player player) {
      return true;
   }

   public boolean rebuild(Block block) {
      return true;
   }
}

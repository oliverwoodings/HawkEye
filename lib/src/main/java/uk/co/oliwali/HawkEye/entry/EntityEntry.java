package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.EntityUtil;

public class EntityEntry extends DataEntry {

   public EntityEntry() {}

   public EntityEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      super(playerId, timestamp, dataId, typeId, data, plugin, worldId, x, y, z);
   }

   public EntityEntry(String player, DataType type, Location loc, String en) {
      this.setInfo(player, type, loc);
      this.data = en;
   }

   public String getStringData() {
      return this.data;
   }

   public boolean rollback(Block block) {
      EntityUtil.setEntityString(block, this.data);
      return true;
   }

   public boolean rollbackPlayer(Block block, Player player) {
      return true;
   }

   public boolean rebuild(Block block) {
      return true;
   }
}

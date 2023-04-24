package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import java.util.Calendar;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.undoData.UndoBlock;
import uk.co.oliwali.HawkEye.undoData.UndoChest;
import uk.co.oliwali.HawkEye.undoData.UndoSign;
import uk.co.oliwali.HawkEye.util.Util;

public class DataEntry {

   private String plugin = null;
   private int dataId;
   private Timestamp timestamp;
   private String player = null;
   private String world;
   private String[] entity = null;
   private int entityId = 0;
   private double x;
   private double y;
   private double z;
   private UndoBlock undo;
   protected DataType type = null;
   protected String data = null;


   public DataEntry() {}

   public DataEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      this.player = DataManager.getPlayer(playerId);
      this.timestamp = timestamp;
      this.dataId = dataId;
      this.type = DataType.fromId(typeId);
      this.plugin = plugin;
      this.world = DataManager.getWorld(worldId);
      this.x = (double)x;
      this.y = (double)y;
      this.z = (double)z;
      this.data = data;
   }

   public DataEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String plugin, int worldId, int x, int y, int z) {
      this.player = DataManager.getPlayer(playerId);
      this.timestamp = timestamp;
      this.dataId = dataId;
      this.type = DataType.fromId(typeId);
      this.plugin = plugin;
      this.world = DataManager.getWorld(worldId);
      this.x = (double)x;
      this.y = (double)y;
      this.z = (double)z;
   }

   public DataEntry(Player player, DataType type, Location loc, String data) {
      this.setInfo(player, type, loc);
      this.setData(data);
   }

   public DataEntry(String player, DataType type, Location loc, String data) {
      this.setInfo(player, type, loc);
      this.setData(data);
   }

   public void setPlugin(String plugin) {
      this.plugin = plugin;
   }

   public String getPlugin() {
      return this.plugin;
   }

   public void setDataId(int dataId) {
      this.dataId = dataId;
   }

   public int getDataId() {
      return this.dataId;
   }

   public void setTimestamp(Timestamp timestamp) {
      this.timestamp = timestamp;
   }

   public Timestamp getTimestamp() {
      return this.timestamp;
   }

   public void setPlayer(String player) {
      this.player = player;
   }

   public String getPlayer() {
      return this.player;
   }

   public void setType(DataType type) {
      this.type = type;
   }

   public DataType getType() {
      return this.type;
   }

   public void setWorld(String world) {
      this.world = world;
   }

   public String getWorld() {
      return this.world;
   }
   
   public void setEntity(String[] entity) {
	   this.entity = entity;
   }
   
   public String[] getEntity() {
	   return this.entity;
   }
   
   public void setEntityId(int entityId) {
	   this.entityId = entityId;
   }
   
   public int getEntityId() {
	   return this.entityId;
   }

   public void setX(double x) {
      this.x = x;
   }

   public double getX() {
      return this.x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public double getY() {
      return this.y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public double getZ() {
      return this.z;
   }

   public void setData(String data) {
      this.data = data;
   }

   public void setUndoState(BlockState state) {
      if(state instanceof InventoryHolder) {
         this.undo = new UndoChest(state);
      } else if(state instanceof Sign) {
         this.undo = new UndoSign(state);
      } else {
         this.undo = new UndoBlock(state);
      }

   }

   public UndoBlock getUndo() {
      return this.undo;
   }

   public String getStringData() {
      return this.data;
   }

   public int getIntData() {
      return Integer.parseInt(this.data.split(":")[0]);
   }

   public void interpretSqlData(String data) {
      this.data = data;
   }

   public String getSqlData() {
      return this.data;
   }

   public boolean rollback(Block block) {
      return false;
   }

   public boolean rollbackPlayer(Block block, Player player) {
      return false;
   }

   public boolean rebuild(Block block) {
      return false;
   }

   public void undo() {
      this.undo.undo();
   }

   public void setInfo(Player player, DataType type, Location loc) {
      this.setInfo(player.getName(), type, loc);
   }

   public void setInfo(String player, DataType type, Location loc) {
      this.setInfo(player, "HawkEye", type, loc);
   }

   public void setInfo(String player, JavaPlugin instance, DataType type, Location loc) {
      this.setInfo(player, instance.getDescription().getName(), type, loc);
   }

   public void setInfo(String player, String instance, DataType type, Location loc) {
      loc = Util.getSimpleLocation(loc);
      this.setTimestamp(new Timestamp(Calendar.getInstance().getTimeInMillis()));
      this.setPlugin(instance);
      this.setPlayer(player);
      this.setType(type);
      this.setWorld(loc.getWorld().getName());
      this.setX(loc.getX());
      this.setY(loc.getY());
      this.setZ(loc.getZ());
   }
}

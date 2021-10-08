package uk.co.oliwali.HawkEye.listeners;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.material.MaterialData;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;

public class MonitorLiquidFlow extends HawkEyeListener {

   private List fluidBlocks = Arrays.asList(new Integer[]{Integer.valueOf(0), Integer.valueOf(27), Integer.valueOf(28), Integer.valueOf(31), Integer.valueOf(32), Integer.valueOf(37), Integer.valueOf(38), Integer.valueOf(39), Integer.valueOf(40), Integer.valueOf(50), Integer.valueOf(51), Integer.valueOf(55), Integer.valueOf(59), Integer.valueOf(66), Integer.valueOf(69), Integer.valueOf(70), Integer.valueOf(75), Integer.valueOf(76), Integer.valueOf(78), Integer.valueOf(93), Integer.valueOf(94)});
   private HashMap playerCache = new HashMap(10);
   private int cacheRunTime = 10;
   private int timerId = -1;


   public MonitorLiquidFlow(HawkEye HawkEye) {
      super(HawkEye);
   }

   public void startCacheCleaner() {
      if(DataType.PLAYER_LAVA_FLOW.isLogged() || DataType.PLAYER_WATER_FLOW.isLogged()) {
         Bukkit.getScheduler().cancelTask(this.timerId);
         this.timerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            public void run() {
               MonitorLiquidFlow.access$010(MonitorLiquidFlow.this);
               if(MonitorLiquidFlow.this.cacheRunTime == 0) {
                  MonitorLiquidFlow.this.playerCache.clear();
               }

            }
         }, 20L, 20L);
      }

   }

   public void addToCache(Location l, String p) {
      this.cacheRunTime = 10;
      this.playerCache.put(l, p);
   }

   @HawkEvent(
      dataType = {DataType.PLAYER_LAVA_FLOW, DataType.PLAYER_WATER_FLOW}
   )
   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
      Material bucket = event.getBucket();
      Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
      if(bucket == Material.WATER_BUCKET && DataType.PLAYER_WATER_FLOW.isLogged() || bucket == Material.LAVA_BUCKET && DataType.PLAYER_LAVA_FLOW.isLogged()) {
         this.playerCache.put(loc, event.getPlayer().getName());
      }

   }

   @HawkEvent(
      dataType = {DataType.PLAYER_LAVA_FLOW, DataType.PLAYER_WATER_FLOW}
   )
   public void onPlayerBlockFromTo(BlockFromToEvent event) {
      if(event.getBlock().isLiquid()) {
         Location loc = event.getToBlock().getLocation();
         BlockState from = event.getBlock().getState();
         BlockState to = event.getToBlock().getState();
         if(from.getType() != to.getType()) {
            Location fromloc = from.getLocation();
            String player = (String)this.playerCache.get(fromloc);
            if(player != null) {
               MaterialData data = from.getData();
               if(from.getTypeId() != 10 && from.getTypeId() != 11) {
                  if(from.getTypeId() == 8 || from.getTypeId() == 9) {
                     if(this.fluidBlocks.contains(Integer.valueOf(to.getTypeId()))) {
                        data.setData((byte)(from.getRawData() + 1));
                        from.setData(data);
                        DataManager.addEntry(new BlockChangeEntry(player, DataType.PLAYER_WATER_FLOW, loc, to, from));
                        this.addToCache(loc, player);
                     }

                     BlockState lower = event.getToBlock().getRelative(BlockFace.DOWN).getState();
                     if(lower.getTypeId() == 10 || lower.getTypeId() == 11) {
                        from.setTypeId(lower.getData().getData() == 0?49:4);
                        loc.setY(loc.getY() - 1.0D);
                        DataManager.addEntry(new BlockChangeEntry(player, DataType.PLAYER_WATER_FLOW, loc, lower, from));
                        this.addToCache(loc, player);
                     }
                  }
               } else {
                  if(this.fluidBlocks.contains(Integer.valueOf(to.getTypeId()))) {
                     data.setData((byte)(from.getRawData() + 1));
                     from.setData(data);
                  } else if(to.getTypeId() == 8 || to.getTypeId() == 9) {
                     from.setTypeId(event.getFace() == BlockFace.DOWN?10:4);
                     data.setData((byte)0);
                     from.setData(data);
                  }

                  DataManager.addEntry(new BlockChangeEntry(player, DataType.PLAYER_LAVA_FLOW, loc, to, from));
                  this.addToCache(loc, player);
               }

            }
         }
      }
   }

   @HawkEvent(
      dataType = {DataType.LAVA_FLOW, DataType.WATER_FLOW}
   )
   public void onBlockFromTo(BlockFromToEvent event) {
      if(event.getBlock().isLiquid()) {
         Location loc = event.getToBlock().getLocation();
         BlockState from = event.getBlock().getState();
         BlockState to = event.getToBlock().getState();
         if(from.getType() != to.getType()) {
            MaterialData data = from.getData();
            if(from.getTypeId() != 10 && from.getTypeId() != 11) {
               if(from.getTypeId() == 8 || from.getTypeId() == 9) {
                  if(this.fluidBlocks.contains(Integer.valueOf(to.getTypeId()))) {
                     data.setData((byte)(from.getRawData() + 1));
                     from.setData(data);
                     DataManager.addEntry(new BlockChangeEntry("Environment", DataType.WATER_FLOW, loc, to, from));
                  }

                  BlockState lower = event.getToBlock().getRelative(BlockFace.DOWN).getState();
                  if(lower.getTypeId() == 10 || lower.getTypeId() == 11) {
                     from.setTypeId(lower.getData().getData() == 0?49:4);
                     loc.setY(loc.getY() - 1.0D);
                     DataManager.addEntry(new BlockChangeEntry("Environment", DataType.WATER_FLOW, loc, lower, from));
                  }
               }
            } else {
               if(this.fluidBlocks.contains(Integer.valueOf(to.getTypeId()))) {
                  data.setData((byte)(from.getRawData() + 1));
                  from.setData(data);
               } else if(to.getTypeId() == 8 || to.getTypeId() == 9) {
                  from.setTypeId(event.getFace() == BlockFace.DOWN?10:4);
                  data.setData((byte)0);
                  from.setData(data);
               }

               DataManager.addEntry(new BlockChangeEntry("Environment", DataType.LAVA_FLOW, loc, to, from));
            }

         }
      }
   }

   // $FF: synthetic method
   static int access$010(MonitorLiquidFlow x0) {
      return x0.cacheRunTime--;
   }
}

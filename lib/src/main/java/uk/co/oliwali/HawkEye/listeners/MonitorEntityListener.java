package uk.co.oliwali.HawkEye.listeners;

import java.util.Arrays;
import java.util.Iterator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.EntityEntry;
import uk.co.oliwali.HawkEye.entry.HangingEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.EntityUtil;
import uk.co.oliwali.HawkEye.util.Util;

public class MonitorEntityListener extends HawkEyeListener {

   public MonitorEntityListener(HawkEye HawkEye) {
      super(HawkEye);
   }

   @HawkEvent(
      dataType = {DataType.PVP_DEATH, DataType.MOB_DEATH, DataType.OTHER_DEATH, DataType.ENTITY_KILL}
   )
   public void onEntityDeath(EntityDeathEvent event) {
      LivingEntity entity = event.getEntity();
      Player killer;
      if(entity instanceof Player) {
         killer = (Player)entity;
         Entity kill;
         if(killer.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            kill = ((EntityDamageByEntityEvent)((EntityDamageByEntityEvent)killer.getLastDamageCause())).getDamager();
            if(kill instanceof Player) {
               if(!DataType.PVP_DEATH.isLogged() && !Config.LogDeathDrops) {
                  return;
               }

               DataManager.addEntry(new DataEntry(killer, DataType.PVP_DEATH, killer.getLocation(), Util.getEntityName(kill)));
            } else {
               if(!DataType.MOB_DEATH.isLogged() && !Config.LogDeathDrops) {
                  return;
               }

               DataManager.addEntry(new DataEntry(killer, DataType.MOB_DEATH, killer.getLocation(), Util.getEntityName(kill)));
            }
         } else {
            if(!DataType.OTHER_DEATH.isLogged() && !Config.LogDeathDrops) {
               return;
            }

            EntityDamageEvent var8 = killer.getLastDamageCause();
            String i$ = var8 == null?"Unknown":killer.getLastDamageCause().getCause().name();
            String[] stack = i$.split("_");

            for(int i = 0; i < stack.length; ++i) {
               stack[i] = stack[i].substring(0, 1).toUpperCase() + stack[i].substring(1).toLowerCase();
            }

            i$ = Util.join(Arrays.asList(stack), " ");
            DataManager.addEntry(new DataEntry(killer, DataType.OTHER_DEATH, killer.getLocation(), i$));
         }

         if(Config.LogDeathDrops) {
            kill = null;

            String var9;
            for(Iterator var10 = event.getDrops().iterator(); var10.hasNext(); DataManager.addEntry(new DataEntry(killer, DataType.ITEM_DROP, killer.getLocation(), var9))) {
               ItemStack var12 = (ItemStack)var10.next();
               if(var12.getData() != null) {
                  var9 = var12.getAmount() + "x " + var12.getTypeId() + ":" + var12.getData().getData();
               } else {
                  var9 = var12.getAmount() + "x " + var12.getTypeId();
               }
            }
         }
      } else {
         if(DataType.ENTITY_KILL.isLogged()) {
            return;
         }

         killer = ((LivingEntity)entity).getKiller();
         if(killer != null && killer instanceof Player) {
            Player var11 = (Player)killer;
            DataManager.addEntry(new EntityEntry(var11.getName(), DataType.ENTITY_KILL, entity.getLocation().getBlock().getLocation(), Util.getEntityName(entity)));
         }
      }

   }

   @HawkEvent(
      dataType = {DataType.EXPLOSION}
   )
   public void onEntityExplode(EntityExplodeEvent event) {
      Entity e = event.getEntity();
      String s = "Environment";
      if(e != null) {
         if(e instanceof TNTPrimed) {
            Entity arr$ = ((TNTPrimed)e).getSource();
            if(arr$ != null && arr$ instanceof Player) {
               s = ((Player)arr$).getName();
            } else {
               s = EntityUtil.entityToString(e);
            }
         } else if(e.getType() != null) {
            s = EntityUtil.entityToString(e);
         }
      }

      Block[] var8 = (Block[])event.blockList().toArray(new Block[0]);
      int len$ = var8.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         Block b = var8[i$];
         DataManager.addEntry(new BlockEntry(s, DataType.EXPLOSION, b));
      }

   }

   @HawkEvent(
      dataType = {DataType.ITEM_BREAK}
   )
   public void onPaintingBreak(HangingBreakEvent event) {
      if(!event.getCause().equals(RemoveCause.ENTITY)) {
         Hanging e = event.getEntity();
         boolean face = false;
         boolean type = false;
         boolean extra = false;
         int face1;
         short type1;
         int extra1;
         if(e instanceof ItemFrame) {
            ItemFrame paint = (ItemFrame)e;
            type1 = 389;
            face1 = EntityUtil.getFace(paint.getAttachedFace());
            extra1 = paint.getItem().getTypeId();
         } else {
            if(!(e instanceof Painting)) {
               return;
            }

            Painting paint1 = (Painting)e;
            type1 = 321;
            face1 = EntityUtil.getFace(paint1.getAttachedFace());
            extra1 = paint1.getArt().getId();
         }

         DataManager.addEntry(new HangingEntry(event.getCause().name(), DataType.ITEM_BREAK, e.getLocation().getBlock().getLocation(), type1, face1, extra1));
      }
   }

   @HawkEvent(
      dataType = {DataType.ITEM_BREAK}
   )
   public void onPaintingBreak(HangingBreakByEntityEvent event) {
      Hanging e = event.getEntity();
      boolean face = false;
      boolean type = false;
      boolean extra = false;
      if(event.getRemover() instanceof Player) {
         int face1;
         short type1;
         int extra1;
         if(e instanceof ItemFrame) {
            ItemFrame paint = (ItemFrame)e;
            type1 = 389;
            face1 = EntityUtil.getFace(paint.getAttachedFace());
            extra1 = paint.getItem().getTypeId();
         } else {
            if(!(e instanceof Painting)) {
               return;
            }

            Painting paint1 = (Painting)e;
            type1 = 321;
            face1 = EntityUtil.getFace(paint1.getAttachedFace());
            extra1 = paint1.getArt().getId();
         }

         DataManager.addEntry(new HangingEntry((Player)event.getRemover(), DataType.ITEM_BREAK, e.getLocation().getBlock().getLocation(), type1, face1, extra1));
      }
   }

   @HawkEvent(
      dataType = {DataType.ENTITY_MODIFY}
   )
   public void onEntityModifyBlock(EntityChangeBlockEvent event) {
      Entity en = event.getEntity();
      if(!(en instanceof Silverfish)) {
         DataManager.addEntry(new BlockEntry(EntityUtil.entityToString(en), DataType.ENTITY_MODIFY, event.getBlock()));
      }
   }

   @HawkEvent(
      dataType = {DataType.BLOCK_INHABIT}
   )
   public void onEntityBlockChange(EntityChangeBlockEvent event) {
      Entity en = event.getEntity();
      if(en instanceof Silverfish) {
         DataManager.addEntry(new BlockEntry("SilverFish", DataType.BLOCK_INHABIT, event.getBlock()));
      }
   }

   @HawkEvent(
      dataType = {DataType.ITEM_PLACE}
   )
   public void onHangingPlace(HangingPlaceEvent event) {
      Hanging e = event.getEntity();
      boolean face = false;
      boolean type = false;
      boolean extra = false;
      int face1;
      short type1;
      int extra1;
      if(e instanceof ItemFrame) {
         ItemFrame paint = (ItemFrame)e;
         type1 = 389;
         face1 = EntityUtil.getFace(paint.getAttachedFace());
         extra1 = paint.getItem().getTypeId();
      } else {
         if(!(e instanceof Painting)) {
            return;
         }

         Painting paint1 = (Painting)e;
         type1 = 321;
         face1 = EntityUtil.getFace(paint1.getAttachedFace());
         extra1 = paint1.getArt().getId();
      }

      DataManager.addEntry(new HangingEntry(event.getPlayer(), DataType.ITEM_PLACE, e.getLocation().getBlock().getLocation(), type1, face1, extra1));
   }

   @HawkEvent(
      dataType = {DataType.ENDERMAN_PICKUP, DataType.ENDERMAN_PLACE}
   )
   public void onEntityChangeBlock(EntityChangeBlockEvent event) {
      if(event.getEntity() instanceof Enderman) {
         Block block = event.getBlock();
         if(event.getTo() == Material.AIR && DataType.ENDERMAN_PICKUP.isLogged()) {
            if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
               DataManager.addEntry(new SignEntry("Environment", DataType.SIGN_BREAK, event.getBlock()));
            }

            DataManager.addEntry(new BlockEntry("Environment", DataType.ENDERMAN_PICKUP, block));
         } else if(DataType.ENDERMAN_PLACE.isLogged()) {
            Enderman enderman = (Enderman)event.getEntity();
            BlockState newState = block.getState();
            if(enderman.getCarriedMaterial() != null) {
               try {
                  newState.setData(enderman.getCarriedMaterial());
               } catch (Exception var6) {
                  ;
               }

               newState.setType(enderman.getCarriedMaterial().getItemType());
            }

            DataManager.addEntry(new BlockChangeEntry("Environment", DataType.ENDERMAN_PLACE, block.getLocation(), block.getState(), newState));
         }

      }
   }
}

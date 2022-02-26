package uk.co.oliwali.HawkEye;

import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.entry.EntityEntry;
import uk.co.oliwali.HawkEye.entry.HangingEntry;
import uk.co.oliwali.HawkEye.entry.SignEntry;
import uk.co.oliwali.HawkEye.entry.SimpleRollbackEntry;

public enum DataType {

   BLOCK_BREAK("BLOCK_BREAK", 0, 0, BlockEntry.class, "block-break", true, true),
   BLOCK_PLACE("BLOCK_PLACE", 1, 1, BlockChangeEntry.class, "block-place", true, true),
   SIGN_PLACE("SIGN_PLACE", 2, 2, SignEntry.class, "sign-place", true, true),
   CHAT("CHAT", 3, 3, DataEntry.class, "chat", false, false),
   COMMAND("COMMAND", 4, 4, DataEntry.class, "command", false, false),
   JOIN("JOIN", 5, 5, DataEntry.class, "join", false, false),
   QUIT("QUIT", 6, 6, DataEntry.class, "quit", false, false),
   TELEPORT("TELEPORT", 7, 7, DataEntry.class, "teleport", false, false),
   LAVA_BUCKET("LAVA_BUCKET", 8, 8, BlockChangeEntry.class, "lava-bucket", true, true),
   WATER_BUCKET("WATER_BUCKET", 9, 9, BlockChangeEntry.class, "water-bucket", true, true),
   OPEN_CONTAINER("OPEN_CONTAINER", 10, 10, DataEntry.class, "open-container", true, false),
   DOOR_INTERACT("DOOR_INTERACT", 11, 11, DataEntry.class, "door-interact", true, false),
   PVP_DEATH("PVP_DEATH", 12, 12, DataEntry.class, "pvp-death", false, false),
   FLINT_AND_STEEL("FLINT_AND_STEEL", 13, 13, BlockChangeEntry.class, "flint-steel", true, true),
   LEVER("LEVER", 14, 14, DataEntry.class, "lever", true, false),
   STONE_BUTTON("STONE_BUTTON", 15, 15, DataEntry.class, "button", true, false),
   OTHER("OTHER", 16, 16, DataEntry.class, "other", true, false),
   EXPLOSION("EXPLOSION", 17, 17, BlockEntry.class, "explosion", true, true),
   BLOCK_BURN("BLOCK_BURN", 18, 18, BlockEntry.class, "block-burn", true, true),
   BLOCK_FORM("BLOCK_FORM", 19, 19, BlockChangeEntry.class, "block-form", true, true),
   LEAF_DECAY("LEAF_DECAY", 20, 20, BlockEntry.class, "leaf-decay", true, true),
   MOB_DEATH("MOB_DEATH", 21, 21, DataEntry.class, "mob-death", false, false),
   OTHER_DEATH("OTHER_DEATH", 22, 22, DataEntry.class, "other-death", false, false),
   ITEM_DROP("ITEM_DROP", 23, 23, DataEntry.class, "item-drop", false, false),
   ITEM_PICKUP("ITEM_PICKUP", 24, 24, DataEntry.class, "item-pickup", false, false),
   BLOCK_FADE("BLOCK_FADE", 25, 25, BlockChangeEntry.class, "block-fade", true, true),
   LAVA_FLOW("LAVA_FLOW", 26, 26, BlockChangeEntry.class, "lava-flow", true, true),
   WATER_FLOW("WATER_FLOW", 27, 27, BlockChangeEntry.class, "water-flow", true, true),
   CONTAINER_TRANSACTION("CONTAINER_TRANSACTION", 28, 28, ContainerEntry.class, "container-transaction", true, true),
   SIGN_BREAK("SIGN_BREAK", 29, 29, SignEntry.class, "sign-break", true, true),
   ITEM_BREAK("ITEM_BREAK", 30, 30, HangingEntry.class, "item-break", true, true),
   ITEM_PLACE("ITEM_PLACE", 31, 31, HangingEntry.class, "item-place", true, true),
   ENDERMAN_PICKUP("ENDERMAN_PICKUP", 32, 32, BlockEntry.class, "enderman-pickup", true, true),
   ENDERMAN_PLACE("ENDERMAN_PLACE", 33, 33, BlockChangeEntry.class, "enderman-place", true, true),
   TREE_GROW("TREE_GROW", 34, 34, BlockChangeEntry.class, "tree-grow", true, true),
   MUSHROOM_GROW("MUSHROOM_GROW", 35, 35, BlockChangeEntry.class, "mushroom-grow", true, true),
   ENTITY_KILL("ENTITY_KILL", 36, 36, EntityEntry.class, "entity-kill", true, true),
   SPAWNMOB_EGG("SPAWNMOB_EGG", 37, 37, DataEntry.class, "spawnmob-egg", false, false),
   HEROCHAT("HEROCHAT", 38, 38, DataEntry.class, "herochat", false, false),
   ENTITY_MODIFY("ENTITY_MODIFY", 39, 39, BlockEntry.class, "entity-modify", true, true),
   BLOCK_INHABIT("BLOCK_INHABIT", 40, 40, BlockEntry.class, "block-inhabit", true, true),
   SUPER_PICKAXE("SUPER_PICKAXE", 41, 41, BlockEntry.class, "super-pickaxe", true, true),
   WORLDEDIT_BREAK("WORLDEDIT_BREAK", 42, 42, BlockEntry.class, "worldedit-break", true, true),
   WORLDEDIT_PLACE("WORLDEDIT_PLACE", 43, 43, BlockChangeEntry.class, "worldedit-place", true, true),
   CROP_TRAMPLE("CROP_TRAMPLE", 44, 44, BlockEntry.class, "crop-trample", true, true),
   BLOCK_IGNITE("BLOCK_IGNITE", 45, 45, SimpleRollbackEntry.class, "block-ignite", true, true),
   FALLING_BLOCK("FALLING_BLOCK", 46, 46, BlockChangeEntry.class, "fallingblock-place", true, true),
   PLAYER_LAVA_FLOW("PLAYER_LAVA_FLOW", 47, 47, BlockChangeEntry.class, "player-lava-flow", true, true),
   PLAYER_WATER_FLOW("PLAYER_WATER_FLOW", 48, 48, BlockChangeEntry.class, "player-water-flow", true, true);
   private int id;
   private boolean canHere;
   private String configName;
   private boolean canRollback;
   private boolean isLogged;
   private Class entryClass;
   private Constructor entryConstructor;
   private static final Map nameMapping = new HashMap();
   private static final Map idMapping = new HashMap();
   // $FF: synthetic field
   private static final DataType[] $VALUES = new DataType[]{BLOCK_BREAK, BLOCK_PLACE, SIGN_PLACE, CHAT, COMMAND, JOIN, QUIT, TELEPORT, LAVA_BUCKET, WATER_BUCKET, OPEN_CONTAINER, DOOR_INTERACT, PVP_DEATH, FLINT_AND_STEEL, LEVER, STONE_BUTTON, OTHER, EXPLOSION, BLOCK_BURN, BLOCK_FORM, LEAF_DECAY, MOB_DEATH, OTHER_DEATH, ITEM_DROP, ITEM_PICKUP, BLOCK_FADE, LAVA_FLOW, WATER_FLOW, CONTAINER_TRANSACTION, SIGN_BREAK, ITEM_BREAK, ITEM_PLACE, ENDERMAN_PICKUP, ENDERMAN_PLACE, TREE_GROW, MUSHROOM_GROW, ENTITY_KILL, SPAWNMOB_EGG, HEROCHAT, ENTITY_MODIFY, BLOCK_INHABIT, SUPER_PICKAXE, WORLDEDIT_BREAK, WORLDEDIT_PLACE, CROP_TRAMPLE, BLOCK_IGNITE, FALLING_BLOCK, PLAYER_LAVA_FLOW, PLAYER_WATER_FLOW};


   private DataType(String var1, int var2, int id, Class entryClass, String configName, boolean canHere, boolean canRollback) {
      this.id = id;
      this.entryClass = entryClass;
      this.canHere = canHere;
      this.configName = configName;
      this.canRollback = canRollback;
      this.isLogged = HawkEye.instance.getConfig().getBoolean("log." + configName);

      try {
         this.entryConstructor = entryClass.getConstructor(new Class[]{Integer.TYPE, Timestamp.class, Integer.TYPE, Integer.TYPE, String.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE});
      } catch (Exception var9) {
         ;
      }

   }

   public int getId() {
      return this.id;
   }

   public String getConfigName() {
      return this.configName;
   }

   public Class getEntryClass() {
      return this.entryClass;
   }

   public static DataType fromName(String name) {
      return (DataType)nameMapping.get(name);
   }

   public static DataType fromId(int id) {
      return (DataType)idMapping.get(Integer.valueOf(id));
   }

   public boolean canRollback() {
      return this.canRollback;
   }

   public boolean canHere() {
      return this.canHere;
   }

   public Constructor getEntryConstructor() {
      return this.entryConstructor;
   }

   public boolean isLogged() {
      return this.isLogged;
   }

   public void reload() {
      this.isLogged = HawkEye.instance.getConfig().getBoolean("log." + this.configName);
   }

   static {
      Iterator i$ = EnumSet.allOf(DataType.class).iterator();

      DataType type;
      while(i$.hasNext()) {
         type = (DataType)i$.next();
         nameMapping.put(type.configName, type);
      }

      i$ = EnumSet.allOf(DataType.class).iterator();

      while(i$.hasNext()) {
         type = (DataType)i$.next();
         idMapping.put(Integer.valueOf(type.id), type);
      }

   }
}

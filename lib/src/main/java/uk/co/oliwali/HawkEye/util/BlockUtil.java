package uk.co.oliwali.HawkEye.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;

public class BlockUtil {

   public static final BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};


   public static String getBlockString(Block block) {
      return getBlockString(block.getState());
   }

   public static String getBlockString(BlockState block) {
      return block.getRawData() != 0?block.getTypeId() + ":" + block.getRawData():Integer.toString(block.getTypeId());
   }

   public static String getItemString(ItemStack stack) {
      short data = stack.getData().getData();
      int type = stack.getTypeId();
      if(type == 373) {
         data = stack.getDurability();
      }

      return stack.getData() != null && data != 0?type + ":" + data:Integer.toString(type);
   }

   public static ItemStack itemStringToStack(String item, Integer amount) {
      String[] itemArr = item.split(":");
      return itemArr.length > 1?new ItemStack(Integer.parseInt(itemArr[0]), amount.intValue(), itemArr[1].length() <= 3?(short)Byte.parseByte(itemArr[1]):(short)Integer.parseInt(itemArr[1])):new ItemStack(Integer.parseInt(itemArr[0]), amount.intValue());
   }

   public static String getBlockStringName(String blockData) {
      String[] blockArr = blockData.split(":");
      return !Util.isInteger(blockArr[0])?blockData:(blockArr.length > 1?Material.getMaterial(Integer.parseInt(blockArr[0])).name() + ":" + blockArr[1]:Material.getMaterial(Integer.parseInt(blockArr[0])).name());
   }

   public static void setBlockString(Block block, String blockData) {
      String[] blockArr = blockData.split(":");
      if(Util.isInteger(blockArr[0])) {
         int type = Integer.parseInt(blockArr[0]);
         int data = blockArr.length > 1?Integer.parseInt(blockArr[1]):0;
         HawkBlockType.getHawkBlock(type).Restore(block, type, data);
      }
   }

   public static int getIdFromString(String string) {
      return !Util.isInteger(string.split(":")[0])?0:Integer.parseInt(string.split(":")[0]);
   }

   public static byte getDataFromString(String string) {
      return string.split(":").length == 1?0:(byte)Integer.parseInt(string.split(":")[1]);
   }

   public static boolean isAttached(Block base, Block attached) {
      MaterialData bs = attached.getState().getData();
      if(bs instanceof Attachable && attached.getType() != Material.VINE) {
         Attachable at = (Attachable)bs;
         return attached.getRelative(at.getAttachedFace()).equals(base);
      } else {
         return true;
      }
   }

}

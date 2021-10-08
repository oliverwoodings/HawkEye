package uk.co.oliwali.HawkEye;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import uk.co.oliwali.HawkEye.callbacks.SearchCallback;
import uk.co.oliwali.HawkEye.database.SearchQuery;
import uk.co.oliwali.HawkEye.util.BlockUtil;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class ToolManager {

   public static void enableTool(PlayerSession session, Player player) {
      PlayerInventory inv = player.getInventory();
      session.setUsingTool(true);
      ItemStack stack = Config.ToolBlock;
      int first;
      if(!inv.contains(stack) && Config.GiveTool) {
         first = inv.firstEmpty();
         if(first == -1) {
            player.getWorld().dropItem(player.getLocation(), stack);
         } else {
            inv.setItem(first, stack);
         }
      }

      first = inv.first(stack);
      if(!player.getItemInHand().equals(stack) && first != -1) {
         ItemStack back = player.getItemInHand().clone();
         player.setItemInHand(inv.getItem(first));
         if(back.getAmount() == 0) {
            inv.clear(first);
         } else {
            inv.setItem(first, back);
         }
      }

      Util.sendMessage(player, "&cHawkEye tool enabled! &7Left click a block or place the tool to get information");
   }

   public static void disableTool(PlayerSession session, Player player) {
      session.setUsingTool(false);
      player.getInventory().remove(Config.ToolBlock);
      Util.sendMessage(player, "&cHawkEye tool disabled");
   }

   public static void toolSearch(Player player, Block b) {
      Location loc = b.getLocation();
      PlayerSession session = SessionManager.getSession(player);
      SearchParser parser;
      int len$;
      if(session.getToolCommand().length != 0 && !session.getToolCommand()[0].equals("")) {
         parser = new SearchParser(player, Arrays.asList(session.getToolCommand()));
      } else {
         parser = new SearchParser(player);
         DataType[] vec = DataType.values();
         int arr$ = vec.length;

         for(len$ = 0; len$ < arr$; ++len$) {
            DataType i$ = vec[len$];
            if(i$.canHere()) {
               parser.actions.add(i$);
            }
         }
      }

      Vector var12 = Util.getSimpleLocation(loc).toVector();
      parser.minLoc = null;
      parser.maxLoc = null;
      parser.loc = var12;
      if(b.getType() == Material.CHEST) {
         BlockFace[] var13 = BlockUtil.faces;
         len$ = var13.length;

         for(int var14 = 0; var14 < len$; ++var14) {
            BlockFace face = var13[var14];
            Block b2 = b.getRelative(face);
            if(b2.getType() == Material.CHEST) {
               Location loc2 = b2.getLocation();
               parser.minLoc = new Vector(Math.min(loc.getX(), loc2.getX()), Math.min(loc.getY(), loc2.getY()), Math.min(loc.getZ(), loc2.getZ()));
               parser.maxLoc = new Vector(Math.max(loc.getX(), loc2.getX()), Math.max(loc.getY(), loc2.getY()), Math.max(loc.getZ(), loc2.getZ()));
               parser.loc = null;
               break;
            }
         }
      }

      parser.worlds = new String[]{loc.getWorld().getName()};
      new SearchQuery(new SearchCallback(SessionManager.getSession(player)), parser, SearchQuery.SearchDir.DESC);
   }

   public static void bindTool(Player player, PlayerSession session, List args) {
      try {
         new SearchParser(player, args);
      } catch (IllegalArgumentException var4) {
         Util.sendMessage(player, "&c" + var4.getMessage());
         return;
      }

      Util.sendMessage(player, "&cParameters bound to tool: &7" + Util.join(args, " "));
      session.setToolCommand((String[])args.toArray(new String[0]));
      if(!session.isUsingTool()) {
         enableTool(session, player);
      }

   }

   public static void resetTool(PlayerSession session) {
      session.setToolCommand(Config.DefaultToolCommand);
   }
}

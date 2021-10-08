package uk.co.oliwali.HawkEye.entry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import uk.co.oliwali.HawkEye.Base64;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.util.Util;

public class SignEntry extends DataEntry {

   private BlockFace facing;
   private boolean wallSign;
   private String[] lines;


   public SignEntry(int playerId, Timestamp timestamp, int dataId, int typeId, String data, String plugin, int worldId, int x, int y, int z) {
      super(playerId, timestamp, dataId, typeId, plugin, worldId, x, y, z);
      this.facing = BlockFace.NORTH;
      this.wallSign = true;
      this.lines = new String[4];
      this.interpretSqlData(data);
   }

   public SignEntry() {
      this.facing = BlockFace.NORTH;
      this.wallSign = true;
      this.lines = new String[4];
   }

   public SignEntry(Player player, DataType type, Block block) {
      this.facing = BlockFace.NORTH;
      this.wallSign = true;
      this.lines = new String[4];
      this.interpretSignBlock(block.getState());
      this.setInfo(player, type, block.getLocation());
   }

   public SignEntry(String player, DataType type, Block block) {
      this.facing = BlockFace.NORTH;
      this.wallSign = true;
      this.lines = new String[4];
      this.interpretSignBlock(block.getState());
      this.setInfo(player, type, block.getLocation());
   }

   public SignEntry(String player, DataType type, BlockState state) {
      this.facing = BlockFace.NORTH;
      this.wallSign = true;
      this.lines = new String[4];
      this.interpretSignBlock(state);
      this.setInfo(player, type, state.getLocation());
   }

   public SignEntry(String player, DataType type, Block block, String[] lines) {
      this.facing = BlockFace.NORTH;
      this.wallSign = true;
      this.lines = new String[4];
      this.interpretSignBlock(block.getState());
      this.lines = lines;
      this.setInfo(player, type, block.getLocation());
   }

   private void interpretSignBlock(BlockState state) {
      if(state instanceof Sign) {
         Sign sign = (Sign)state;
         org.bukkit.material.Sign signData = (org.bukkit.material.Sign)sign.getData();
         if(signData.isWallSign()) {
            this.facing = signData.getAttachedFace();
         } else {
            this.facing = signData.getFacing();
         }

         this.wallSign = signData.isWallSign();
         this.lines = sign.getLines();
      }
   }

   public String getStringData() {
      return this.data == null?Util.join(Arrays.asList(this.lines), " | "):this.data;
   }

   public String getSqlData() {
      if(this.data != null) {
         return this.data;
      } else {
         ArrayList encoded = new ArrayList();

         for(int i = 0; i < 4; ++i) {
            encoded.add(this.lines[i] == null?"":Base64.encode(this.lines[i].getBytes()));
         }

         return this.wallSign + "@" + this.facing + "@" + Util.join(encoded, ",");
      }
   }

   public boolean rollback(Block block) {
      if(this.type == DataType.SIGN_PLACE) {
         block.setTypeId(0);
      } else {
         if(this.wallSign) {
            block.setType(Material.WALL_SIGN);
         } else {
            block.setType(Material.SIGN_POST);
         }

         Sign sign = (Sign)((Sign)block.getState());

         for(int i = 0; i < this.lines.length; ++i) {
            if(this.lines[i] != null) {
               sign.setLine(i, this.lines[i]);
            }
         }

         if(this.wallSign) {
            ((org.bukkit.material.Sign)sign.getData()).setFacingDirection(this.facing.getOppositeFace());
         } else {
            ((org.bukkit.material.Sign)sign.getData()).setFacingDirection(this.facing);
         }

         sign.update();
      }

      return true;
   }

   public boolean rollbackPlayer(Block block, Player player) {
      if(this.type == DataType.SIGN_PLACE) {
         player.sendBlockChange(block.getLocation(), 0, (byte)0);
      } else {
         player.sendBlockChange(block.getLocation(), this.wallSign?Material.WALL_SIGN:Material.SIGN_POST, (byte)0);
      }

      return true;
   }

   public boolean rebuild(Block block) {
      if(this.type == DataType.SIGN_BREAK) {
         block.setTypeId(0);
      } else {
         if(this.wallSign) {
            block.setType(Material.WALL_SIGN);
         } else {
            block.setType(Material.SIGN_POST);
         }

         Sign sign = (Sign)((Sign)block.getState());

         for(int i = 0; i < this.lines.length; ++i) {
            if(this.lines[i] != null) {
               sign.setLine(i, this.lines[i]);
            }
         }

         if(this.wallSign) {
            ((org.bukkit.material.Sign)sign.getData()).setFacingDirection(this.facing.getOppositeFace());
         } else {
            ((org.bukkit.material.Sign)sign.getData()).setFacingDirection(this.facing);
         }

         sign.update();
      }

      return true;
   }

   public void interpretSqlData(String data) {
      if(data.indexOf("@") != -1) {
         String[] arr = data.split("@");
         if(!arr[0].equals("true")) {
            this.wallSign = false;
         }

         BlockFace[] encLines = BlockFace.values();
         int i = encLines.length;

         for(int i$ = 0; i$ < i; ++i$) {
            BlockFace face = encLines[i$];
            if(face.toString().equalsIgnoreCase(arr[1])) {
               this.facing = face;
            }
         }

         if(arr.length == 3) {
            String[] var7 = arr[2].split(",");

            for(i = 0; i < var7.length; ++i) {
               if(var7[i] != null && !var7[i].equals("")) {
                  this.lines[i] = new String(Base64.decode(var7[i]));
               }
            }

         }
      }
   }
}

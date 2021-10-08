package uk.co.oliwali.HawkEye.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.blocks.HawkBlockType;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.BlockChangeEntry;
import uk.co.oliwali.HawkEye.entry.BlockEntry;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;
import uk.co.oliwali.HawkEye.util.Util;

public class MonitorPlayerListener extends HawkEyeListener {

    public MonitorPlayerListener(HawkEye HawkEye) {
        super(HawkEye);
    }

    @HawkEvent(
            dataType = {DataType.CHAT}
    )
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DataManager.addEntry(new DataEntry(player, DataType.CHAT, player.getLocation(), event.getMessage()));
    }

    @HawkEvent(
            dataType = {DataType.COMMAND}
    )
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!Config.CommandFilter.contains(event.getMessage().split(" ")[0])) {
            DataManager.addEntry(new DataEntry(player, DataType.COMMAND, player.getLocation(), event.getMessage()));
        }
    }

    @HawkEvent(
            dataType = {DataType.JOIN}
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        DataManager.addEntry(new DataEntry(player, DataType.JOIN, loc, Config.LogIpAddresses ? player.getAddress().getAddress().getHostAddress().toString() : ""));
    }

    @HawkEvent(
            dataType = {DataType.QUIT}
    )
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        String ip = "";

        try {
            ip = player.getAddress().getAddress().getHostAddress().toString();
        } catch (Exception var6) {
            ;
        }

        DataManager.addEntry(new DataEntry(player, DataType.QUIT, loc, Config.LogIpAddresses ? ip : ""));
    }

    @HawkEvent(
            dataType = {DataType.TELEPORT}
    )
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (Util.distance(from, to) > 5.0D) {
            DataManager.addEntry(new DataEntry(event.getPlayer(), DataType.TELEPORT, from, to.getWorld().getName() + ": " + to.getX() + ", " + to.getY() + ", " + to.getZ()));
        }

    }

    @HawkEvent(
            dataType = {DataType.OPEN_CONTAINER, DataType.DOOR_INTERACT, DataType.LEVER, DataType.STONE_BUTTON, DataType.SPAWNMOB_EGG, DataType.CROP_TRAMPLE}
    )
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null) {
            Location loc = block.getLocation();
            switch (block.getType()) {
                case SOIL:
                    if (event.getAction() == Action.PHYSICAL) {
                        Block locs = block.getRelative(BlockFace.UP);
                        if (HawkBlockType.getHawkBlock(locs.getTypeId()).equals(HawkBlockType.plant)) {
                            DataManager.addEntry(new BlockEntry(player, DataType.CROP_TRAMPLE, locs));
                        }
                    }
                    break;
                case FURNACE:
                case DISPENSER:
                case CHEST:
                case ANVIL:
                case BEACON:
                case BREWING_STAND:
                case ENDER_CHEST:
                case HOPPER:
                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        DataManager.addEntry(new DataEntry(player, DataType.OPEN_CONTAINER, loc, Integer.toString(block.getTypeId())));
                    }
                    break;
                case WOODEN_DOOR:
                case TRAP_DOOR:
                case FENCE_GATE:
                    DataManager.addEntry(new DataEntry(player, DataType.DOOR_INTERACT, loc, ""));
                    break;
                case LEVER:
                    DataManager.addEntry(new DataEntry(player, DataType.LEVER, loc, ""));
                    break;
                case STONE_BUTTON:
                    DataManager.addEntry(new DataEntry(player, DataType.STONE_BUTTON, loc, ""));
                    break;
                default:
                    return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location locs1 = block.getLocation();
                if (player.getItemInHand().getType().equals(Material.MONSTER_EGG)) {
                    DataManager.addEntry(new DataEntry(player, DataType.SPAWNMOB_EGG, locs1, ""));
                }
            }
        }

    }

    @HawkEvent(
            dataType = {DataType.ITEM_DROP}
    )
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItemDrop().getItemStack();
        String data = null;
        if (stack.getDurability() != 0) {
            data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
        } else {
            data = stack.getAmount() + "x " + stack.getTypeId();
        }

        DataManager.addEntry(new DataEntry(player, DataType.ITEM_DROP, player.getLocation().getBlock().getLocation(), data));
    }

    @HawkEvent(
            dataType = {DataType.ITEM_PICKUP}
    )
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem().getItemStack();
        String data = null;
        if (stack.getDurability() != 0) {
            data = stack.getAmount() + "x " + stack.getTypeId() + ":" + stack.getData().getData();
        } else {
            data = stack.getAmount() + "x " + stack.getTypeId();
        }

        DataManager.addEntry(new DataEntry(player, DataType.ITEM_PICKUP, player.getLocation().getBlock().getLocation(), data));
    }

    @HawkEvent(
            dataType = {DataType.LAVA_BUCKET, DataType.WATER_BUCKET}
    )
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
        DataType type = event.getBucket().equals(Material.WATER_BUCKET) ? DataType.WATER_BUCKET : DataType.LAVA_BUCKET;
        DataManager.addEntry(new BlockChangeEntry(event.getPlayer(), type, loc, loc.getBlock().getState(), event.getBucket().getId()));
    }

    @HawkEvent(
            dataType = {DataType.CONTAINER_TRANSACTION}
    )
    public void onInventoryClose(InventoryCloseEvent event) {
        String player = event.getPlayer().getName();
        InventoryHolder holder = event.getInventory().getHolder();
        if (InventoryUtil.isHolderValid(holder) && HawkEye.InvSession.containsKey(player)) {
            String data = InventoryUtil.compareInvs((HashMap) HawkEye.InvSession.get(player), InventoryUtil.compressInventory(holder.getInventory().getContents()));
            if (data == null) {
                return;
            }

            DataManager.addEntry(new ContainerEntry(event.getPlayer().getName(), InventoryUtil.getHolderLoc(holder), data));
            HawkEye.InvSession.remove(player);
        }

    }

    @HawkEvent(
            dataType = {DataType.CONTAINER_TRANSACTION}
    )
    public void onInventoryClose(InventoryOpenEvent event) {
        String player = event.getPlayer().getName();
        InventoryHolder holder = event.getInventory().getHolder();
        if (InventoryUtil.isHolderValid(holder)) {
            if (HawkEye.InvSession.containsKey(player)) {
                HawkEye.InvSession.remove(player);
            }

            HawkEye.InvSession.put(player, InventoryUtil.compressInventory(holder.getInventory().getContents()));
        }

    }

}

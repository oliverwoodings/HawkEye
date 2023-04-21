package uk.co.oliwali.HawkEye.listeners;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftMinecart;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
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
import uk.co.oliwali.HawkEye.entry.MinecartEntry;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.InventoryUtil;
import uk.co.oliwali.HawkEye.util.Util;

public class MonitorPlayerListener extends HawkEyeListener {
	public HawkEye plugin;

    public MonitorPlayerListener(HawkEye HawkEye) {
        super(HawkEye);
        this.plugin = HawkEye;
    }
    /*
    @HawkEvent(
            dataType = {DataType.CHAT}
    )
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        DataManager.addEntry(new DataEntry(player, DataType.CHAT, player.getLocation(), event.getMessage()));
    }
*/
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

    @SuppressWarnings("deprecation")
	@HawkEvent(
            dataType = {DataType.OPEN_CONTAINER, DataType.DOOR_INTERACT, DataType.LEVER, DataType.STONE_BUTTON, DataType.SPAWNMOB_EGG, DataType.CROP_TRAMPLE}
    )
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block != null) {
            Location loc = block.getLocation();
            boolean rail = false;
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
                case RAILS:
                	if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                		rail = true;
                	}
                	break;
                default:
                	int id = block.getType().getId();
                	if(id == 2125 | id == 2080) {
                		rail = true;
                	}
                	else {
                		return;
                	}
                	break;
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location locs1 = block.getLocation();
                if (player.getItemInHand().getType().equals(Material.MONSTER_EGG)) {
                    DataManager.addEntry(new DataEntry(player, DataType.SPAWNMOB_EGG, locs1, ""));
                }
                
                if(rail) {
                	String minecartType = "";
            		switch(player.getItemInHand().getType()) {
        				case MINECART:
        					minecartType = "MINECART";
            				break;
        				case POWERED_MINECART:
        					minecartType = "MINECART_FURNACE";
            				break;
        				case EXPLOSIVE_MINECART:
        					minecartType = "MINECART_TNT";
            				break;
        				case HOPPER_MINECART:
        					minecartType = "MINECART_HOPPER";
            				break;
        				case STORAGE_MINECART:
        					minecartType = "MINECART_CHEST";
            				break;
            			default:
            				break;
            		}
            		
            		if(minecartType.length() > 0) {
            			this.plugin.minecartLocation.put(minecartType + ":" + player.getName(), loc);
            		}
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
            dataType = {DataType.CONTAINER_TRANSACTION, DataType.MINECART_TRANSACTION}
    )
    public void onInventoryClose(InventoryCloseEvent event) {
        String player = event.getPlayer().getName();

        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if ((InventoryUtil.isPlayerInventoryValid(inventory, player) || InventoryUtil.isHolderValid(holder)) && HawkEye.InvSession.containsKey(player)) {
            ItemStack[] items = InventoryUtil.isPlayerInventoryValid(inventory, player) ? InventoryUtil.getInventory(inventory) : InventoryUtil.getHolderInventory(holder);

            String data = InventoryUtil.compareInvs((HashMap) HawkEye.InvSession.get(player), InventoryUtil.compressInventory(items));
            if (data == null) {
                return;
            }

            if(InventoryUtil.isPlayerInventoryValid(inventory, player)) {
                Bukkit.getLogger().log(Level.INFO, InventoryUtil.getPlayerInventoryType(inventory, player) + " Transaction - " +  player + " - " + data);
            } else {
                switch(event.getInventory().getHolder().getClass().getSimpleName()) {
	            	case "CraftMinecartChest":
	            	case "CraftMinecartHopper":
	            		CraftMinecart entity = (CraftMinecart)event.getInventory().getHolder();
	            		String[] uuidAndType = new String[] {
	            				entity.getUniqueId().toString(),
	            				entity.getType().toString()
	            		};
	            		DataManager.addEntry(new MinecartEntry(event.getPlayer().getName(), DataType.MINECART_TRANSACTION, InventoryUtil.getHolderLoc(holder), uuidAndType, data));
	            		break;
	            	default:
	            		DataManager.addEntry(new ContainerEntry(event.getPlayer().getName(), InventoryUtil.getHolderLoc(holder), data));
	            		break;
            	}
            }

            HawkEye.InvSession.remove(player);
        }

    }

    @HawkEvent(
            dataType = {DataType.CONTAINER_TRANSACTION, DataType.MINECART_TRANSACTION}
    )
    public void onItemMove(InventoryMoveItemEvent event) {
        if(!event.getSource().getViewers().isEmpty())
            event.setCancelled(true);
    }

    @HawkEvent(
            dataType = {DataType.CONTAINER_TRANSACTION, DataType.MINECART_TRANSACTION}
    )
    public void onInventoryClose(InventoryOpenEvent event) {
        String player = event.getPlayer().getName();
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();
        if (InventoryUtil.isPlayerInventoryValid(inventory, player) || InventoryUtil.isHolderValid(holder)) {
            if (HawkEye.InvSession.containsKey(player)) {
                HawkEye.InvSession.remove(player);
            }

            ItemStack[] items = InventoryUtil.isPlayerInventoryValid(inventory, player) ? InventoryUtil.getInventory(inventory) : InventoryUtil.getHolderInventory(holder);

            HawkEye.InvSession.put(player, InventoryUtil.compressInventory(items));
        }
    }
    
    @HawkEvent(
            dataType = {DataType.MINECART_OPEN}
    )
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	switch(event.getRightClicked().getType()) {
	    	case MINECART_HOPPER:
	    	case MINECART_CHEST:
	    		String[] uuidAndType = new String[] {
	    				event.getRightClicked().getUniqueId().toString(),
	    				event.getRightClicked().getType().toString()
	    		};
	    		DataManager.addEntry(
	    			new MinecartEntry(
	    				event.getPlayer().getName(),
	    				DataType.MINECART_OPEN,
	    				event.getRightClicked().getLocation().getBlock().getLocation(),
	    				uuidAndType
	    			)
	    		);
	    		break;
	    	default:
	    		break;
    	}
    }
}

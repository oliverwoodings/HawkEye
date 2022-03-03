package uk.co.oliwali.HawkEye.util;

import be.pyrrh4.ntools.Main;
import be.pyrrh4.ntools.backpack.util.Backpack;
import net.minecraft.server.v1_6_R3.IInventory;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class InventoryUtil {

    public static ItemStack[] getHolderInventory(InventoryHolder inventoryHolder) {
        return getInventory(inventoryHolder.getInventory());
    }

    public static ItemStack[] getInventory(Inventory inventory) {
        ItemStack[] inv;

        IInventory iInventory = ((CraftInventory) inventory).getInventory();
        inv = new ItemStack[iInventory.getSize()];

        for (int i = 0; i < iInventory.getSize(); i++) {
            inv[i] = (iInventory.getItem(i) == null) ? null : CraftItemStack.asCraftMirror(iInventory.getItem(i));
        }

        return inv;
    }

    public static HashMap compressInventory(ItemStack[] inventory) {
        HashMap items = new HashMap();
        ItemStack[] arr$ = inventory;
        int len$ = inventory.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            ItemStack item = arr$[i$];
            if (item != null) {
                String enchantments = "";
                Map enchants = item.getEnchantments();
                Entry entry;
                if (!enchants.isEmpty()) {
                    for (Iterator iString = enchants.entrySet().iterator(); iString.hasNext(); enchantments = enchantments + "-" + Enchantment.getByName(((Enchantment) entry.getKey()).getName()).getId() + "x" + entry.getValue()) {
                        entry = (Entry) iString.next();
                    }
                }

                String var10 = BlockUtil.getItemString(item) + enchantments;
                if (items.containsKey(var10)) {
                    items.put(var10, Integer.valueOf(((Integer) items.get(var10)).intValue() + item.getAmount()));
                } else {
                    items.put(var10, Integer.valueOf(item.getAmount()));
                }
            }
        }

        return items;
    }

    public static ItemStack uncompressItem(String data) {
        data = data.substring(1);
        String[] item = data.split("~");
        String[] enchants = item[0].split("-");
        String[] info = enchants[0].split(":");
        ItemStack stack = null;
        if (info.length == 1) {
            stack = BlockUtil.itemStringToStack(info[0], Integer.valueOf(Integer.parseInt(item[1])));
        } else {
            stack = BlockUtil.itemStringToStack(info[0] + ":" + info[1], Integer.valueOf(Integer.parseInt(item[1])));
        }

        if (enchants.length > 0) {
            String[] arr$ = enchants;
            int len$ = enchants.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String s = arr$[i$];
                String[] types = s.split("x");
                if (types.length != 1) {
                    Enchantment en = Util.isInteger(types[0]) ? Enchantment.getById(Integer.parseInt(types[0])) : Enchantment.getByName(types[0]);
                    if (en != null) {
                        stack.addUnsafeEnchantment(en, Integer.parseInt(types[1]));
                    }
                }
            }
        }

        return stack;
    }

    public static String dataToString(String data) {
        StringBuffer type = new StringBuffer();
        String[] arr$ = data.split("@");
        int len$ = arr$.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            String changes = arr$[i$];
            String[] item = changes.split("~");
            String[] enchants = item[0].substring(1).split("-");
            String c = changes.startsWith("+") ? "&a" : "&4";
            String ench = "";
            if (enchants.length != 1) {
                ench = "*Enchant*";
            }

            type.append(", " + c + item[1] + "x " + BlockUtil.getBlockStringName(enchants[0]) + ench);
        }

        return type.toString().substring(2);
    }

    public static String compareInvs(HashMap items1, HashMap items) {
        if (items1 == null && items == null) {
            return null;
        } else {
            StringBuffer ns = new StringBuffer();
            Iterator info = items.entrySet().iterator();

            Entry entry;
            while (info.hasNext()) {
                entry = (Entry) info.next();
                int count = ((Integer) entry.getValue()).intValue();
                String key = (String) entry.getKey();
                if (items1.containsKey(key)) {
                    int c = ((Integer) items1.get(key)).intValue();
                    if (count < c) {
                        ns.append("@-" + key + "~" + (c - count));
                    } else if (count > c) {
                        ns.append("@+" + key + "~" + (count - c));
                    }

                    items1.remove(key);
                } else {
                    ns.append("@+" + key + "~" + count);
                }
            }

            info = items1.entrySet().iterator();

            while (info.hasNext()) {
                entry = (Entry) info.next();
                ns.append("@-" + (String) entry.getKey() + "~" + entry.getValue());
            }

            String info1 = ns.toString();
            return info1 != null && !info1.equals("") ? info1.substring(1) : null;
        }
    }

    public static void handleHolderRemoval(String remover, BlockState state) {
        InventoryHolder holder = (InventoryHolder) state;
        if (isHolderValid(holder)) {
            String data = compareInvs(compressInventory((holder instanceof Chest ? ((Chest) state).getBlockInventory() : holder.getInventory()).getContents()), new HashMap());
            if (data != null) {
                DataManager.addEntry(new ContainerEntry(remover, getHolderLoc(holder), data));
            }
        }

    }

    public static Location getHolderLoc(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return ((DoubleChest) holder).getLocation().getBlock().getLocation();
        } else if (holder instanceof BlockState) {
            return ((BlockState) holder).getLocation();
        }

        return null;
    }

    public static boolean isHolderValid(InventoryHolder holder) {
        return getHolderLoc(holder) != null;
    }

    public static boolean isPlayerInventoryValid(Inventory inventory, String player) {
        return getPlayerInventoryType(inventory, player) != null;
    }

    public static String getPlayerInventoryType(Inventory inventory, String player) {
        if(inventory.getType() == InventoryType.ENDER_CHEST)
            return "EnderChest";

        Backpack backpack = Main.instance().getBackpack(player);

        if(backpack != null && ((CraftInventory)backpack.getInventory()).getInventory() == ((CraftInventory)inventory).getInventory()) {
            return "BackPack";
        }

        return null;
    }

    public static String updateInv(String old) {
        StringBuffer ns = new StringBuffer();
        String[] sides = old.split("@");
        String[] arr$;
        int len$;
        int i$;
        String s;
        if (sides.length == 1) {
            arr$ = sides[0].split("&");
            len$ = arr$.length;

            for (i$ = 0; i$ < len$; ++i$) {
                s = arr$[i$];
                ns.append("@+" + s);
            }
        } else if (sides[0].equals("")) {
            arr$ = sides[1].split("&");
            len$ = arr$.length;

            for (i$ = 0; i$ < len$; ++i$) {
                s = arr$[i$];
                ns.append("@-" + s);
            }
        } else {
            arr$ = sides[0].split("&");
            len$ = arr$.length;

            for (i$ = 0; i$ < len$; ++i$) {
                s = arr$[i$];
                ns.append("@+" + s);
            }

            arr$ = sides[1].split("&");
            len$ = arr$.length;

            for (i$ = 0; i$ < len$; ++i$) {
                s = arr$[i$];
                ns.append("@-" + s);
            }
        }

        return ns.toString().replace(",", "~").substring(1);
    }
}

package uk.co.oliwali.HawkEye.commands;

import net.ilexiconn.nationsgui.bukkit.util.Translation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.util.Util;

import java.util.Arrays;

public class GiveCommand extends BaseCommand {

    public GiveCommand() {
        this.name = "give";
        this.argLength = 1;
        this.permission = "give";
        this.usage = "<id> <- teleport to location of the data entry";
    }

    public boolean execute() {
        if (!Util.isInteger((String) this.args.get(0))) {
            Util.sendMessage(this.sender, "&cPlease supply a entry id!");
            return true;
        } else {
            DataEntry entry = DataManager.getEntry(Integer.parseInt((String) this.args.get(0)));
            if (entry == null) {
                Util.sendMessage(this.sender, "&cEntry not found");
                return true;
            } else {
                if (entry.getType() == DataType.CONTAINER_TRANSACTION) {
                    String[] itemList = entry.getSqlData().split("@+");
                    for (String item : itemList) {
                        item = item.replace("+", "");
                        item = item.replace("-", "");

                        String amount = item.split("~")[1];

                        item = item.replaceAll("~\\d", "");

                        ((Player) sender).getInventory().addItem(new ItemStack(Integer.parseInt(item), Integer.parseInt(amount)));
                    }
                } else {
                    ((Player) sender).sendMessage(Translation.get("§cUne interraction de ce type ne peut pas être récupérée"));
                }


            /*
            World world = HawkEye.server.getWorld(entry.getWorld());
            if(world == null) {
               Util.sendMessage(this.sender, "&cWorld &7" + entry.getWorld() + "&c does not exist!");
               return true;
            } else {
               Location loc = new Location(world, entry.getX(), entry.getY(), entry.getZ());
               this.player.teleport(loc);
               Util.sendMessage(this.sender, "&7Teleported to location of data entry &c" + (String)this.args.get(0));
               return true;
            } */
            }
        }
        return true;
    }

    public void moreHelp() {
        Util.sendMessage(this.sender, "&cTakes you to the location of the data entry with the specified ID");
        Util.sendMessage(this.sender, "&cThe ID can be found in either the DataLog interface or when you do a search command");
    }
}

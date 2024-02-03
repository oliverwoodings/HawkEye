package uk.co.oliwali.HawkEye;

import com.dthielke.herochat.Herochat;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.oliwali.HawkEye.WorldEdit.WESessionFactory;
import uk.co.oliwali.HawkEye.commands.*;
import uk.co.oliwali.HawkEye.database.ConnectionManager;
import uk.co.oliwali.HawkEye.database.DataManager;
import uk.co.oliwali.HawkEye.entry.ContainerEntry;
import uk.co.oliwali.HawkEye.entry.DataEntry;
import uk.co.oliwali.HawkEye.listeners.*;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.HawkEyeAPI;
import uk.co.oliwali.HawkEye.util.InventoryUtil;
import uk.co.oliwali.HawkEye.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HawkEye extends JavaPlugin {

    public String name;
    public String version;
    public Config config;
    public static Server server;
    public static HawkEye instance;
    public MonitorBlockListener monitorBlockListener = new MonitorBlockListener(this);
    public MonitorEntityListener monitorEntityListener = new MonitorEntityListener(this);
    public MonitorPlayerListener monitorPlayerListener = new MonitorPlayerListener(this);
    public MonitorWorldListener monitorWorldListener = new MonitorWorldListener(this);
    public MonitorFallingBlockListener monitorFBListerner = new MonitorFallingBlockListener(this);
    public MonitorWorldEditListener monitorWorldEditListener = new MonitorWorldEditListener();
    public MonitorLiquidFlow monitorLiquidFlow;
    public ToolListener toolListener = new ToolListener();
    private DataManager dbmanager;
    public MonitorHeroChatListener monitorHeroChatListener = new MonitorHeroChatListener(this);
    public static List commands = new ArrayList();
    public static HashMap InvSession = new HashMap();
    public static WorldEditPlugin worldEdit = null;
    public static Herochat herochat = null;


    public void onDisable() {
        this.dbmanager.run();
        if (!ConnectionManager.getConnections().isEmpty()) {
            while (ConnectionManager.areConsOpen()) {
                Util.debug("Not ready");
                if (DataManager.getQueue().size() != 0) {
                    this.dbmanager.run();
                }
            }
        }

        DataManager.close();
        Util.info("Version " + this.version + " disabled!");
    }

    public void onEnable() {
        try {
            Metrics pm = new Metrics(this);
            pm.start();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        PluginManager pm1 = this.getServer().getPluginManager();

        try {
            Class.forName("org.bukkit.event.hanging.HangingPlaceEvent");
        } catch (ClassNotFoundException var4) {
            Util.info("HawkEye requires CraftBukkit 1.4+ to run properly!");
            pm1.disablePlugin(this);
            return;
        }

        instance = this;
        server = this.getServer();
        this.name = this.getDescription().getName();
        this.version = this.getDescription().getVersion();
        Util.info("Starting HawkEye " + this.version + " initiation process...");
        this.config = new Config(this);
        this.setupUpdater();
        new SessionManager();

        try {
            this.dbmanager = new DataManager(this);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, this.dbmanager, (long) (Config.LogDelay * 20), (long) (Config.LogDelay * 20));
        } catch (Exception var3) {
            Util.severe("Error initiating HawkEye database connection, disabling plugin");
            pm1.disablePlugin(this);
            return;
        }

        this.checkDependencies(pm1);
        this.monitorLiquidFlow = new MonitorLiquidFlow(this);
        this.registerListeners(pm1);
        this.registerCommands();
        Util.info("Version " + this.version + " enabled!");
    }

    private void checkDependencies(PluginManager pm) {
        Plugin we = pm.getPlugin("WorldEdit");
        Plugin hc = pm.getPlugin("Herochat");
        if (we != null) {
            worldEdit = (WorldEditPlugin) we;
        }

        if (hc != null) {
            herochat = (Herochat) hc;
        }

    }

    public void registerListeners(PluginManager pm) {
        this.monitorBlockListener.registerEvents();
        this.monitorPlayerListener.registerEvents();
        this.monitorEntityListener.registerEvents();
        this.monitorWorldListener.registerEvents();
        this.monitorFBListerner.registerEvents();
        this.monitorLiquidFlow.registerEvents();
        this.monitorLiquidFlow.startCacheCleaner();
        pm.registerEvents(this.toolListener, this);
        if (herochat != null) {
            this.monitorHeroChatListener.registerEvents();
        }

        if (worldEdit != null) {
            if (DataType.SUPER_PICKAXE.isLogged()) {
                pm.registerEvents(this.monitorWorldEditListener, this);
            }

            if (DataType.WORLDEDIT_BREAK.isLogged() || DataType.WORLDEDIT_PLACE.isLogged()) {
                WESessionFactory.enableWELogging();
            }
        }

    }

    private void registerCommands() {
        commands.add(new HelpCommand());
        commands.add(new ToolBindCommand());
        commands.add(new ToolResetCommand());
        commands.add(new ToolCommand());
        commands.add(new SearchCommand());
        commands.add(new PageCommand());
        commands.add(new TptoCommand());
        commands.add(new HereCommand());
        commands.add(new PreviewApplyCommand());
        commands.add(new PreviewCancelCommand());
        commands.add(new PreviewCommand());
        commands.add(new RollbackCommand());
        commands.add(new UndoCommand());
        commands.add(new RebuildCommand());
        commands.add(new DeleteCommand());
        commands.add(new GiveCommand());
        commands.add(new InfoCommand());
        commands.add(new WriteLogCommand());
        commands.add(new ReloadCommand());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("hawk")) {
            return false;
        } else {
            if (args.length == 0) {
                args = new String[]{"help"};
            }

            BaseCommand[] arr$ = (BaseCommand[]) commands.toArray(new BaseCommand[0]);
            int len$ = arr$.length;
            int i$ = 0;

            label33:
            while (i$ < len$) {
                BaseCommand command = arr$[i$];
                String[] cmds = command.name.split(" ");

                for (int i = 0; i < cmds.length; ++i) {
                    if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])) {
                        ++i$;
                        continue label33;
                    }
                }

                return command.run(this, sender, args, commandLabel);
            }

            (new HelpCommand()).run(this, sender, args, commandLabel);
            return true;
        }
    }

    private void setupUpdater() {
        if (this.getConfig().getBoolean("general.check-for-updates")) {
            new Updater(this, "hawkeye-reload", this.getFile(), Updater.UpdateType.DEFAULT, false);
        }

    }

    public void logTransaction(int x, int y,  int z, String worldId, String player, String data) {
        DataManager.addEntry(new ContainerEntry(player, new Location(Bukkit.getWorld(worldId), x, y, z), data));
    }

    public void logTransactionAsCustom(int x, int y,  int z, String worldId, String player, String customTitle, String data) {
        Location logLocation = new Location(Bukkit.getWorld(worldId), x, y, z);

        DataEntry dataEntry = new ContainerEntry(player, logLocation, data);
        HawkEyeAPI.addCustomEntry(this, customTitle, player, logLocation, dataEntry.getStringData());
    }


    public HashMap parseBlockInventory(int posX, int posY, int posZ) {
        World world = Bukkit.getWorlds().get(0);

        Block block = world.getBlockAt(posX, posY, posZ);

        if (block.getState() instanceof InventoryHolder) {
            ItemStack[] itemStacks = InventoryUtil.getHolderInventory((InventoryHolder) block.getState());

            return InventoryUtil.compressInventory(itemStacks);
        }

        return null;
    }

    public void logInvDiff(int posX, int posY, int posZ, HashMap map, String playerName) {
        World world = Bukkit.getWorlds().get(0);

        Block block = world.getBlockAt(posX, posY, posZ);

        if (block.getState() instanceof InventoryHolder) {

            InventoryHolder inventoryHolder = (InventoryHolder) block.getState();

            ItemStack[] itemStacks = InventoryUtil.getHolderInventory(inventoryHolder);

            String data = InventoryUtil.compareInvs(map, InventoryUtil.compressInventory(itemStacks));

            if (data == null) {
                return;
            }

            DataManager.addEntry(new ContainerEntry(playerName, InventoryUtil.getHolderLoc(inventoryHolder), data));
        }
    }
}

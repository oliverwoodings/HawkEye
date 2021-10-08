package uk.co.oliwali.HawkEye;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

public class Metrics {

   private static final int REVISION = 6;
   private static final String BASE_URL = "http://mcstats.org";
   private static final String REPORT_URL = "/report/%s";
   private static final String CUSTOM_DATA_SEPARATOR = "~~";
   private static final int PING_INTERVAL = 10;
   private final Plugin plugin;
   private final Set graphs = Collections.synchronizedSet(new HashSet());
   private final Graph defaultGraph = new Graph("Default", null);
   private final YamlConfiguration configuration;
   private final File configurationFile;
   private final String guid;
   private final boolean debug;
   private final Object optOutLock = new Object();
   private volatile BukkitTask task = null;


   public Metrics(Plugin plugin) throws IOException {
      if(plugin == null) {
         throw new IllegalArgumentException("Plugin cannot be null");
      } else {
         this.plugin = plugin;
         this.configurationFile = this.getConfigFile();
         this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
         this.configuration.addDefault("opt-out", Boolean.valueOf(false));
         this.configuration.addDefault("guid", UUID.randomUUID().toString());
         this.configuration.addDefault("debug", Boolean.valueOf(false));
         if(this.configuration.get("guid", (Object)null) == null) {
            this.configuration.options().header("http://mcstats.org").copyDefaults(true);
            this.configuration.save(this.configurationFile);
         }

         this.guid = this.configuration.getString("guid");
         this.debug = this.configuration.getBoolean("debug", false);
      }
   }

   public Graph createGraph(String name) {
      if(name == null) {
         throw new IllegalArgumentException("Graph name cannot be null");
      } else {
         Graph graph = new Graph(name, null);
         this.graphs.add(graph);
         return graph;
      }
   }

   public void addGraph(Graph graph) {
      if(graph == null) {
         throw new IllegalArgumentException("Graph cannot be null");
      } else {
         this.graphs.add(graph);
      }
   }

   public void addCustomData(Plotter plotter) {
      if(plotter == null) {
         throw new IllegalArgumentException("Plotter cannot be null");
      } else {
         this.defaultGraph.addPlotter(plotter);
         this.graphs.add(this.defaultGraph);
      }
   }

   public boolean start() {
      Object var1 = this.optOutLock;
      synchronized(this.optOutLock) {
         if(this.isOptOut()) {
            return false;
         } else if(this.task != null) {
            return true;
         } else {
            this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {

               private boolean firstPost = true;

               public void run() {
                  try {
                     synchronized(Metrics.this.optOutLock) {
                        if(Metrics.this.isOptOut() && Metrics.this.task != null) {
                           Metrics.this.task.cancel();
                           Metrics.this.task = null;
                           Iterator i$ = Metrics.this.graphs.iterator();

                           while(i$.hasNext()) {
                              Graph graph = (Graph)i$.next();
                              graph.onOptOut();
                           }
                        }
                     }

                     Metrics.this.postPlugin(!this.firstPost);
                     this.firstPost = false;
                  } catch (IOException var6) {
                     if(Metrics.this.debug) {
                        Bukkit.getLogger().log(Level.INFO, "[Metrics] " + var6.getMessage());
                     }
                  }

               }
            }, 0L, 12000L);
            return true;
         }
      }
   }

   public boolean isOptOut() {
      Object var1 = this.optOutLock;
      synchronized(this.optOutLock) {
         try {
            this.configuration.load(this.getConfigFile());
         } catch (IOException var4) {
            if(this.debug) {
               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + var4.getMessage());
            }

            return true;
         } catch (InvalidConfigurationException var5) {
            if(this.debug) {
               Bukkit.getLogger().log(Level.INFO, "[Metrics] " + var5.getMessage());
            }

            return true;
         }

         return this.configuration.getBoolean("opt-out", false);
      }
   }

   public void enable() throws IOException {
      Object var1 = this.optOutLock;
      synchronized(this.optOutLock) {
         if(this.isOptOut()) {
            this.configuration.set("opt-out", Boolean.valueOf(false));
            this.configuration.save(this.configurationFile);
         }

         if(this.task == null) {
            this.start();
         }

      }
   }

   public void disable() throws IOException {
      Object var1 = this.optOutLock;
      synchronized(this.optOutLock) {
         if(!this.isOptOut()) {
            this.configuration.set("opt-out", Boolean.valueOf(true));
            this.configuration.save(this.configurationFile);
         }

         if(this.task != null) {
            this.task.cancel();
            this.task = null;
         }

      }
   }

   public File getConfigFile() {
      File pluginsFolder = this.plugin.getDataFolder().getParentFile();
      return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
   }

   private void postPlugin(boolean isPing) throws IOException {
      PluginDescriptionFile description = this.plugin.getDescription();
      String pluginName = description.getName();
      boolean onlineMode = Bukkit.getServer().getOnlineMode();
      String pluginVersion = description.getVersion();
      String serverVersion = Bukkit.getVersion();
      int playersOnline = Bukkit.getServer().getOnlinePlayers().length;
      StringBuilder data = new StringBuilder();
      data.append(encode("guid")).append('=').append(encode(this.guid));
      encodeDataPair(data, "version", pluginVersion);
      encodeDataPair(data, "server", serverVersion);
      encodeDataPair(data, "players", Integer.toString(playersOnline));
      encodeDataPair(data, "revision", String.valueOf(6));
      String osname = System.getProperty("os.name");
      String osarch = System.getProperty("os.arch");
      String osversion = System.getProperty("os.version");
      String java_version = System.getProperty("java.version");
      int coreCount = Runtime.getRuntime().availableProcessors();
      if(osarch.equals("amd64")) {
         osarch = "x86_64";
      }

      encodeDataPair(data, "osname", osname);
      encodeDataPair(data, "osarch", osarch);
      encodeDataPair(data, "osversion", osversion);
      encodeDataPair(data, "cores", Integer.toString(coreCount));
      encodeDataPair(data, "online-mode", Boolean.toString(onlineMode));
      encodeDataPair(data, "java_version", java_version);
      if(isPing) {
         encodeDataPair(data, "ping", "true");
      }

      Set url = this.graphs;
      synchronized(this.graphs) {
         Iterator connection = this.graphs.iterator();

         while(connection.hasNext()) {
            Graph writer1 = (Graph)connection.next();
            Iterator reader1 = writer1.getPlotters().iterator();

            while(reader1.hasNext()) {
               Plotter response1 = (Plotter)reader1.next();
               String key1 = String.format("C%s%s%s%s", new Object[]{"~~", writer1.getName(), "~~", response1.getColumnName()});
               String iter1 = Integer.toString(response1.getValue());
               encodeDataPair(data, key1, iter1);
            }
         }
      }

      URL url1 = new URL("http://mcstats.org" + String.format("/report/%s", new Object[]{encode(pluginName)}));
      URLConnection connection1;
      if(this.isMineshafterPresent()) {
         connection1 = url1.openConnection(Proxy.NO_PROXY);
      } else {
         connection1 = url1.openConnection();
      }

      connection1.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(connection1.getOutputStream());
      writer.write(data.toString());
      writer.flush();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
      String response = reader.readLine();
      writer.close();
      reader.close();
      if(response != null && !response.startsWith("ERR")) {
         if(response.contains("OK This is your first update this hour")) {
            Set key = this.graphs;
            synchronized(this.graphs) {
               Iterator iter = this.graphs.iterator();

               while(iter.hasNext()) {
                  Graph graph = (Graph)iter.next();
                  Iterator i$ = graph.getPlotters().iterator();

                  while(i$.hasNext()) {
                     Plotter plotter = (Plotter)i$.next();
                     plotter.reset();
                  }
               }
            }
         }

      } else {
         throw new IOException(response);
      }
   }

   private boolean isMineshafterPresent() {
      try {
         Class.forName("mineshafter.MineServer");
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   private static void encodeDataPair(StringBuilder buffer, String key, String value) throws UnsupportedEncodingException {
      buffer.append('&').append(encode(key)).append('=').append(encode(value));
   }

   private static String encode(String text) throws UnsupportedEncodingException {
      return URLEncoder.encode(text, "UTF-8");
   }

   public static class Graph {

      private final String name;
      private final Set plotters;


      private Graph(String name) {
         this.plotters = new LinkedHashSet();
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public void addPlotter(Plotter plotter) {
         this.plotters.add(plotter);
      }

      public void removePlotter(Plotter plotter) {
         this.plotters.remove(plotter);
      }

      public Set getPlotters() {
         return Collections.unmodifiableSet(this.plotters);
      }

      public int hashCode() {
         return this.name.hashCode();
      }

      public boolean equals(Object object) {
         if(!(object instanceof Graph)) {
            return false;
         } else {
            Graph graph = (Graph)object;
            return graph.name.equals(this.name);
         }
      }

      protected void onOptOut() {}

      // $FF: synthetic method
      Graph(String x0, Object x1) {
         this(x0);
      }
   }

   public abstract static class Plotter {

      private final String name;


      public Plotter() {
         this("Default");
      }

      public Plotter(String name) {
         this.name = name;
      }

      public abstract int getValue();

      public String getColumnName() {
         return this.name;
      }

      public void reset() {}

      public int hashCode() {
         return this.getColumnName().hashCode();
      }

      public boolean equals(Object object) {
         if(!(object instanceof Plotter)) {
            return false;
         } else {
            Plotter plotter = (Plotter)object;
            return plotter.name.equals(this.name) && plotter.getValue() == this.getValue();
         }
      }
   }
}

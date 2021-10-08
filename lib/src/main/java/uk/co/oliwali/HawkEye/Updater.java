package uk.co.oliwali.HawkEye;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Updater {

   private Plugin plugin;
   private UpdateType type;
   private String versionTitle;
   private String versionLink;
   private long totalSize;
   private int sizeLine;
   private int multiplier;
   private boolean announce;
   private URL url;
   private File file;
   private Thread thread;
   private static final String DBOUrl = "http://dev.bukkit.org/server-mods/";
   private String[] noUpdateTag = new String[]{"-DEV", "-PRE", "-SNAPSHOT"};
   private static final int BYTE_SIZE = 1024;
   private String updateFolder = YamlConfiguration.loadConfiguration(new File("bukkit.yml")).getString("settings.update-folder");
   private UpdateResult result;
   private static final String TITLE = "title";
   private static final String LINK = "link";
   private static final String ITEM = "item";


   public Updater(Plugin plugin, String slug, File file, UpdateType type, boolean announce) {
      this.result = UpdateResult.SUCCESS;
      this.plugin = plugin;
      this.type = type;
      this.announce = announce;
      this.file = file;

      try {
         this.url = new URL("http://dev.bukkit.org/server-mods/" + slug + "/files.rss");
      } catch (MalformedURLException var7) {
         plugin.getLogger().warning("The author of this plugin (" + (String)plugin.getDescription().getAuthors().get(0) + ") has misconfigured their Auto Update system");
         plugin.getLogger().warning("The project slug given (\'" + slug + "\') is invalid. Please nag the author about this.");
         this.result = UpdateResult.FAIL_BADSLUG;
      }

      this.thread = new Thread(new UpdateRunnable((NamelessClass1817017283)null));
      this.thread.start();
   }

   public UpdateResult getResult() {
      this.waitForThread();
      return this.result;
   }

   public long getFileSize() {
      this.waitForThread();
      return this.totalSize;
   }

   public String getLatestVersionString() {
      this.waitForThread();
      return this.versionTitle;
   }

   public void waitForThread() {
      if(this.thread.isAlive()) {
         try {
            this.thread.join();
         } catch (InterruptedException var2) {
            var2.printStackTrace();
         }
      }

   }

   private void saveFile(File folder, String file, String u) {
      if(!folder.exists()) {
         folder.mkdir();
      }

      BufferedInputStream in = null;
      FileOutputStream fout = null;

      try {
         URL ex = new URL(u);
         int fileLength = ex.openConnection().getContentLength();
         in = new BufferedInputStream(ex.openStream());
         fout = new FileOutputStream(folder.getAbsolutePath() + "/" + file);
         byte[] data = new byte[1024];
         if(this.announce) {
            this.plugin.getLogger().info("About to download a new update: " + this.versionTitle);
         }

         long downloaded = 0L;

         int count;
         while((count = in.read(data, 0, 1024)) != -1) {
            downloaded += (long)count;
            fout.write(data, 0, count);
            int dFile = (int)(downloaded * 100L / (long)fileLength);
            if(this.announce & dFile % 10 == 0) {
               this.plugin.getLogger().info("Downloading update: " + dFile + "% of " + fileLength + " bytes.");
            }
         }

         File[] var26 = (new File("plugins/" + this.updateFolder)).listFiles();
         int len$ = var26.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            File xFile = var26[i$];
            if(xFile.getName().endsWith(".zip")) {
               xFile.delete();
            }
         }

         File var27 = new File(folder.getAbsolutePath() + "/" + file);
         if(var27.getName().endsWith(".zip")) {
            this.unzip(var27.getCanonicalPath());
         }

         if(this.announce) {
            this.plugin.getLogger().info("Finished updating.");
         }
      } catch (Exception var24) {
         this.plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
         this.result = UpdateResult.FAIL_DOWNLOAD;
      } finally {
         try {
            if(in != null) {
               in.close();
            }

            if(fout != null) {
               fout.close();
            }
         } catch (Exception var23) {
            ;
         }

      }

   }

   private void unzip(String file) {
      try {
         File ex = new File(file);
         String zipPath = file.substring(0, file.length() - 4);
         ZipFile zipFile = new ZipFile(ex);
         Enumeration e = zipFile.entries();

         while(e.hasMoreElements()) {
            ZipEntry arr$ = (ZipEntry)e.nextElement();
            File len$ = new File(zipPath, arr$.getName());
            len$.getParentFile().mkdirs();
            if(!arr$.isDirectory()) {
               BufferedInputStream i$ = new BufferedInputStream(zipFile.getInputStream(arr$));
               byte[] oFile = new byte[1024];
               FileOutputStream contents = new FileOutputStream(len$);
               BufferedOutputStream arr$1 = new BufferedOutputStream(contents, 1024);

               int dFile;
               while((dFile = i$.read(oFile, 0, 1024)) != -1) {
                  arr$1.write(oFile, 0, dFile);
               }

               arr$1.flush();
               arr$1.close();
               i$.close();
               String len$1 = len$.getName();
               if(len$1.endsWith(".jar") && this.pluginFile(len$1)) {
                  len$.renameTo(new File("plugins/" + this.updateFolder + "/" + len$1));
               }

               arr$ = null;
               len$ = null;
            }
         }

         e = null;
         zipFile.close();
         zipFile = null;
         File[] var22 = (new File(zipPath)).listFiles();
         int var23 = var22.length;

         for(int var24 = 0; var24 < var23; ++var24) {
            File var25 = var22[var24];
            if(var25.isDirectory() && this.pluginFile(var25.getName())) {
               File var26 = new File("plugins/" + var25.getName());
               File[] var27 = var26.listFiles();
               File[] var28 = var25.listFiles();
               int var29 = var28.length;

               for(int i$1 = 0; i$1 < var29; ++i$1) {
                  File cFile = var28[i$1];
                  boolean found = false;
                  File[] arr$2 = var27;
                  int len$2 = var27.length;

                  for(int i$2 = 0; i$2 < len$2; ++i$2) {
                     File xFile = arr$2[i$2];
                     if(xFile.getName().equals(cFile.getName())) {
                        found = true;
                        break;
                     }
                  }

                  if(!found) {
                     cFile.renameTo(new File(var26.getCanonicalFile() + "/" + cFile.getName()));
                  } else {
                     cFile.delete();
                  }
               }
            }

            var25.delete();
         }

         (new File(zipPath)).delete();
         ex.delete();
      } catch (IOException var21) {
         var21.printStackTrace();
         this.plugin.getLogger().warning("The auto-updater tried to unzip a new update file, but was unsuccessful.");
         this.result = UpdateResult.FAIL_DOWNLOAD;
      }

      (new File(file)).delete();
   }

   public boolean pluginFile(String name) {
      File[] arr$ = (new File("plugins")).listFiles();
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         File file = arr$[i$];
         if(file.getName().equals(name)) {
            return true;
         }
      }

      return false;
   }

   private String getFile(String link) {
      String download = null;

      try {
         URL ex = new URL(link);
         URLConnection urlConn = ex.openConnection();
         InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
         BufferedReader buff = new BufferedReader(inStream);
         int counter = 0;

         String line;
         while((line = buff.readLine()) != null) {
            ++counter;
            if(line.contains("<li class=\"user-action user-action-download\">")) {
               download = line.split("<a href=\"")[1].split("\">Download</a>")[0];
            } else if(line.contains("<dt>Size</dt>")) {
               this.sizeLine = counter + 1;
            } else if(counter == this.sizeLine) {
               String size = line.replaceAll("<dd>", "").replaceAll("</dd>", "");
               this.multiplier = size.contains("MiB")?1048576:1024;
               size = size.replace(" KiB", "").replace(" MiB", "");
               this.totalSize = (long)(Double.parseDouble(size) * (double)this.multiplier);
            }
         }

         urlConn = null;
         inStream = null;
         buff.close();
         buff = null;
         return download;
      } catch (Exception var10) {
         var10.printStackTrace();
         this.plugin.getLogger().warning("The auto-updater tried to contact dev.bukkit.org, but was unsuccessful.");
         this.result = UpdateResult.FAIL_DBO;
         return null;
      }
   }

   private boolean versionCheck(String title) {
      if(this.type != UpdateType.NO_VERSION_CHECK) {
         String version = this.plugin.getDescription().getVersion();
         if(title.split(" v").length != 2) {
            this.plugin.getLogger().warning("The author of this plugin (" + (String)this.plugin.getDescription().getAuthors().get(0) + ") has misconfigured their Auto Update system");
            this.plugin.getLogger().warning("Files uploaded to BukkitDev should contain the version number, seperated from the name by a \'v\', such as PluginName v1.0");
            this.plugin.getLogger().warning("Please notify the author of this error.");
            this.result = UpdateResult.FAIL_NOVERSION;
            return false;
         }

         String remoteVersion = title.split(" v")[1].split(" ")[0];
         boolean remVer = true;
         int curVer = 0;

         int remVer1;
         try {
            remVer1 = this.calVer(remoteVersion).intValue();
            curVer = this.calVer(version).intValue();
         } catch (NumberFormatException var7) {
            remVer1 = -1;
         }

         if(this.hasTag(version) || version.equalsIgnoreCase(remoteVersion) || curVer >= remVer1) {
            this.result = UpdateResult.NO_UPDATE;
            return false;
         }
      }

      return true;
   }

   private Integer calVer(String s) throws NumberFormatException {
      if(s.contains(".")) {
         StringBuilder sb = new StringBuilder();

         for(int i = 0; i < s.length(); ++i) {
            Character c = Character.valueOf(s.charAt(i));
            if(Character.isLetterOrDigit(c.charValue())) {
               sb.append(c);
            }
         }

         return Integer.valueOf(Integer.parseInt(sb.toString()));
      } else {
         return Integer.valueOf(Integer.parseInt(s));
      }
   }

   private boolean hasTag(String version) {
      String[] arr$ = this.noUpdateTag;
      int len$ = arr$.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String string = arr$[i$];
         if(version.contains(string)) {
            return true;
         }
      }

      return false;
   }

   private boolean readFeed() {
      try {
         String e = "";
         String link = "";
         XMLInputFactory inputFactory = XMLInputFactory.newInstance();
         InputStream in = this.read();
         if(in == null) {
            return false;
         } else {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while(eventReader.hasNext()) {
               XMLEvent event = eventReader.nextEvent();
               if(event.isStartElement()) {
                  if(event.asStartElement().getName().getLocalPart().equals("title")) {
                     event = eventReader.nextEvent();
                     e = event.asCharacters().getData();
                  } else if(event.asStartElement().getName().getLocalPart().equals("link")) {
                     event = eventReader.nextEvent();
                     link = event.asCharacters().getData();
                  }
               } else if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("item")) {
                  this.versionTitle = e;
                  this.versionLink = link;
                  break;
               }
            }

            return true;
         }
      } catch (XMLStreamException var7) {
         this.plugin.getLogger().warning("Could not reach dev.bukkit.org for update checking. Is it offline?");
         return false;
      }
   }

   private InputStream read() {
      try {
         return this.url.openStream();
      } catch (IOException var2) {
         this.plugin.getLogger().warning("Could not reach BukkitDev file stream for update checking. Is dev.bukkit.org offline?");
         return null;
      }
   }

   public static enum UpdateResult {

      SUCCESS("SUCCESS", 0),
      NO_UPDATE("NO_UPDATE", 1),
      FAIL_DOWNLOAD("FAIL_DOWNLOAD", 2),
      FAIL_DBO("FAIL_DBO", 3),
      FAIL_NOVERSION("FAIL_NOVERSION", 4),
      FAIL_BADSLUG("FAIL_BADSLUG", 5),
      UPDATE_AVAILABLE("UPDATE_AVAILABLE", 6);
      // $FF: synthetic field
      private static final UpdateResult[] $VALUES = new UpdateResult[]{SUCCESS, NO_UPDATE, FAIL_DOWNLOAD, FAIL_DBO, FAIL_NOVERSION, FAIL_BADSLUG, UPDATE_AVAILABLE};


      private UpdateResult(String var1, int var2) {}

   }

   private class UpdateRunnable implements Runnable {

      private UpdateRunnable() {}

      public void run() {
         if(Updater.this.url != null) {
            Updater.this.plugin.getLogger().info("Searching for updates..");
            if(Updater.this.readFeed()) {
               if(Updater.this.versionCheck(Updater.this.versionTitle)) {
                  String fileLink = Updater.this.getFile(Updater.this.versionLink);
                  Updater.this.plugin.getLogger().info("Update found! Downloading...");
                  if(fileLink != null && Updater.this.type != UpdateType.NO_DOWNLOAD) {
                     String name = Updater.this.file.getName();
                     Updater.this.saveFile(new File("plugins/" + Updater.this.updateFolder), name, fileLink);
                     Updater.this.plugin.getLogger().info("The new version will be installed on startup!");
                  } else {
                     Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
                  }
               } else {
                  Updater.this.plugin.getLogger().info("No updates found!");
               }
            }
         }

      }

      // $FF: synthetic method
      UpdateRunnable(NamelessClass1817017283 x1) {
         this();
      }
   }

   // $FF: synthetic class
   static class NamelessClass1817017283 {
   }

   public static enum UpdateType {

      DEFAULT("DEFAULT", 0),
      NO_VERSION_CHECK("NO_VERSION_CHECK", 1),
      NO_DOWNLOAD("NO_DOWNLOAD", 2);
      // $FF: synthetic field
      private static final UpdateType[] $VALUES = new UpdateType[]{DEFAULT, NO_VERSION_CHECK, NO_DOWNLOAD};


      private UpdateType(String var1, int var2) {}

   }
}

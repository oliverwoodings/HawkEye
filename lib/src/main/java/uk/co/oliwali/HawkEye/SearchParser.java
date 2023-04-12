package uk.co.oliwali.HawkEye;

import com.sk89q.worldedit.bukkit.selections.Selection;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;

public class SearchParser {

   public CommandSender player = null;
   public List players = new ArrayList();
   public Vector loc = null;
   public Vector minLoc = null;
   public Vector maxLoc = null;
   public Integer radius = null;
   public List actions = new ArrayList();
   public String[] worlds = null;
   public String dateFrom = null;
   public String dateTo = null;
   public String[] filters = null;
   public List<Integer> entitys = new ArrayList<>();


   public SearchParser() {}

   public SearchParser(CommandSender player) {
      this.player = player;
   }

   public SearchParser(CommandSender player, int radius) {
      this.player = player;
      this.radius = Integer.valueOf(radius);
      this.parseLocations();
   }

   public SearchParser(CommandSender player, List args, boolean radiusRequired) throws IllegalArgumentException {
      this.player = player;
      String lastParam = "";
      boolean paramSet = false;
      boolean worldedit = false;
      boolean radiusSet = false;

      for(int i = 0; i < args.size(); ++i) {
         String arg = (String)args.get(i);
         if(!arg.isEmpty()) {
            if(!paramSet) {
               if(arg.length() < 2) {
                  throw new IllegalArgumentException("Invalid argument format: &7" + arg);
               }

               if(!arg.substring(1, 2).equals(":")) {
                  if(arg.contains(":")) {
                     throw new IllegalArgumentException("Invalid argument format: &7" + arg);
                  }

                  this.players.add(arg);
                  continue;
               }

               lastParam = arg.substring(0, 1).toLowerCase();
               paramSet = true;
               if(arg.length() == 2) {
                  if(i == args.size() - 1) {
                     throw new IllegalArgumentException("Invalid argument format: &7" + arg);
                  }
                  continue;
               }

               arg = arg.substring(2);
            }

            if(paramSet) {
               if(arg.isEmpty()) {
                  throw new IllegalArgumentException("Invalid argument format: &7" + lastParam + ":");
               }

               String[] values = arg.split(",");
               String[] type;
               int isTo;
               int form;
               String days;
               if(lastParam.equals("p")) {
                  type = values;
                  isTo = values.length;

                  for(form = 0; form < isTo; ++form) {
                     days = type[form];
                     this.players.add(days.toLowerCase());
                  }
               } else if(lastParam.equals("w")) {
                  this.worlds = values;
               } else if(lastParam.equals("f")) {
                  if(this.filters != null) {
                     this.filters = (String[])Util.concat(this.filters, new String[][]{values});
                  } else {
                     this.filters = values;
                  }
               } else if(lastParam.equals("b")) {
                  for(int var20 = 0; var20 < values.length; ++var20) {
                     if(Material.getMaterial(values[var20]) != null) {
                        values[var20] = Integer.toString(Material.getMaterial(values[var20]).getId());
                     }
                  }
               } else if(lastParam.equals("a")) {
                  type = values;
                  isTo = values.length;

                  for(form = 0; form < isTo; ++form) {
                     days = type[form];
                     DataType hours = DataType.fromName(days);
                     if(hours == null) {
                        throw new IllegalArgumentException("Invalid action supplied: &7" + days);
                     }

                     if(!Util.hasPerm(player, "search." + hours.getConfigName().toLowerCase())) {
                        throw new IllegalArgumentException("You do not have permission to search for: &7" + hours.getConfigName());
                     }

                     this.actions.add(hours);
                  }
               } else if(lastParam.equals("l") && player instanceof Player) {
                  if(values[0].equalsIgnoreCase("here")) {
                     this.loc = ((Player)player).getLocation().toVector();
                  } else {
                     this.loc = new Vector();
                     this.loc.setX(Integer.parseInt(values[0]));
                     this.loc.setY(Integer.parseInt(values[1]));
                     this.loc.setZ(Integer.parseInt(values[2]));
                  }
               } else if(lastParam.equals("e")) {
            	   for(String val : values) {
            		   try{
            			   entitys.add(Integer.parseInt(val, 16));
            		   }
            		   catch(Exception e) {
            			   throw new IllegalArgumentException("Invalid entity id supplied: &7" + val);
            		   }
            	   }
            	   
               } else {
                  int var24;
                  if(lastParam.equals("r") && player instanceof Player) {
                     radiusSet = true;
                     if(!Util.isInteger(values[0])) {
                        if(!values[0].equalsIgnoreCase("we") && !values[0].equalsIgnoreCase("worldedit") || HawkEye.worldEdit == null) {
                           throw new IllegalArgumentException("Invalid radius supplied: &7" + values[0]);
                        }

                        Selection var23 = HawkEye.worldEdit.getSelection((Player)player);
                        isTo = (int)Math.ceil((double)(var23.getLength() / 2));
                        form = (int)Math.ceil((double)(var23.getWidth() / 2));
                        var24 = (int)Math.ceil((double)(var23.getHeight() / 2));
                        if(Config.MaxRadius != 0 && (isTo > Config.MaxRadius || form > Config.MaxRadius || var24 > Config.MaxRadius)) {
                           throw new IllegalArgumentException("Selection too large, max radius: &7" + Config.MaxRadius);
                        }

                        worldedit = true;
                        this.minLoc = new Vector(var23.getMinimumPoint().getX(), var23.getMinimumPoint().getY(), var23.getMinimumPoint().getZ());
                        this.maxLoc = new Vector(var23.getMaximumPoint().getX(), var23.getMaximumPoint().getY(), var23.getMaximumPoint().getZ());
                     } else {
                        this.radius = Integer.valueOf(Integer.parseInt(values[0]));
                        if(Config.MaxRadius != 0 && this.radius.intValue() > Config.MaxRadius) {
                           throw new IllegalArgumentException("Radius too large, max allowed: &7" + Config.MaxRadius);
                        }
                     }
                  } else {
                     if(!lastParam.equals("t")) {
                        throw new IllegalArgumentException("Invalid parameter supplied: &7" + lastParam);
                     }

                     byte var21 = 2;
                     boolean var22 = false;

                     for(form = 0; form < arg.length(); ++form) {
                        days = arg.substring(form, form + 1);
                        if(!Util.isInteger(days)) {
                           if(days.equals("m") || days.equals("s") || days.equals("h") || days.equals("d") || days.equals("w")) {
                              var21 = 0;
                           }

                           if(days.equals("-") || days.equals(":")) {
                              var21 = 1;
                           }
                        }
                     }

                     if(var21 == 0) {
                        form = 0;
                        var24 = 0;
                        int var25 = 0;
                        int mins = 0;
                        int secs = 0;
                        String nums = "";

                        for(int cal = 0; cal < values[0].length(); ++cal) {
                           String form1 = values[0].substring(cal, cal + 1);
                           if(form1.equals("!")) {
                              form1 = values[0].substring(cal, cal + 2).replace("!", "");
                              var22 = true;
                           } else if(Util.isInteger(form1)) {
                              nums = nums + form1;
                           } else {
                              int num = Integer.parseInt(nums);
                              if(form1.equals("w")) {
                                 form = num;
                              } else if(form1.equals("d")) {
                                 var24 = num;
                              } else if(form1.equals("h")) {
                                 var25 = num;
                              } else if(form1.equals("m")) {
                                 mins = num;
                              } else {
                                 if(!form1.equals("s")) {
                                    throw new IllegalArgumentException("Invalid time measurement: &7" + form1);
                                 }

                                 secs = num;
                              }

                              nums = "";
                           }
                        }

                        Calendar var27 = Calendar.getInstance();
                        var27.add(3, -1 * form);
                        var27.add(5, -1 * var24);
                        var27.add(10, -1 * var25);
                        var27.add(12, -1 * mins);
                        var27.add(13, -1 * secs);
                        SimpleDateFormat var28 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if(var22) {
                           this.dateTo = var28.format(var27.getTime());
                        } else {
                           this.dateFrom = var28.format(var27.getTime());
                        }
                     } else if(var21 == 1) {
                        if(values.length == 1) {
                           SimpleDateFormat var26 = new SimpleDateFormat("yyyy-MM-dd");
                           this.dateFrom = var26.format(Calendar.getInstance().getTime()) + " " + values[0];
                        }

                        if(values.length >= 2) {
                           this.dateFrom = values[0] + " " + values[1];
                        }

                        if(values.length == 4) {
                           this.dateTo = values[2] + " " + values[3];
                        }
                     } else if(var21 == 2) {
                        throw new IllegalArgumentException("Invalid time format!");
                     }
                  }
               }

               paramSet = false;
            }
         }
      }

      if(radiusRequired && !radiusSet)
         throw new InvalidParameterException("No radius set !");

      if(!worldedit) {
         this.parseLocations();
      }

   }

   public void parseLocations() {
      if(this.player instanceof Player) {
         if(this.radius == null && Config.MaxRadius != 0) {
            this.radius = Integer.valueOf(Config.MaxRadius);
         }

         if(this.radius != null) {
            if(this.loc == null) {
               this.loc = ((Player)this.player).getLocation().toVector();
            }

            if(this.worlds == null) {
               this.worlds = new String[]{((Player)this.player).getWorld().getName()};
            }

            this.minLoc = new Vector(this.loc.getX() - (double)this.radius.intValue(), this.loc.getY() - (double)this.radius.intValue(), this.loc.getZ() - (double)this.radius.intValue());
            this.maxLoc = new Vector(this.loc.getX() + (double)this.radius.intValue(), this.loc.getY() + (double)this.radius.intValue(), this.loc.getZ() + (double)this.radius.intValue());
         }

      }
   }
}

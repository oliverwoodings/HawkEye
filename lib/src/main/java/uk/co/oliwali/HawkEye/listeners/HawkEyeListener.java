package uk.co.oliwali.HawkEye.listeners;

import java.lang.reflect.Method;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import uk.co.oliwali.HawkEye.DataType;
import uk.co.oliwali.HawkEye.HawkEvent;
import uk.co.oliwali.HawkEye.HawkEye;
import uk.co.oliwali.HawkEye.util.Util;

public abstract class HawkEyeListener implements Listener {

   public HawkEye plugin;


   public HawkEyeListener(HawkEye HawkEye) {
      this.plugin = HawkEye;
   }

   public final void registerEvents() {
      PluginManager pm = this.plugin.getServer().getPluginManager();
      Method[] methods = this.getClass().getDeclaredMethods();

      for(int i = 0; i < methods.length; ++i) {
         final Method method = methods[i];
         HawkEvent he = (HawkEvent)method.getAnnotation(HawkEvent.class);
         if(he != null) {
            boolean register = false;
            DataType[] params = he.dataType();
            int eventClass = params.length;

            for(int executor = 0; executor < eventClass; ++executor) {
               DataType dt = params[executor];
               if(dt.isLogged()) {
                  register = true;
               }
            }

            if(register) {
               Class[] var11 = method.getParameterTypes();
               if(Event.class.isAssignableFrom(var11[0]) && var11.length == 1) {
                  final Class var12 = var11[0].asSubclass(Event.class);
                  method.setAccessible(true);
                  EventExecutor var13 = new EventExecutor() {
                     public void execute(Listener listener, Event event) throws EventException {
                        try {
                           if(var12.isAssignableFrom(event.getClass())) {
                              Util.debug(Util.DebugLevel.HIGH, "Calling event: " + event.getEventName());
                              method.invoke(listener, new Object[]{event});
                              Util.debug(Util.DebugLevel.HIGH, "Event call complete: " + event.getEventName());
                           }
                        } catch (Exception var4) {
                           throw new EventException(var4.getCause());
                        }
                     }
                  };
                  Util.debug("Registering listener for " + var12.getName());
                  pm.registerEvent(var12, this, he.priority(), var13, this.plugin, he.ignoreCancelled());
               }
            }
         }
      }

   }
}

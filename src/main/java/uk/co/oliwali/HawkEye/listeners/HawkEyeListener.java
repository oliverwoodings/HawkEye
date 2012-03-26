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
import uk.co.oliwali.HawkEye.util.Config;
import uk.co.oliwali.HawkEye.util.Util;
import uk.co.oliwali.HawkEye.util.Util.DebugLevel;

public abstract class HawkEyeListener implements Listener {

	public HawkEye plugin;

	public HawkEyeListener(HawkEye HawkEye) {
		plugin = HawkEye;
	}

	public final void registerEvents() {
		PluginManager pm = plugin.getServer().getPluginManager();

		Method[] methods = this.getClass().getDeclaredMethods();

		for (int i = 0; i < methods.length; i++) {
			final Method method = methods[i];
			final HawkEvent he = method.getAnnotation(HawkEvent.class);
			if (he == null) continue;

			boolean register = false;
			for (DataType dt : he.dataType()) {
				if (Config.isLogged(dt)) register = true;
			}
			if (!register) continue;

			Class<?>[] params = method.getParameterTypes();
			if (!Event.class.isAssignableFrom(params[0]) || params.length != 1) {
				continue;
			}

			final Class<? extends Event> eventClass = params[0].asSubclass(Event.class);
			method.setAccessible(true);

			EventExecutor executor = new EventExecutor() {
				public void execute(Listener listener, Event event) throws EventException {
					try {
						if (!eventClass.isAssignableFrom(event.getClass())) {
							return;
						}

						Util.debug(DebugLevel.HIGH, "Calling event: " + event.getEventName());
						method.invoke(listener, event);
						Util.debug(DebugLevel.HIGH, "Event call complete: " + event.getEventName());
					} catch (Exception ex) {
						throw new EventException(ex.getCause());
					}
				}
			};

			Util.debug("Registering listener for " + eventClass.getName());

			pm.registerEvent(eventClass, this, he.priority(), executor, plugin, he.ignoreCancelled());
		}
	}

}

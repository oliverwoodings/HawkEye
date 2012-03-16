package uk.co.oliwali.HawkEye;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.bukkit.event.EventPriority;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HawkEvent {
	DataType[] dataType();

	boolean ignoreCancelled() default true;

	EventPriority priority() default EventPriority.MONITOR;
}

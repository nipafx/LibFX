package org.codefx.libfx.control.properties;

import org.codefx.libfx.listener.ListenerHandle;

/**
 * This is a {@link ListenerHandle handle} on a {@code ControlPropertyListener}, which can be used to {@link #attach()}
 * and {@link #detach()} it. The {@code ControlPropertyListener} is no type on its own so it is described here.
 * <p>
 * A control property listener listens to the changes in a Control's
 * {@link javafx.scene.control.Control#getProperties() propertyMap} and hands values to a value processor (a
 * {@link java.util.function.Consumer Consumer}) specified during construction.
 * <p>
 * Even though the property map's value type is {@code Object}, the processor might limit the value's type to any other
 * class. If the actual value can not be cast to that type, it is silently ignored.
 * <p>
 * Regardless of whether a value could be cast and processed or not, it will be removed from the map. So if the same
 * value is set repeatedly, the specified value processor is called every time.
 * <p>
 * A listener is best created with the {@link ControlPropertyListenerBuilder}.
 */
public interface ControlPropertyListenerHandle extends ListenerHandle {

	/**
	 * Attaches/adds the listener to the properties map. This immediately processes the key if it is present.
	 */
	@Override
	void attach();

	/**
	 * Detaches/removes the listener from the properties map.
	 */
	@Override
	void detach();

}

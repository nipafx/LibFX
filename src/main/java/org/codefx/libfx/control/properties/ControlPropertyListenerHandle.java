package org.codefx.libfx.control.properties;

import org.codefx.libfx.listener.ListenerHandle;

/**
 * This is a {@link ListenerHandle handle} on a {@code ControlPropertyListener}, which can be used to {@link #attach()}
 * and {@link #detach()} it. The {@code ControlPropertyListener} is no type on its own so it is described here.
 * <p>
 * <h2>ControlPropertyListener</h2> A control property listener listens to the changes in a Control's
 * {@link javafx.scene.control.Control#getProperties() propertyMap}. It is created to listen for a specific key and
 * hands all new values for that key to a value processor (a {@link java.util.function.Consumer Consumer})..
 * <p>
 * Even though the property map's value type is {@code Object}, the processor might limit the value's type to any other
 * class. If the actual value can not be cast to that type, it is silently ignored.
 * <p>
 * Regardless of whether a value could be cast and processed or not, it will be removed from the map. So if the same
 * value is set repeatedly, the specified value processor is called every time.
 * <p>
 * <h2>ControlPropertyListenerHandle</h2> Listener handles are not thread-safe. See {@link ListenerHandle} for details.
 * Additionally, a new value might be processed twice if inserted into a map by another thread while {@link #attach()}
 * is executed. This behavior should not be relied upon and might change (i.e. be fixed) in the future.
 * <p>
 * A listener handle is best created with the {@link ControlPropertyListenerBuilder}.
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

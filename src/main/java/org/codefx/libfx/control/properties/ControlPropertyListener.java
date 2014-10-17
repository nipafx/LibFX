package org.codefx.libfx.control.properties;

/**
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
 * The listener can be detached and reattached by calling the methods provided by this interface.
 * <p>
 * A listener is best created with the {@link ControlPropertyListenerBuilder}.
 */
public interface ControlPropertyListener {

	/**
	 * Attaches/adds the listener to the properties map.
	 */
	void attach();

	/**
	 * Detaches/removes the listener from the properties map.
	 */
	void detach();

}

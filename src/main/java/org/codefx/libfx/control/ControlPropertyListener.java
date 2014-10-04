package org.codefx.libfx.control;

/**
 * A control property listener listens to the changes in a Control's
 * {@link javafx.scene.control.Control#getProperties() propertyMap} and processes values as specified during
 * construction.
 * <p>
 * Regardless whether a value could be processed or not (e.g. because it was of the wrong type), it will be removed from
 * the map. So if the same value is set repeatedly, the specified value processor is called every time.
 * <p>
 * The listener can be detached and reattached by calling the methods provided by this interface.
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

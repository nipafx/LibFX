package org.codefx.libfx.control.properties;

import javafx.collections.ObservableMap;

/**
 * Gives access to a {@link ControlPropertyListenerBuilder}.
 */
public class ControlProperties {

	/**
	 * Creates a builder for a {@link ControlPropertyListener} which observes the specified property map.
	 * <p>
	 * Note that it is often necessary to explicitly specify the type parameter {@code T} like so:
	 * 
	 * <pre>
	 * ControlProperties.&lt;String&gt; on(...)
	 * </pre>
	 *
	 * @param <T>
	 *            the type of values which the listener processes
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @return a {@link ControlPropertyListenerBuilder}
	 */
	public static <T> ControlPropertyListenerBuilder<T> on(ObservableMap<Object, Object> properties) {
		return new ControlPropertyListenerBuilder<T>(properties);
	}

}

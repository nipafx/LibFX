package org.codefx.libfx.control;

import java.util.Objects;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * Abstract superclass to implementations of {@link ControlPropertyListener}. Handles all aspects of listening except
 * the actual processing of the value which is delegated to the implementations.
 */
abstract class AbstractControlPropertyListener implements ControlPropertyListener {

	// #region ATTRIBUTES

	/**
	 * The properties to which the {@link #listener} will be added.
	 */
	private final ObservableMap<Object, Object> properties;

	/**
	 * The listener which will be added to the {@link #properties}.
	 */
	private final MapChangeListener<Object, Object> listener;

	// #end ATTRIBUTES

	// #region CONSTRUCTION

	/**
	 * Creates a new listener.
	 *
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @param key
	 *            the key to which the listener will listen
	 */
	protected AbstractControlPropertyListener(
			ObservableMap<Object, Object> properties, Object key) {

		Objects.requireNonNull(properties, "The argument 'properties' must not be null.");
		Objects.requireNonNull(key, "The argument 'key' must not be null.");

		this.properties = properties;
		this.listener = createListener(properties, key);
	}

	/**
	 * Creates a map listener which checks whether a value was set for the correct key, delegates to
	 * {@link #processValueIfPossible(Object)} if that is so and then removes the key-value-pair from the map.
	 *
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @param key
	 *            the key to which the listener will listen
	 * @return a {@link MapChangeListener}
	 */
	private MapChangeListener<Object, Object> createListener(ObservableMap<Object, Object> properties, Object key) {
		return change -> {
			boolean setForCorrectKey = change.wasAdded() && Objects.equals(key, change.getKey());
			if (setForCorrectKey) {
				processValueIfPossible(change.getValueAdded());
				// remove the value from the properties map
				properties.remove(key);
			}
		};
	}

	// #end CONSTRUCTION

	// #region PROCESS VALUE

	/**
	 * Called when a value was set for the correct key.
	 *
	 * @param value
	 *            the value associated with the key
	 * @return whether the value could be processed
	 */
	protected abstract boolean processValueIfPossible(Object value);

	// #end PROCESS VALUE

	// #region IMPLEMENTATION OF 'ControlPropertyListener'

	@Override
	public void attach() {
		properties.addListener(listener);
	}

	@Override
	public void detach() {
		properties.removeListener(listener);
	}

	// #end IMPLEMENTATION OF 'ControlPropertyListener'

}

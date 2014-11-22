package org.codefx.libfx.control.properties;

import java.util.Objects;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

/**
 * Abstract superclass to implementations of {@link ControlPropertyListenerHandle}. Handles all aspects of listening
 * except the actual processing of the value which is delegated to the implementations.
 */
abstract class AbstractControlPropertyListenerHandle implements ControlPropertyListenerHandle {

	// #region FIELDS

	/**
	 * The properties to which the {@link #listener} will be added.
	 */
	private final ObservableMap<Object, Object> properties;

	/**
	 * The key to which the {@link #listener} listens.
	 */
	private final Object key;

	/**
	 * The listener which will be added to the {@link #properties}.
	 */
	private final MapChangeListener<Object, Object> listener;

	/**
	 * Indicates whether the {@link #listener} is currently attached to the {@link #properties} map.
	 */
	private boolean attached;

	// #end FIELDS

	// #region CONSTRUCTION

	/**
	 * Creates a new listener handle. Initially detached.
	 *
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @param key
	 *            the key to which the listener will listen
	 */
	protected AbstractControlPropertyListenerHandle(
			ObservableMap<Object, Object> properties, Object key) {

		Objects.requireNonNull(properties, "The argument 'properties' must not be null.");
		Objects.requireNonNull(key, "The argument 'key' must not be null.");

		this.properties = properties;
		this.key = key;
		this.listener = createListener(key);
	}

	/**
	 * Creates a map listener which checks whether a value was set for the correct key, delegates to
	 * {@link #processValueIfPossible(Object)} if that is so and then removes the key-value-pair from the map.
	 *
	 * @param key
	 *            the key to which the listener will listen
	 * @return a {@link MapChangeListener}
	 */
	private MapChangeListener<Object, Object> createListener(Object key) {
		return change -> {
			boolean setForCorrectKey = change.wasAdded() && Objects.equals(key, change.getKey());
			if (setForCorrectKey)
				processAndRemoveValue(change.getValueAdded());
		};
	}

	// #end CONSTRUCTION

	// #region PROCESS VALUE

	/**
	 * Processes the specified value for the {@link #key} before removing the pair from the {@link #properties}
	 *
	 * @param value
	 *            the value added to the map
	 */
	private void processAndRemoveValue(Object value) {
		processValueIfPossible(value);
		properties.remove(key);
	}

	/**
	 * Called when a value was set for the correct key.
	 *
	 * @param value
	 *            the value associated with the key
	 * @return whether the value could be processed
	 */
	protected abstract boolean processValueIfPossible(Object value);

	// #end PROCESS VALUE

	// #region IMPLEMENTATION OF 'ControlPropertyListenerHandle'

	@Override
	public void attach() {
		if (attached)
			return;

		attached = true;
		properties.addListener(listener);
		if (properties.containsKey(key))
			processAndRemoveValue(properties.get(key));
	}

	@Override
	public void detach() {
		attached = false;
		properties.removeListener(listener);
	}

	// #end IMPLEMENTATION OF 'ControlPropertyListenerHandle'

}

package org.codefx.libfx.control.properties;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.collections.ObservableMap;

/**
 * A {@link ControlPropertyListener} which uses a {@link Class} instance specified during construction to check whether
 * a value is of the correct type.
 *
 * @param <T>
 *            the type of values which the listener processes
 */
final class TypeCheckingControlPropertyListener<T> extends AbstractControlPropertyListener {

	/**
	 * The type of values which the listener processes.
	 */
	private final Class<T> valueType;

	/**
	 * The user specified processor for values.
	 */
	private final Consumer<? super T> valueProcessor;

	/**
	 * Creates a listener.
	 *
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @param key
	 *            the key to which the listener will listen
	 * @param valueType
	 *            the type of values which the listener processes
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's values
	 */
	TypeCheckingControlPropertyListener(
			ObservableMap<Object, Object> properties, Object key, Class<T> valueType, Consumer<? super T> valueProcessor) {

		super(properties, key);
		Objects.requireNonNull(valueProcessor, "The argument 'valueProcessor' must not be null.");
		Objects.requireNonNull(valueType, "The argument 'valueType' must not be null.");

		this.valueType = valueType;
		this.valueProcessor = valueProcessor;
	}

	@Override
	protected boolean processValueIfPossible(Object value) {
		boolean valueHasCorrectType = valueType.isInstance(value);
		if (valueHasCorrectType) {
			// due to the check above the cast always succeeds
			@SuppressWarnings("unchecked")
			T convertedValue = (T) value;
			valueProcessor.accept(convertedValue);
			return true;
		} else
			return false;
	}

}

package org.codefx.libfx.control;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.collections.ObservableMap;

/**
 * Implementation of {@link ControlPropertyListener} which optimistically casts all values to the expected type. If that
 * does not work, the {@link ClassCastException} is caught and ignored.
 *
 * @param <T>
 *            the type of values which the listener processes
 */
final class CastingControlPropertyListener<T> extends AbstractControlPropertyListener {

	/**
	 * The user specified processor for values.
	 */
	private final Consumer<? super T> valueProcessor;

	/**
	 * Creates a new listener.
	 *
	 * @param properties
	 *            the {@link ObservableMap} holding the properties
	 * @param key
	 *            the key to which the listener will listen
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's values
	 */
	CastingControlPropertyListener(
			ObservableMap<Object, Object> properties, Object key, Consumer<? super T> valueProcessor) {

		super(properties, key);
		Objects.requireNonNull(valueProcessor, "The argument 'valueProcessor' must not be null.");

		this.valueProcessor = valueProcessor;
	}

	@Override
	protected boolean processValueIfPossible(Object value) {
		// give the value to the consumer if it has the correct type
		try {
			// note that this cast does nothing except to calm the compiler
			@SuppressWarnings("unchecked")
			T convertedValue = (T) value;
			// this is where the exception might actually be created
			valueProcessor.accept(convertedValue);
			return true;
		} catch (ClassCastException e) {
			// the value was of the wrong type so it can't be processed by the consumer
			return false;
		}
	}

}

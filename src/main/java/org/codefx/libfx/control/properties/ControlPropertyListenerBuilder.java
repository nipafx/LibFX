package org.codefx.libfx.control.properties;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.collections.ObservableMap;

/**
 * A builder for {@link ControlPropertyListener}.
 * <p>
 * It is best created by calling {@link ControlProperties#on(ObservableMap)} with the control's property map as an
 * argument. It is necessary to set a key (with {@link #forKey(Object)}) and a processor function for the value (with
 * {@link #processValue(Consumer)}) before calling {@link #build()}.
 * <p>
 * Specifying the value's type with {@link #forValueType(Class)} is optional. If it is done, the built listener will use
 * it to check the type of the value before casting it to the type accepted by the value processor. If those types do
 * not match, this prevents {@link ClassCastException} (which would otherwise be caught and silently ignored). If that
 * case occurs frequently, specifying the type to allow the check will improve performance considerably.
 *
 * @param <T>
 *            the type of values which the listener processes
 */
public class ControlPropertyListenerBuilder<T> {

	// #region ATTRIBUTES

	/**
	 * The properties which will be observed.
	 */
	private final ObservableMap<Object, Object> properties;

	/**
	 * The key to which the listener will listen; must no be null by the time {@link #build()} is called.
	 */
	private Object key;

	/**
	 * The processor of the key's values; must no be null by the time {@link #build()} is called.
	 */
	private Consumer<? super T> valueProcessor;

	/**
	 * The type of value which the listener processes
	 */
	private Optional<Class<T>> valueType;

	// #end ATTRIBUTES

	// #region CONSTRUCTION & SETTING VALUES

	/**
	 * Creates a new builder.
	 *
	 * @param properties
	 *            the properties which will be observed by the built listener
	 */
	public ControlPropertyListenerBuilder(ObservableMap<Object, Object> properties) {
		Objects.requireNonNull(properties, "The argument 'properties' must not be null.");
		this.properties = properties;
		this.valueType = Optional.empty();
	}

	/**
	 * Sets the key. This must be called before {@link #build()}.
	 *
	 * @param key
	 *            the key the built listener will observe
	 * @return this builder instance for fluent API
	 */
	public ControlPropertyListenerBuilder<T> forKey(Object key) {
		Objects.requireNonNull(key, "The argument 'key' must not be null.");
		this.key = key;
		return this;
	}

	/**
	 * Sets the type of the values which the built listener will process. Used to type check before calling the
	 * {@link #processValue(Consumer) valueProcessor}.
	 * <p>
	 * This type is optional. See the class comment on {@link ControlPropertyListenerBuilder this builder} for details.
	 *
	 * @param valueType
	 *            the type of values the built listener will process
	 * @return this builder instance for fluent API
	 */
	public ControlPropertyListenerBuilder<T> forValueType(Class<T> valueType) {
		Objects.requireNonNull(valueType, "The argument 'valueType' must not be null.");
		this.valueType = Optional.of(valueType);
		return this;
	}

	/**
	 * Sets the processor for the key's values. This must be called before {@link #build()}.
	 *
	 * @param valueProcessor
	 *            the {@link Consumer} for the key's values
	 * @return this builder instance for fluent API
	 */
	public ControlPropertyListenerBuilder<T> processValue(Consumer<? super T> valueProcessor) {
		Objects.requireNonNull(valueProcessor, "The argument 'valueProcessor' must not be null.");
		this.valueProcessor = valueProcessor;
		return this;
	}

	// #end CONSTRUCTION & SETTING VALUES

	// #region BUILD

	/**
	 * Usability method which calls {@link #build()} and (on the built listener)
	 * {@link ControlPropertyListener#attach() attach()} before returning the new listener.
	 *
	 * @return a {@link ControlPropertyListener}
	 */
	public ControlPropertyListener buildAndAttach() {
		ControlPropertyListener listener = build();
		listener.attach();
		return listener;
	}

	/**
	 * Creates a new property listener according to the arguments specified before.
	 * <p>
	 * Note that this builder is not yet added to the map! This can be done by calling
	 * {@link ControlPropertyListener#attach() attach()} on the returned instance.
	 *
	 * @return a {@link ControlPropertyListener}
	 */
	public ControlPropertyListener build() {
		checkAttributes();

		if (valueType.isPresent())
			return new TypeCheckingControlPropertyListener<T>(properties, key, valueType.get(), valueProcessor);
		else
			return new CastingControlPropertyListener<T>(properties, key, valueProcessor);
	}

	/**
	 * Checks whether the attributes are valid so they can be used to {@link #build()} a listener.
	 */
	private void checkAttributes() {
		if (key == null)
			throw new IllegalStateException("Set a key with 'forKey' before calling 'build'.");
		if (valueProcessor == null)
			throw new IllegalStateException("Set a value processor with 'processValue' before calling 'build'.");
		// value type is optional, so no checks
	}

	// #end BUILD

}

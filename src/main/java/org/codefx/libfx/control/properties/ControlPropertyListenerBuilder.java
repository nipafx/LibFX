package org.codefx.libfx.control.properties;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.collections.ObservableMap;

/**
 * A builder for a {@code ControlPropertyListener}. This is no type on its own as explained in
 * {@link ControlPropertyListenerHandle}. Such a handle is returned by this builder.
 * <p>
 * It is best created by calling {@link ControlProperties#on(ObservableMap)} with the control's property map as an
 * argument. It is necessary to set a key (with {@link #forKey(Object)}) and a processor function for the value (with
 * {@link #processValue(Consumer)}) before calling {@link #buildDetached()}.
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

	// #region FIELDS

	/**
	 * The properties which will be observed.
	 */
	private final ObservableMap<Object, Object> properties;

	/**
	 * The key to which the listener will listen; must no be null by the time {@link #buildDetached()} is called.
	 */
	private Object key;

	/**
	 * The processor of the key's values; must no be null by the time {@link #buildDetached()} is called.
	 */
	private Consumer<? super T> valueProcessor;

	/**
	 * The type of value which the listener processes
	 */
	private Optional<Class<T>> valueType;

	// #end FIELDS

	// #region CONSTRUCTION & SETTING VALUES

	/**
	 * Creates a new builder.
	 *
	 * @param properties
	 *            the properties which will be observed by the built listener
	 */
	private ControlPropertyListenerBuilder(ObservableMap<Object, Object> properties) {
		Objects.requireNonNull(properties, "The argument 'properties' must not be null.");
		this.properties = properties;
		this.valueType = Optional.empty();
	}

	/**
	 * Creates a builder for a {@link ControlPropertyListenerHandle} which observes the specified property map.
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

	/**
	 * Sets the key. This must be called before {@link #buildDetached()}.
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
	 * Creates a new property listener according to the arguments specified before and
	 * {@link ControlPropertyListenerHandle#attach() attaches} it.
	 *
	 * @return a {@link ControlPropertyListenerHandle}
	 * @see ControlPropertyListenerHandle#attach()
	 */
	public ControlPropertyListenerHandle build() {
		ControlPropertyListenerHandle listener = buildDetached();
		listener.attach();
		return listener;
	}

	/**
	 * Creates a new property listener according to the arguments specified before.
	 * <p>
	 * Note that this builder is not yet attached to the map! This can be done by calling
	 * {@link ControlPropertyListenerHandle#attach() attach()} on the returned instance.
	 *
	 * @return a {@link ControlPropertyListenerHandle}
	 * @see #build()
	 * @see ControlPropertyListenerHandle#attach()
	 */
	public ControlPropertyListenerHandle buildDetached() {
		checkFields();

		if (valueType.isPresent())
			return new TypeCheckingControlPropertyListenerHandle<T>(properties, key, valueType.get(), valueProcessor);
		else
			return new CastingControlPropertyListenerHandle<T>(properties, key, valueProcessor);
	}

	/**
	 * Checks whether the fields are valid so they can be used to {@link #buildDetached() build} a listener.
	 */
	private void checkFields() {
		if (key == null)
			throw new IllegalStateException("Set a key with 'forKey' before calling 'build'.");
		if (valueProcessor == null)
			throw new IllegalStateException("Set a value processor with 'processValue' before calling 'build'.");
		// value type is optional, so no checks
	}

	// #end BUILD

}

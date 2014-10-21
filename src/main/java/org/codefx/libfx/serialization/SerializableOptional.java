package org.codefx.libfx.serialization;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * Convenience class to wrap an {@link Optional} for serialization. Instances of this class are immutable.
 * <p>
 * Note that it does not provide any of the methods {@code Optional} has as its only goal is to enable serialization.
 * But it holds a reference to the {@code Optional} which was used to create it (can be accessed with
 * {@link #asOptional()}). This {@code Optional} instance is of course reconstructed on deserialization, so it will not
 * the same as the one specified for its creation.
 * <p>
 * The class can be used as an argument or return type for serialization-based RPC technologies like RMI.
 * <p>
 * There are three ways to use this class to serialize instances which have an optional attribute.
 * <p>
 * <h2>Transform On Serialization</h2> The attribute can be declared as {@code transient Optional<T> optionalAttribute},
 * which will exclude it from serialization.
 * <p>
 * The class then needs to implement custom (de)serialization methods {@code writeObject} and {@code readObject}. They
 * must transform the {@code optionalAttribute} to a {@code SerializableOptional} when writing the object and after
 * reading such an instance transform it back to an {@code Optional}.
 * <p>
 * <h3>Code Example</h3>
 *
 * <pre>
 * private void writeObject(ObjectOutputStream out) throws IOException {
 * 	out.defaultWriteObject();
 * 	out.writeObject(
 * 		SerializableOptional.fromOptional(optionalAttribute));
 * }
 *
 * private void readObject(ObjectInputStream in)
 * 	throws IOException, ClassNotFoundException {
 *
 * 	in.defaultReadObject();
 * 	optionalAttribute =
 * 		((SerializableOptional<T>) in.readObject()).toOptional();
 * }
 * </pre>
 * <p>
 * <h2>Transform On Replace</h2> If the class is serialized using the Serialization Proxy Pattern (see <i>Effective
 * Java</i> by Joshua Bloch, Item 78), the proxy can have an instance of {@link SerializableOptional} to clearly denote
 * the attribute as being optional.
 * <p>
 * In this case, the proxy needs to transform the {@code Optional} to {@code SerializableOptional} in its constructor
 * (using {@link SerializableOptional#fromOptional(Optional)}) and the other way in {@code readResolve()} (with
 * {@link SerializableOptional#asOptional()}).
 * <p>
 * <h2>Transform On Access</h2> The attribute can be declared as {@code SerializableOptional<T> optionalAttribute}. This
 * will include it in the (de)serialization process so it does not need to be customized.
 * <p>
 * But methods interacting with the attribute need to get an {@code Optional} instead. This can easily be done by
 * writing the accessor methods such that they transform the attribute on each access.
 * <p>
 * Note that {@link #asOptional()} simply returns the {@code Optional} which with this instance was created so no
 * constructor needs to be invoked.
 * <p>
 * <h3>Code Example</h3> Note that it is rarely useful to expose an optional attribute via accessor methods. Hence the
 * following are private and for use inside the class.
 *
 * <pre>
 * private Optional<T> getOptionalAttribute() {
 * 	return optionalAttribute.asOptional();
 * }
 *
 * private void setOptionalAttribute(Optional<T> optionalAttribute) {
 * 	this.optionalAttribute = SerializableOptional.fromOptional(optionalAttribute);
 * }
 * </pre>
 *
 * @param <T>
 *            the type of the wrapped value
 */
public final class SerializableOptional<T extends Serializable> implements Serializable {

	// ATTRIBUTES

	@SuppressWarnings("javadoc")
	private static final long serialVersionUID = -652697447004597911L;

	/**
	 * The wrapped {@link Optional}. Note that this attribute is transient so it will not be (de)serializd
	 * automatically.
	 */
	private final Optional<T> optional;

	// CONSTRUCTION AND TRANSFORMATION

	/**
	 * Creates a new instance. Private to enforce use of {@link #fromOptional(Optional)}.
	 *
	 * @param optional
	 *            the wrapped {@link Optional}
	 */
	private SerializableOptional(Optional<T> optional) {
		Objects.requireNonNull(optional, "The argument 'optional' must not be null.");
		this.optional = optional;
	}

	/**
	 * Creates a serializable optional from the specified optional.
	 *
	 * @param <T>
	 *            the type of the wrapped value
	 * @param optional
	 *            the {@link Optional} from which the serializable wrapper will be created
	 * @return a {@link SerializableOptional} which wraps the specified optional
	 */
	public static <T extends Serializable> SerializableOptional<T> fromOptional(Optional<T> optional) {
		return new SerializableOptional<>(optional);
	}

	/**
	 * Creates a serializable optional for the specified value by wrapping it in an {@link Optional}.
	 *
	 * @param <T>
	 *            the type of the wrapped value; must be non-null
	 * @param value
	 *            the value which will be contained in the wrapped {@link Optional}; may be null
	 * @return a {@link SerializableOptional} which wraps the an optional for the specified value
	 * @throws NullPointerException
	 *             if value is null
	 * @see Optional#of(Object)
	 */
	public static <T extends Serializable> SerializableOptional<T> of(T value) throws NullPointerException {
		return new SerializableOptional<>(Optional.of(value));
	}

	/**
	 * Creates a serializable optional for the specified value by wrapping it in an {@link Optional}.
	 *
	 * @param <T>
	 *            the type of the wrapped value
	 * @param value
	 *            the value which will be contained in the wrapped {@link Optional}; may be null
	 * @return a {@link SerializableOptional} which wraps the an optional for the specified value
	 * @see Optional#ofNullable(Object)
	 */
	public static <T extends Serializable> SerializableOptional<T> ofNullable(T value) {
		return new SerializableOptional<>(Optional.ofNullable(value));
	}

	/**
	 * Returns the {@code Optional} instance with which this instance was created.
	 *
	 * @return this instance as an {@link Optional}
	 */
	public Optional<T> asOptional() {
		return optional;
	}

	// SERIALIZATION

	/**
	 * Implements the "write part" of the Serialization Proxy Pattern by creating a proxy which will be serialized
	 * instead of this instance.
	 *
	 * @return the {@link SerializationProxy}
	 */
	private Object writeReplace() {
		return new SerializationProxy<>(this);
	}

	/**
	 * Since this class should never be deserialized directly, this method should not be called. If it is, someone
	 * purposely created a serialization of this class to bypass that mechanism, so throw an exception.
	 */
	@SuppressWarnings({ "static-method", "javadoc", "unused" })
	private void readObject(ObjectInputStream in) throws IOException {
		throw new InvalidObjectException("Serialization proxy expected.");
	}

	/**
	 * The proxy which is serialized instead of an instance of {@link SerializableOptional}.
	 *
	 * @param <T>
	 *            the type of the wrapped value
	 */
	private static class SerializationProxy<T extends Serializable> implements Serializable {

		@SuppressWarnings("javadoc")
		private static final long serialVersionUID = -1326520485869949065L;

		/**
		 * This value is (de)serialized. It comes from the {@link Optional} wrapped by the {@code SerializableOptional}.
		 */
		private final T value;

		/**
		 * Creates a new serialization proxy for the specified serializable optional.
		 *
		 * @param serializableOptional
		 *            the {@link SerializableOptional} for which this proxy is created
		 */
		public SerializationProxy(SerializableOptional<T> serializableOptional) {
			value = serializableOptional.asOptional().orElse(null);
		}

		/**
		 * Implements the "read part" of the Serialization Proxy Pattern by creating a serializable optional for the
		 * deserialized value.
		 *
		 * @return a {@link SerializableOptional}
		 */
		private Object readResolve() {
			return SerializableOptional.ofNullable(value);
		}

	}

}

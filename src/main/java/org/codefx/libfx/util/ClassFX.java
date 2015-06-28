package org.codefx.libfx.util;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Provides utility methods related to {@link Class classes}.
 */
public interface ClassFX {

	/**
	 * Returns an {@link Optional} that contains the specified object if it is a non-null
	 * {@link Class#isInstance(Object) instance} of the specified type or otherwise an empty {@code Optional}.
	 * <p>
	 * This method can be used to combine filter-and-map into one step:
	 *
	 * <pre>
	 * Optional&lt;Object&gt; someOptional = // ...
	 *
	 * // the usual way to get an 'Optional&lt;String&gt;' is to filter, then cast
	 * Optional&lt;String&gt; filterThanCast = someOptional
	 * 	.filter(String.class::isInstance)
	 * 	.map(String.class::cast);
	 *
	 * // with 'castIntoOptional' this is a single step
	 * Optional&lt;String&gt; with = someOptional
	 * 	.flatMap(obj -&gt; castIntoOptional(obj, String.class));
	 * </pre>
	 *
	 * @param <T>
	 *            the type to which the object will be cast
	 * @param object
	 *            the object to cast; may be null in which case an empty {@code Optional} will be returned
	 * @param type
	 *            a token of the type to which the object will be cast
	 * @return an {@code Optional}; will contain the object if it is non-null and of the correct type
	 */
	static <T> Optional<T> castIntoOptional(Object object, Class<T> type) {
		Objects.requireNonNull(type, "The argument 'type' must not be null.");
		if (type.isInstance(object))
			return Optional.of(type.cast(object));
		else
			return Optional.empty();
	}

	/**
	 * Returns a {@link Stream} that contains the specified object if it is a non-null {@link Class#isInstance(Object)
	 * instance} of the specified type or otherwise an empty {@code Stream}.
	 * <p>
	 * This method can be used to combine filter-and-map into one step:
	 *
	 * <pre>
	 * Stream&lt;Object&gt; someStream = // ...
	 *
	 * // the usual way to get a 'Stream&lt;String&gt;' is to filter, then cast
	 * Stream&lt;String&gt; filterThanCast = someStream
	 * 	.filter(String.class::isInstance)
	 * 	.map(String.class::cast);
	 *
	 * // with 'castIntoStream' this is a single step
	 * Stream&lt;String&gt; with = someStream
	 * 	.flatMap(obj -&gt; castIntoStream(obj, String.class));
	 * </pre>
	 *
	 * @param <T>
	 *            the type to which the object will be cast
	 * @param object
	 *            the object to cast; may be null in which case an empty {@code Stream} will be returned
	 * @param type
	 *            a token of the type to which the object will be cast
	 * @return an {@code Stream}; will contain the object if it is non-null and of the correct type
	 */
	static <T> Stream<T> castIntoStream(Object object, Class<T> type) {
		Objects.requireNonNull(type, "The argument 'type' must not be null.");
		if (type.isInstance(object))
			return Stream.of(type.cast(object));
		else
			return Stream.empty();
	}

}

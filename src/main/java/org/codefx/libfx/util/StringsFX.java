package org.codefx.libfx.util;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Provides utility methods related to {@link String}s.
 */
public class StringsFX {

	public static <T> String join(
			Iterable<T> items, Function<T, String> toString,
			String delimiter, String prefix, String suffix, String emptyValue) {
		return join(StreamFX.stream(items), toString, delimiter, prefix, suffix, emptyValue);
	}

	/**
	 * Joins the strings to which the objects from the specified {@link Collection} are mapped.
	 * <p/>
	 * See {@link StreamFX#joining(CharSequence, CharSequence, CharSequence, CharSequence) StreamFX.joining} for how
	 * exactly the strings are joined.
	 *
	 * @param <T>
	 * 		the type of the elements contained in the specified collection
	 * @param items
	 * 		the joined items
	 * @param toString
	 * 		maps the items to strings; could be as simple as {@code Object::toString}
	 * @param delimiter
	 * 		inserted between two values
	 * @param prefix
	 * 		the first characters of the created string; followed by the first value
	 * @param suffix
	 * 		the last characters of the created string; follows the last value
	 * @param emptyValue
	 * 		used if the string is empty
	 *
	 * @return the joined string
	 */
	public static <T> String join(
			Collection<T> items, Function<T, String> toString,
			String delimiter, String prefix, String suffix, String emptyValue) {
		requireNonNull(items, "The argument 'items' must not be null.");
		return join(items.stream(), toString, delimiter, prefix, suffix, emptyValue);
	}

	/**
	 * Joins the strings to which the objects from the specified {@link Stream} are mapped.
	 * <p/>
	 * See {@link StreamFX#joining(CharSequence, CharSequence, CharSequence, CharSequence) StreamFX.joining} for how
	 * exactly the strings are joined.
	 *
	 * @param <T>
	 * 		the type of the elements contained in the specified stream
	 * @param items
	 * 		the joined items
	 * @param toString
	 * 		maps the items to strings; could be as simple as {@code Object::toString}
	 * @param delimiter
	 * 		inserted between two values
	 * @param prefix
	 * 		the first characters of the created string; followed by the first value
	 * @param suffix
	 * 		the last characters of the created string; follows the last value
	 * @param emptyValue
	 * 		used if the string is empty
	 *
	 * @return the joined string
	 */
	public static <T> String join(
			Stream<T> items, Function<T, String> toString,
			String delimiter, String prefix, String suffix, String emptyValue) {
		requireNonNull(items, "The argument 'items' must not be null.");
		requireNonNull(toString, "The argument 'toString' must not be null.");

		// 'StreamFX.joining' tests all other arguments for null

		return items.map(toString).collect(StreamFX.joining(delimiter, prefix, suffix, emptyValue));
	}

}

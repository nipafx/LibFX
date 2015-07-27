package org.codefx.libfx.util;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Provides utility methods related to {@link String}s.
 */
public class StringsFX {

	public static <T> String join(
			Iterable<T> items, Function<T, String> toString,
			String prefix, String delimiter, String suffix, String emptyValue) {
		return join(StreamFX.sequentialStream(items), toString, prefix, delimiter, suffix, emptyValue);
	}

    public static <T> String join(
			Collection<T> items, Function<T, String> toString,
			String prefix, String delimiter, String suffix, String emptyValue) {
		return join(items.stream(), toString, prefix, delimiter, suffix, emptyValue);
	}

    public static <T> String join(
			Stream<T> items, Function<T, String> toString,
			String prefix, String delimiter, String suffix, String emptyValue) {
		return items.map(toString).collect(StreamFX.joining(delimiter, prefix, suffix, emptyValue));
	}

    // TODO consider adding

}

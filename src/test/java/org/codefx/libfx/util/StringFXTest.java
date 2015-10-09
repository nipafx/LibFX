package org.codefx.libfx.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.codefx.libfx.util.StringFXTest.CollectionJoinTest;
import org.codefx.libfx.util.StringFXTest.IterableJoinTest;
import org.codefx.libfx.util.StringFXTest.IteratorJoinTest;
import org.codefx.libfx.util.StringFXTest.StreamJoinTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Tests {@link StringsFX}.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
		IteratorJoinTest.class,
		IterableJoinTest.class,
		CollectionJoinTest.class,
		StreamJoinTest.class
})
@SuppressWarnings("varargs")
public class StringFXTest {

	public static class IteratorJoinTest extends AbstractJoinTest {

		@Override
		protected <T> CallWithElements<T> withElements(T... elements) {
			Iterator<T> iterator = Arrays.asList(elements).iterator();
			return (toString, delimiter, prefix, suffix, emptyValue)
					-> StringsFX.join(iterator, toString, delimiter, prefix, suffix, emptyValue);
		}
	}

	public static class IterableJoinTest extends AbstractJoinTest {

		@Override
		protected <T> CallWithElements<T> withElements(T... elements) {
			Iterable<T> iterable = Arrays.asList(elements);
			return (toString, delimiter, prefix, suffix, emptyValue)
					-> StringsFX.join(iterable, toString, delimiter, prefix, suffix, emptyValue);
		}
	}

	public static class CollectionJoinTest extends AbstractJoinTest {

		@Override
		protected <T> CallWithElements<T> withElements(T... elements) {
			Collection<T> collection = Arrays.asList(elements);
			return (toString, delimiter, prefix, suffix, emptyValue)
					-> StringsFX.join(collection, toString, delimiter, prefix, suffix, emptyValue);
		}
	}

	public static class StreamJoinTest extends AbstractJoinTest {

		@Override
		protected <T> CallWithElements<T> withElements(T... elements) {
			Stream<T> stream = Arrays.asList(elements).stream();
			return (toString, delimiter, prefix, suffix, emptyValue)
					-> StringsFX.join(stream, toString, delimiter, prefix, suffix, emptyValue);
		}
	}

	@SuppressWarnings("unchecked")
	public static abstract class AbstractJoinTest {

		@Test
		public void join_noElements_emptyValue() {
			String emptyValue = "<< EMPTY >>";
			String joined = withNoElements().call(Object::toString, " - ", "<< ", " >>", emptyValue);

			assertThat(joined).isEqualTo(emptyValue);
		}

		@Test
		public void join_oneElement_preAndSuffixedelement() {
			String joined = withElements("single").call(Object::toString, " - ", "<< ", " >>", "<< EMPTY >>");

			assertThat(joined).isEqualTo("<< single >>");
		}

		@Test
		public void join_twoElement_joinedElements() {
			String joined = withElements("first", "second").call(Object::toString, " - ", "<< ", " >>", "<< EMPTY >>");

			assertThat(joined).isEqualTo("<< first - second >>");
		}

		protected abstract <T> CallWithElements<T> withElements(T... elements);

		protected <T> CallWithElements<T> withNoElements() {
			return withElements();
		}

	}

	public interface CallWithElements<T> {

		String call(Function<T, String> toString, String delimiter, String prefix, String suffix, String emptyValue);

	}

}

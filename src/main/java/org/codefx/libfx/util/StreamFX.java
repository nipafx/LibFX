package org.codefx.libfx.util;

import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides utility methods for {@link Stream}s.
 */
public class StreamFX {

	// #begin CREATION

	/**
	 * Creates a sequential stream from the specified iterable.
	 *
	 * @param iterable
	 * 		the iterable to stream over
	 * @param <T>
	 * 		the type of elements returned by the iterator
	 *
	 * @return a new {@link Stream}
	 */
	public static <T> Stream<T> stream(Iterable<T> iterable) {
		requireNonNull(iterable, "The argument 'iterable' must not be null.");
		Spliterator<T> spliterator = iterable.spliterator();
		return StreamSupport.stream(spliterator, false);
	}

	/**
	 * Creates a parallel stream from the specified iterable if its {@link Iterable#spliterator()} is {@link
	 * Spliterator#CONCURRENT concurrent}. Otherwise the stream is sequential.
	 *
	 * @param iterable
	 * 		the iterable to stream over
	 * @param <T>
	 * 		the type of elements returned by the iterator
	 *
	 * @return a new {@link Stream}, parallel if possible
	 */

	public static <T> Stream<T> parallelStream(Iterable<T> iterable) {
		requireNonNull(iterable, "The argument 'iterable' must not be null.");
		Spliterator<T> spliterator = iterable.spliterator();
		return StreamSupport.stream(spliterator, spliterator.hasCharacteristics(Spliterator.CONCURRENT));
	}

	/**
	 * Splits the specified string with the specified regex and returns a stream of the resulting matches.
	 *
	 * @param string
	 * 		the string to split
	 * @param regex
	 * 		the regular expression used to split the string
	 *
	 * @return a stream of matches
	 */
	public static Stream<String> split(String string, String regex) {
		requireNonNull(string, "The argument 'string' must not be null.");
		requireNonNull(regex, "The argument 'regex' must not be null.");
		return Pattern.compile(regex).splitAsStream(string);
	}

	/**
	 * Splits the specified string along new lines and returns a stream of the resulting lines.
	 *
	 * @param string
	 * 		the string to split
	 *
	 * @return a stream of lines
	 */
	public static Stream<String> splitIntoLines(String string) {
		requireNonNull(string, "The argument 'string' must not be null.");
		return new BufferedReader(new StringReader(string)).lines();
	}

	// #end CREATION

	// #begin COLLECTION

	/**
	 * Returns a {@link BinaryOperator} that throws an {@link IllegalStateException} when it is called.
	 * <p/>
	 * This can be used to reduce a stream to its only element, verifying the assumption that it indeed only contains
	 * the one.
	 * <pre>
	 * Stream.empty().reduce(toOnlyElement()); // returns empty Optional
	 * Stream.of("a").reduce(toOnlyElement()); // returns 'Optional["a"]'
	 * Stream.of("a", "b").reduce(toOnlyElement()); // throws 'IllegalStateException'
	 * </pre>
	 * Note that null elements also trigger the exception and that reducing a stream of a single null fails because the
	 * stream API will call {@code Optional.of(null)}:
	 * <pre>
	 * Stream.of("a", null).reduce(toOnlyElement()); // throws 'IllegalStateException'
	 * Stream.of(null).reduce(toOnlyElement()); // throws 'NullPointerException'
	 * </pre>
	 *
	 * @param <T>
	 * 		the type of the stream elements
	 *
	 * @return a {@code BinaryOperator}
	 *
	 * @see #toOnlyNonNullElement()
	 */
	public static <T> BinaryOperator<T> toOnlyElement() {
		return (element, otherElement) -> {
			throw new IllegalStateException("The stream contains more than one element.");
		};
	}

	/**
	 * Returns a {@link BinaryOperator} that throws an {@link IllegalStateException} when it is called with two
	 * non-null arguments.
	 * <p/>
	 * This can be used to reduce a stream to its only non-null element, verifying the assumption that it indeed only
	 * contains the one.
	 * <pre>
	 * Stream.empty().reduce(toOnlyNonNullElement()); // returns empty Optional
	 * Stream.of("a").reduce(toOnlyNonNullElement()); // returns 'Optional["a"]'
	 * Stream.of("a", null).reduce(toOnlyNonNullElement()); // returns 'Optional["a"]'
	 * Stream.of("a", "b").reduce(toOnlyNonNullElement()); // throws 'IllegalStateException'
	 * </pre>
	 * Note that reducing a stream of a single null fails because the stream API will call {@code Optional.of(null)}:
	 * <pre>
	 * Stream.of(null).reduce(toOnlyNonNullElement()); // throws 'NullPointerException'
	 * </pre>
	 *
	 * @param <T>
	 * 		the type of the stream elements
	 *
	 * @return a {@code BinaryOperator}
	 *
	 * @see #toOnlyElement()
	 */
	public static <T> BinaryOperator<T> toOnlyNonNullElement() {
		return (element, otherElement) -> {
			if (element != null && otherElement != null)
				throw new IllegalStateException("The stream contains more than one non-null element.");
			if (element != null)
				return element;
			if (otherElement != null)
				return otherElement;
			return null;
		};
	}

	/**
	 * Creates a {@link Collector} that joins a {@code Stream<String>}.
	 * <p/>
	 * For an empty stream , the collector simply returns {@code emptyValue}, otherwise (without the spaces):
	 * <pre>
	 *     prefix value_1 delimiter value_2 delimiter ... suffix
	 * </pre>
	 * where {@code value_i} is a value from the stream.
	 *
	 * @param delimiter
	 * 		inserted between two values
	 * @param prefix
	 * 		the first characters of the created string; followed by the first value
	 * @param suffix
	 * 		the last characters of the created string; follows the last value
	 * @param emptyValue
	 * 		used if the string is empty
	 *
	 * @return a string created as described above
	 */
	public static Collector<CharSequence, ?, String> joining(
			CharSequence delimiter, CharSequence prefix, CharSequence suffix, CharSequence emptyValue) {
		// constructor of 'StringJoiner' tests all arguments for null
		return joining(() -> new StringJoiner(delimiter, prefix, suffix).setEmptyValue(emptyValue));
	}

	/**
	 * Creates a {@link Collector} from the {@link StringJoiner}s returned by the specified supplier.
	 *
	 * @param stringJoinerSupplier
	 * 		used to create new {@code StringJoiner} instances; is only called more than once if the
	 *
	 * @return a collector using the string joiners created by the specified supplier
	 */
	public static Collector<CharSequence, ?, String> joining(Supplier<StringJoiner> stringJoinerSupplier) {
		requireNonNull(stringJoinerSupplier, "The argument 'stringJoinerSupplier' must not be null.");
		return new FunctionalCollector<>(
				stringJoinerSupplier, StringJoiner::add, StringJoiner::merge, StringJoiner::toString);
	}

	// #end COLLECTION

	/**
	 * An implementation of {@link Collector} which delegates all method calls to functions specified during
	 * construction.
	 *
	 * @param <T>
	 * 		the type of input elements to the reduction operation
	 * @param <A>
	 * 		the mutable accumulation type of the reduction operation (often hidden as an implementation detail)
	 * @param <R>
	 * 		the result type of the reduction operation
	 */
	public static final class FunctionalCollector<T, A, R> implements Collector<T, A, R> {

		private final Supplier<A> supplier;
		private final BiConsumer<A, T> accumulator;
		private final BinaryOperator<A> combiner;
		private final Function<A, R> finisher;
		private final Set<Characteristics> characteristics;

		/**
		 * Creates a new collector.
		 *
		 * @param supplier
		 * 		see {@link Collector#supplier()}
		 * @param accumulator
		 * 		see {@link Collector#accumulator()}
		 * @param combiner
		 * 		see {@link Collector#combiner()}
		 * @param finisher
		 * 		see {@link Collector#finisher()}
		 * @param characteristics
		 * 		see {@link Collector#characteristics()}
		 */
		public FunctionalCollector(
				Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
				Function<A, R> finisher, Set<Characteristics> characteristics) {

			this.supplier = requireNonNull(supplier, "The argument 'supplier' must not be null.");
			this.accumulator = requireNonNull(accumulator, "The argument 'accumulator' must not be null.");
			this.combiner = requireNonNull(combiner, "The argument 'combiner' must not be null.");
			this.finisher = requireNonNull(finisher, "The argument 'finisher' must not be null.");
			this.characteristics = Collections.unmodifiableSet(new HashSet<>(
					requireNonNull(characteristics, "The argument 'characteristics' must not be null.")));
		}

		/**
		 * Creates a new collector.
		 *
		 * @param supplier
		 * 		see {@link Collector#supplier()}
		 * @param accumulator
		 * 		see {@link Collector#accumulator()}
		 * @param combiner
		 * 		see {@link Collector#combiner()}
		 * @param finisher
		 * 		see {@link Collector#finisher()}
		 * @param characteristics
		 * 		see {@link Collector#characteristics()}
		 */
		public FunctionalCollector(
				Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
				Function<A, R> finisher, Characteristics... characteristics) {
			this(supplier, accumulator, combiner, finisher, createCharacteristicsSet(characteristics));
		}

		private static Set<Characteristics> createCharacteristicsSet(Characteristics[] characteristics) {
			Set<Characteristics> characteristicsSet = EnumSet.noneOf(Characteristics.class);
			if (characteristics != null && characteristics.length > 0)
				Arrays.stream(characteristics).forEach(characteristicsSet::add);
			return Collections.unmodifiableSet(characteristicsSet);
		}

		@Override
		public Supplier<A> supplier() {
			return supplier;
		}

		@Override
		public BiConsumer<A, T> accumulator() {
			return accumulator;
		}

		@Override
		public BinaryOperator<A> combiner() {
			return combiner;
		}

		@Override
		public Function<A, R> finisher() {
			return finisher;
		}

		@Override
		public Set<Characteristics> characteristics() {
			return characteristics;
		}

	}

}

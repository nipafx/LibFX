package org.codefx.libfx.util;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codefx.libfx.util.StreamFX.joining;
import static org.codefx.libfx.util.StreamFX.parallelStream;
import static org.codefx.libfx.util.StreamFX.split;
import static org.codefx.libfx.util.StreamFX.splitIntoLines;
import static org.codefx.libfx.util.StreamFX.stream;
import static org.codefx.libfx.util.StreamFX.toOnlyElement;
import static org.codefx.libfx.util.StreamFX.toOnlyNonNullElement;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codefx.libfx.util.StreamFX.FunctionalCollector;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Tests {@link StreamFX}.
 */
public class StreamFXTest {

	// #begin CREATION

	@Test
	public void stream_iterator_streamsListValues() {
		Iterator<Integer> iterator = Arrays.asList(1, 2, 3).iterator();
		List<Integer> collectedStream = stream(iterator).collect(toList());

		assertThat(collectedStream).containsExactly(1, 2, 3);
	}

	@Test
	public void stream_iterable_streamsListValues() {
		List<Integer> iterable = Arrays.asList(1, 2, 3);
		List<Integer> collectedStream = stream(iterable).collect(toList());

		assertThat(collectedStream).containsExactly(1, 2, 3);
	}

	@Test
	public void parallelStream_iterable_streamsListValues() {
		List<Integer> iterable = Arrays.asList(1, 2, 3);
		List<Integer> collectedStream = parallelStream(iterable).collect(toList());

		assertThat(collectedStream).containsExactly(1, 2, 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void parallelStream_isParallel() {
		Spliterator<Integer> spliterator = mock(Spliterator.class);
		when(spliterator.hasCharacteristics(Spliterator.CONCURRENT)).thenReturn(true);
		Iterable<Integer> iterable = mock(Iterable.class);
		when(iterable.spliterator()).thenReturn(spliterator);

		Stream<Integer> stream = parallelStream(iterable);

		assertThat(stream.isParallel()).isTrue();
	}

	@Test
	public void split_emptyString_emptyStream() {
		List<String> stringSplits = split("", ", ").collect(toList());

		assertThat(stringSplits).isEmpty();
	}

	@Test
	public void split_unmatchedString_streamOfMatches() {
		List<String> stringSplits = split("a-lot-of-dashes", ",").collect(toList());

		assertThat(stringSplits).containsExactly("a-lot-of-dashes");
	}

	@Test
	public void split_matchedString_streamOfMatches() {
		List<String> stringSplits = split("a-lot-of-dashes", "-").collect(toList());

		assertThat(stringSplits).containsExactly("a", "lot", "of", "dashes");
	}

	@Test
	public void splitIntoLines_emptyString_emptyStream() {
		List<String> stringSplits = splitIntoLines("").collect(toList());

		assertThat(stringSplits).isEmpty();
	}

	@Test
	public void splitIntoLines_singleLine_singletonStream() {
		List<String> stringSplits = splitIntoLines("only").collect(toList());

		assertThat(stringSplits).containsExactly("only");
	}

	@Test
	public void splitIntoLines_lineString_streamOfLines() {
		List<String> stringSplits = splitIntoLines(format("first%nsecond%nthird")).collect(toList());

		assertThat(stringSplits).containsExactly("first", "second", "third");
	}

	// #end CREATION

	// #begin TO_ONLY_ELEMENT

	@Test
	public void toOnlyElement_emptyStream_returnsEmptyOptional() {
		Optional<Object> noElement = Stream.empty().reduce(toOnlyElement());

		assertThat(noElement).isEmpty();
	}

	@Test
	public void toOnlyElement_streamOfSingleElement_returnsElement() {
		Object element = "the element";
		Optional<Object> onlyElement = Stream.of(element).reduce(toOnlyElement());

		assertThat(onlyElement).contains(element);
	}

	@Test(expected = NullPointerException.class)
	public void toOnlyElement_streamOfASingleNull_throwsNullPointerException() {
		Object[] nullElement = { null };
		// the NullPointerException is thrown by Optional's factory method;
		Stream.of(nullElement).reduce(toOnlyElement());
	}

	@Test(expected = IllegalStateException.class)
	public void toOnlyElement_streamOfMultipleElements_throwsIllegalStateException() {
		Stream.of("a", "b").reduce(toOnlyElement());
	}

	@Test(expected = IllegalStateException.class)
	public void toOnlyElement_streamOfMultipleElementsWithOnlyOneNonNull_throwsIllegalStateException() {
		Stream.of("a", null).reduce(toOnlyElement());
	}

	@Test(expected = IllegalStateException.class)
	public void toOnlyElement_streamOfMultipleNulls_throwsIllegalStateException() {
		Stream.of(null, null).reduce(toOnlyElement());
	}

	// #end TO_ONLY_ELEMENT

	// #begin TO_ONLY_NON_NULL_ELEMENT

	@Test
	public void toOnlyNonNullElement_emptyStream_returnsEmptyOptional() {
		Optional<Object> noElement = Stream.empty().reduce(toOnlyNonNullElement());

		assertThat(noElement).isEmpty();
	}

	@Test
	public void toOnlyNonNullElement_streamOfSingleElement_returnsElement() {
		Object element = "the element";
		Optional<Object> onlyElement = Stream.of(element).reduce(toOnlyNonNullElement());

		assertThat(onlyElement).contains(element);
	}

	@Test(expected = NullPointerException.class)
	public void toOnlyNonNullElement_streamOfASingleNull_throwsNullPointerException() {
		Object[] nullElement = { null };
		// the NullPointerException is thrown by Optional's factory method;
		Stream.of(nullElement).reduce(toOnlyNonNullElement());
	}

	@Test(expected = IllegalStateException.class)
	public void toOnlyNonNullElement_streamOfMultipleNonNullElements_throwsIllegalStateException() {
		Stream.of("a", "b").reduce(toOnlyNonNullElement());
	}

	@Test
	public void toOnlyNonNullElement_streamOfMultipleElementsWithOnlyOneNonNull_returnsElement() {
		Object element = "the element";
		Optional<Object> onlyElement = Stream.of(element, null).reduce(toOnlyNonNullElement());

		assertThat(onlyElement).contains(element);
	}

	@Test(expected = NullPointerException.class)
	public void toOnlyNonNullElement_streamOfMultipleNulls_throwsIllegalStateException() {
		// the NullPointerException is thrown by Optional's factory method;
		Stream.of(null, null).reduce(toOnlyNonNullElement());
	}

	// #end TO_ONLY_NON_NULL_ELEMENT

	// #begin JOINING

	@Test
	public void joiningFromArguments_emptyStream_emptyValue() {
		String emptyValue = "EMPTY";
		String joinedString = Stream.<String> of().collect(joining(", ", "<< ", " >>", emptyValue));

		assertThat(joinedString).isEqualTo(emptyValue);
	}

	@Test
	public void joiningFromArguments_streamOfOneValue_preAndSuffixedValue() {
		String joinedString = Stream.of("SINGLE").collect(joining(" - ", "<< ", " >>", "EMPTY"));

		assertThat(joinedString).isEqualTo("<< SINGLE >>");
	}

	@Test
	public void joiningFromArguments_streamOfTwoValues_joinedValues() {
		String joinedString = Stream.of("ONE", "TWO").collect(joining(" - ", "<< ", " >>", "EMPTY"));

		assertThat(joinedString).isEqualTo("<< ONE - TWO >>");
	}

	@Test
	public void joiningWithJoiner_emptyStream_emptyValue() {
		String emptyValue = "EMPTY";
		StringJoiner joiner = new StringJoiner(", ").setEmptyValue(emptyValue);
		String joinedString = Stream.<String> of().collect(joining(() -> joiner));

		assertThat(joinedString).isEqualTo(emptyValue);
	}

	@Test
	public void joiningWithJoiner_streamOfOneValue_preAndSuffixedValue() {
		StringJoiner joiner = new StringJoiner(" - ", "<< ", " >>").setEmptyValue("<< >>");
		String joinedString = Stream.of("SINGLE").collect(joining(() -> joiner));

		assertThat(joinedString).isEqualTo("<< SINGLE >>");
	}

	@Test
	public void joiningWithJoiner_streamOfTwoValues_joinedValues() {
		StringJoiner joiner = new StringJoiner(" - ", "<< ", " >>").setEmptyValue("<< >>");
		String joinedString = Stream.of("ONE", "TWO").collect(joining(() -> joiner));

		assertThat(joinedString).isEqualTo("<< ONE - TWO >>");
	}

	// #end JOINING

	// #begin FUNCTIONAL_COLLECTOR

	@Test
	@SuppressWarnings("unchecked")
	public void functionalCollector_suppliedFunctionsAreCalled() {
		Supplier<List<Integer>> supplier = mock(Supplier.class);
		BiConsumer<List<Integer>, Integer> accumulator = mock(BiConsumer.class);
		BinaryOperator<List<Integer>> combiner = mock(BinaryOperator.class);
		Function<List<Integer>, List<Integer>> finisher = mock(Function.class);

		Stream.of(1, 2).collect(new FunctionalCollector<>(supplier, accumulator, combiner, finisher));

		verify(supplier, atLeastOnce()).get();
		verify(accumulator, atLeastOnce()).accept(any(), any());
		// it would be nice to verify interaction with 'combiner' but we would have to create a parallel stream for
		// that and it is unclear whether this is reliably possible regardless of the machine running the tests
		verify(finisher, atLeastOnce()).apply(any());
	}

	// #end FUNCTIONAL_COLLECTOR

}

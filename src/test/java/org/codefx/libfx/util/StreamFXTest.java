package org.codefx.libfx.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codefx.libfx.util.StreamFX.toOnlyElement;
import static org.codefx.libfx.util.StreamFX.toOnlyNonNullElement;

public class StreamFXTest {
	
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
	
	
}

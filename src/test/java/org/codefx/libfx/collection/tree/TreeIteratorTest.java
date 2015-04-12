package org.codefx.libfx.collection.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link TreeIterator}.
 */
public class TreeIteratorTest {

	private TreeIterationStrategy<String> strategy;

	private TreeIterator<String> treeIterator;

	@Before
	@SuppressWarnings({ "unchecked", "javadoc" })
	public void setUp() {
		strategy = mock(TreeIterationStrategy.class);
		treeIterator = new TreeIterator<>(strategy);
	}

	// create

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void create_nullStrategy_throwsNullPointerExeption() throws Exception {
		@SuppressWarnings("unused")
		TreeIterator<?> treeIterator = new TreeIterator<>(null);
	}

	// has next

	@Test
	@SuppressWarnings("javadoc")
	public void hasNext_strategyReturnsEmptyOptional_returnsFalse() throws Exception {
		when(strategy.goToNextNode()).thenReturn(Optional.empty());

		boolean hasNext = treeIterator.hasNext();

		assertFalse(hasNext);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void hasNext_strategyReturnsNonEmptyOptional_returnsTrue() throws Exception {
		when(strategy.goToNextNode()).thenReturn(Optional.of("A"));

		boolean hasNext = treeIterator.hasNext();

		assertTrue(hasNext);
	}

	// next

	@Test(expected = NoSuchElementException.class)
	@SuppressWarnings("javadoc")
	public void next_strategyReturnsEmptyOptional_throwsNoSuchElementException() throws Exception {
		when(strategy.goToNextNode()).thenReturn(Optional.empty());

		treeIterator.next();
	}

	@Test
	@SuppressWarnings("javadoc")
	public void next_strategyReturnsNonEmptyOptional_returnsThatElement() throws Exception {
		String element = "element";
		when(strategy.goToNextNode()).thenReturn(Optional.of(element));

		String nextElement = treeIterator.next();

		assertSame(element, nextElement);
	}

	// calls to strategy

	@Test
	@SuppressWarnings("javadoc")
	public void create_noCallsToGoToNext() throws Exception {
		verifyZeroInteractions(strategy);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void hasNext_manyCallsToExhaustedStrategy_callsGoToNextOnlyOnce() throws Exception {
		// strategy is "exhausted" so it always returns 'empty'
		when(strategy.goToNextNode()).thenReturn(Optional.empty());

		treeIterator.hasNext();
		treeIterator.hasNext();
		treeIterator.hasNext();

		verify(strategy, times(1)).goToNextNode();
	}

	@Test
	@SuppressWarnings("javadoc")
	public void hasNext_manyCallsToNonExhaustedStrategy_callsGoToNextOnlyOnce() throws Exception {
		// strategy is not "exhausted" so it returns non-empty Optionals
		when(strategy.goToNextNode()).thenReturn(Optional.of("A"));

		treeIterator.hasNext();
		treeIterator.hasNext();
		treeIterator.hasNext();

		verify(strategy, times(1)).goToNextNode();
	}

	@Test
	@SuppressWarnings("javadoc")
	public void hasNextThenNext_callsGoToNextOnlyOnce() throws Exception {
		when(strategy.goToNextNode()).thenReturn(Optional.of("A"));

		treeIterator.hasNext();
		treeIterator.hasNext();
		treeIterator.next();

		verify(strategy).goToNextNode();
	}

	@Test
	@SuppressWarnings("javadoc")
	public void next_manyCalls_callsGoToNextExactlyAsOften() throws Exception {
		when(strategy.goToNextNode()).thenReturn(Optional.of("A"));

		treeIterator.next();
		treeIterator.next();
		treeIterator.next();

		verify(strategy, times(3)).goToNextNode();
	}

	@Test
	@SuppressWarnings("javadoc")
	public void hasNextThenNext_manyCalls_callsGoToNextCorrectly() throws Exception {
		when(strategy.goToNextNode()).thenReturn(Optional.of("A"));

		verify(strategy, times(0)).goToNextNode();
		treeIterator.next(); // must move forward so calls 'goToNext'
		verify(strategy, times(1)).goToNextNode();
		treeIterator.hasNext(); // must move forward so calls 'goToNext'
		verify(strategy, times(2)).goToNextNode();
		treeIterator.hasNext(); // no additional information required so no more calls
		treeIterator.hasNext();
		treeIterator.hasNext();
		verify(strategy, times(2)).goToNextNode();
		treeIterator.next(); // no additional information required so no more calls
		verify(strategy, times(2)).goToNextNode();
	}

}

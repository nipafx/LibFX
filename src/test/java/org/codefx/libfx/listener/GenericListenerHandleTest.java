package org.codefx.libfx.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.function.BiConsumer;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the class {@link GenericListenerHandle}.
 */
public class GenericListenerHandleTest {

	// #region INSTANCES

	/**
	 * The tested handle.
	 */
	private GenericListenerHandle<Object, Object> handle;

	/**
	 * The observable on which the {@link #handle} operates.
	 */
	private Object observable;

	/**
	 * The listener on which the {@link #handle} operates.
	 */
	private Object listener;

	/**
	 * The function which adds the listener to the observable. This is a mock so calls can be verified.
	 */
	private BiConsumer<Object, Object> add;

	/**
	 * The function which adds the listener to the observable. This is a mock so calls can be verified.
	 */
	private BiConsumer<Object, Object> remove;

	// #end INSTANCES

	// #region SETUP

	/**
	 * Creates the tested instances.
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		add = mock(BiConsumer.class);
		remove = mock(BiConsumer.class);
		observable = "observable";
		listener = "listner";

		handle = new GenericListenerHandle<Object, Object>(observable, listener, add, remove);
	}

	// #end SETUP

	// #region TESTS

	/**
	 * Tests whether the construction of the handle does not cause any calls to {@link #add} and {@link #remove}.
	 */
	@Test
	public void testNoCallsToAddAndRemoveOnConstruction() {
		verifyZeroInteractions(add, remove);
	}

	/**
	 * Tests whether the a call to {@link ListenerHandle#detach() detach()} after construction does not cause any calls
	 * to {@link #add} and {@link #remove}.
	 */
	@Test
	public void testDetachAfterConstruction() {
		handle.detach();

		verifyZeroInteractions(add, remove);
	}

	/**
	 * Tests whether the first call to {@link ListenerHandle#attach() attach()} correctly calls {@link #add}.
	 */
	@Test
	public void testAttachAfterConstruction() {
		handle.attach();

		verify(add, times(1)).accept(observable, listener);
		verifyNoMoreInteractions(add);
		verifyZeroInteractions(remove);
	}

	/**
	 * Tests whether calling {@link ListenerHandle#attach() attach()} multiple times in a row calls {@link #add} only
	 * once.
	 */
	@Test
	public void testMultipleAttach() {
		handle.attach();
		handle.attach();
		handle.attach();

		verify(add, times(1)).accept(observable, listener);
		verifyNoMoreInteractions(add);
		verifyZeroInteractions(remove);
	}

	/**
	 * Tests whether calling {@link ListenerHandle#detach() detach()} correctly calls {@link #remove}.
	 */
	@Test
	public void testDetach() {
		handle.attach();
		handle.detach();

		// the order of those calls is not verified here;
		// but if it would not match the intuition (first 'add', then 'remove'), a more specific test above would fail
		verify(add, times(1)).accept(observable, listener);
		verify(remove, times(1)).accept(observable, listener);
		verifyNoMoreInteractions(add, remove);
	}

	/**
	 * Tests whether calling {@link ListenerHandle#detach() detach()} multiple times in a row calls {@link #remove} only
	 * once.
	 */
	@Test
	public void testMultipleDetach() {
		handle.attach();
		handle.detach();
		handle.detach();
		handle.detach();

		verify(add, times(1)).accept(observable, listener);
		verify(remove, times(1)).accept(observable, listener);
		verifyZeroInteractions(remove);
	}

	/**
	 * Tests whether reattaching calls {@link #add} twice.
	 */
	@Test
	public void testReattach() {
		handle.attach();
		handle.detach();
		handle.attach();

		// the order of those calls is not verified here;
		// but if it would not match the intuition ('add', 'remove', 'add'), a more specific test above would fail
		verify(add, times(2)).accept(observable, listener);
		verify(remove, times(1)).accept(observable, listener);
		verifyNoMoreInteractions(add, remove);
	}

	// #end TESTS

}

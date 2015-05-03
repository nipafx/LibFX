package org.codefx.libfx.listener.handle;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.BiConsumer;

import org.junit.Test;

/**
 * Tests the class {@link ListenerHandleBuilder}.
 */
public class ListenerHandleBuilderTest {

	/**
	 * A not null {@link Object} which can be used to represent an observable or a listener.
	 */
	private static final Object NOT_NULL = new Object();

	/**
	 * A not null {@link BiConsumer} which can be used to represent an add or a remove function.
	 */
	private static final BiConsumer<Object, Object> NOT_NULL_CONSUMER = (o, l) -> { /* do nothing */};

	// #begin TESTS

	// construction

	/**
	 * Tests whether the factory method can not be called with a null observable.
	 */
	@Test(expected = NullPointerException.class)
	public void testConstructorWithNullObservable() {
		ListenerHandleBuilder.from(null, NOT_NULL);
	}

	/**
	 * Tests whether the factory method can not be called with a null listener.
	 */
	@Test(expected = NullPointerException.class)
	public void testConstructorWithNullListener() {
		ListenerHandleBuilder.from(NOT_NULL, null);
	}

	/**
	 * Tests whether the factory method returns a non-null builder.
	 */
	@Test
	public void testSuccessfulConstruction() {
		ListenerHandleBuilder<?, ?> builder = ListenerHandleBuilder.from(NOT_NULL, NOT_NULL);
		assertNotNull(builder);
	}

	// setting values

	/**
	 * Tests whether the builder does not accepts a null add function.
	 */
	@Test(expected = NullPointerException.class)
	public void testSetNullAdd() {
		ListenerHandleBuilder<?, ?> builder = ListenerHandleBuilder.from(NOT_NULL, NOT_NULL);
		builder.onAttach(null);
	}

	/**
	 * Tests whether the builder does not accepts a null remove function.
	 */
	@Test(expected = NullPointerException.class)
	public void testSetNullRemove() {
		ListenerHandleBuilder<?, ?> builder = ListenerHandleBuilder.from(NOT_NULL, NOT_NULL);
		builder.onDetach(null);
	}

	// build

	/**
	 * Tests whether {@link ListenerHandleBuilder#buildAttached() build} can not be called when neither
	 * {@link ListenerHandleBuilder#onAttach(BiConsumer) onAttach} nor
	 * {@link ListenerHandleBuilder#onDetach(BiConsumer) onDetach} were called.
	 */
	@Test(expected = IllegalStateException.class)
	public void testNotCallingOnAttachAndOnDetachBeforeBuild() {
		ListenerHandleBuilder
				.from(NOT_NULL, NOT_NULL)
				.buildAttached();
	}

	/**
	 * Tests whether {@link ListenerHandleBuilder#buildAttached() build} can not be called when
	 * {@link ListenerHandleBuilder#onAttach(BiConsumer) onAttach} was not called.
	 */
	@Test(expected = IllegalStateException.class)
	public void testNotCallingOnAttachBeforeBuild() {
		ListenerHandleBuilder
				.from(NOT_NULL, NOT_NULL)
				.onDetach(NOT_NULL_CONSUMER)
				.buildAttached();
	}

	/**
	 * Tests whether {@link ListenerHandleBuilder#buildAttached() build} can not be called when
	 * {@link ListenerHandleBuilder#onDetach(BiConsumer) onDetach} was not called.
	 */
	@Test(expected = IllegalStateException.class)
	public void testNotCallingOnDetachBeforeBuild() {
		ListenerHandleBuilder
				.from(NOT_NULL, NOT_NULL)
				.onAttach(NOT_NULL_CONSUMER)
				.buildAttached();
	}

	/**
	 * Tests whether the built {@link ListenerHandle} is not null.
	 */
	@Test
	public void testSuccessfulBuild() {
		ListenerHandle handle = ListenerHandleBuilder
				.from(NOT_NULL, NOT_NULL)
				.onAttach(NOT_NULL_CONSUMER)
				.onDetach(NOT_NULL_CONSUMER)
				.buildAttached();

		assertNotNull(handle);
	}

	// correct arguments for 'add' and 'remove' functions

	/**
	 * Tests whether the add function is called with the correct arguments.
	 */
	@Test
	public void testAddCalledWithCorrectArguments() {
		// setup
		@SuppressWarnings("unchecked")
		BiConsumer<Object, Object> add = mock(BiConsumer.class);
		ListenerHandle handle = ListenerHandleBuilder
				.from(NOT_NULL, NOT_NULL)
				.onAttach(add)
				.onDetach(NOT_NULL_CONSUMER)
				.buildDetached();

		// trigger a call to 'add'
		handle.attach();

		// verify
		verify(add, times(1)).accept(NOT_NULL, NOT_NULL);
		verifyNoMoreInteractions(add);
	}

	/**
	 * Tests whether the remove function is called with the correct arguments.
	 */
	@Test
	public void testRemoveCalledWithCorrectArguments() {
		// setup
		@SuppressWarnings("unchecked")
		BiConsumer<Object, Object> remove = mock(BiConsumer.class);
		ListenerHandle handle = ListenerHandleBuilder
				.from(NOT_NULL, NOT_NULL)
				.onAttach(NOT_NULL_CONSUMER)
				.onDetach(remove)
				.buildDetached();

		// trigger a call to 'remove'
		handle.attach();
		handle.detach();

		// verify
		verify(remove, times(1)).accept(NOT_NULL, NOT_NULL);
		verifyNoMoreInteractions(remove);
	}

	// #end TESTS

}

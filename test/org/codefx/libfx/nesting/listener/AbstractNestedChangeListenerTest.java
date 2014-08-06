package org.codefx.libfx.nesting.listener;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of {@link NestedChangeListener}.
 */
public abstract class AbstractNestedChangeListenerTest {

	// #region INSTANCES USED FOR TESTING

	/**
	 * The nesting's inner observable.
	 */
	private StringProperty innerObservable;

	/**
	 * The nesting to which the listener is added.
	 */
	private NestingAccess.EditableNesting<StringProperty> nesting;

	/**
	 * The added listener. This {@link ChangeListener} will be mocked to verify possible invocations.
	 */
	private ChangeListener<String> listener;

	/**
	 * The tested nested listener, which adds the {@link #listener} to the {@link #nesting}.
	 */
	private NestedChangeListener<String> nestedListener;

	//#end INSTANCES USED FOR TESTING

	/**
	 * Creates a new instance of {@link #nesting}, {@link #listener} and {@link #nestedListener}.
	 */
	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		innerObservable = new SimpleStringProperty("initial value");
		nesting = NestingAccess.EditableNesting.createWithInnerObservable(innerObservable);
		listener = mock(ChangeListener.class);
		nestedListener = createNestedListener(nesting, listener);
	}

	// #region TESTS

	/**
	 * Tests whether the properties the tested nested listener owns have the correct bean.
	 */
	@Test
	public void testPropertyBean() {
		assertSame(nestedListener, nestedListener.innerObservablePresentProperty().getBean());
	}

	/**
	 * Tests whether the {@link #nestedListener} and {@link #listener} are in the correct state after construction.
	 */
	@Test
	public void testStateAfterConstruction() {
		assertTrue(nestedListener.isInnerObservablePresent());
		// the listener must not have been called
		verifyZeroInteractions(listener);
	}

	/**
	 * Tests whether the listener is correctly invoked when the nesting's observable changes its value.
	 */
	@Test
	public void testChangingValue() {
		innerObservable.set("new value");

		// assert that 'changed' was called once and with the right arguments
		verify(listener, times(1)).changed(innerObservable, "initial value", "new value");
		verifyNoMoreInteractions(listener);
	}

	/**
	 * Tests whether no listener invocation occurs when the nesting's inner observable is changed.
	 */
	@Test
	public void testChangingObservable() {
		StringProperty newObservable = new SimpleStringProperty("new observable's initial value");
		setNestingObservable(nesting, newObservable);

		assertTrue(nestedListener.isInnerObservablePresent());
		verifyZeroInteractions(listener);
	}

	/**
	 * Tests whether no listener invocation occurs when the nesting's inner observable is changed to null.
	 */
	@Test
	public void testChangingObservableToNull() {
		setNestingObservable(nesting, null);

		assertFalse(nestedListener.isInnerObservablePresent());
		verifyZeroInteractions(listener);
	}

	/**
	 * Tests whether the listener is correctly invoked when the nesting's new observable gets a new value.
	 */
	@Test
	public void testChangingNewObservablesValue() {
		// set a new observable ...
		StringProperty newObservable = new SimpleStringProperty("new observable's initial value");
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertEquals(newObservable, getNestingObservable(nesting));

		// ... and change its value
		newObservable.setValue("new observable's new value");

		// assert that the listener was invoked once and with the new observable's old and new value
		verify(listener, times(1)).changed(newObservable,
				"new observable's initial value", "new observable's new value");
		verifyNoMoreInteractions(listener);
	}

	/**
	 * Tests whether the listener is not invoked when the nesting's old observable gets a new value.
	 */
	@Test
	public void testChangingOldObservablesValue() {
		// set a new observable ...
		StringProperty newObservable = new SimpleStringProperty("new observable's initial value");
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertEquals(newObservable, getNestingObservable(nesting));

		// ... and change the old observable's value
		innerObservable.setValue("intial observable's new value");

		// assert the listener was not invoked
		verifyZeroInteractions(listener);
	}

	//#end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Creates a new nested listener from the specified nesting and listener.
	 *
	 * @param nesting
	 *            the {@link Nesting} to which the listener will be added
	 * @param listener
	 *            the {@link ChangeListener} which will be added to the nesting
	 * @return a new {@link NestedChangeListener}
	 */
	protected abstract NestedChangeListener<String> createNestedListener(
			EditableNesting<? extends ObservableValue<String>> nesting, ChangeListener<String> listener);

	//end ABSTRACT METHODS

}

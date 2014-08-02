package org.codefx.libfx.nesting;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;

import org.codefx.libfx.nesting.testhelper.NestingAccess;
import org.junit.Test;

/**
 * Tests the class {@link NestingObserver}.
 */
public class NestingObserverTest {

	// #region INSTANCES USED FOR TESTING

	/**
	 * The nesting's initial {@link Nesting#innerObservableProperty() innerObservable}.
	 */
	private Observable initialInnerObservable;

	/**
	 * The observed {@link Nesting}.
	 */
	private NestingAccess.EditableNesting<Observable> nesting;

	/**
	 * The {@link MethodCallVerifier} which called when the nesting changes.
	 */
	private MethodCallVerifier verifier;

	//#end INSTANCES USED FOR TESTING

	// #region INITIALIZATION

	/**
	 * Creates instances of {@link #initialInnerObservable}, {@link #nesting}, {@link #verifier} and a
	 * {@link NestingObserver} which uses the latter two.
	 *
	 * @param initiallyPresent
	 *            indicates whether the created {@link #nesting} will have the {@link #initialInnerObservable} set or
	 *            whether it will be missing
	 * @param resetMock
	 *            indicates whether the mocked {@link #verifier} will be reset after the nesting observer is created
	 */
	public void setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT initiallyPresent, RESET_MOCK resetMock) {
		// create a nesting with the initial inner observable and mock a method call verifier
		initialInnerObservable = new SimpleStringProperty();
		if (initiallyPresent.toBoolean())
			nesting = NestingAccess.EditableNesting.createWithInnerObservable(initialInnerObservable);
		else
			nesting = NestingAccess.EditableNesting.createWithInnerObservableNull();
		verifier = mock(MethodCallVerifier.class);

		// create a nesting observer for the nesting which calls the verifier's methods
		NestingObserver
				.observe(nesting)
				.withOldInnerObservable(verifier::oldInnerObservableMethod)
				.withNewInnerObservable(verifier::newInnerObservableMethod)
				.whenInnerObservableChanges(verifier::innerObservableChangesMethod)
				.build();

		// creating the observer already called some methods; reset the mock to not confuse the counts
		if (resetMock.toBoolean())
			reset(verifier);
	}

	//#end INITIALIZATION

	// #region TESTS

	/**
	 * Tests whether the observer's construction leads to a correct initialization of the methods if the nesting's
	 * initial inner observable is present.
	 */
	@Test
	public void testInitialCallsAfterConstructionWithPresentObservable() {
		// create a nesting with the initial inner observable and mock a call verifier
		setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT.YES, RESET_MOCK.NO);

		/*
		 * During its creation the observer must call some of the methods for a first time to initialize the state to
		 * match the nesting's state. There is never an old observable and in this case there is a new observable
		 * (namely 'initialInnerObservable').
		 */
		verify(verifier, times(1)).newInnerObservableMethod(initialInnerObservable);
		verify(verifier, times(1)).innerObservableChangesMethod(false, true);
		verifyNoMoreInteractions(verifier);
	}

	/**
	 * Tests whether the observer's construction leads to a correct initialization of the methods if the nesting's
	 * initial inner observable is missing.
	 */
	@Test
	public void testInitialCallsAfterConstructionWithMissingObservable() {
		// create a nesting with the no inner observable and mock a call verifier
		setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT.NO, RESET_MOCK.NO);

		/*
		 * During its creation the observer must call some of the methods for a first time to initialize the state to
		 * match the nesting's state. There is never an old observable and in this case there also no new observable.
		 */
		verify(verifier, times(1)).innerObservableChangesMethod(false, false);
		verifyNoMoreInteractions(verifier);
	}

	/**
	 * Tests whether replacing a missing inner observable with another missing one leads to correct method calls.
	 */
	@Test
	public void testMissingPresentWithMissingInnerObservable() {
		// create a nesting without an inner observable and mock a call verifier, which is then reset
		setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT.NO, RESET_MOCK.YES);

		// set a new inner observable
		nesting.setInnerObservable(Optional.empty());

		// since both the old and the new observable are missing, the nesting did not really change,
		// so no method should have been called
		verifyZeroInteractions(verifier);
	}

	/**
	 * Tests whether replacing a missing inner observable with a present one leads to correct method calls.
	 */
	@Test
	public void testReplacingMissingWithPresentInnerObservable() {
		// create a nesting without an inner observable and mock a call verifier, which is then reset
		setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT.NO, RESET_MOCK.YES);

		// set a new inner observable
		Observable newInnerObservable = new SimpleStringProperty();
		nesting.setInnerObservable(Optional.of(newInnerObservable));

		// since only the new observable is present, the "changes"- and the "new"-method must be called:
		verify(verifier, times(1)).newInnerObservableMethod(newInnerObservable);
		verify(verifier, times(1)).innerObservableChangesMethod(false, true);
		verifyNoMoreInteractions(verifier);
	}

	/**
	 * Tests whether replacing a present inner observable with a missing one leads to correct method calls.
	 */
	@Test
	public void testReplacingPresentWithMissingInnerObservable() {
		// create a nesting with the initial inner observable and mock a call verifier, which is then reset
		setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT.YES, RESET_MOCK.YES);

		// set a new inner observable
		nesting.setInnerObservable(Optional.empty());

		// since only the old observable is present, the "changes"- and the "old"-method must be called:
		verify(verifier, times(1)).oldInnerObservableMethod(initialInnerObservable);
		verify(verifier, times(1)).innerObservableChangesMethod(true, false);
		verifyNoMoreInteractions(verifier);
	}

	/**
	 * Tests whether replacing a present inner observable with another present one leads to correct method calls.
	 */
	@Test
	public void testReplacingPresentWithPresentInnerObservable() {
		// create a nesting with the initial inner observable and mock a call verifier, which is then reset
		setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT.YES, RESET_MOCK.YES);

		// set a new inner observable
		Observable newInnerObservable = new SimpleStringProperty();
		nesting.setInnerObservable(Optional.of(newInnerObservable));

		// since both the old and the new observable are present, all methods have to be called:
		verify(verifier, times(1)).oldInnerObservableMethod(initialInnerObservable);
		verify(verifier, times(1)).newInnerObservableMethod(newInnerObservable);
		verify(verifier, times(1)).innerObservableChangesMethod(true, true);
		verifyNoMoreInteractions(verifier);
	}

	//#end TESTS

	// #region INNER CLASSES

	/**
	 * The {@link NestingObserver NestingObservers} created in this test call this class' methods when the nesting
	 * changes. The methods actually don't do anything; this class is only meant to be mocked so the correct interaction
	 * with the mock can be asserted with Mockito.
	 */
	@SuppressWarnings("unused")
	private static class MethodCallVerifier {

		/**
		 * Called when the {@link Nesting#innerObservableProperty() innerObservable} changes and the old inner
		 * observable was present.
		 *
		 * @param oldInnerObservable
		 *            the old inner observable
		 */
		public void oldInnerObservableMethod(Object oldInnerObservable) {
			// method is only used in mocking to count calls
		}

		/**
		 * Called when the {@link Nesting#innerObservableProperty() innerObservable} changes and the new inner
		 * observable is present.
		 *
		 * @param newInnerObservable
		 *            the new inner observable
		 */
		public void newInnerObservableMethod(Object newInnerObservable) {
			// method is only used in mocking to count calls
		}

		/**
		 * Called when the inner observable changes.
		 *
		 * @param oldInnerObservablePresent
		 *            indicates whether the old inner observable was present
		 * @param newInnerObservablePresent
		 *            indicates whether the new inner observable is present
		 */
		public void innerObservableChangesMethod(Boolean oldInnerObservablePresent, Boolean newInnerObservablePresent) {
			// method is only used in mocking to count calls
		}

	}

	/**
	 * Indicates whether {@link NestingObserverTest#setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT, RESET_MOCK)
	 * setUpObservation} will create a nesting whose inner observable is initially present or not.
	 */
	private enum INNER_OBSERVABLE_INITIALLY_PRESENT {

		/**
		 * The nesting's inner observable will initially be present.
		 */
		YES,

		/**
		 * The nesting's inner observable will initially be missing.
		 */
		NO;

		/**
		 * Returns a boolean value corresponding to this instances value.
		 *
		 * @return <code>true</code> if this is {@link #YES}; <code>false</code> if this is {@link #NO}
		 */
		public boolean toBoolean() {
			switch (this) {
				case YES:
					return true;
				case NO:
					return false;
				default:
					throw new UnsupportedOperationException();
			}
		}
	}

	/**
	 * Indicates whether {@link NestingObserverTest#setUpObservation(INNER_OBSERVABLE_INITIALLY_PRESENT, RESET_MOCK)
	 * setUpObservation} will reset the mocked {@link NestingObserverTest#verifier verifier} after the observation was
	 * set up or not.
	 */
	private enum RESET_MOCK {

		/**
		 * The verifier will be reset.
		 */
		YES,

		/**
		 * The verifier will not be reset.
		 */
		NO;

		/**
		 * Returns a boolean value corresponding to this instances value.
		 *
		 * @return <code>true</code> if this is {@link #YES}; <code>false</code> if this is {@link #NO}
		 */
		public boolean toBoolean() {
			switch (this) {
				case YES:
					return true;
				case NO:
					return false;
				default:
					throw new UnsupportedOperationException();
			}
		}
	}

	//#end INNER CLASSES

}

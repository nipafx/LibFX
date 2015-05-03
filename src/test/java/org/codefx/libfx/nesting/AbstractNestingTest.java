package org.codefx.libfx.nesting;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.junit.Assert.assertSame;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of nestings. By implementing the few abstract methods subclasses can run all tests which
 * apply to all implementations.
 *
 * @param <OO>
 *            the type of the nesting hierarchy's outer observable
 * @param <IO>
 *            the type the nesting hierarchy's inner observable which is also the type wrapped by the nesting
 */
public abstract class AbstractNestingTest<OO extends Observable, IO extends Observable> {

	// #begin INSTANCES USED FOR TESTING

	/**
	 * The outer observable of the nesting hierarchy contained in {@link #nesting}.
	 */
	protected OO outerObservable;

	/**
	 * The nesting which is tested.
	 */
	protected Nesting<IO> nesting;

	//#end INSTANCES USED FOR TESTING

	/**
	 * Creates a new instance of {@link #nesting} and {@link #outerObservable}.
	 */
	@Before
	public void setUp() {
		outerObservable = createNewNestingHierarchy();
		nesting = createNewNestingFromOuterObservable(outerObservable);
	}

	// #begin TESTS

	// construction

	/**
	 * Tests whether creating a nesting with on a null outer observable throws an exception.
	 */
	@Test(expected = NullPointerException.class)
	public void testExceptionWhenNullObservable() {
		nesting = createNewNestingFromOuterObservable(null);
	}

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservableProperty() innerObservable} property contains
	 * the correct observable, which is the {@link #outerObservable}'s inner observable.
	 */
	@Test
	public void testCorrectAfterConstruction() {
		assertSame(getNestingObservable(nesting), getInnerObservable(outerObservable));
	}

	//#end TESTS

	// #begin ABSTRACT METHODS

	/**
	 * Creates a new nesting hierarchy and returns its outer observable. All returned instances must be new for each
	 * call.
	 *
	 * @return an {@link ObservableValue} containing the outer value of a nesting hierarchy
	 */
	protected abstract OO createNewNestingHierarchy();

	/**
	 * Creates a new nesting from the specified outer observable.
	 *
	 * @param outerObservable
	 *            the {@link ObservableValue} which contains the nesting hierarchy's outer value
	 * @return a new {@link Nesting} instance
	 */
	protected abstract Nesting<IO> createNewNestingFromOuterObservable(OO outerObservable);

	/**
	 * Returns the specified outer observable's inner observable which is represented by the nesting created by
	 * {@link #createNewNestingFromOuterObservable(Observable) createNewNestingFromOuterObservable}.
	 *
	 * @param outerObservable
	 *            the outer observable of the nesting hierarchy
	 * @return the inner observable which is wrapped in the nesting
	 */
	protected abstract IO getInnerObservable(OO outerObservable);

	//#end ABSTRACT METHODS

}

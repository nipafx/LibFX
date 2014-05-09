package org.codefx.nesting;

import static org.codefx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;

import org.junit.Test;

/**
 * Abstract superclass to tests of deep nestings. By implementing the few abstract methods subclasses can run all tests
 * which apply to all implementations.
 *
 * @param <OO>
 *            the type of the nesting hierarchy's outer observable
 * @param <IO>
 *            the type the nesting hierarchy's inner observable which is also the type wrapped by the nesting
 */
public abstract class AbstractDeepNestingTest<OO extends Observable, IO extends Observable>
		extends AbstractNestingTest<OO, IO> {

	// #region TESTS

	// nested value

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservable() innerObservable} is updated correctly when
	 * one of the {@link #outerObservable}'s nested values is changed.
	 */
	@Test
	public void testWhenSettingNestedValue() {
		setNewValue(outerObservable, Level.NESTED, Value.ANY);
		assertSame(getNestingObservable(nesting), getInnerObservable(outerObservable));
	}

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservable() innerObservable} is updated correctly when
	 * one of the {@link #outerObservable}'s nested values is changed to one which contains null observables.
	 */
	@Test
	public void testWhenSettingNestedValueWithNullObservables() {
		setNewValue(outerObservable, Level.NESTED, Value.ANY_WITH_NULL_OBSERVABLE);
		assertNull(getNestingObservable(nesting));
	}

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservable() innerObservable} is updated correctly when
	 * one of the {@link #outerObservable}'s nested values is set to null.
	 */
	@Test
	public void testWhenSettingNestedValueToNull() {
		setNewValue(outerObservable, Level.NESTED, Value.NULL);
		assertNull(getNestingObservable(nesting));
	}

	// outer value

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservable() innerObservable} is updated correctly when
	 * the {@link #outerObservable}'s outer value is changed.
	 */
	@Test
	public void testWhenSettingOuterValue() {
		setNewValue(outerObservable, Level.OUTER, Value.ANY);
		assertSame(getNestingObservable(nesting), getInnerObservable(outerObservable));
	}

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservable() innerObservable} is updated correctly when
	 * the {@link #outerObservable}'s outer value is changed to one which contains null observables.
	 */
	@Test
	public void testWhenSettingOuterValueWithNullObservables() {
		setNewValue(outerObservable, Level.OUTER, Value.ANY_WITH_NULL_OBSERVABLE);
		assertNull(getNestingObservable(nesting));
	}

	/**
	 * Tests whether the {@link #nesting}'s {@link Nesting#innerObservable() innerObservable} is updated correctly when
	 * the {@link #outerObservable}'s outer value is set to null.
	 */
	@Test
	public void testWhenSettingOuterValueToNull() {
		setNewValue(outerObservable, Level.OUTER, Value.NULL);
		assertNull(getNestingObservable(nesting));
	}

	//#end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Sets a new value of the specified kind on the specified level of the nesting hierarchy contained in the specified
	 * outer observable.
	 *
	 * @param outerObservable
	 *            the {@link ObservableValue} which contains the nesting hierarchy's outer value
	 * @param level
	 *            the {@link Level} on which the new value will be set
	 * @param newValue
	 *            the kind of {@link Value} which will be set
	 */
	protected abstract void setNewValue(OO outerObservable, Level level, Value newValue);

	//#end ABSTRACT METHODS

	// #region INNER CLASSES

	/**
	 * Indicates on which level of the nesting hierarchy a new value will be set by
	 * {@link AbstractDeepNestingTest#setNewValue(Observable, Level, Value) setNewValue}.
	 */
	protected enum Level {

		/**
		 * A level below the outer level.
		 */
		NESTED,

		/**
		 * The outer level.
		 */
		OUTER,
	}

	/**
	 * Indicates what kind of value will be set by {@link AbstractDeepNestingTest#setNewValue(Observable, Level, Value)
	 * setNewValue}.
	 */
	protected enum Value {

		/**
		 * The new value will be some fully initialized instance.
		 */
		ANY,

		/**
		 * The new value will be an instance whose observables are null
		 */
		ANY_WITH_NULL_OBSERVABLE,

		/**
		 * The new value will be null.
		 */
		NULL,

	}

	//#end INNER CLASSES

}

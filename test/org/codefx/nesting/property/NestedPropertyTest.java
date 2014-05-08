package org.codefx.nesting.property;

import static org.codefx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.nesting.testhelper.NestingAccess.getNestingValue;
import static org.codefx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.codefx.nesting.testhelper.NestingAccess.setNestingValue;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import javafx.beans.property.Property;

import org.codefx.nesting.Nesting;
import org.codefx.nesting.testhelper.NestingAccess;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to all tests of nested properties. By implementing the few abstract methods subclasses can run
 * all tests which apply to all nested property implementations.
 *
 * @param <T>
 *            the type wrapped by the nested property
 */
public abstract class NestedPropertyTest<T> {

	// #region INSTANCES USED FOR TESTING

	/**
	 * The nesting on which the tested property is based.
	 */
	private NestingAccess.EditableNesting<Property<T>> nesting;

	/**
	 * The tested property.
	 */
	private NestedProperty<T> property;

	//#end INSTANCES USED FOR TESTING

	/**
	 * Creates a new instance of {@link #nesting} and {@link #property}.
	 */
	@Before
	public void setUp() {
		Property<T> innerObservable = createNewObservableWithSomeValue();
		nesting = NestingAccess.EditableNesting.createWithInnerObservable(innerObservable);
		property = createNestedPropertyFromNesting(nesting);
	}

	// #region TESTS

	/**
	 * Tests whether the property's initial value (i.e. after construction) is the one held by the nesting's inner
	 * observable.
	 */
	@Test
	public void testInnerValueAfterConstruction() {
		assertSame(getNestingValue(nesting), property.getValue());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting's observable changes its value.
	 */
	@Test
	public void testChangingValue() {
		T newValue = createNewValue();
		setNestingValue(nesting, newValue);
		// assert that setting the value worked
		assertSame(newValue, getNestingValue(nesting));

		// assert that nesting and property hold the new value
		assertSame(getNestingValue(nesting), property.getValue());
		assertSame(newValue, property.getValue());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting gets a new observable.
	 */
	@Test
	public void testChangingObservable() {
		T newValue = createNewValue();
		Property<T> newObservable = createNewObservableWithValue(newValue);
		setNestingObservable(nesting, newObservable);
		// assert that setting the observable worked
		assertSame(newObservable, getNestingObservable(nesting));

		// assert that nesting and property hold the new value
		assertSame(getNestingValue(nesting), property.getValue());
		assertSame(newValue, property.getValue());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting's new observable gets a new value.
	 */
	@Test
	public void testChangingNewObservablesValue() {
		// set a new observable ...
		Property<T> newObservable = createNewObservableWithSomeValue();
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertSame(newObservable, getNestingObservable(nesting));

		// ... and change its value
		T newValue = createNewValue();
		newObservable.setValue(newValue);

		// assert that nesting and property hold the new value
		assertSame(getNestingValue(nesting), property.getValue());
		assertSame(newValue, property.getValue());
	}

	/**
	 * Tests whether the property's value is not updated when the nesting's old observable gets a new value.
	 */
	@Test
	public void testChangingOldObservablesValue() {
		// store the old observable ...
		Property<T> oldObservable = getNestingObservable(nesting);

		// ... set a new observable ...
		T newValueInNewObservable = createNewValue();
		Property<T> newObservable = createNewObservableWithValue(newValueInNewObservable);
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertNotSame(oldObservable, getNestingObservable(nesting));

		// ... and change the old observable's value
		T newValueInOldObservable = createNewValue();
		oldObservable.setValue(newValueInOldObservable);

		// assert that nesting and property do not hold the old observable's value ...
		assertNotSame(newValueInOldObservable, property.getValue());
		// ... but the new one
		assertSame(getNestingValue(nesting), property.getValue());
		assertSame(newValueInNewObservable, property.getValue());
	}

	//#end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Creates the property, which will be tested, from the specified nesting.
	 *
	 * @param nesting
	 *            the nesting from which the nested property is created
	 * @return a new {@link NestedProperty} instance
	 */
	protected abstract NestedProperty<T> createNestedPropertyFromNesting(Nesting<Property<T>> nesting);

	/**
	 * Creates a new value. Each call must return a new instance.
	 *
	 * @return a new instance of type {@code T}
	 */
	protected abstract T createNewValue();

	/**
	 * Creates a new observable which holds the specified value. Each call must return a new instance.
	 *
	 * @param value
	 *            the new observable's value
	 * @return a new {@link Property} instance with the specified value
	 */
	protected abstract Property<T> createNewObservableWithValue(T value);

	/**
	 * Creates a new observable which holds some arbitrary value (there are no constraints for this value). Each call
	 * must return a new instance.
	 *
	 * @return a new {@link Property} instance with the specified value
	 */
	protected abstract Property<T> createNewObservableWithSomeValue();

	//#end ABSTRACT METHODS

}

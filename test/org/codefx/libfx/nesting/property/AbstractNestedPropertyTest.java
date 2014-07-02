package org.codefx.libfx.nesting.property;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingValue;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess;
import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass to tests of nested properties. By implementing the few abstract methods subclasses can run all
 * tests which apply to all nested property implementations.
 *
 * @param <T>
 *            the type wrapped by the nested property
 * @param <P>
 *            the type of property wrapped by the nesting
 */
public abstract class AbstractNestedPropertyTest<T, P extends Property<T>> {

	// #region INSTANCES USED FOR TESTING

	/**
	 * The nesting on which the tested property is based.
	 */
	private NestingAccess.EditableNesting<P> nesting;

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
		P innerObservable = createNewObservableWithSomeValue();
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
		assertEquals(getNestingValue(nesting), property.getValue());
		assertFalse(property.isInnerObservableNull());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting's observable changes its value.
	 */
	@Test
	public void testChangingValue() {
		T newValue = createNewValue();
		setNestingValue(nesting, newValue);
		// assert that setting the value worked
		assertEquals(newValue, getNestingValue(nesting));

		// assert that nesting and property hold the new value
		assertEquals(getNestingValue(nesting), property.getValue());
		assertEquals(newValue, property.getValue());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting's observable changes its value to null.
	 */
	@Test
	public void testChangingValueToNull() {
		if (!allowsNullValues())
			return;

		setNestingValue(nesting, null);
		// assert that setting the value worked
		assertNull(getNestingValue(nesting));

		// assert that the property holds null
		assertNull(property.getValue());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting gets a new observable.
	 */
	@Test
	public void testChangingObservable() {
		T newValue = createNewValue();
		P newObservable = createNewObservableWithValue(newValue);
		setNestingObservable(nesting, newObservable);
		// assert that setting the observable worked
		assertEquals(newObservable, getNestingObservable(nesting));

		// assert that nesting and property hold the new value
		assertEquals(getNestingValue(nesting), property.getValue());
		assertEquals(newValue, property.getValue());
		// assert that 'isInnerObservableNull' is still false
		assertFalse(property.isInnerObservableNull());
	}

	/**
	 * Tests whether the property's value is not updated when the nesting gets null as a new observable.
	 */
	@Test
	public void testChangingObservableToNull() {
		T oldValue = property.getValue();
		setNestingObservable(nesting, null);
		// assert that setting the null observable worked
		assertNull(getNestingObservable(nesting));

		// assert that the nesting still holds the old value
		assertEquals(oldValue, property.getValue());
		// assert that 'isInnerObservableNull' is now true
		assertTrue(property.isInnerObservableNull());
	}

	/**
	 * Tests whether the property's value is correctly updated when the nesting's new observable gets a new value.
	 */
	@Test
	public void testChangingNewObservablesValue() {
		// set a new observable ...
		P newObservable = createNewObservableWithSomeValue();
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertEquals(newObservable, getNestingObservable(nesting));

		// ... and change its value
		T newValue = createNewValue();
		newObservable.setValue(newValue);

		// assert that nesting and property hold the new value
		assertEquals(getNestingValue(nesting), property.getValue());
		assertEquals(newValue, property.getValue());
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
		P newObservable = createNewObservableWithValue(newValueInNewObservable);
		setNestingObservable(nesting, newObservable);
		// (assert that setting the observable worked)
		assertNotSame(oldObservable, getNestingObservable(nesting));

		// ... and change the old observable's value
		T newValueInOldObservable = createNewValue();
		oldObservable.setValue(newValueInOldObservable);

		// assert that nesting and property do not hold the old observable's new value ...
		assertNotEquals(newValueInOldObservable, property.getValue());
		// ... but the new observable's value
		assertEquals(getNestingValue(nesting), property.getValue());
		assertEquals(newValueInNewObservable, property.getValue());
	}

	//#end TESTS

	// #region ABSTRACT METHODS

	/**
	 * Indicates whether the tested nested property allows null values.
	 *
	 * @return true if the nested properties allows null values
	 */
	protected abstract boolean allowsNullValues();

	/**
	 * Creates the property, which will be tested, from the specified nesting.
	 *
	 * @param nesting
	 *            the nesting from which the nested property is created
	 * @return a new {@link NestedProperty} instance
	 */
	protected abstract NestedProperty<T> createNestedPropertyFromNesting(Nesting<P> nesting);

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
	protected abstract P createNewObservableWithValue(T value);

	/**
	 * Creates a new observable which holds some arbitrary value (there are no constraints for this value). Each call
	 * must return a new instance.
	 *
	 * @return a new {@link Property} instance with the specified value
	 */
	protected abstract P createNewObservableWithSomeValue();

	//#end ABSTRACT METHODS

	// #region ATTRIBUTE ACCESS

	/**
	 * @return the nesting on which the tested property is based
	 */
	public NestingAccess.EditableNesting<P> getNesting() {
		return nesting;
	}

	/**
	 * @return the tested property
	 */
	public NestedProperty<T> getProperty() {
		return property;
	}

	/**
	 * @return the {@link #getProperty tested property}'s current value
	 */
	public T getPropertyValue() {
		return property.getValue();
	}

	//#end ATTRIBUTE ACCESS

}

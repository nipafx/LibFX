package org.codefx.libfx.nesting.property;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingObservable;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.getNestingValue;
import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nesting;
import org.junit.Test;

/**
 * Abstract superclass to tests for {@link NestedBooleanProperty NestedBooleanProperty} which only leaves the creation
 * of the tested properties (by {@link #createNestedPropertyFromNesting(Nesting)}) to the subclasses.
 */
public abstract class AbstractNestedBooleanPropertyTest extends AbstractNestedPropertyTest<Boolean, BooleanProperty> {

	/*
	 * Since Boolean has only two values, 'createNewValue' can not fulfill its contract. Instead it always returns
	 * 'true' whereas 'createNewObservableWithSomeValue' uses false. All tests where this might come into play are
	 * overridden below (for better readability or just to make them work).
	 */

	@Override
	protected boolean allowsNullValues() {
		return false;
	}

	@Override
	protected Boolean createNewValue() {
		return true;
	}

	@Override
	protected BooleanProperty createNewObservableWithValue(Boolean value) {
		return new SimpleBooleanProperty(value);
	}

	@Override
	protected BooleanProperty createNewObservableWithSomeValue() {
		return createNewObservableWithValue(false);
	}

	// #begin OVERRIDDEN TEST METHODS

	@Override
	@Test
	public void testChangingNewObservablesValue() {
		// set a new observable whose value is 'false'...
		BooleanProperty newObservable = createNewObservableWithValue(false);
		setNestingObservable(getNesting(), newObservable);
		// (assert that setting the observable worked)
		assertEquals(newObservable, getNestingObservable(getNesting()));

		// ... and change its value to 'true'
		newObservable.setValue(true);

		// assert that nesting and property hold the new value
		assertTrue(getNestingValue(getNesting()));
		assertTrue(getPropertyValue());
	}

	@Override
	@Test
	public void testChangingOldObservablesValue() {
		// store the old observable which has the value 'false' (see 'createNewObservableWithSomeValue') ...
		BooleanProperty oldObservable = getNestingObservable(getNesting());

		// ... set a new observable with value 'false' ...
		BooleanProperty newObservableWithFalse = createNewObservableWithValue(false);
		setNestingObservable(getNesting(), newObservableWithFalse);
		// (assert that setting the observable worked)
		assertNotSame(oldObservable, getNestingObservable(getNesting()));

		// ... and change the old observable's value
		oldObservable.setValue(true);

		// assert that nesting and property hold the new observable's value (i.e. 'false') instead of the old observable's new value (i.e. 'true')
		assertFalse(getNestingValue(getNesting()));
		assertFalse(getPropertyValue());
	}

	@Override
	@Test
	public void testChangedValueNotPropagatedAfterObservableWasMissing() {
		// set the nesting observable and change the nested property's value to 'true'
		setNestingObservable(getNesting(), null);
		getProperty().setValue(true);

		// set the new observable and assert that the property reflects its value, i.e. holds 'false'
		BooleanProperty newObservable = createNewObservableWithValue(false);
		setNestingObservable(getNesting(), newObservable);
		assertFalse(getPropertyValue());
	}

	//#end OVERRIDDEN TEST METHODS

}

package org.codefx.libfx.nesting.property;

import static org.codefx.libfx.nesting.testhelper.NestingAccess.setNestingObservable;
import static org.codefx.tarkastus.AssertFX.assertSameOrEqual;
import static org.junit.Assert.assertNotEquals;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.InnerObservableMissingBehavior.WhenInnerObservableMissingOnUpdate;
import org.junit.Test;

/**
 * Abstract superclass to tests for {@link NestedBooleanProperty NestedBooleanProperty} which only leaves the creation
 * of the tested properties (by {@link #createNestedPropertyFromNesting(Nesting, InnerObservableMissingBehavior)}) to
 * the subclasses.
 */
public abstract class AbstractNestedBooleanPropertyTest extends
		AbstractNestedPropertyTest<Boolean, Boolean, BooleanProperty> {

	/*
	 * Since Boolean has only two values, 'createNewValue' can not fulfill its contract. Instead it always returns
	 * 'true' whereas 'createNewObservableWithSomeValue' uses false. All tests where this leads to a failing test are
	 * overridden below.
	 */

	@Override
	protected boolean wrapsPrimitive() {
		return true;
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

	// #begin TESTS

	@Override
	@Test
	public void newInnerObservableAfterSetValueOnMissingInnerObservable_acceptUntilNext_newInnerObservableKeepsValue() {
		boolean valueWhileMissing = true;
		boolean valueOfNewInnerObservable = false;

		MissingBehavior<Boolean> missingBehavior = MissingBehavior
				.<Boolean> defaults()
				.onUpdate(WhenInnerObservableMissingOnUpdate.ACCEPT_VALUE_UNTIL_NEXT_INNER_OBSERVABLE);
		NestedProperty<Boolean> property = createNestedPropertyFromNesting(getNesting(), missingBehavior);
		setNestingObservable(getNesting(), null);
		BooleanProperty newObservable = createNewObservableWithValue(valueOfNewInnerObservable);

		// change the nested property's value (which can not be written to the nesting's observable as none is present);
		property.setValue(valueWhileMissing);
		// the values of the nested property and the new observable are not equal
		assertNotEquals(newObservable.getValue(), property.getValue());

		// set the new observable and assert that it kept its value and the nested property was updated
		setNestingObservable(getNesting(), newObservable);

		assertSameOrEqual(valueOfNewInnerObservable, newObservable.getValue(), wrapsPrimitive());
		assertSameOrEqual(valueOfNewInnerObservable, property.getValue(), wrapsPrimitive());
	}

	//#end OVERRIDDEN TEST METHODS

}

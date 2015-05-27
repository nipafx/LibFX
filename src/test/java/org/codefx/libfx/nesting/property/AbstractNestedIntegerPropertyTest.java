package org.codefx.libfx.nesting.property;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Abstract superclass to tests for {@link NestedIntegerProperty NestedIntegerProperty} which only leaves the creation
 * of the tested properties (by {@link #createNestedPropertyFromNesting(Nesting, InnerObservableMissingBehavior)}) to
 * the subclasses.
 */
public abstract class AbstractNestedIntegerPropertyTest extends
		AbstractNestedPropertyTest<Integer, Number, IntegerProperty> {

	/**
	 * The last value returned by {@link #createNewValue()}.
	 */
	private int lastValue = 0;

	@Override
	protected boolean wrapsPrimitive() {
		return true;
	}

	@Override
	protected Integer createNewValue() {
		return lastValue++;
	}

	@Override
	protected IntegerProperty createNewObservableWithValue(Number value) {
		return new SimpleIntegerProperty(value.intValue());
	}

	@Override
	protected IntegerProperty createNewObservableWithSomeValue() {
		return createNewObservableWithValue(0);
	}

}

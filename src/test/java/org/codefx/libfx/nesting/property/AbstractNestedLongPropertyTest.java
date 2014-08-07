package org.codefx.libfx.nesting.property;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Abstract superclass to tests for {@link NestedLongProperty NestedLongProperty} which only leaves the creation of the
 * tested properties (by {@link #createNestedPropertyFromNesting(Nesting)}) to the subclasses.
 */
public abstract class AbstractNestedLongPropertyTest extends AbstractNestedPropertyTest<Number, LongProperty> {

	/**
	 * The last value returned by {@link #createNewValue()}.
	 */
	private long lastValue = 0;

	@Override
	protected boolean allowsNullValues() {
		return false;
	}

	@Override
	protected Number createNewValue() {
		lastValue += 1;
		return lastValue;
	}

	@Override
	protected LongProperty createNewObservableWithValue(Number value) {
		return new SimpleLongProperty(value.longValue());
	}

	@Override
	protected LongProperty createNewObservableWithSomeValue() {
		return createNewObservableWithValue(0);
	}

}

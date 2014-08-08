package org.codefx.libfx.nesting.property;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Abstract superclass to tests for {@link NestedDoubleProperty NestedDoubleProperty} which only leaves the creation of
 * the tested properties (by {@link #createNestedPropertyFromNesting(Nesting)}) to the subclasses.
 */
public abstract class AbstractNestedDoublePropertyTest extends AbstractNestedPropertyTest<Number, DoubleProperty> {

	/**
	 * The last value returned by {@link #createNewValue()}.
	 */
	private double lastValue = 1.5;

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
	protected DoubleProperty createNewObservableWithValue(Number value) {
		return new SimpleDoubleProperty(value.doubleValue());
	}

	@Override
	protected DoubleProperty createNewObservableWithSomeValue() {
		return createNewObservableWithValue(0.0);
	}

}

package org.codefx.nesting.property;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;

import org.codefx.nesting.Nesting;

/**
 * Tests the class {@link NestedIntegerProperty}.
 */
public class NestedIntegerPropertyTest extends AbstractNestedPropertyTest<Number> {

	/**
	 * The next value returned by {@link #createNewValue()}.
	 */
	private int nextValue = 1;

	@Override
	protected NestedIntegerProperty createNestedPropertyFromNesting(Nesting<Property<Number>> nesting) {
		return new NestedIntegerProperty(nesting, null, null);
	}

	@Override
	protected Number createNewValue() {
		return ++nextValue;
	}

	@Override
	protected Property<Number> createNewObservableWithValue(Number value) {
		return new SimpleIntegerProperty(value.intValue());
	}

	@Override
	protected Property<Number> createNewObservableWithSomeValue() {
		return createNewObservableWithValue(0);
	}

}

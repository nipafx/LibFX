package org.codefx.nesting.property;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.Nesting;
import org.codefx.nesting.testhelper.SomeValue;

/**
 * Tests the class {@link NestedObjectProperty}.
 */
public class NestedObjectPropertyTest extends AbstractNestedPropertyTest<SomeValue> {

	@Override
	protected boolean allowsNullValues() {
		return true;
	}

	@Override
	protected NestedProperty<SomeValue> createNestedPropertyFromNesting(Nesting<Property<SomeValue>> nesting) {
		return new NestedObjectProperty<>(nesting, null, null);
	}

	@Override
	protected SomeValue createNewValue() {
		return new SomeValue();
	}

	@Override
	protected Property<SomeValue> createNewObservableWithValue(SomeValue value) {
		return new SimpleObjectProperty<>(value);
	}

	@Override
	protected Property<SomeValue> createNewObservableWithSomeValue() {
		SomeValue someValue = new SomeValue();
		return createNewObservableWithValue(someValue);
	}

}

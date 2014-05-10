package org.codefx.nesting.property;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.Nesting;
import org.codefx.nesting.testhelper.SomeValue;

/**
 * Abstract superclass to tests for {@link NestedObjectProperty NestedObjectProperties} which only leaves the creation
 * of the tested properties (by {@link #createNestedPropertyFromNesting(Nesting)}) to the subclasses.
 */
public abstract class AbstractNestedObjectPropertyTest
		extends AbstractNestedPropertyTest<SomeValue, Property<SomeValue>> {

	@Override
	protected final boolean allowsNullValues() {
		return true;
	}

	@Override
	protected final SomeValue createNewValue() {
		return new SomeValue();
	}

	@Override
	protected final Property<SomeValue> createNewObservableWithValue(SomeValue value) {
		return new SimpleObjectProperty<>(value);
	}

	@Override
	protected final Property<SomeValue> createNewObservableWithSomeValue() {
		SomeValue someValue = new SomeValue();
		return createNewObservableWithValue(someValue);
	}

}

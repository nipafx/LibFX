package org.codefx.libfx.nesting.property;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Abstract superclass to tests for {@link NestedStringProperty NestedStringProperty} which only leaves the creation of
 * the tested properties (by {@link #createNestedPropertyFromNesting(Nesting)}) to the subclasses.
 */
public abstract class AbstractNestedStringPropertyTest extends AbstractNestedPropertyTest<String, StringProperty> {

	/**
	 * The last value returned by {@link #createNewValue()}.
	 */
	private String lastValue = "";

	@Override
	protected boolean allowsNullValues() {
		return false;
	}

	@Override
	protected String createNewValue() {
		lastValue += "a";
		return lastValue;
	}

	@Override
	protected StringProperty createNewObservableWithValue(String value) {
		return new SimpleStringProperty(value);
	}

	@Override
	protected StringProperty createNewObservableWithSomeValue() {
		return createNewObservableWithValue("");
	}

}

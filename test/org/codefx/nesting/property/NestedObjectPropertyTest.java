package org.codefx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.nesting.Nesting;
import org.codefx.nesting.testhelper.SomeValue;

/**
 * Tests the class {@link NestedObjectProperty}.
 */
public class NestedObjectPropertyTest extends AbstractNestedObjectPropertyTest {

	@Override
	protected NestedProperty<SomeValue> createNestedPropertyFromNesting(Nesting<Property<SomeValue>> nesting) {
		return new NestedObjectProperty<>(nesting, null, null);
	}

}

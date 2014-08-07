package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.NestedObjectProperty;
import org.codefx.libfx.nesting.property.NestedProperty;
import org.codefx.libfx.nesting.testhelper.SomeValue;

/**
 * Tests the class {@link NestedObjectProperty}.
 */
public class NestedObjectPropertyTest extends AbstractNestedObjectPropertyTest {

	@Override
	protected NestedProperty<SomeValue> createNestedPropertyFromNesting(Nesting<Property<SomeValue>> nesting) {
		return new NestedObjectProperty<>(nesting, null, null);
	}

}

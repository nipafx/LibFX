package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.SomeValue;

/**
 * Tests the class {@link NestedObjectProperty}.
 */
public class NestedObjectPropertyTest extends AbstractNestedObjectPropertyTest {

	@Override
	protected NestedProperty<SomeValue> createNestedPropertyFromNesting(
			Nesting<Property<SomeValue>> nesting, InnerObservableMissingBehavior<SomeValue> missingBehavior) {
		return new NestedObjectProperty<>(nesting, missingBehavior, null, null);
	}

}

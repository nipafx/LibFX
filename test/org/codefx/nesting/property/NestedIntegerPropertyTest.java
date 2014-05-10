package org.codefx.nesting.property;

import javafx.beans.property.IntegerProperty;

import org.codefx.nesting.Nesting;

/**
 * Tests the class {@link NestedIntegerProperty}.
 */
public class NestedIntegerPropertyTest extends AbstractNestedIntegerPropertyTest {

	@Override
	protected NestedProperty<Number> createNestedPropertyFromNesting(Nesting<IntegerProperty> nesting) {
		return new NestedIntegerProperty(nesting, null, null);
	}

}

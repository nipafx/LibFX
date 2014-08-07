package org.codefx.libfx.nesting.property;

import javafx.beans.property.IntegerProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.NestedIntegerProperty;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * Tests the class {@link NestedIntegerProperty}.
 */
public class NestedIntegerPropertyTest extends AbstractNestedIntegerPropertyTest {

	@Override
	protected NestedProperty<Number> createNestedPropertyFromNesting(Nesting<IntegerProperty> nesting) {
		return new NestedIntegerProperty(nesting, null, null);
	}

}

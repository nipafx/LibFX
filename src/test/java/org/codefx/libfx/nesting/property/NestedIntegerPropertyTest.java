package org.codefx.libfx.nesting.property;

import javafx.beans.property.IntegerProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedIntegerProperty}.
 */
public class NestedIntegerPropertyTest extends AbstractNestedIntegerPropertyTest {

	@Override
	protected NestedProperty<Number> createNestedPropertyFromNesting(
			Nesting<IntegerProperty> nesting, InnerObservableMissingBehavior<Integer> missingBehavior) {
		return new NestedIntegerProperty(nesting, missingBehavior, null, null);
	}

}

package org.codefx.libfx.nesting.property;

import javafx.beans.property.FloatProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedFloatProperty}.
 */
public class NestedFloatPropertyTest extends AbstractNestedFloatPropertyTest {

	@Override
	protected NestedProperty<Number> createNestedPropertyFromNesting(
			Nesting<FloatProperty> nesting, InnerObservableMissingBehavior<Float> missingBehavior) {
		return new NestedFloatProperty(nesting, missingBehavior, null, null);
	}

}

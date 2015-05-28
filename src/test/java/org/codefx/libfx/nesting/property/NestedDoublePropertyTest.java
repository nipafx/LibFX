package org.codefx.libfx.nesting.property;

import javafx.beans.property.DoubleProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedDoubleProperty}.
 */
public class NestedDoublePropertyTest extends AbstractNestedDoublePropertyTest {

	@Override
	protected NestedProperty<Number> createNestedPropertyFromNesting(
			Nesting<DoubleProperty> nesting, InnerObservableMissingBehavior<Double> missingBehavior) {
		return new NestedDoubleProperty(nesting, missingBehavior, null, null);
	}

}

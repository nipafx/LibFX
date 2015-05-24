package org.codefx.libfx.nesting.property;

import javafx.beans.property.LongProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedLongProperty}.
 */
public class NestedLongPropertyTest extends AbstractNestedLongPropertyTest {

	@Override
	protected NestedProperty<Number> createNestedPropertyFromNesting(
			Nesting<LongProperty> nesting, InnerObservableMissingBehavior<Long> missingBehavior) {
		return new NestedLongProperty(nesting, missingBehavior, null, null);
	}

}

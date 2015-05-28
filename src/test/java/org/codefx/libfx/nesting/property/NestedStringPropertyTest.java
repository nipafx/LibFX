package org.codefx.libfx.nesting.property;

import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedStringProperty}.
 */
public class NestedStringPropertyTest extends AbstractNestedStringPropertyTest {

	@Override
	protected NestedProperty<String> createNestedPropertyFromNesting(
			Nesting<StringProperty> nesting, InnerObservableMissingBehavior<String> missingBehavior) {
		return new NestedStringProperty(nesting, missingBehavior, null, null);
	}

}

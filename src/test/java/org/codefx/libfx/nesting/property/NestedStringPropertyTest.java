package org.codefx.libfx.nesting.property;

import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.Nesting;

/**
 * Tests the class {@link NestedStringProperty}.
 */
public class NestedStringPropertyTest extends AbstractNestedStringPropertyTest {

	@Override
	protected NestedProperty<String> createNestedPropertyFromNesting(Nesting<StringProperty> nesting) {
		return new NestedStringProperty(nesting, null, null);
	}

}

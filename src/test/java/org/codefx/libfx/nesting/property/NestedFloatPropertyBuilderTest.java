package org.codefx.libfx.nesting.property;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedFloatPropertyBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedFloatPropertyBuilderTest.AbstractBuilderContract.class,
		NestedFloatPropertyBuilderTest.CreatedProperties.class,
})
public class NestedFloatPropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
			extends AbstractNestedPropertyBuilderTest<FloatProperty, NestedFloatProperty> {

		@Override
		protected AbstractNestedPropertyBuilder<FloatProperty, NestedFloatProperty> createBuilder() {
			FloatProperty innerObservable = new SimpleFloatProperty(0);
			EditableNesting<FloatProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedFloatPropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedFloatPropertyTest {

		@Override
		protected NestedProperty<Number> createNestedPropertyFromNesting(Nesting<FloatProperty> nesting) {
			// use the builder to create the property
			NestedFloatPropertyBuilder builder = NestedFloatPropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

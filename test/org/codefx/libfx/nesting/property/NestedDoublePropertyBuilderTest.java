package org.codefx.libfx.nesting.property;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedDoublePropertyBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedDoublePropertyBuilderTest.AbstractBuilderContract.class,
		NestedDoublePropertyBuilderTest.CreatedProperties.class,
})
public class NestedDoublePropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
			extends AbstractNestedPropertyBuilderTest<DoubleProperty, NestedDoubleProperty> {

		@Override
		protected AbstractNestedPropertyBuilder<DoubleProperty, NestedDoubleProperty> createBuilder() {
			DoubleProperty innerObservable = new SimpleDoubleProperty(0);
			EditableNesting<DoubleProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedDoublePropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedDoublePropertyTest {

		@Override
		protected NestedProperty<Number> createNestedPropertyFromNesting(Nesting<DoubleProperty> nesting) {
			// use the builder to create the property
			NestedDoublePropertyBuilder builder = NestedDoublePropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

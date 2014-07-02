package org.codefx.libfx.nesting.property;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedLongPropertyBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedLongPropertyBuilderTest.AbstractBuilderContract.class,
		NestedLongPropertyBuilderTest.CreatedProperties.class,
})
public class NestedLongPropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
			extends AbstractNestedPropertyBuilderTest<LongProperty, NestedLongProperty> {

		@Override
		protected AbstractNestedPropertyBuilder<LongProperty, NestedLongProperty> createBuilder() {
			LongProperty innerObservable = new SimpleLongProperty(0);
			EditableNesting<LongProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedLongPropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedLongPropertyTest {

		@Override
		protected NestedProperty<Number> createNestedPropertyFromNesting(Nesting<LongProperty> nesting) {
			// use the builder to create the property
			NestedLongPropertyBuilder builder = NestedLongPropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

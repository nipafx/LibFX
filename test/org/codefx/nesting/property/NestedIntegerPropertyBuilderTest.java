package org.codefx.nesting.property;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import org.codefx.nesting.Nesting;
import org.codefx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedIntegerPropertyBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedIntegerPropertyBuilderTest.AbstractBuilderContract.class,
		NestedIntegerPropertyBuilderTest.CreatedProperties.class,
})
public class NestedIntegerPropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
	extends AbstractNestedPropertyBuilderTest<IntegerProperty, NestedIntegerProperty> {

		@Override
		protected AbstractNestedPropertyBuilder<IntegerProperty, NestedIntegerProperty> createBuilder() {
			IntegerProperty innerObservable = new SimpleIntegerProperty(0);
			EditableNesting<IntegerProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedIntegerPropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedIntegerPropertyTest {

		@Override
		protected NestedProperty<Number> createNestedPropertyFromNesting(Nesting<IntegerProperty> nesting) {
			// use the builder to create the property
			NestedIntegerPropertyBuilder builder = NestedIntegerPropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

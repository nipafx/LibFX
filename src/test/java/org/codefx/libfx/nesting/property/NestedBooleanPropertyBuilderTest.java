package org.codefx.libfx.nesting.property;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedBooleanPropertyBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		NestedBooleanPropertyBuilderTest.AbstractBuilderContract.class,
		NestedBooleanPropertyBuilderTest.CreatedProperties.class,
})
public class NestedBooleanPropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
			extends AbstractNestedPropertyBuilderTest<BooleanProperty, NestedBooleanProperty> {

		@Override
		protected AbstractNestedPropertyBuilder<BooleanProperty, NestedBooleanProperty> createBuilder() {
			BooleanProperty innerObservable = new SimpleBooleanProperty(false);
			EditableNesting<BooleanProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedBooleanPropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedBooleanPropertyTest {

		@Override
		protected NestedProperty<Boolean> createNestedPropertyFromNesting(Nesting<BooleanProperty> nesting) {
			// use the builder to create the property
			NestedBooleanPropertyBuilder builder = NestedBooleanPropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

package org.codefx.libfx.nesting.property;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.property.AbstractNestedPropertyBuilder;
import org.codefx.libfx.nesting.property.NestedObjectPropertyBuilder;
import org.codefx.libfx.nesting.property.NestedProperty;
import org.codefx.libfx.nesting.testhelper.SomeValue;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests the class {@link NestedObjectPropertyBuilder}.
 */
@RunWith(Suite.class)
@SuiteClasses({
	NestedObjectPropertyBuilderTest.AbstractBuilderContract.class,
	NestedObjectPropertyBuilderTest.CreatedProperties.class,
})
public class NestedObjectPropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
			extends AbstractNestedPropertyBuilderTest<Property<SomeValue>, NestedProperty<SomeValue>> {

		@Override
		protected AbstractNestedPropertyBuilder<Property<SomeValue>, NestedProperty<SomeValue>> createBuilder() {
			Property<SomeValue> innerObservable = new SimpleObjectProperty<>(new SomeValue());
			EditableNesting<Property<SomeValue>> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedObjectPropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedObjectPropertyTest {

		@Override
		protected NestedProperty<SomeValue> createNestedPropertyFromNesting(Nesting<Property<SomeValue>> nesting) {
			// use the builder to create the property
			NestedObjectPropertyBuilder<SomeValue> builder = NestedObjectPropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

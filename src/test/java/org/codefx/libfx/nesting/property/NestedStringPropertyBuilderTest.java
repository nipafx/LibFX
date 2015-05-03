package org.codefx.libfx.nesting.property;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.testhelper.NestingAccess.EditableNesting;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

/**
 * Tests the class {@link NestedStringPropertyBuilder}.
 */
@RunWith(NestedRunner.class)
public class NestedStringPropertyBuilderTest {

	/**
	 * Tests whether the builder fulfills the contract defined by {@link AbstractNestedPropertyBuilder}.
	 */
	public static class AbstractBuilderContract
			extends AbstractNestedPropertyBuilderTest<StringProperty, NestedStringProperty> {

		@Override
		protected AbstractNestedPropertyBuilder<StringProperty, NestedStringProperty> createBuilder() {
			StringProperty innerObservable = new SimpleStringProperty("");
			EditableNesting<StringProperty> nesting = EditableNesting.createWithInnerObservable(innerObservable);
			return NestedStringPropertyBuilder.forNesting(nesting);
		}

	}

	/**
	 * Uses the builder to create properties which are then tested.
	 */
	public static class CreatedProperties extends AbstractNestedStringPropertyTest {

		@Override
		protected NestedProperty<String> createNestedPropertyFromNesting(Nesting<StringProperty> nesting) {
			// use the builder to create the property
			NestedStringPropertyBuilder builder = NestedStringPropertyBuilder.forNesting(nesting);
			return builder.build();
		}

	}

}

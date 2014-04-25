package org.codefx.nesting;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

import org.codefx.nesting.types.OuterType;
import org.junit.Test;

/**
 * Tests for the class {@link Nestings} and {@link NestingBuilder}.
 */
public class NestingsTest {

	/**
	 * Tests whether a nesting of depth > 1 can be built.
	 */
	@Test
	public void testBuildDeepNesting() {
		// the property which holds the outer type
		ObjectProperty<OuterType> outerType = new SimpleObjectProperty<>(new OuterType());

		@SuppressWarnings("unused")
		Nesting<Property<Number>> nesting = Nestings
		.on(outerType)
		.nest(someOuterType -> someOuterType.firstInnerProperty())
		.nest(someInnerType -> someInnerType.integerProperty())
		.buildNesting();
	}

}

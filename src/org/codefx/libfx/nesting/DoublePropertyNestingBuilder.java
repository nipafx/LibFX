package org.codefx.libfx.nesting;

import javafx.beans.property.DoubleProperty;

import org.codefx.libfx.nesting.property.NestedDoubleProperty;
import org.codefx.libfx.nesting.property.NestedDoublePropertyBuilder;

/**
 * A builder for all kinds of nested functionality whose innermost value is held by a {@link DoubleProperty}.
 */
public class DoublePropertyNestingBuilder extends AbstractNestingBuilder<Number, DoubleProperty> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as a nested builder.
	 *
	 * @param <P>
	 *            the type the previous builder wraps
	 * @param previousNestedBuilder
	 *            the previous builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 */
	<P> DoublePropertyNestingBuilder(
			AbstractNestingBuilder<P, ?> previousNestedBuilder,
			NestingStep<P, DoubleProperty> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link NestedDoubleProperty} instance with no owning bean and no name
	 */
	public NestedDoubleProperty buildProperty() {
		Nesting<DoubleProperty> nesting = buildNesting();
		return NestedDoublePropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedDoublePropertyBuilder}
	 */
	public NestedDoublePropertyBuilder buildPropertyWithBuilder() {
		Nesting<DoubleProperty> nesting = buildNesting();
		return NestedDoublePropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

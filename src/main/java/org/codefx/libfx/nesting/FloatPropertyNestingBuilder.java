package org.codefx.libfx.nesting;

import javafx.beans.property.FloatProperty;

import org.codefx.libfx.nesting.property.NestedFloatProperty;
import org.codefx.libfx.nesting.property.NestedFloatPropertyBuilder;

/**
 * A builder for all kinds of nested functionality whose inner observable is a {@link FloatProperty}.
 */
public class FloatPropertyNestingBuilder extends AbstractNestingBuilderOnProperty<Number, FloatProperty> {

	// #begin CONSTRUCTION

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
	<P> FloatPropertyNestingBuilder(
			AbstractNestingBuilderOnObservableValue<P, ?> previousNestedBuilder,
			NestingStep<P, FloatProperty> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #begin BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link NestedFloatProperty} instance with no owning bean and no name
	 */
	public NestedFloatProperty buildProperty() {
		Nesting<FloatProperty> nesting = buildNesting();
		return NestedFloatPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedFloatPropertyBuilder}
	 */
	public NestedFloatPropertyBuilder buildPropertyWithBuilder() {
		Nesting<FloatProperty> nesting = buildNesting();
		return NestedFloatPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

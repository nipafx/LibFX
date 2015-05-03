package org.codefx.libfx.nesting;

import javafx.beans.property.LongProperty;

import org.codefx.libfx.nesting.property.NestedLongProperty;
import org.codefx.libfx.nesting.property.NestedLongPropertyBuilder;

/**
 * A builder for all kinds of nested functionality whose inner observable is a {@link LongProperty}.
 */
public class LongPropertyNestingBuilder extends AbstractNestingBuilderOnProperty<Number, LongProperty> {

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
	<P> LongPropertyNestingBuilder(
			AbstractNestingBuilderOnObservableValue<P, ?> previousNestedBuilder,
			NestingStep<P, LongProperty> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #begin BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link NestedLongProperty} instance with no owning bean and no name
	 */
	public NestedLongProperty buildProperty() {
		Nesting<LongProperty> nesting = buildNesting();
		return NestedLongPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedLongPropertyBuilder}
	 */
	public NestedLongPropertyBuilder buildPropertyWithBuilder() {
		Nesting<LongProperty> nesting = buildNesting();
		return NestedLongPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

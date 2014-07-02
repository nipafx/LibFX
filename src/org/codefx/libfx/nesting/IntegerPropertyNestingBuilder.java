package org.codefx.libfx.nesting;

import javafx.beans.property.IntegerProperty;

import org.codefx.libfx.nesting.property.NestedIntegerPropertyBuilder;
import org.codefx.libfx.nesting.property.NestedObjectPropertyBuilder;

/**
 * A builder for all kinds of nested functionality whose innermost value is held by an {@link IntegerProperty}.
 */
public class IntegerPropertyNestingBuilder extends AbstractNestingBuilder<Number, IntegerProperty> {

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
	<P> IntegerPropertyNestingBuilder(
			AbstractNestingBuilder<P, ?> previousNestedBuilder,
			NestingStep<P, IntegerProperty> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link IntegerProperty} instance with no owning bean and no name
	 */
	public IntegerProperty buildProperty() {
		Nesting<IntegerProperty> nesting = buildNesting();
		return NestedIntegerPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedObjectPropertyBuilder}
	 */
	public NestedIntegerPropertyBuilder buildPropertyWithBuilder() {
		Nesting<IntegerProperty> nesting = buildNesting();
		return NestedIntegerPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

package org.codefx.libfx.nesting;

import javafx.beans.property.BooleanProperty;

import org.codefx.libfx.nesting.property.NestedBooleanProperty;
import org.codefx.libfx.nesting.property.NestedBooleanPropertyBuilder;

/**
 * A builder for all kinds of nested functionality whose innermost value is held by a {@link BooleanProperty}.
 */
public class BooleanPropertyNestingBuilder extends AbstractNestingBuilderOnProperty<Boolean, BooleanProperty> {

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
	<P> BooleanPropertyNestingBuilder(
			AbstractNestingBuilderOnObservableValue<P, ?> previousNestedBuilder,
			NestingStep<P, BooleanProperty> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link NestedBooleanProperty} instance with no owning bean and no name
	 */
	public NestedBooleanProperty buildProperty() {
		Nesting<BooleanProperty> nesting = buildNesting();
		return NestedBooleanPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedBooleanPropertyBuilder}
	 */
	public NestedBooleanPropertyBuilder buildPropertyWithBuilder() {
		Nesting<BooleanProperty> nesting = buildNesting();
		return NestedBooleanPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

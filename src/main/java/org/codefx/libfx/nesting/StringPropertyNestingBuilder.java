package org.codefx.libfx.nesting;

import javafx.beans.property.StringProperty;

import org.codefx.libfx.nesting.property.NestedStringProperty;
import org.codefx.libfx.nesting.property.NestedStringPropertyBuilder;

/**
 * A builder for all kinds of nested functionality whose inner observable is a {@link StringProperty}.
 */
public class StringPropertyNestingBuilder extends AbstractNestingBuilderOnProperty<String, StringProperty> {

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
	<P> StringPropertyNestingBuilder(
			AbstractNestingBuilderOnObservableValue<P, ?> previousNestedBuilder,
			NestingStep<P, StringProperty> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link NestedStringProperty} instance with no owning bean and no name
	 */
	public NestedStringProperty buildProperty() {
		Nesting<StringProperty> nesting = buildNesting();
		return NestedStringPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedStringPropertyBuilder}
	 */
	public NestedStringPropertyBuilder buildPropertyWithBuilder() {
		Nesting<StringProperty> nesting = buildNesting();
		return NestedStringPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

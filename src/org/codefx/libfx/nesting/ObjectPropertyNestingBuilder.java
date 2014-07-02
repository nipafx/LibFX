package org.codefx.libfx.nesting;

import javafx.beans.property.Property;

import org.codefx.libfx.nesting.property.NestedObjectPropertyBuilder;
import org.codefx.libfx.nesting.property.NestedProperty;

/**
 * A builder for all kinds of nested functionality whose innermost value is held by a {@link Property}.
 *
 * @param <T>
 *            the type of the wrapped value
 */
public class ObjectPropertyNestingBuilder<T> extends AbstractNestingNestingBuilder<T, Property<T>> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	ObjectPropertyNestingBuilder(Property<T> outerObservable) {
		super(outerObservable);
	}

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
	<P> ObjectPropertyNestingBuilder(
			AbstractNestingBuilder<P, ?> previousNestedBuilder,
			NestingStep<P, Property<T>> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region NEST

	/**
	 * Usability method which simply calls {@link #nestProperty(NestingStep) nestProperty}.
	 * <p>
	 * Returns a builder for nestings whose inner observable is a {@link Property}. The created nestings depend on this
	 * builder's outer observable and nesting steps and adds the specified step as the next one.
	 *
	 * @param <N>
	 *            the type wrapped by the created nesting builder
	 * @param nestingStep
	 *            the function which performs the nesting step from one observable to the next
	 * @return an {@link ObservableValueNestingBuilder} which builds a nesting from this builder's settings and the
	 *         specified nesting steps
	 * @throws NullPointerException
	 *             if the specified function is null
	 */
	public <N> ObjectPropertyNestingBuilder<N> nest(NestingStep<T, Property<N>> nestingStep) {
		return super.nestProperty(nestingStep);
	}

	//#end NEST

	// #region BUILD

	/**
	 * Creates a nested property from this builder's settings. This method can be called arbitrarily often and each call
	 * returns a new instance.
	 *
	 * @return a new {@link NestedProperty} instance with no owning bean and no name
	 */
	public NestedProperty<T> buildProperty() {
		Nesting<Property<T>> nesting = buildNesting();
		return NestedObjectPropertyBuilder.forNesting(nesting).build();
	}

	/**
	 * Returns a nested object property builder which can be used to define the new property's attributes before
	 * building it.
	 *
	 * @return a new instance of {@link NestedObjectPropertyBuilder}
	 */
	public NestedObjectPropertyBuilder<T> buildPropertyWithBuilder() {
		Nesting<Property<T>> nesting = buildNesting();
		return NestedObjectPropertyBuilder.forNesting(nesting);
	}

	//#end BUILD
}

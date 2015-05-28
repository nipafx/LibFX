package org.codefx.libfx.nesting;

import javafx.beans.Observable;
import javafx.beans.property.Property;

/**
 * A nesting builder which allows bindings.
 *
 * @param <T>
 *            the type of the wrapped value
 * @param <O>
 *            the type of {@link Observable} this builder uses as an inner observable
 */
abstract class AbstractNestingBuilderOnProperty<T, O extends Property<T>>
		extends AbstractNestingBuilderOnObservableValue<T, O> {

	// #begin CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	protected AbstractNestingBuilderOnProperty(O outerObservable) {
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
	protected <P> AbstractNestingBuilderOnProperty(
			AbstractNestingBuilderOnObservable<P, ?> previousNestedBuilder, NestingStep<P, O> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

}

package org.codefx.libfx.nesting;

import javafx.beans.value.ObservableValue;

/**
 * A nesting builder which allows adding change listeners.
 *
 * @param <T>
 *            the type of the wrapped value
 * @param <O>
 *            the type of observable this builder can build
 */
abstract class AbstractNestingBuilderOnObservableValue<T, O extends ObservableValue<T>>
		extends AbstractNestingBuilderOnObservable<T, O> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	protected AbstractNestingBuilderOnObservableValue(O outerObservable) {
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
	protected <P> AbstractNestingBuilderOnObservableValue(
			AbstractNestingBuilderOnObservable<P, ?> previousNestedBuilder, NestingStep<P, O> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

}

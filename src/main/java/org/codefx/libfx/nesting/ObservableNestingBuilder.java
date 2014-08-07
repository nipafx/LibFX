package org.codefx.libfx.nesting;

import javafx.beans.Observable;

/**
 * A builder for all kinds of nested functionality whose inner observable is an {@link Observable}.
 * <p>
 * Note that an {@code Observable} provides no way do access a value. It is hence not possible to nest further or create
 * nestings which depend on a value, e.g. nested properties.
 */
public class ObservableNestingBuilder extends AbstractNestingBuilderOnObservable<Object, Observable> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	ObservableNestingBuilder(Observable outerObservable) {
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
	<P> ObservableNestingBuilder(
			AbstractNestingBuilderOnObservableValue<P, ?> previousNestedBuilder, NestingStep<P, Observable> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

}

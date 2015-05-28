package org.codefx.libfx.nesting;

import javafx.beans.value.ObservableNumberValue;

/**
 * A builder for all kinds of nested functionality whose inner observable is an {@link ObservableNumberValue}.
 * <p>
 * Note that an {@code ObservableNumberValue} provides no way do write a value. It is hence not possible to create
 * nestings which depend on writing a value, e.g. nested properties.
 */
public class ObservableNumberValueNestingBuilder extends
		AbstractNestingBuilderOnObservableValue<Number, ObservableNumberValue> {

	// #begin CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	ObservableNumberValueNestingBuilder(ObservableNumberValue outerObservable) {
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
	<P> ObservableNumberValueNestingBuilder(
			AbstractNestingBuilderOnObservableValue<P, ?> previousNestedBuilder,
			NestingStep<P, ObservableNumberValue> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

}

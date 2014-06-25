package org.codefx.libfx.nesting;

import javafx.beans.value.ObservableValue;

/**
 * A builder for all kinds of nested functionality whose innermost value is held by an {@link ObservableValue}.
 * <p>
 * Note that an {@code ObservableValue} provides no way do write a value. It is hence not possible to create nestings
 * which depend on writing a value, e.g. nested properties.
 *
 * @param <T>
 *            the type of the wrapped value
 */
public class ObservableValueNestingBuilder<T> extends AbstractNestingNestingBuilder<T, ObservableValue<T>> {

	// #region CONSTRUCTION

	/**
	 * Creates a new nesting builder which acts as the outer builder.
	 *
	 * @param outerObservable
	 *            the outer observable upon which the constructed nesting depends
	 */
	ObservableValueNestingBuilder(ObservableValue<T> outerObservable) {
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
	<P> ObservableValueNestingBuilder(
			AbstractNestingBuilder<P, ?> previousNestedBuilder,
			NestingStep<P, ObservableValue<T>> nestingStep) {

		super(previousNestedBuilder, nestingStep);
	}

	//#end CONSTRUCTION

	// #region NEST

	/**
	 * Usability method which simply calls {@link #nestObservable(NestingStep) nestProperty}.
	 * <p>
	 * Returns a builder for nestings whose inner observable is an {@link ObservableValue}. The created nestings depend
	 * on this builder's outer observable and nesting steps and adds the specified step as the next one.
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
	public <N> ObservableValueNestingBuilder<N> nest(NestingStep<T, ObservableValue<N>> nestingStep) {
		return super.nestObservable(nestingStep);
	}

	//#end NEST

}

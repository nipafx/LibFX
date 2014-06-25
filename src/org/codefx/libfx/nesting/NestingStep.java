package org.codefx.libfx.nesting;

import javafx.beans.Observable;

/**
 * Functions of this type perform the nesting steps from one oservable's value to the next nested observable.
 *
 * @param <T>
 *            the type of the value whose observable is returned
 * @param <O>
 *            the type of observable returned by the step
 */
@FunctionalInterface
public interface NestingStep<T, O extends Observable> {

	/**
	 * Performs the nesting step from the specified instance to its observable.
	 *
	 * @param from
	 *            the instance whose nested observable will be returned
	 * @return {@code from's} observable to which the nesting steps
	 */
	O step(T from);

}

package org.codefx.libfx.nesting;

import org.codefx.libfx.nesting.Nesting;
import org.codefx.libfx.nesting.ShallowNesting;

import javafx.beans.Observable;

/**
 * Abstract superclass tests of {@link ShallowNesting ShallowNestings}. Implements all abstract methods from
 * {@link AbstractNestingTest} except the creation of the nesting.
 *
 * @param <O>
 *            the type of the nesting hierarchy's only observable
 */
public abstract class AbstractShallowNestingTest<O extends Observable> extends AbstractNestingTest<O, O> {

	@Override
	protected Nesting<O> createNewNestingFromOuterObservable(O outerObservable) {
		return new ShallowNesting<O>(outerObservable);
	}

	@Override
	protected O getInnerObservable(O outerObservable) {
		return outerObservable;
	}

}

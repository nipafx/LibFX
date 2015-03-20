package org.codefx.libfx.collection.transform;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Abstract superclass to {@link Iterator}s which wrap another iterator and transform the returned elements from their
 * inner type {@code I} to an outer type {@code O}.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner iterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this iterator
 */
abstract class AbstractTransformingIterator<I, O> implements Iterator<O> {

	// #region IMPLEMENTATION OF 'Iterator<O>'

	@Override
	public boolean hasNext() {
		return getInnerIterator().hasNext();
	}

	@Override
	public O next() {
		I nextElement = getInnerIterator().next();
		return transformToOuter(nextElement);
	}

	@Override
	public void remove() {
		getInnerIterator().remove();
	}

	@Override
	public void forEachRemaining(Consumer<? super O> action) {
		Consumer<I> transformThenAction = innerElement -> {
			O asOuterElement = transformToOuter(innerElement);
			action.accept(asOuterElement);
		};
		getInnerIterator().forEachRemaining(transformThenAction);
	}

	// #end IMPLEMENTATION OF 'Iterator<O>'

	// #region ABSTRACT METHODS

	/**
	 * @return the wrapped/inner iterator
	 */
	protected abstract Iterator<I> getInnerIterator();

	/**
	 * Transforms an element from the inner type {@code I} to the outer type {@code O}.
	 *
	 * @param innerElement
	 *            an element returned by the {@link #getInnerIterator() innerIterator}
	 * @return an equivalent element of type {@code O}
	 */
	protected abstract O transformToOuter(I innerElement);

	// #end ABSTRACT METHODS

}

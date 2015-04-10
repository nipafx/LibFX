package org.codefx.libfx.collection.transform;

import java.util.ListIterator;

/**
 * Abstract superclass to {@link ListIterator}s which wrap another list iterator and transform elements from an inner
 * type {@code I} to an outer type {@code O} and vice versa
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner iterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this iterator
 */
abstract class AbstractTransformingListIterator<I, O> extends AbstractTransformingIterator<I, O> implements
		ListIterator<O> {

	// #region IMPLEMENTATION OF 'ListIterator'

	@Override
	public boolean hasPrevious() {
		return getInnerIterator().hasPrevious();
	}

	@Override
	public int nextIndex() {
		return getInnerIterator().nextIndex();
	}

	@Override
	public int previousIndex() {
		return getInnerIterator().previousIndex();
	}

	@Override
	public O previous() {
		I previousElement = getInnerIterator().previous();
		return transformToOuter(previousElement);
	}

	@Override
	public void add(O element) {
		I innerElement = transformToInner(element);
		getInnerIterator().add(innerElement);
	}

	@Override
	public void set(O element) {
		I innerElement = transformToInner(element);
		getInnerIterator().set(innerElement);
	}

	// #end IMPLEMENTATION OF 'ListIterator'

	// #region ABSTRACT METHODS

	@Override
	protected abstract ListIterator<I> getInnerIterator();

	/**
	 * Transforms an element from the outer type {@code O} to the inner type {@code I}.
	 *
	 * @param outerElement
	 *            an element specified as an argument to a method call
	 * @return an equivalent element of type {@code I}
	 */
	protected abstract I transformToInner(O outerElement);

	// #end ABSTRACT METHODS

}

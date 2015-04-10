package org.codefx.libfx.collection.transform;

import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link ListIterator} which wraps another list iterator and transforms elements from an inner type {@code I} to an
 * outer type {@code O} and vice versa.
 * <p>
 * The transformation of null elements of either inner or outer type is fixed to {@code null -> null}. The
 * transformation functions specified during construction do not have to handle null input elements and are not allowed
 * to produce a null result.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner list iterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this list iterator
 */
public final class TransformingListIterator<I, O> extends AbstractTransformingListIterator<I, O> {

	/**
	 * The wrapped/inner list iterator.
	 */
	private final ListIterator<I> innerListIterator;

	/**
	 * Function to transform elements from the inner type {@code I} to the outer type {@code O}.
	 */
	private final Function<I, O> transformToOuter;

	/**
	 * Function to transform elements from the outer type {@code O} to the inner type {@code I}.
	 */
	private final Function<O, I> transformToInner;

	/**
	 * Creates a new transforming list iterator.
	 * <p>
	 * If the specified list iterator is used by any other instance, the behavior is undefined. The specified transform
	 * functions will not be called with null elements and are not allowed to return null.
	 *
	 * @param innerListIterator
	 *            the wrapped/inner list iterator
	 * @param transformToOuter
	 *            transforms elements from the inner type {@code I} to the outer type {@code O}
	 * @param transformToInner
	 *            transforms elements from the outer type {@code O} to the inner type {@code I}
	 */
	public TransformingListIterator(
			ListIterator<I> innerListIterator, Function<I, O> transformToOuter, Function<O, I> transformToInner) {

		Objects.requireNonNull(innerListIterator, "The argument 'innerListIterator' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.innerListIterator = innerListIterator;
		this.transformToOuter = transformToOuter;
		this.transformToInner = transformToInner;
	}

	@Override
	protected ListIterator<I> getInnerIterator() {
		return innerListIterator;
	}

	@Override
	protected O transformToOuter(I innerElement) {
		if (innerElement == null)
			return null;

		O outerElement = transformToOuter.apply(innerElement);
		Objects.requireNonNull(outerElement, "The transformation must not create null instances.");
		return outerElement;
	}

	@Override
	protected I transformToInner(O outerElement) {
		if (outerElement == null)
			return null;

		I innerElement = transformToInner.apply(outerElement);
		Objects.requireNonNull(innerElement, "The transformation must not create null instances.");
		return innerElement;
	}

}

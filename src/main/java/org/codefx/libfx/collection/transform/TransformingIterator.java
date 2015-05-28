package org.codefx.libfx.collection.transform;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * An {@link Iterator} which wraps another iterator and transforms the returned elements from their inner type {@code I}
 * to an outer type {@code O}.
 * <p>
 * The transformation of null elements is fixed to {@code null -> null}. The transformation function specified during
 * construction does not have to handle null input elements and is not allowed to produce a null result.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner iterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this iterator
 */
public final class TransformingIterator<I, O> extends AbstractTransformingIterator<I, O> {

	private final Iterator<? extends I> innerIterator;
	private final Function<? super I, ? extends O> transformToOuter;

	/**
	 * Creates a new iterator.
	 * <p>
	 * If the specified iterator is used by any other instance, the behavior is undefined. The specified transform
	 * function will not be called with null elements and is not allowed to return null.
	 *
	 * @param innerIterator
	 *            the wrapped/inner iterator
	 * @param transformToOuter
	 *            transforms elements from the inner type {@code I} to the outer type {@code O}
	 */
	public TransformingIterator(
			Iterator<? extends I> innerIterator, Function<? super I, ? extends O> transformToOuter) {
		Objects.requireNonNull(innerIterator, "The argument 'innerIterator' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");

		this.innerIterator = innerIterator;
		this.transformToOuter = transformToOuter;
	}

	@Override
	protected Iterator<? extends I> getInnerIterator() {
		return innerIterator;
	}

	@Override
	protected O transformToOuter(I innerElement) {
		if (innerElement == null)
			return null;

		O outerElement = transformToOuter.apply(innerElement);
		Objects.requireNonNull(outerElement, "The transformation must not create null instances.");
		return outerElement;
	}

}

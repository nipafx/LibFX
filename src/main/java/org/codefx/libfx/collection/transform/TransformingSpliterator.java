package org.codefx.libfx.collection.transform;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Function;

/**
 * A {@link Spliterator} which wraps another spliterator and transform the returned elements from their inner type
 * {@code I} to an outer type {@code O}.
 * <p>
 * Note the comment on {@link AbstractTransformingSpliterator} regarding sorting.
 * <p>
 * The transformation of null elements of either inner or outer type is fixed to {@code null -> null}. The
 * transformation functions specified during construction do not have to handle null input elements and are not allowed
 * to produce a null result.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner spliterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this spliterator
 */
public final class TransformingSpliterator<I, O> extends AbstractTransformingSpliterator<I, O> {

	/**
	 * The wrapped/inner spliterator.
	 */
	private final Spliterator<I> innerSpliterator;

	/**
	 * Function to transform elements from the inner type {@code I} to the outer type {@code O}.
	 */
	private final Function<I, O> transformToOuter;

	/**
	 * Function to transform elements from the outer type {@code O} to the inner type {@code I}.
	 */
	private final Function<O, I> transformToInner;

	/**
	 * Creates a new transforming spliterator.
	 * <p>
	 * If the specified spliterator is used by any other instance, the behavior is undefined. The specified transform
	 * functions will not be called with null elements and are not allowed to return null.
	 *
	 * @param innerSpliterator
	 *            the wrapped/inner spliterator
	 * @param transformToOuter
	 *            transforms elements from the inner type {@code I} to the outer type {@code O}
	 * @param transformToInner
	 *            transforms elements from the outer type {@code O} to the inner type {@code I}
	 */
	public TransformingSpliterator(
			Spliterator<I> innerSpliterator, Function<I, O> transformToOuter, Function<O, I> transformToInner) {

		Objects.requireNonNull(innerSpliterator, "The argument 'innerSpliterator' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.innerSpliterator = innerSpliterator;
		this.transformToOuter = transformToOuter;
		this.transformToInner = transformToInner;
	}

	@Override
	protected Spliterator<I> getInnerSpliterator() {
		return innerSpliterator;
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

	@Override
	protected Spliterator<O> wrapNewSpliterator(Spliterator<I> newSpliterator) {
		return new TransformingSpliterator<>(newSpliterator, transformToOuter, transformToInner);
	}

}

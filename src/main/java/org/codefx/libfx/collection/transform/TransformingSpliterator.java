package org.codefx.libfx.collection.transform;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Function;

/**
 * A {@link Spliterator} which wraps another spliterator and transforms the returned elements from an inner type
 * {@code I} to an outer type {@code O} and vice versa.
 * <p>
 * The transformation of null elements of either inner or outer type is fixed to {@code null -> null}. The
 * transformation functions specified during construction do not have to handle null input elements and are not allowed
 * to produce a null result.
 * <p>
 * Note that this spliterator reports the exact same {@link Spliterator#SORTED SORTED} {@link #characteristics()
 * characteristic} as the inner one. It's {@link #getComparator()} transforms the elements it should compare from the
 * outer to the inner type and calls the inner spliterator's {@link Spliterator#getComparator() comparator} with it.
 * This means that sorting streams is always done by the inner spliterator's logic.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner spliterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this spliterator
 */
public final class TransformingSpliterator<I, O> extends AbstractTransformingSpliterator<I, O> {

	private final Spliterator<I> innerSpliterator;
	private final Function<? super I, ? extends O> transformToOuter;
	private final Function<? super O, ? extends I> transformToInner;

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
			Spliterator<I> innerSpliterator,
			Function<? super I, ? extends O> transformToOuter, Function<? super O, ? extends I> transformToInner) {

		Objects.requireNonNull(innerSpliterator, "The argument 'innerSpliterator' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");

		this.innerSpliterator = innerSpliterator;
		this.transformToInner = transformToInner;
		this.transformToOuter = transformToOuter;
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

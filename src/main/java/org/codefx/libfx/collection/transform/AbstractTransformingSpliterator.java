package org.codefx.libfx.collection.transform;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Abstract superclass to {@link Spliterator}s which wrap another spliterator and transform the returned elements from
 * their inner type {@code I} to an outer type {@code O}.
 * <p>
 * Note that this spliterator reports the exact same {@link Spliterator#SORTED SORTED} {@link #characteristics()
 * characteristic} as the inner one. It's {@link #getComparator()} transforms the elements it should compare from the
 * outer to the inner type and calls the inner spliterator's {@link Spliterator#getComparator() comparator} with it.
 * This means that sorting is always done by the inner spliterator's logic.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements returned by the wrapped/inner spliterator
 * @param <O>
 *            the outer type, i.e. the type of elements returned by this spliterator
 */
public abstract class AbstractTransformingSpliterator<I, O> implements Spliterator<O> {

	// #region IMPLEMENTATION OF 'Spliterator<O>'

	@Override
	public boolean tryAdvance(Consumer<? super O> action) {
		Consumer<I> transformThenAction = transformThen(action);
		return getInnerSpliterator().tryAdvance(transformThenAction);
	}

	@Override
	public void forEachRemaining(Consumer<? super O> action) {
		Consumer<I> transformThenAction = transformThen(action);
		getInnerSpliterator().forEachRemaining(transformThenAction);
	}

	@Override
	public Spliterator<O> trySplit() {
		Spliterator<I> newSpliterator = getInnerSpliterator().trySplit();
		if (newSpliterator == null)
			return null;
		else
			return wrapNewSpliterator(newSpliterator);
	}

	@Override
	public long estimateSize() {
		return getInnerSpliterator().estimateSize();
	}

	@Override
	public long getExactSizeIfKnown() {
		return getInnerSpliterator().getExactSizeIfKnown();
	}

	@Override
	public int characteristics() {
		return getInnerSpliterator().characteristics();
	}

	@Override
	public boolean hasCharacteristics(int characteristics) {
		return getInnerSpliterator().hasCharacteristics(characteristics);
	}

	@Override
	public Comparator<? super O> getComparator() {
		Comparator<? super I> innerComparator = getInnerSpliterator().getComparator();
		if (innerComparator == null)
			return null;

		return (leftOuter, rightOuter) -> {
			I leftInner = transformToInner(leftOuter);
			I rightInner = transformToInner(rightOuter);
			return innerComparator.compare(leftInner, rightInner);
		};
	}

	// #end IMPLEMENTATION OF 'Spliterator<O>'

	// #region ABSTRACT METHODS

	/**
	 * @return the wrapped/inner spliterator
	 */
	protected abstract Spliterator<I> getInnerSpliterator();

	/**
	 * Transforms an element from the inner type {@code I} to the outer type {@code O}.
	 *
	 * @param innerElement
	 *            an element returned by the {@link #getInnerSpliterator() innerSpliterator}
	 * @return an equivalent element of type {@code O}
	 */
	protected abstract O transformToOuter(I innerElement);

	/**
	 * Transforms an element from the outer type {@code O} to the inner type {@code I}.
	 *
	 * @param outerElement
	 *            an element of type {@code O}
	 * @return an equivalent element of type {@code I}
	 */
	protected abstract I transformToInner(O outerElement);

	/**
	 * Transforms the specified element of type {@code I} with {@link #transformToOuter(Object) transformToOuter} before
	 * passing it to the specified consumer.
	 *
	 * @param action
	 *            the {@link Consumer} of outer elements to which the transformed element will be passed
	 * @return a {@link Consumer} of inner elements
	 */
	private Consumer<I> transformThen(Consumer<? super O> action) {
		return innerElement -> {
			O asOuterElement = transformToOuter(innerElement);
			action.accept(asOuterElement);
		};
	}

	/**
	 * Wraps the specified spliterator over {@code I} into a spliterator over {@code O}.
	 * <p>
	 * This method is called inside {@link #trySplit()}. It is not called with null.
	 *
	 * @param newSpliterator
	 *            the newly created inner {@link Spliterator Spliterator&lt;I&gt;}
	 * @return a {@link Spliterator Spliterator&lt;O&gt;}
	 */
	protected abstract Spliterator<O> wrapNewSpliterator(Spliterator<I> newSpliterator);

	// #end ABSTRACT METHODS

}

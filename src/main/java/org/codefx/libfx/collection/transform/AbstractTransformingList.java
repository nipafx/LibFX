package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * Abstract superclass to {@link List}s which transform another collection.
 * <p>
 * This class allows null elements. Subclasses might override that by implementing aggressive null checks.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements contained in the wrapped/inner list
 * @param <O>
 *            the outer type, i.e. the type of elements appearing to be in this list
 */
abstract class AbstractTransformingList<I, O> extends AbstractTransformingCollection<I, O> implements List<O> {

	@Override
	protected final Collection<I> getInnerCollection() {
		return getInnerList();
	}

	/**
	 * @return the inner list wrapped by this transforming list
	 */
	protected abstract List<I> getInnerList();

	// get & index

	@Override
	public O get(int index) {
		I innerElement = getInnerList().get(index);
		return transformToOuter(innerElement);
	}

	@Override
	public int indexOf(Object object) {
		if (isOuterElement(object)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInner' might. In that case a
			 * 'ClassCastException' will be thrown which is in accordance with the contract of 'indexOf'. If
			 * 'isOuterElement' does its job well (which can be hard due to erasure) this will not happen.
			 */
			O outerElement = (O) object;
			I innerElement = transformToInner(outerElement);
			return getInnerList().indexOf(innerElement);
		} else
			return -1;
	}

	@Override
	public int lastIndexOf(Object object) {
		if (isOuterElement(object)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInner' might. In that case a
			 * 'ClassCastException' will be thrown which is in accordance with the contract of 'lastIndexOf'. If
			 * 'isOuterElement' does its job well (which can be hard due to erasure) this will not happen.
			 */
			O outerElement = (O) object;
			I innerElement = transformToInner(outerElement);
			return getInnerList().lastIndexOf(innerElement);
		} else
			return -1;
	}

	// mutate

	@Override
	public void add(int index, O element) {
		I innerElement = transformToInner(element);
		getInnerList().add(index, innerElement);
	}

	@Override
	public boolean addAll(int index, Collection<? extends O> otherCollection) {
		Objects.requireNonNull(otherCollection, "The argument 'otherCollection' must not be null.");

		return callAddAllOnInner(index, otherCollection);
	}

	/**
	 * Wraps the specified collection into a transformation and calls {@link List#addAll(int, Collection) addAll} on the
	 * {@link #getInnerList() innerList}.
	 * <p>
	 * Subclasses may choose to use this method if they override {@link #addAll(int, Collection)}.
	 * <p>
	 * Accessing the wrapped collection will lead to {@link ClassCastException}s when its elements are not of this
	 * list's outer type {@code O}. Consider using {@link #callAddOnThis(int, Collection)}.
	 *
	 * @param startIndex
	 *            index at which to insert the first element from the specified collection
	 * @param otherCollection
	 *            the parameter to {@code addAll}
	 * @return result of the call to {@code addAll}
	 */
	protected final boolean callAddAllOnInner(int startIndex, Collection<? extends O> otherCollection) {
		Collection<I> asInnerCollection = new TransformToReadOnlyInnerCollection<>(otherCollection);
		return getInnerList().addAll(startIndex, asInnerCollection);
	}

	/**
	 * Iterates over the specified collection and calls {@link #add(int, Object) add()} (on this list) for each element.
	 * <p>
	 * Subclasses may choose to use this method if they override {@link #addAll(int, Collection)}.
	 * <p>
	 * Manually iterating over the specified collection and calling {@code this.}{@link #add(int, Object)} individually
	 * might break guarantees (e.g. regarding atomicity) or optimizations made by the inner collection. Consider using
	 * {@link #callAddAllOnInner(int, Collection)}.
	 *
	 * @param startIndex
	 *            index at which to insert the first element from the specified collection
	 * @param otherCollection
	 *            the collection whose elements are passed to {@code add}
	 * @return true if at least one call to {@code add} returns true; otherwise false
	 */
	protected final boolean callAddOnThis(int startIndex, Collection<? extends O> otherCollection) {
		boolean changed = false;
		int currentIndex = startIndex;
		for (O entry : otherCollection) {
			add(currentIndex, entry);
			currentIndex++;
			changed = true;
		}
		return changed;
	}

	@Override
	public O set(int index, O element) {
		I innerElement = transformToInner(element);
		I formerInnerElement = getInnerList().set(index, innerElement);
		return transformToOuter(formerInnerElement);
	}

	@Override
	public void replaceAll(UnaryOperator<O> operator) {
		Objects.requireNonNull(operator, "The argument 'operator' must not be null.");

		UnaryOperator<I> operatorOnInner = inner -> transformToInner(operator.apply(transformToOuter(inner)));
		getInnerList().replaceAll(operatorOnInner);
	}

	@Override
	public O remove(int index) {
		I removedInnerElement = getInnerList().remove(index);
		return transformToOuter(removedInnerElement);
	}

	// sort

	@Override
	public void sort(Comparator<? super O> comparator) {
		Objects.requireNonNull(comparator, "The argument 'comparator' must not be null.");

		Comparator<I> comparatorOfInner = (leftInner, rightInner) ->
				comparator.compare(
						transformToOuter(leftInner),
						transformToOuter(rightInner));
		getInnerList().sort(comparatorOfInner);
	}

	// iteration & sublist

	@Override
	public ListIterator<O> listIterator() {
		return new ForwardingTransformingIterator();
	}

	@Override
	public ListIterator<O> listIterator(int startIndex) {
		return new ForwardingTransformingIterator(startIndex);
	}

	@Override
	public List<O> subList(int fromIndex, int toIndex) {
		return new ForwardingSubList(fromIndex, toIndex);
	}

	// #begin OBJECT

	@Override
	public final boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof List))
			return false;

		List<?> other = (List<?>) object;

		// check all elements
		ListIterator<O> thisIterator = listIterator();
		ListIterator<?> otherIterator = other.listIterator();
		while (thisIterator.hasNext() && otherIterator.hasNext())
			if (!(Objects.equals(thisIterator.next(), otherIterator.next())))
				return false;

		// make sure both have the same length
		return !thisIterator.hasNext() && !otherIterator.hasNext();
	}

	@Override
	public final int hashCode() {
		int hashCode = 1;
		for (O element : this)
			hashCode = 31 * hashCode + (element == null ? 0 : element.hashCode());
		return hashCode;
	}

	// #end OBJECT

	// #begin INNER CLASSES

	/**
	 * A transforming list iterator which directly forwards all transformation calls to the abstract methods in this
	 * list.
	 */
	private class ForwardingTransformingIterator extends AbstractTransformingListIterator<I, O> {

		private final ListIterator<I> innerIterator;

		public ForwardingTransformingIterator() {
			innerIterator = getInnerList().listIterator();
		}

		public ForwardingTransformingIterator(int startIndex) {
			innerIterator = getInnerList().listIterator(startIndex);
		}

		@Override
		protected ListIterator<I> getInnerIterator() {
			return innerIterator;
		}

		@Override
		protected O transformToOuter(I innerElement) {
			return AbstractTransformingList.this.transformToOuter(innerElement);
		}

		@Override
		protected I transformToInner(O outerElement) {
			return AbstractTransformingList.this.transformToInner(outerElement);
		}

	}

	/**
	 * A transforming list which is the sub list of this list and directly forwards all transformation calls to the
	 * abstract methods in this list.
	 */
	private class ForwardingSubList extends AbstractTransformingList<I, O> {

		private final List<I> innerSubList;

		public ForwardingSubList(int fromIndex, int toIndex) {
			innerSubList = AbstractTransformingList.this.getInnerList().subList(fromIndex, toIndex);
		}

		@Override
		protected List<I> getInnerList() {
			return innerSubList;
		}

		@Override
		protected boolean isInnerElement(Object object) {
			return AbstractTransformingList.this.isInnerElement(object);
		}

		@Override
		protected O transformToOuter(I innerElement) throws ClassCastException {
			return AbstractTransformingList.this.transformToOuter(innerElement);
		}

		@Override
		protected boolean isOuterElement(Object object) {
			return AbstractTransformingList.this.isOuterElement(object);
		}

		@Override
		protected I transformToInner(O outerElement) throws ClassCastException {
			return AbstractTransformingList.this.transformToInner(outerElement);
		}

	}

	// #end INNER CLASSES

}

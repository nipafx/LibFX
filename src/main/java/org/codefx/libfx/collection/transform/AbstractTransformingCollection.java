package org.codefx.libfx.collection.transform;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract superclass to {@link Collection}s which transform another collection.
 * <p>
 * This class allows null elements. Subclasses might override that by implementing aggressive null checks.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements contained in the wrapped/inner collection
 * @param <O>
 *            the outer type, i.e. the type of elements appearing to be in this collection
 */
abstract class AbstractTransformingCollection<I, O> implements Collection<O> {

	// #region CONSTANTS

	/**
	 * The largest possible (non-power of two) array size.
	 * <p>
	 * Note that some collections can contain more entries than fit into {@code int} (see e.g.
	 * {@link java.util.concurrent.ConcurrentHashMap#mappingCount() mappingCount()}). In that case, the return value of
	 * {@link #size()} is capped at {@link Integer#MAX_VALUE}, which is ok because it is greater than
	 * {@code MAX_ARRAY_SIZE} and will throw an error anyways.
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
	 * The message used for {@link OutOfMemoryError}s.
	 */
	private static final String COLLECTION_TOO_LARGE_ERROR_MESSAGE = "Required array size too large";

	// #end CONSTANTS

	// #region IMPLEMENTATION OF 'Collection<O>'

	/**
	 * Indicates whether the specified collection is equivalent to this one. This is the case if it is also an
	 * {@link AbstractTransformingCollection} and wraps the same {@link #getInnerCollection() innerCollection}.
	 *
	 * @param otherCollection
	 *            the {@link Collection} which is compared with this one
	 * @return true if this and the specified collection are equivalent
	 */
	protected final boolean isThisCollection(Collection<?> otherCollection) {
		if (otherCollection == this)
			return true;

		if (otherCollection instanceof AbstractTransformingCollection) {
			AbstractTransformingCollection<?, ?> otherTransformingCollection =
					(AbstractTransformingCollection<?, ?>) otherCollection;
			boolean sameInnerCollection = otherTransformingCollection.getInnerCollection() == getInnerCollection();
			return sameInnerCollection;
		}

		return false;
	}

	// size

	@Override
	public int size() {
		return getInnerCollection().size();
	}

	@Override
	public boolean isEmpty() {
		return getInnerCollection().isEmpty();
	}

	// contains

	@Override
	public boolean contains(Object object) {
		if (isOuterElement(object)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInner' might. In that case a
			 * 'ClassCastException' will be thrown which is in accordance with the contract of 'contains'. If
			 * 'isOuterElement' does its job well (which can be hard due to erasure) this will not happen.
			 */
			O outerElement = (O) object;
			I innerElement = transformToInner(outerElement);
			return getInnerCollection().contains(innerElement);
		} else
			return false;
	}

	@Override
	public boolean containsAll(Collection<?> otherCollection) {
		Objects.requireNonNull(otherCollection, "The argument 'otherCollection' must not be null.");
		if (isThisCollection(otherCollection))
			return true;

		return callContainsAllOnInner(otherCollection);
	}

	/**
	 * Wraps the specified collection into a transformation and calls {@link Collection#containsAll(Collection)
	 * containsAll} on the {@link #getInnerCollection() innerCollection}.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #containsAll(Collection)}.
	 * <p>
	 * Accessing the wrapped collection will lead to {@link ClassCastException}s when its elements are not of this
	 * collection's outer type {@code O}. Consider using {@link #callContainsOnThis(Collection)}.
	 *
	 * @param otherCollection
	 *            the parameter to {@code containsAll}
	 * @return result of the call to {@code containsAll}
	 */
	protected final boolean callContainsAllOnInner(Collection<?> otherCollection) {
		Collection<I> asInnerCollection = new TransformToReadOnlyInnerCollection<>(otherCollection);
		return getInnerCollection().containsAll(asInnerCollection);
	}

	/**
	 * Iterates over the specified collection and calls {@link #contains(Object)} (on this collection) for each element.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #containsAll(Collection)}.
	 * <p>
	 * Manually iterating over the specified collection and calling {@code this.}{@link #contains(Object)} individually
	 * might break guarantees or optimizations made by the inner collection. Consider using
	 * {@link #callContainsAllOnInner(Collection)}.
	 *
	 * @param otherCollection
	 *            the collection whose elements are passed to {@code contains}
	 * @return false if at least one call to {@code contains} returns false; otherwise true
	 */
	protected final boolean callContainsOnThis(Collection<?> otherCollection) {
		for (Object item : otherCollection)
			if (!contains(item))
				return false;
		return true;
	}

	// add

	@Override
	public boolean add(O element) {
		I innerElement = transformToInner(element);
		return getInnerCollection().add(innerElement);
	}

	@Override
	public boolean addAll(Collection<? extends O> otherCollection) {
		Objects.requireNonNull(otherCollection, "The argument 'otherCollection' must not be null.");

		return callAddAllOnInner(otherCollection);
	}

	/**
	 * Wraps the specified collection into a transformation and calls {@link Collection#addAll(Collection) addAll} on
	 * the {@link #getInnerCollection() innerCollection}.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #addAll(Collection)}.
	 * <p>
	 * Accessing the wrapped collection will lead to {@link ClassCastException}s when its elements are not of this
	 * collection's outer type {@code O}. Consider using {@link #callAddOnThis(Collection)}.
	 *
	 * @param otherCollection
	 *            the parameter to {@code addAll}
	 * @return result of the call to {@code addAll}
	 */
	protected final boolean callAddAllOnInner(Collection<? extends O> otherCollection) {
		Collection<I> asInnerCollection = new TransformToReadOnlyInnerCollection<>(otherCollection);
		return getInnerCollection().addAll(asInnerCollection);
	}

	/**
	 * Iterates over the specified collection and calls {@link #add(Object) add(O)} (on this collection) for each
	 * element.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #addAll(Collection)}.
	 * <p>
	 * Manually iterating over the specified collection and calling {@code this.}{@link #add(Object)} individually might
	 * break guarantees (e.g. regarding atomicity) or optimizations made by the inner collection. Consider using
	 * {@link #callAddAllOnInner(Collection)}.
	 *
	 * @param otherCollection
	 *            the collection whose elements are passed to {@code add}
	 * @return true if at least one call to {@code add} returns true; otherwise false
	 */
	protected final boolean callAddOnThis(Collection<? extends O> otherCollection) {
		boolean changed = false;
		for (O entry : otherCollection)
			changed |= add(entry);
		return changed;
	}

	// remove

	@Override
	public boolean remove(Object object) {
		if (isOuterElement(object)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInner' might. In that case a
			 * 'ClassCastException' will be thrown which is in accordance with the contract of 'remove'. If
			 * 'isOuterElement' does its job well (which can be hard due to erasure) this will not happen.
			 */
			O outerElement = (O) object;
			I innerElement = transformToInner(outerElement);
			return getInnerCollection().remove(innerElement);
		} else
			return false;
	}

	@Override
	public boolean removeIf(Predicate<? super O> filter) {
		Objects.requireNonNull(filter, "The argument 'filter' must not be null.");

		Predicate<I> innerFilter = innerElement -> {
			O outerElement = transformToOuter(innerElement);
			return filter.test(outerElement);
		};
		return getInnerCollection().removeIf(innerFilter);
	}

	@Override
	public boolean removeAll(Collection<?> otherCollection) {
		Objects.requireNonNull(otherCollection, "The argument 'otherCollection' must not be null.");
		if (isThisCollection(otherCollection))
			return clearToRemoveAll();

		return callRemoveAllOnInner(otherCollection);
	}

	/**
	 * Calls {@link #clear()} to remove all instances from the {@link #getInnerCollection() innerCollection}.
	 *
	 * @return true if the call changed the innerCollection
	 */
	protected final boolean clearToRemoveAll() {
		if (size() == 0)
			return false;

		clear();
		return true;
	}

	/**
	 * Wraps the specified collection into a transformation and calls {@link Collection#removeAll(Collection) removeAll}
	 * on the {@link #getInnerCollection() innerCollection}.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #removeAll(Collection)}.
	 * <p>
	 * Accessing the wrapped collection will lead to {@link ClassCastException}s when its elements are not of this
	 * collection's outer type {@code O}. Consider using {@link #callRemoveOnThis(Collection)}.
	 *
	 * @param otherCollection
	 *            the parameter to {@code removeAll}
	 * @return result of the call to {@code removeAll}
	 */
	protected final boolean callRemoveAllOnInner(Collection<?> otherCollection) {
		Collection<I> asInnerCollection = new TransformToReadOnlyInnerCollection<>(otherCollection);
		return getInnerCollection().removeAll(asInnerCollection);
	}

	/**
	 * Iterates over the specified collection and calls {@link #remove(Object)} (on this collection) for each element.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #removeAll(Collection)}.
	 * <p>
	 * Manually iterating over the specified collection and calling {@code this.}{@link #remove(Object)} individually
	 * might break guarantees (e.g. regarding atomicity) or optimizations made by the inner collection. Consider using
	 * {@link #callRemoveAllOnInner(Collection)}.
	 *
	 * @param otherCollection
	 *            the collection whose elements are passed to {@code remove}
	 * @return true if at least one call to {@code remove} returns true; otherwise false
	 */
	protected final boolean callRemoveOnThis(Collection<?> otherCollection) {
		boolean changed = false;
		for (Object entry : otherCollection)
			changed |= remove(entry);
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> otherCollection) {
		Objects.requireNonNull(otherCollection, "The argument 'otherCollection' must not be null.");
		if (isThisCollection(otherCollection))
			return false;

		return callRetainAllOnInner(otherCollection);
	}

	/**
	 * Wraps the specified collection into a transformation and calls {@link Collection#retainAll(Collection) retainAll}
	 * on the {@link #getInnerCollection() innerCollection}.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #retainAll(Collection)}.
	 * <p>
	 * Accessing the wrapped collection will lead to {@link ClassCastException}s when its elements are not of this
	 * collection's outer type {@code O}. Consider using {@link #retianByCallingRemoveOnThis(Collection)}.
	 *
	 * @param otherCollection
	 *            the parameter to {@code retainAll}
	 * @return result of the call to {@code retainAll}
	 */
	protected final boolean callRetainAllOnInner(Collection<?> otherCollection) {
		Collection<I> asInnerCollection = new TransformToReadOnlyInnerCollection<>(otherCollection);
		return getInnerCollection().retainAll(asInnerCollection);
	}

	/**
	 * Iterates over this collection (i.e. over the outer elements) and removes each element which is not contained in
	 * the specified collection.
	 * <p>
	 * Subclasses may chose to use this method if they override {@link #retainAll(Collection)}.
	 * <p>
	 * Manually iterating over this collection and calling {@code this.}{@link #remove(Object)} individually might break
	 * guarantees (e.g. regarding atomicity) or optimizations made by the inner collection. Consider using
	 * {@link #callRetainAllOnInner(Collection)}.
	 *
	 * @param otherCollection
	 *            the collection whose elements are not removed from this collection
	 * @return true if at least one element was removed; otherwise false
	 */
	protected final boolean retianByCallingRemoveOnThis(Collection<?> otherCollection) {
		boolean changed = false;
		for (Iterator<O> iterator = iterator(); iterator.hasNext();) {
			O element = iterator.next();
			boolean remove = !otherCollection.contains(element);
			if (remove) {
				iterator.remove();
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public void clear() {
		getInnerCollection().clear();
	}

	// iteration

	@Override
	public Iterator<O> iterator() {
		// use an iterator which immediately forwards all transformation calls to this collection;
		// this excludes the 'TransformingIterator' which does some null handling on its own
		return new ForwardingTransformingIterator();
	}

	@Override
	public Spliterator<O> spliterator() {
		// use an spliterator which immediately forwards all transformation calls to this collection;
		// this excludes the 'TransformingSpliterator' which does some null handling on its own
		return new ForwardingTransformingSpliterator();
	}

	// #region TOARRAY

	@Override
	public Object[] toArray() {
		/*
		 * Because this collection view might be used on a map which allows concurrent modifications, the method must be
		 * able to handle the situation where the number of elements changes throughout the its execution. For this
		 * reason the code is inspired by 'ConcurrentHashMap.CollectionView.toArray'.
		 */

		Object[] array = createObjectArrayWithMapSize();

		int currentElementIndex = 0;
		for (O element : this) {
			// the map might have grown, in which case a new array has to be allocated
			array = provideArrayWithSufficientLength(array, currentElementIndex);
			array[currentElementIndex] = element;
			currentElementIndex++;
		}

		// the map might have shrunk or a larger array might have been allocated;
		// in both cases the array has to be truncated to the correct length
		return truncateArrayToLength(array, currentElementIndex);
	}

	/**
	 * Creates an object array with this collection's current {@link #size()}.
	 *
	 * @return an empty object array
	 */
	private Object[] createObjectArrayWithMapSize() {
		int size = size();
		if (size > MAX_ARRAY_SIZE)
			throw new OutOfMemoryError(COLLECTION_TOO_LARGE_ERROR_MESSAGE);

		return new Object[size];
	}

	/**
	 * Provides an array with at least the specified minimum length. If the specified array has that length, it is
	 * returned. Otherwise a new array with an unspecified length but sufficient is returned to which the input array's
	 * elements were copied.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array whose length is tested
	 * @param minLength
	 *            the minimum length of the required array
	 * @return an array with at least length {@code minLength}
	 */
	private static <T> T[] provideArrayWithSufficientLength(T[] array, int minLength) {
		boolean arrayHasSufficientLength = minLength < array.length;
		if (arrayHasSufficientLength)
			return array;
		else
			return copyToLargerArray(array);
	}

	/**
	 * Creates a new array with a length greater than the specified one's and copies all elements to it.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array whose elements are copied
	 * @return a new array
	 */
	private static <T> T[] copyToLargerArray(T[] array) {
		if (array.length == MAX_ARRAY_SIZE)
			throw new OutOfMemoryError(COLLECTION_TOO_LARGE_ERROR_MESSAGE);
		int newSize = getIncreasedSize(array.length);
		return Arrays.copyOf(array, newSize);
	}

	/**
	 * Returns the size for the new array, which is guaranteed to be greater than the specified size.
	 *
	 * @param size
	 *            the current size
	 * @return the new size
	 */
	private static int getIncreasedSize(int size) {
		// bit shifting is used to increase the size by ~ 50 %
		boolean sizeWouldBeIncreasedAboveMaximum = size >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1;
		if (sizeWouldBeIncreasedAboveMaximum)
			return MAX_ARRAY_SIZE;
		else
			return size + (size >>> 1) + 1;
	}

	/**
	 * Returns an array with the specified length.If the specified array has the correct length, it is returned,
	 * otherwise a new array is allocated and the values are copied into it.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array to be truncated
	 * @param length
	 *            the new array's length
	 * @return an array with the specified length
	 */
	private static <T> T[] truncateArrayToLength(T[] array, int length) {
		if (array.length == length)
			return array;
		else
			return Arrays.copyOf(array, length);
	}

	@Override
	public <T> T[] toArray(T[] inputArray) {
		/*
		 * Because this collection view might be used on a map which allows concurrent modifications, the method must be
		 * able to handle the situation where the number of elements changes throughout its execution. For this reason
		 * the code is inspired by 'ConcurrentHashMap.CollectionView.toArray'.
		 */
		Objects.requireNonNull(inputArray, "The argument 'inputArray' must not be null.");

		T[] array = provideTypedArrayWithMapSize(inputArray);

		int currentElementIndex = 0;
		for (O element : this) {
			// the map might have grown, in which case a new array has to be allocated
			array = provideArrayWithSufficientLength(array, currentElementIndex);
			@SuppressWarnings("unchecked")
			// due to erasure, this cast can never fail, but writing the reference to the array can;
			// this would throw a ArrayStoreException which is in accordance with the contract of
			// 'Collection.toArray(T[])'
			T unsafelyTypedElement = (T) element;
			array[currentElementIndex] = unsafelyTypedElement;
			currentElementIndex++;
		}

		// if the original array is still used, it must be terminated with null as per contract of this method;
		// otherwise, the array created above might be too large so it has to be truncated
		return markOrTruncateArray(inputArray, array, currentElementIndex);
	}

	/**
	 * Provides an array of the same type as the specified array which has at least the length of this collection's
	 * current {@link #size() size}. If the input array is sufficiently long, it is returned; otherwise a new array is
	 * created.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param inputArray
	 *            the input array
	 * @return an array {@code T[]} with length equal to or greater than {@link #size() size}
	 */
	private <T> T[] provideTypedArrayWithMapSize(T[] inputArray) {
		int size = size();
		boolean arrayHasSufficientLength = size <= inputArray.length;
		if (arrayHasSufficientLength)
			return inputArray;
		else {
			if (size > MAX_ARRAY_SIZE)
				throw new OutOfMemoryError(COLLECTION_TOO_LARGE_ERROR_MESSAGE);

			@SuppressWarnings("unchecked")
			// the array created by 'Array.newInstance' is of the correct type
			T[] array = (T[]) Array.newInstance(inputArray.getClass().getComponentType(), size);
			return array;
		}
	}

	/**
	 * The specified {@code array} is prepared to be returned by {@link #toArray(Object[])}.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param inputArray
	 *            the array which was given to {@link #toArray(Object[])}
	 * @param array
	 *            the array which contains this collection's elements (might be the same as {@code inputArray})
	 * @param nrOfElements
	 *            the number of elements in the {@code array}
	 * @return an array which fulfills the contract of {@link #toArray(Object[])}
	 */
	private static <T> T[] markOrTruncateArray(T[] inputArray, T[] array, int nrOfElements) {
		boolean usingInputArray = array == inputArray;
		if (usingInputArray)
			return markEndWithNull(array, nrOfElements);
		else
			return truncateArrayToLength(array, nrOfElements);
	}

	/**
	 * Returns the specified array but with a null reference at the specified index if the array's length allows it.
	 *
	 * @param <T>
	 *            the component type of the array
	 * @param array
	 *            the array which might be edited
	 * @param nullIndex
	 *            the index where a null reference has to inserted
	 * @return the specified array
	 */
	private static <T> T[] markEndWithNull(T[] array, int nullIndex) {
		if (nullIndex < array.length)
			array[nullIndex] = null;

		return array;
	}

	// #end TOARRAY

	// #end IMPLEMENTATION OF 'Collection<O>'

	// #region OBJECT

	@Override
	public abstract boolean equals(Object object);

	@Override
	public abstract int hashCode();

	@Override
	public String toString() {
		return stream()
				.map(Objects::toString)
				.collect(Collectors.joining(", ", "[", "]"));
	}

	// #end OBJECT

	// #region ABSTRACT METHODS

	/**
	 * @return the inner collection wrapped by this transforming collection
	 */
	protected abstract Collection<I> getInnerCollection();

	/**
	 * Checks whether the specified object might be an inner element.
	 * <p>
	 * This method does not have to be exact (which might be impossible due to involved generic types) and might produce
	 * false positives (but no false negatives).
	 *
	 * @param object
	 *            the object to check; may be null
	 * @return true if the object might be an inner element
	 */
	protected abstract boolean isInnerElement(Object object);

	/**
	 * Transforms the specified element to an instance of the outer type.
	 * <p>
	 * It can not be guaranteed that the specified element is really of the inner type. If not, an exception can be
	 * thrown.
	 *
	 * @param innerElement
	 *            the element to transform; may be null
	 * @return the transformed element
	 * @throws ClassCastException
	 *             if the specified element is not of the correct type
	 */
	protected abstract O transformToOuter(I innerElement) throws ClassCastException;

	/**
	 * Checks whether the specified object might be an outer element.
	 * <p>
	 * This method does not have to be exact (which might be impossible due to involved generic types) and might produce
	 * false positives (but no false negatives).
	 *
	 * @param object
	 *            the object to check; may be null
	 * @return true if the object might be an outer element
	 */
	protected abstract boolean isOuterElement(Object object);

	/**
	 * Transforms the specified element to an instance of the inner type.
	 * <p>
	 * It can not be guaranteed that the specified element is really of the outer type. If not, an exception can be
	 * thrown.
	 *
	 * @param outerElement
	 *            the element to transform; may be null
	 * @return the transformed element
	 * @throws ClassCastException
	 *             if the specified element is not of the correct type
	 */
	protected abstract I transformToInner(O outerElement) throws ClassCastException;

	// #end ABSTRACT METHODS

	// #region INNER CLASSES

	/**
	 * A transforming iterator which directly forwards all transformation calls to the abstract methods in this
	 * collection.
	 */
	private class ForwardingTransformingIterator extends AbstractTransformingIterator<I, O> {

		private final Iterator<I> innerIterator = getInnerCollection().iterator();

		@Override
		protected Iterator<I> getInnerIterator() {
			return innerIterator;
		}

		@Override
		protected O transformToOuter(I innerElement) {
			return AbstractTransformingCollection.this.transformToOuter(innerElement);
		}

	}

	/**
	 * A transforming spliterator which directly forwards all transformation calls to the abstract methods in this
	 * collection.
	 */
	private class ForwardingTransformingSpliterator extends AbstractTransformingSpliterator<I, O> {

		private final Spliterator<I> innerSpliterator = getInnerCollection().spliterator();

		@Override
		protected Spliterator<I> getInnerSpliterator() {
			return innerSpliterator;
		}

		@Override
		protected O transformToOuter(I innerElement) {
			return AbstractTransformingCollection.this.transformToOuter(innerElement);
		}

		@Override
		protected I transformToInner(O outerElement) {
			return AbstractTransformingCollection.this.transformToInner(outerElement);
		}

		@Override
		protected Spliterator<O> wrapNewSpliterator(Spliterator<I> newSpliterator) {
			return new ForwardingTransformingSpliterator();
		}

	}

	/**
	 * Wraps a collection with any element type {@code E} into a transforming collection with the inner type {@code I}.
	 * <p>
	 * This works under the assumption that {@code E = O}. Of course, it is highly unsafe and can lead to
	 * {@link ClassCastException}s when this is not the case and the collection is accessed.
	 *
	 * @param <E>
	 *            the type of elements in the specified collection
	 */
	private class TransformToReadOnlyInnerCollection<E> extends AbstractReadOnlyTransformingCollection<E, I> {

		private final Collection<E> transformedCollection;

		public TransformToReadOnlyInnerCollection(Collection<E> transformedCollection) {
			assert transformedCollection != null : "The argument 'innerCollection' must not be null.";

			this.transformedCollection = transformedCollection;
		}

		@Override
		protected Collection<E> getInnerCollection() {
			return transformedCollection;
		}

		@Override
		protected boolean isInnerElement(Object object) {
			return AbstractTransformingCollection.this.isOuterElement(object);
		}

		@Override
		protected I transformToOuter(E innerElement) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInner' might. In that case a
			 * 'ClassCastException' will be thrown which is in accordance with the contract of the method which created
			 * this wrapper.
			 */
			O asClientOuterElement = (O) innerElement;
			return AbstractTransformingCollection.this.transformToInner(asClientOuterElement);
		}

		@Override
		protected boolean isOuterElement(Object object) {
			return AbstractTransformingCollection.this.isInnerElement(object);
		}

		@Override
		protected E transformToInner(I outerElement) {
			O transformedToClientOuterElement = AbstractTransformingCollection.this.transformToOuter(outerElement);
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but whatever happens next might. In that case a
			 * 'ClassCastException' will be thrown which is in accordance with the contract of the method which created
			 * this wrapper.
			 */
			E asThisInnerElement = (E) transformedToClientOuterElement;
			return asThisInnerElement;
		}

		@Override
		public boolean equals(Object object) {
			if (object == this)
				return true;
			if (!(object instanceof Collection))
				return false;

			Collection<?> other = (Collection<?>) object;
			if (isThisCollection(other))
				return true;

			return other.containsAll(this) && this.containsAll(other);
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			for (I clientInnerElement : this)
				hashCode = 31 * hashCode + (clientInnerElement == null ? 0 : clientInnerElement.hashCode());
			return hashCode;
		}

	}

	// #end INNER CLASSES

}

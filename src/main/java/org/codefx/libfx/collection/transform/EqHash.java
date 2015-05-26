package org.codefx.libfx.collection.transform;

import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

/**
 * Wraps elements which are pout into an inner hashing data structure and delegates {@link #equals(Object)} and
 * {@link #hashCode()} to functions specified during construction.
 *
 * @param <E>
 *            the type of the wrapped elements
 */
class EqHash<E> {

	/**
	 * The default hash code used for null keys.
	 * <p>
	 * This value is mentioned in the comments of {@link EqualityTransformingMap} and {@link EqualityTransformingSet}.
	 * Update on change.
	 */
	public static final int NULL_KEY_HASH_CODE = 0;

	private final E element;
	private final BiPredicate<? super E, ? super E> equals;
	private final ToIntFunction<? super E> hash;

	public EqHash(E element, BiPredicate<? super E, ? super E> equals, ToIntFunction<? super E> hash) {
		// null is allowed as an element
		assert equals != null : "The argument 'equals' must not be null.";
		assert hash != null : "The argument 'hash' must not be null.";

		this.element = element;
		this.equals = equals;
		this.hash = hash;
	}

	/**
	 * @return the wrapped element
	 */
	public E getElement() {
		return element;
	}

	@Override
	public int hashCode() {
		return hash.applyAsInt(element);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EqHash))
			return false;

		@SuppressWarnings("unchecked")
		// This cast is ok because no instance of EqHash can ever leave the inner map (without being transformed
		// by the equality transforming map).
		// If it can not leave it can not end up in an equality test in another map.
		EqHash<E> other = (EqHash<E>) obj;
		return equals.test(this.element, other.element);
	}

}

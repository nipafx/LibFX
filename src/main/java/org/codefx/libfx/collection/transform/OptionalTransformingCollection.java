package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

/**
 * A transforming {@link Collection} which is a flattened view on a collection of {@link Optional}s, i.e. it only
 * presents the contained values.
 * <p>
 * TODO null not allowed in inner Collection and only in outer collection if default value; fix that behavior
 *
 * @param <E>
 *            the type of elements contained in the {@code Optional}s
 */
public final class OptionalTransformingCollection<E> extends AbstractTransformingCollection<Optional<E>, E> {

	// #region FIELDS

	private final Collection<Optional<E>> innerCollection;

	private final Class<? super E> outerTypeToken;

	private final E outerDefaultElement;

	// #end FIELDS

	// #region CONSTRUCTION

	/**
	 * Creates a new transforming collection which uses a type token to identify the outer elements.
	 *
	 * @param innerCollection
	 *            the wrapped collection
	 * @param outerTypeToken
	 *            the token for the outer type
	 * @param outerDefaultElement
	 *            the element used to represent {@link Optional#empty()}; it is of crucial importance that this element
	 *            does not occur inside an optional because then the transformations are from that optional to an
	 *            element and back are not inverse
	 */
	public OptionalTransformingCollection(
			Collection<Optional<E>> innerCollection,
			Class<? super E> outerTypeToken, E outerDefaultElement) {
		Objects.requireNonNull(innerCollection, "The argument 'innerCollection' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");

		this.innerCollection = innerCollection;
		this.outerTypeToken = outerTypeToken;
		this.outerDefaultElement = outerDefaultElement;
	}

	/**
	 * Creates a new transforming collection.
	 *
	 * @param innerCollection
	 *            the wrapped collection
	 */
	public OptionalTransformingCollection(Collection<Optional<E>> innerCollection) {
		this(innerCollection, Object.class, null);
	}

	/**
	 * Creates a new transforming collection which uses a type token to identify the outer elements.
	 *
	 * @param innerCollection
	 *            the wrapped collection
	 * @param outerTypeToken
	 *            the token for the outer type
	 */
	public OptionalTransformingCollection(Collection<Optional<E>> innerCollection, Class<? super E> outerTypeToken) {
		this(innerCollection, outerTypeToken, null);
	}

	/**
	 * Creates a new transforming collection which uses the type of the specified default element as a token to identify
	 * the outer elements.
	 *
	 * @param innerCollection
	 *            the wrapped collection
	 * @param outerDefaultElement
	 *            the element used to represent {@link Optional#empty()}; it is of crucial importance that this element
	 *            does not occur inside an optional because then the transformations are from that optional to an
	 *            element and back are not inverse
	 */
	public OptionalTransformingCollection(
			Collection<Optional<E>> innerCollection,
			E outerDefaultElement) {
		this(innerCollection, getClassOfDefaultElement(outerDefaultElement), outerDefaultElement);
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClassOfDefaultElement(T outerDefaultElement) {
		Objects.requireNonNull(outerDefaultElement, "The argument 'outerDefaultElement' must not be null.");
		return (Class<T>) outerDefaultElement.getClass();
	}

	// #end CONSTRUCTION

	// #region OVERRIDING METHODS

	@Override
	public Iterator<E> iterator() {
		return new OptionalTransformingIterator();
	}

	// #end OVERRIDING METHODS

	// #region ABSTRACT METHODS FOM SUPERCLASS

	@Override
	protected Collection<Optional<E>> getInnerCollection() {
		return innerCollection;
	}

	@Override
	protected boolean isInnerElement(Object object) {
		// null can not be an element of the inner collection; 'isInstance' conforms to that
		return Optional.class.isInstance(object);
	}

	@Override
	protected E transformToOuter(Optional<E> innerElement) {
		Objects.requireNonNull(innerElement, "No element of the inner collection can be null.");
		return innerElement.orElse(outerDefaultElement);
	}

	@Override
	protected boolean isOuterElement(Object object) {
		// the second part of the check ensures
		// that 'null' is only considered an outer element if the default element is also null
		return outerTypeToken.isInstance(object) || Objects.equals(object, outerDefaultElement);
	}

	@Override
	protected Optional<E> transformToInner(E outerElement) {
		return Objects.equals(outerElement, outerDefaultElement)
				? Optional.empty()
				: Optional.of(outerElement);
	}

	// #end ABSTRACT METHODS FOM SUPERCLASS

	// #region OBJECT

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
		for (E outerElement : this)
			hashCode = 31 * hashCode + (outerElement == null ? 0 : outerElement.hashCode());
		return hashCode;
	}

	// #end OBJECT

	// #region INNER CLASSES

	private class OptionalTransformingIterator extends AbstractTransformingIterator<Optional<E>, E> {

		/**
		 * The wrapped/inner iterator.
		 */
		private final Iterator<Optional<E>> innerIterator = getInnerCollection().iterator();

		@Override
		protected Iterator<Optional<E>> getInnerIterator() {
			return innerIterator;
		}

		@Override
		protected E transformToOuter(Optional<E> innerElement) {
			return OptionalTransformingCollection.this.transformToOuter(innerElement);
		}

	}

	// #end INNER CLASSES
}

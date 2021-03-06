package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Collection} which decorates another collection and transforms the element type from the inner type {@code I}
 * to an outer type {@code O}.
 * <p>
 * See the {@link org.codefx.libfx.collection.transform package} documentation for general comments on transformation.
 * <p>
 * This implementation mitigates the type safety problems by using a token of the inner and the outer type to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might occur when an element can not be transformed by the transformation functions.
 * <p>
 * Null elements are allowed unless the inner collection does not accept them. These are handled explicitly and fixed to
 * the transformation {@code null -> null}. The transforming functions specified during construction neither have to
 * handle that case nor are they allowed to produce null elements.
 * <p>
 * If the {@link #stream() stream} returned by this collection is told to {@link java.util.stream.Stream#sorted() sort}
 * itself, it will do so on the base of the comparator returned by the inner collection's spliterator (e.g. based on the
 * natural order of {@code I} if it has one).
 * <p>
 * {@code TransformingCollection}s are created with a {@link TransformingCollectionBuilder}.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements contained in the wrapped/inner collection
 * @param <O>
 *            the outer type, i.e. the type of elements appearing to be in this collection
 */
public final class TransformingCollection<I, O> extends AbstractTransformingCollection<I, O> {

	// #begin FIELDS

	private final Collection<I> innerCollection;
	private final Class<? super I> innerTypeToken;
	private final Class<? super O> outerTypeToken;
	private final Function<? super I, ? extends O> transformToOuter;
	private final Function<? super O, ? extends I> transformToInner;

	// #end FIELDS

	/**
	 * Creates a new transforming collection.
	 *
	 * @param innerCollection
	 *            the wrapped collection
	 * @param innerTypeToken
	 *            the token for the inner type
	 * @param outerTypeToken
	 *            the token for the outer type
	 * @param transformToOuter
	 *            transforms an element from an inner to an outer type; will never be called with null argument and must
	 *            not produce null
	 * @param transformToInner
	 *            transforms an element from an outer to an inner type; will never be called with null argument and must
	 *            not produce null
	 */
	TransformingCollection(
			Collection<I> innerCollection,
			Class<? super I> innerTypeToken, Class<? super O> outerTypeToken,
			Function<? super I, ? extends O> transformToOuter, Function<? super O, ? extends I> transformToInner) {

		Objects.requireNonNull(innerCollection, "The argument 'innerCollection' must not be null.");
		Objects.requireNonNull(innerTypeToken, "The argument 'innerTypeToken' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.innerCollection = innerCollection;
		this.innerTypeToken = innerTypeToken;
		this.outerTypeToken = outerTypeToken;
		this.transformToOuter = transformToOuter;
		this.transformToInner = transformToInner;
	}

	// #begin IMPLEMENTATION OF 'AbstractTransformingCollection'

	@Override
	protected Collection<I> getInnerCollection() {
		return innerCollection;
	}

	@Override
	protected boolean isInnerElement(Object object) {
		return object == null || innerTypeToken.isInstance(object);
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
	protected boolean isOuterElement(Object object) {
		return object == null || outerTypeToken.isInstance(object);
	}

	@Override
	protected I transformToInner(O outerElement) {
		if (outerElement == null)
			return null;

		I innerElement = transformToInner.apply(outerElement);
		Objects.requireNonNull(innerElement, "The transformation must not create null instances.");
		return innerElement;
	}

	// #end IMPLEMENTATION OF 'AbstractTransformingCollection'

	// #begin OBJECT

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
		for (O outerElement : this)
			hashCode = 31 * hashCode + (outerElement == null ? 0 : outerElement.hashCode());
		return hashCode;
	}

	// #end OBJECT
}

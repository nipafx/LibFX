package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

// TODO document:
//  - intermediate decision regarding null elements: is allowed
//  - transformations do not need to handle nulls and must not create them (is this already documented somewhere?)
public final class TransformingCollection<I, O> extends AbstractTransformingCollection<I, O> {

	// #region FIELDS

	private final Collection<I> innerCollection;

	private final Class<O> outerTypeToken;

	private final Class<I> innerTypeToken;

	private final Function<I, O> transformToOuter;

	private final Function<O, I> transformToInner;

	// #end FIELDS

	public TransformingCollection(
			Collection<I> innerCollection,
			Class<I> innerTypeToken, Function<I, O> transformToOuter,
			Class<O> outerTypeToken, Function<O, I> transformToInner) {

		Objects.requireNonNull(innerCollection, "The argument 'innerCollection' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");
		Objects.requireNonNull(innerTypeToken, "The argument 'innerTypeToken' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.innerCollection = innerCollection;
		this.outerTypeToken = outerTypeToken;
		this.innerTypeToken = innerTypeToken;
		this.transformToOuter = transformToOuter;
		this.transformToInner = transformToInner;
	}

	// #region ABSTRACT METHODS FOM SUPERCLASS

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
		for (O outerElement : this)
			hashCode = 31 * hashCode + (outerElement == null ? 0 : outerElement.hashCode());
		return hashCode;
	}

	// #end OBJECT
}

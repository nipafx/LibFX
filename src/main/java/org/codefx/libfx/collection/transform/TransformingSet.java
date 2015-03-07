package org.codefx.libfx.collection.transform;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

// TODO document:
//  - intermediate decision regarding null elements: is allowed
//  - transformations do not need to handle nulls and must not create them (is this already documented somewhere?)
public class TransformingSet<I, O> extends AbstractTransformingSet<I, O> {

	// #region FIELDS

	private final Set<I> innerSet;

	private final Class<O> outerTypeToken;

	private final Class<I> innerTypeToken;

	private final Function<I, O> transformToOuter;

	private final Function<O, I> transformToInner;

	// #end FIELDS

	public TransformingSet(
			Set<I> innerSet,
			Class<I> innerTypeToken, Function<I, O> transformToOuter,
			Class<O> outerTypeToken, Function<O, I> transformToInner) {

		Objects.requireNonNull(innerSet, "The argument 'innerSet' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");
		Objects.requireNonNull(innerTypeToken, "The argument 'innerTypeToken' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.innerSet = innerSet;
		this.outerTypeToken = outerTypeToken;
		this.innerTypeToken = innerTypeToken;
		this.transformToOuter = transformToOuter;
		this.transformToInner = transformToInner;
	}

	// #region ABSTRACT METHODS FOM SUPERCLASS

	@Override
	protected Set<I> getInnerCollection() {
		return innerSet;
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

}

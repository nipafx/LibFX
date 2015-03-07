package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Objects;

public class TransformToReadOnlyInnerCollection<E, I, O> extends AbstractTransformingCollection<E, I> {

	private final AbstractTransformingCollection<I, O> clientCollection;

	private final Collection<E> innerCollection;

	public TransformToReadOnlyInnerCollection(
			AbstractTransformingCollection<I, O> clientCollection, Collection<E> innerCollection) {

		assert clientCollection != null : "The argument 'clientCollection' must not be null.";
		assert innerCollection != null : "The argument 'innerCollection' must not be null.";

		this.clientCollection = clientCollection;
		this.innerCollection = innerCollection;
	}

	// #region IMPLEMENTATION OF 'AbstractTransformingCollection'

	@Override
	protected Collection<E> getInnerCollection() {
		return innerCollection;
	}

	@Override
	protected boolean isInnerElement(Object object) {
		return clientCollection.isOuterElement(object);
	}

	@Override
	protected I transformToOuter(E innerElement) {
		O asClientOuterElement = (O) innerElement;
		return clientCollection.transformToInner(asClientOuterElement);
	}

	@Override
	protected boolean isOuterElement(Object object) {
		return clientCollection.isInnerElement(object);
	}

	@Override
	protected E transformToInner(I outerElement) {
		O transformedToClientOuterElement = clientCollection.transformToOuter(outerElement);
		E asThisInnerElement = (E) transformedToClientOuterElement;
		return asThisInnerElement;
	}

	// #end IMPLEMENTATION OF 'AbstractTransformingCollection'

	// #region PREVENT MODIFICATION

	@Override
	public boolean add(I element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends I> otherCollection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	// TODO iterator?

	// TODO default methods?

	// #end PREVENT MODIFICATION

	// #region OBJECT

	@Override
	public boolean equals(Object object) {
		return Objects.equals(this, object);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}

	// #end OBJECT

}

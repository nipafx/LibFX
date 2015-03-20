package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Abstract superclass to read-only {@link Collection}s which transform another collection.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements contained in the wrapped/inner collection
 * @param <O>
 *            the outer type, i.e. the type of elements appearing to be in this collection
 * @see AbstractTransformingCollection
 */
abstract class AbstractReadOnlyTransformingCollection<I, O> extends AbstractTransformingCollection<I, O> {

	// prevent modification

	@Override
	public final boolean add(O element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean addAll(Collection<? extends O> otherCollection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean remove(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeIf(Predicate<? super O> filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean removeAll(Collection<?> otherCollection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean retainAll(Collection<?> otherCollection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void clear() {
		throw new UnsupportedOperationException();
	}

}

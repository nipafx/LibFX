package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class AbstractReadOnlyTransformingSet<I, O> extends AbstractTransformingSet<I, O> {

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

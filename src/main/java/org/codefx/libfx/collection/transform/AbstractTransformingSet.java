package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Set;

// TODO
//  - point to AbstractTransformingCollection documentation
abstract class AbstractTransformingSet<I, O> extends AbstractTransformingCollection<I, O> implements Set<O> {

	@Override
	protected final Collection<I> getInnerCollection() {
		return getInnerSet();
	}

	protected abstract Set<I> getInnerSet();

	// #region OBJECT

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof Set))
			return false;

		Set<?> other = (Set<?>) object;
		if (isThisCollection(other))
			return true;

		if (other.size() != size())
			return false;
		try {
			return containsAll(other);
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hashCode = 0;
		for (O outerElement : this)
			if (outerElement != null)
				hashCode += outerElement.hashCode();
		return hashCode;
	}

	// #end OBJECT

}

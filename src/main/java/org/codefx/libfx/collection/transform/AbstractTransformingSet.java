package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Set;

/**
 * Abstract superclass to {@link Set}s which transform wrap another collection.
 * <p>
 * This class allows null elements. Subclasses might override that by implementing aggressive null checks.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements contained in the wrapped/inner set
 * @param <O>
 *            the outer type, i.e. the type of elements appearing to be in this set
 * @see AbstractTransformingCollection
 */
abstract class AbstractTransformingSet<I, O> extends AbstractTransformingCollection<I, O> implements Set<O> {

	@Override
	protected final Collection<I> getInnerCollection() {
		return getInnerSet();
	}

	/**
	 * @return the inner set wrapped by this transforming set
	 */
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

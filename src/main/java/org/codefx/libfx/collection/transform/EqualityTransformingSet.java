package org.codefx.libfx.collection.transform;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

/**
 * An equality transforming set allows to define the implementations of {@link Object#equals(Object) equals} and
 * {@link Object#hashCode() hashCode} which are used by the set.
 * <p>
 * It does so by storing the entries in an inner set and providing a transforming view on them. See the
 * {@link org.codefx.libfx.collection.transform package} documentation for general comments on that. Note that instances
 * of {@code EqualityTransformingSet}s are created with a {@link EqualityTransformingBuilder builder}.
 * <p>
 * This implementation mitigates the type safety problems by optionally using a token of the outer type to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might still occur.
 * <p>
 * By default the inner set will be a {@link HashSet} but another map can be provided to the builder. Such instances
 * must be empty and not be referenced anywhere else. The implementations of {@code equals} and {@code hashCode} are
 * provided as functions to the builder - see there for details.
 * <p>
 * The transformations used by this set preserve object identity of outer values. This means if values are added to this
 * set, an iteration over it will return the same instances.
 *
 * @param <E>
 *            the type of elements in this set
 */
public class EqualityTransformingSet<E> extends AbstractTransformingSet<EqHash<E>, E> {

	// #begin FIELDS

	private final Set<EqHash<E>> innerSet;

	private final Class<? super E> outerTypeToken;

	/**
	 * Compares two outer elements for equality.
	 */
	private final BiPredicate<? super E, ? super E> equals;

	/**
	 * Computes a hashCode for an outer element.
	 */
	private final ToIntFunction<? super E> hash;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a new transforming set.
	 *
	 * @param innerSet
	 *            the decorated set; must be empty
	 * @param outerTypeToken
	 *            the token used to verify outer elements
	 * @param equals
	 *            the function computing equality of elements
	 * @param hash
	 *            the function computing the hash code of elements
	 */
	EqualityTransformingSet(
			Set<?> innerSet, Class<? super E> outerTypeToken,
			BiPredicate<? super E, ? super E> equals, ToIntFunction<? super E> hash) {
		this.innerSet = castInnerSet(innerSet);
		this.outerTypeToken = outerTypeToken;
		this.equals = equals;
		this.hash = hash;
	}

	private static <E> Set<EqHash<E>> castInnerSet(Set<?> untypedInnerSet) {
		@SuppressWarnings("unchecked")
		// This class' contract states that the 'innerSet' must be empty and that no other
		// references to it must exist. This implies that only this class can ever access or mutate it.
		// Thanks to erasure its generic element type can hence be cast to any other type.
		Set<EqHash<E>> innerMap = (Set<EqHash<E>>) untypedInnerSet;
		return innerMap;
	}

	// #end CONSTRUCTION

	@Override
	protected Set<EqHash<E>> getInnerSet() {
		return innerSet;
	}

	@Override
	protected boolean isInnerElement(Object object) {
		// this excludes null objects from being inner element which is correct because even null will be wrapped in EqHash
		return object instanceof EqHash;
	}

	@Override
	protected E transformToOuter(EqHash<E> innerElement) throws ClassCastException {
		return innerElement.getElement();
	}

	@Override
	protected boolean isOuterElement(Object object) {
		return object == null || outerTypeToken.isInstance(object);
	}

	@Override
	protected EqHash<E> transformToInner(E outerElement) throws ClassCastException {
		return EqHash.create(outerElement, equals, hash);
	}

}

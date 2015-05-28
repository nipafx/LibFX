package org.codefx.libfx.collection.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

/**
 * An equality transforming map allows to define the implementations of {@link Object#equals(Object) equals} and
 * {@link Object#hashCode() hashCode} which are used for the map's keys.
 * <p>
 * It does so by storing the entries in an inner map and providing a transforming view on them. See the
 * {@link org.codefx.libfx.collection.transform package} documentation for general comments on that. Note that instances
 * of {@code EqualityTransformingMap}s are created with a {@link EqualityTransformingCollectionBuilder builder}.
 * <p>
 * This implementation mitigates the type safety problems by optionally using a token of the (outer) key type to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might still occur.
 * <p>
 * By default the inner map will be a new {@link HashMap} but the another map can be provided to the builder. Such
 * instances must be empty and not be referenced anywhere else. The implementations of {@code equals} and
 * {@code hashCode} are provided as functions to the builder - see there for details.
 * <p>
 * The transformations used by this map preserve object identity of outer keys and values. This means if keys and values
 * are added to this map, an iteration over it will return the same instances.
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
public final class EqualityTransformingMap<K, V> extends AbstractTransformingMap<EqHash<K>, K, V, V> {

	// #begin FIELDS

	private final Map<EqHash<K>, V> innerMap;

	private final Class<? super K> outerKeyTypeToken;

	/**
	 * Compares two outer keys for equality.
	 */
	private final BiPredicate<? super K, ? super K> equals;

	/**
	 * Computes a hashCode for an outer key.
	 */
	private final ToIntFunction<? super K> hash;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a new transforming map.
	 * 
	 * @param innerMap
	 *            the decorated map; must be empty
	 * @param outerKeyTypeToken
	 *            the token used to verify outer keys
	 * @param equals
	 *            the function computing equality of keys
	 * @param hash
	 *            the function computing the hash code of keys
	 */
	EqualityTransformingMap(
			Map<?, ?> innerMap,
			Class<? super K> outerKeyTypeToken,
			BiPredicate<? super K, ? super K> equals,
			ToIntFunction<? super K> hash) {

		assert innerMap != null : "The argument 'innerMap' must not be null.";
		assert outerKeyTypeToken != null : "The argument 'outerKeyTypeToken' must not be null.";
		assert equals != null : "The argument 'equals' must not be null.";
		assert hash != null : "The argument 'hash' must not be null.";

		this.innerMap = castInnerMap(innerMap);
		this.outerKeyTypeToken = outerKeyTypeToken;
		this.equals = equals;
		this.hash = hash;
	}

	private static <K, V> Map<EqHash<K>, V> castInnerMap(Map<?, ?> untypedInnerMap) {
		@SuppressWarnings("unchecked")
		// This class' contract states that the 'innerMap' must be empty and that no other
		// references to it must exist. This implies that only this class can ever access or mutate it.
		// Thanks to erasure its generic key and value types can hence be cast to any other type.
		Map<EqHash<K>, V> innerMap = (Map<EqHash<K>, V>) untypedInnerMap;
		return innerMap;
	}

	// #end CONSTRUCTION

	// #begin IMPLEMENTATION OF 'AbstractTransformingMap'

	@Override
	protected Map<EqHash<K>, V> getInnerMap() {
		return innerMap;
	}

	@Override
	protected boolean isInnerKey(Object object) {
		// this excludes null objects from being inner keys which is correct because even null will be wrapped in EqHash
		return object instanceof EqHash;
	}

	@Override
	protected K transformToOuterKey(EqHash<K> innerKey) throws ClassCastException {
		return innerKey.getElement();
	}

	@Override
	protected boolean isOuterKey(Object object) {
		return object == null || outerKeyTypeToken.isInstance(object);
	}

	@Override
	protected EqHash<K> transformToInnerKey(K outerKey) throws ClassCastException {
		return EqHash.create(outerKey, equals, hash);
	}

	@Override
	protected boolean isInnerValue(Object object) {
		return true;
	}

	@Override
	protected V transformToOuterValue(V innerValue) throws ClassCastException {
		return innerValue;
	}

	@Override
	protected boolean isOuterValue(Object object) {
		return true;
	}

	@Override
	protected V transformToInnerValue(V outerValue) throws ClassCastException {
		return outerValue;
	}

	// #end IMPLEMENTATION OF 'AbstractTransformingMap'

}

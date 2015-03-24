package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.codefx.libfx.collection.transform.EqualityTransformingMap.EqHash;

/**
 * TODO
 * <p>
 * The transformations used by this map preserve object identity of outer keys and values. This means if keys and values
 * are added to this map, an iteration over it will return the same instances.
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
public class EqualityTransformingMap<K, V> extends AbstractTransformingMap<EqHash<K>, K, V, V> {

	// #region FIELDS

	private final Map<EqHash<K>, V> innerMap;

	private final Class<? super K> outerKeyTypeToken;

	/**
	 * Compares two outer keys for equality.
	 * <p>
	 * Used to implement {@code equals} for the inner keys.
	 */
	private final BiPredicate<? super K, ? super K> equal;

	/**
	 * Computes a hashCode for an outer key.
	 * <p>
	 * Used to implement {@code hashCode} for the inner keys.
	 */
	private final ToIntFunction<? super K> hash;

	// #end FIELDS

	// #region CONSTRUCTION

	public EqualityTransformingMap(
			Supplier<Map<EqHash<K>, V>> innerMapConstructor,
			Class<? super K> outerKeyTypeToken,
			BiPredicate<? super K, ? super K> equal, ToIntFunction<? super K> hash) {

		Objects.requireNonNull(innerMapConstructor, "The argument 'innerMapConstructor' must not be null.");
		Objects.requireNonNull(outerKeyTypeToken, "The argument 'outerKeyTypeToken' must not be null.");
		Objects.requireNonNull(equal, "The argument 'equal' must not be null.");
		Objects.requireNonNull(hash, "The argument 'hash' must not be null.");

		this.innerMap = innerMapConstructor.get();
		this.outerKeyTypeToken = outerKeyTypeToken;
		this.equal = equal;
		this.hash = hash;
	}

	// #end CONSTRUCTION

	// #region IMPLEMENTATION OF 'AbstractTransformingMap'

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
		return innerKey.outerKey;
	}

	@Override
	protected boolean isOuterKey(Object object) {
		return object == null || outerKeyTypeToken.isInstance(object);
	}

	@Override
	protected EqHash<K> transformToInnerKey(K outerKey) throws ClassCastException {
		return new EqHash<>(this, outerKey);
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

	// #region INNER CLASSES

	static class EqHash<K> {

		private final EqualityTransformingMap<K, ?> transformingMap;

		private final K outerKey;

		private EqHash(EqualityTransformingMap<K, ?> transformingMap, K outerKey) {
			this.transformingMap = transformingMap;
			this.outerKey = outerKey;
		}

		@Override
		public int hashCode() {
			// TODO keep this fixed behavior for null keys or delegate to 'hash' as well?
			return outerKey == null ? 31 : transformingMap.hash.applyAsInt(outerKey);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof EqualityTransformingMap.EqHash))
				return false;

			@SuppressWarnings("unchecked")
			// This cast is ok because no instance of EqHash can ever leave the inner map (without being transformed
			// by this equality transforming map).
			// If it can not leave it can not end up in an equality test in another map.
			EqHash<K> other = (EqHash<K>) obj;

			// TODO keep this fixed behavior for null keys or delegate to 'equal' as well?
			if (this.outerKey == null && other.outerKey == null)
				return true;
			if (this.outerKey == null || other.outerKey == null)
				return false;

			return transformingMap.equal.test(this.outerKey, other.outerKey);
		}

	}

	// #end INNER CLASSES

}

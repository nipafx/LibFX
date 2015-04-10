package org.codefx.libfx.collection.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.codefx.libfx.collection.transform.EqualityTransformingMap.EqHash;

/**
 * An equality transforming map allows to define the implementations of {@link Object#equals(Object) equals} and
 * {@link Object#hashCode() hashCode} which are used for the map's keys.
 * <p>
 * It does so by storing the entries in an inner map and providing a transforming view on them. See the
 * {@link org.codefx.libfx.collection.transform package} documentation for general comments on that. Note that instances
 * of {@code EqualityTransformingMap}s are created with a {@link EqualityTransformingMap.Builder builder}, which can be
 * obtained by calling the static {@code with...}-methods.
 * <p>
 * This implementation mitigates the type safety problems by optionally using a token of the (outer) key type to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might still occur.
 * <p>
 * By default the inner map will be a {@link HashMap} but the constructor for another map can be provided to the
 * builder. Any instances created by it must be empty and not be referenced anywhere else. All method calls (of abstract
 * and default methods existing in JDK 8) are forwarded to <b>the same method</b> on the inner map. This implies that
 * all guarantees made by such methods (e.g. regarding atomicity) are upheld by the transformation.
 * <p>
 * The implementations of {@code equals} and {@code hashCode} are provided as functions to the builder. They must of
 * course fulfill the general contract between those two methods (see {@link Object#hashCode() here}). The functions can
 * be provided on two ways:
 * <ol>
 * <li>Via the {@code with[Equals|Hash]}-methods. In this case, the functions will never be called with null instances,
 * which are handled by this map as follows:
 * <ul>
 * <li>{@code hashCode(null) == }{@link EqualityTransformingMap#NULL_KEY_HASH_CODE NULL_KEY_HASH_CODE}
 * <li>{@code equals(null, null) == true};
 * <li>{@code equals(not-null, null) == false}
 * <li>{@code equals(null, not-null) == false}
 * </ul>
 * <li>If those defaults are not sufficient, the functions can handle null null themselves. Those variants can be
 * provided via the {@code with[Equals|Hash]HandlingNull}-methods.
 * </ol>
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

	// #region FIELDS

	/**
	 * The default hash code used for null keys.
	 */
	public static final int NULL_KEY_HASH_CODE = 31;

	private final Map<EqHash<K>, V> innerMap;

	private final Class<? super K> outerKeyTypeToken;

	/**
	 * Compares two outer keys for equality.
	 * <p>
	 * Used to implement {@code equals} for the inner keys.
	 */
	private final BiPredicate<? super K, ? super K> equals;

	/**
	 * Computes a hashCode for an outer key.
	 * <p>
	 * Used to implement {@code hashCode} for the inner keys.
	 */
	private final ToIntFunction<? super K> hash;

	// #end FIELDS

	// #region CONSTRUCTION

	private EqualityTransformingMap(
			Supplier<Map<?, ?>> innerMapConstructor,
			Class<? super K> outerKeyTypeToken,
			BiPredicate<? super K, ? super K> equals,
			ToIntFunction<? super K> hash) {

		assert innerMapConstructor != null : "The argument 'innerMapConstructor' must not be null.";
		assert outerKeyTypeToken != null : "The argument 'outerKeyTypeToken' must not be null.";
		assert equals != null : "The argument 'equals' must not be null.";
		assert hash != null : "The argument 'hash' must not be null.";

		this.innerMap = createInnerMap(innerMapConstructor);
		this.outerKeyTypeToken = outerKeyTypeToken;
		this.equals = equals;
		this.hash = hash;
	}

	private Map<EqHash<K>, V> createInnerMap(Supplier<Map<?, ?>> innerMapConstructor) {
		@SuppressWarnings("unchecked")
		// This class' contract states that the map created by 'innerMapConstructor' must be empty and that no other
		// references to it must exist. This implies that only this class can ever access or mutate it.
		// Thanks to erasure its generic key and value types can hence be cast to any other type.
		Map<EqHash<K>, V> innerMap = (Map<EqHash<K>, V>) innerMapConstructor.get();
		return innerMap;
	}

	/**
	 * Returns a new {@link EqualityTransformingMap.Builder builder} to create equality transforming maps.
	 * <p>
	 * See the documentation of this class for how the map's behavior can be defined. The mentioned properties can be
	 * set on the builder before {@link Builder#build() building} the map.
	 * <p>
	 * This method can be called if no type token for the keys can be provided (if it can be, call
	 * {@link #withKeyType(Class)} instead). A call might look as follows (for a generic type {@code T}):
	 *
	 * <pre>
	 * EqualityTransformingMap.&lt;T&gt; withUnspecifiedKeyType();
	 * </pre>
	 *
	 * @param <K>
	 *            the type of keys maintained by the map created by the builder
	 * @return a new builder
	 */
	public static <K> Builder<K> withUnspecifiedKeyType() {
		return new Builder<>(Object.class);
	}

	/**
	 * Returns a new {@link EqualityTransformingMap.Builder builder} to create equality transforming maps for keys of
	 * the specified type.
	 * <p>
	 * See the documentation of this class for how the map's behavior can be defined. The mentioned properties can be
	 * set on the builder before {@link Builder#build() building} the map.
	 * <p>
	 * If a type token for the keys can not be provided, call {@link #withUnspecifiedKeyType()} instead.
	 *
	 * @param <K>
	 *            the type of keys maintained by the map created by the builder
	 * @param keyTypeToken
	 *            a type token for the keys maintained by the map created by the builder
	 * @return a new builder
	 */
	public static <K> Builder<K> withKeyType(Class<? super K> keyTypeToken) {
		return new Builder<>(keyTypeToken);
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

	/**
	 * Wraps the keys before they go into the inner map and delegates {@link #equals(Object)} and {@link #hashCode()} to
	 * the transforming map's {@link EqualityTransformingMap#equals equals} and {@link EqualityTransformingMap#hash
	 * hash} functions.
	 *
	 * @param <K>
	 *            the type of the wrapped keys
	 */
	static class EqHash<K> {

		private final EqualityTransformingMap<K, ?> transformingMap;

		private final K outerKey;

		private EqHash(EqualityTransformingMap<K, ?> transformingMap, K outerKey) {
			this.transformingMap = transformingMap;
			this.outerKey = outerKey;
		}

		@Override
		public int hashCode() {
			return transformingMap.hash.applyAsInt(outerKey);
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
			return transformingMap.equals.test(this.outerKey, other.outerKey);
		}

	}

	/**
	 * Builds {@link EqualityTransformingMap}s.
	 * <p>
	 * The same builder instance can be reused to create multiple map instances. The builder is not thread-safe and
	 * should not be used concurrently.
	 *
	 * @param <K>
	 *            the type of keys maintained by the created map
	 */
	public static class Builder<K> {

		private final Class<? super K> outerKeyTypeToken;

		private Supplier<Map<?, ?>> innerMapConstructor;

		private BiPredicate<? super K, ? super K> equals;

		private ToIntFunction<? super K> hash;

		private Builder(Class<? super K> outerKeyTypeToken) {
			this.outerKeyTypeToken = outerKeyTypeToken;
			this.innerMapConstructor = HashMap::new;
			this.equals = Objects::equals;
			this.hash = Objects::hashCode;
		}

		/**
		 * @param innerMapConstructor
		 *            used to create instances of the the transforming map's inner map; nstances created by it must not
		 *            be referenced anywhere else
		 * @return this builder
		 */
		public Builder<K> withInnerMap(Supplier<Map<?, ?>> innerMapConstructor) {
			Objects.requireNonNull(innerMapConstructor, "The argument 'innerMapConstructor' must not be null.");
			this.innerMapConstructor = innerMapConstructor;
			return this;
		}

		/**
		 * @param equals
		 *            a function determining equality of keys; might be called with null keys
		 * @return this builder
		 */
		public Builder<K> withEqualsHandlingNull(BiPredicate<? super K, ? super K> equals) {
			this.equals = equals;
			return this;
		}

		/**
		 * @param equals
		 *            a function determining equality of keys; will not be called with null keys
		 * @return this builder
		 */
		public Builder<K> withEquals(BiPredicate<? super K, ? super K> equals) {
			return withEqualsHandlingNull(makeNullSafe(equals));
		}

		private static <K> BiPredicate<? super K, ? super K> makeNullSafe(BiPredicate<? super K, ? super K> equals) {
			return (outerKey1, outerKey2) -> {
				if (outerKey1 == null && outerKey2 == null)
					return true;
				if (outerKey1 == null || outerKey2 == null)
					return false;

				return equals.test(outerKey1, outerKey2);
			};
		}

		/**
		 * @param hash
		 *            a function computing the hash code of a key; might be called with null keys
		 * @return this builder
		 */
		public Builder<K> withHashHandlingNull(ToIntFunction<? super K> hash) {
			this.hash = hash;
			return this;
		}

		/**
		 * @param hash
		 *            a function computing the hash code of a key; will not be called with null keys
		 * @return this builder
		 */
		public Builder<K> withHash(ToIntFunction<? super K> hash) {
			return withHashHandlingNull(makeNullSafe(hash));
		}

		private static <K> ToIntFunction<? super K> makeNullSafe(ToIntFunction<? super K> hash) {
			return outerKey -> outerKey == null ? NULL_KEY_HASH_CODE : hash.applyAsInt(outerKey);
		}

		/**
		 * @param <V>
		 *            the type of values mapped by the new map
		 * @return a new instance of {@link EqualityTransformingMap}
		 */
		public <V> Map<K, V> build() {
			return new EqualityTransformingMap<>(innerMapConstructor, outerKeyTypeToken, equals, hash);
		}

	}

	// #end INNER CLASSES

}

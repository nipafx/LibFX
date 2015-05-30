package org.codefx.libfx.collection.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

/**
 * Builds {@link EqualityTransformingSet}s and {@link EqualityTransformingMap}s.
 * <p>
 * (For simplification the comments only talk about sets but unless otherwise noted the same applies to maps.)
 * <p>
 * The implementations of {@code equals} and {@code hashCode} are provided as functions to the builder. They must of
 * course fulfill the general contract between those two methods (see {@link Object#hashCode() here}). The functions can
 * be provided on two ways:
 * <ol>
 * <li>Via the {@code with[Equals|Hash]}-methods. In this case, the functions will never be called with null instances,
 * which are handled by this map as follows:
 * <ul>
 * <li>{@code hashCode(null) == 0}
 * <li>{@code equals(null, null) == true};
 * <li>{@code equals(not-null, null) == false}
 * <li>{@code equals(null, not-null) == false}
 * </ul>
 * <li>If those defaults are not sufficient, the functions can handle null themselves. Those variants can be provided
 * via the {@code with[Equals|Hash]HandlingNull}-methods.
 * </ol>
 * <p>
 * The same builder instance can be reused to create multiple instances. The builder is not thread-safe and should not
 * be used concurrently.
 *
 * @param <E>
 *            the type of elements maintained by the created set or keys by the created map.
 */
public final class EqualityTransformingCollectionBuilder<E> {

	private final Class<? super E> outerKeyTypeToken;
	private BiPredicate<? super E, ? super E> equals;
	private ToIntFunction<? super E> hash;

	// #begin CONSTRUCTION

	private EqualityTransformingCollectionBuilder(Class<? super E> outerKeyTypeToken) {
		this.outerKeyTypeToken = outerKeyTypeToken;
		// note that the methods from 'Objects' already implement the contract for null-safety
		// imposed by the transforming set and map
		this.equals = Objects::equals;
		this.hash = Objects::hashCode;
	}

	/**
	 * Returns a new builder for the specified element type.
	 * <p>
	 * If a type token for the elements can not be provided, call {@link #forTypeUnknown()} instead.
	 *
	 * @param <E>
	 *            the type of elements contained in the created set
	 * @param keyTypeToken
	 *            a type token for the elements contained in the created set
	 * @return a new builder
	 */
	public static <E> EqualityTransformingCollectionBuilder<E> forType(Class<? super E> keyTypeToken) {
		Objects.requireNonNull(keyTypeToken, "The argument 'keyTypeToken' must not be null.");
		return new EqualityTransformingCollectionBuilder<>(keyTypeToken);
	}

	/**
	 * Returns a new builder for an unknown key type.
	 * <p>
	 * This is equivalent to calling {@link #forType(Class) forKeyType(Object.class)}. To obtain a builder for
	 * {@code <E>} you will have to call {@code EqualityTransformingCollectionBuilder.<E> forTypeUnknown()}.
	 *
	 * @param <E>
	 *            the type of elements contained in the set created by the builder
	 * @return a new builder
	 */
	public static <E> EqualityTransformingCollectionBuilder<E> forTypeUnknown() {
		return new EqualityTransformingCollectionBuilder<>(Object.class);
	}

	// #end CONSTRUCTION

	// #begin SET PROPERTIES

	/**
	 * @param equals
	 *            a function determining equality of elements; might be called with null elements
	 * @return this builder
	 */
	public EqualityTransformingCollectionBuilder<E> withEqualsHandlingNull(BiPredicate<? super E, ? super E> equals) {
		Objects.requireNonNull(equals, "The argument 'equals' must not be null.");
		this.equals = equals;
		return this;
	}

	/**
	 * @param equals
	 *            a function determining equality of elements; will not be called with null elements
	 * @return this builder
	 */
	public EqualityTransformingCollectionBuilder<E> withEquals(BiPredicate<? super E, ? super E> equals) {
		Objects.requireNonNull(equals, "The argument 'equals' must not be null.");
		return withEqualsHandlingNull(makeNullSafe(equals));
	}

	private static <E> BiPredicate<? super E, ? super E> makeNullSafe(BiPredicate<? super E, ? super E> equals) {
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
	 *            a function computing the hash code of an element; might be called with null elements
	 * @return this builder
	 */
	public EqualityTransformingCollectionBuilder<E> withHashHandlingNull(ToIntFunction<? super E> hash) {
		Objects.requireNonNull(hash, "The argument 'hash' must not be null.");
		this.hash = hash;
		return this;
	}

	/**
	 * @param hash
	 *            a function computing the hash code of an element; will not be called with null elements
	 * @return this builder
	 */
	public EqualityTransformingCollectionBuilder<E> withHash(ToIntFunction<? super E> hash) {
		Objects.requireNonNull(hash, "The argument 'hash' must not be null.");
		return withHashHandlingNull(makeNullSafe(hash));
	}

	private static <E> ToIntFunction<? super E> makeNullSafe(ToIntFunction<? super E> hash) {
		return outerKey -> outerKey == null ? EqHash.NULL_KEY_HASH_CODE : hash.applyAsInt(outerKey);
	}

	// #end SET PROPERTIES

	// #begin BUILD

	/**
	 * Creates a new {@link EqualityTransformingSet} by decorating a {@link HashSet}.
	 *
	 * @return a new instance of {@link EqualityTransformingSet}
	 */
	public EqualityTransformingSet<E> buildSet() {
		return new EqualityTransformingSet<>(new HashSet<>(), outerKeyTypeToken, equals, hash);
	}

	/**
	 * Creates a new {@link EqualityTransformingSet} by decorating the specified set.
	 *
	 * @param emptySet
	 *            an empty set which is not otherwise referenced
	 * @return a new instance of {@link EqualityTransformingSet}
	 */
	public EqualityTransformingSet<E> buildSet(Set<Object> emptySet) {
		return new EqualityTransformingSet<>(emptySet, outerKeyTypeToken, equals, hash);
	}

	/**
	 * Creates a new {@link EqualityTransformingMap} by decorating a {@link HashMap}.
	 *
	 * @param <V>
	 *            the type of values mapped by the new map
	 * @return a new instance of {@link EqualityTransformingMap}
	 */
	public <V> EqualityTransformingMap<E, V> buildMap() {
		return new EqualityTransformingMap<>(new HashMap<>(), outerKeyTypeToken, equals, hash);
	}

	/**
	 * Creates a new {@link EqualityTransformingMap} by decorating the specified map.
	 *
	 * @param <V>
	 *            the type of values mapped by the new map
	 * @param emptyMap
	 *            an empty map which is not otherwise referenced
	 * @return a new instance of {@link EqualityTransformingMap}
	 */
	public <V> EqualityTransformingMap<E, V> buildMap(Map<Object, Object> emptyMap) {
		return new EqualityTransformingMap<>(emptyMap, outerKeyTypeToken, equals, hash);
	}

	// #end BUILD

}

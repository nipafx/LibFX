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
public class EqualityTransformingBuilder<E> {

	private final Class<? super E> outerKeyTypeToken;
	private BiPredicate<? super E, ? super E> equals;
	private ToIntFunction<? super E> hash;

	private EqualityTransformingBuilder(Class<? super E> outerKeyTypeToken) {
		this.outerKeyTypeToken = outerKeyTypeToken;
		// note that the methods from 'Objects' already implement the contract for null-safety
		// imposed by the transforming set and map
		this.equals = Objects::equals;
		this.hash = Objects::hashCode;
	}

	// #begin SET PROPERTIES

	/**
	 * Returns a new builder.
	 * <p>
	 * This method can be called if no type token for the elements can be provided (if it can be, call the preferable
	 * {@link #forKeyType(Class)} instead). A call might look as follows (for a generic type {@code T}):
	 *
	 * <pre>
	 * EqualityTransformingBuilder.&lt;T&gt; forUnspecifiedKeyType();
	 * </pre>
	 *
	 * @param <E>
	 *            the type of elements contained in the set created by the builder
	 * @return a new builder
	 */
	public static <E> EqualityTransformingBuilder<E> forUnspecifiedKeyType() {
		return new EqualityTransformingBuilder<>(Object.class);
	}

	/**
	 * Returns a new builder to create equality transforming sets for elements of the specified type.
	 * <p>
	 * If a type token for the keys can not be provided, call {@link #forUnspecifiedKeyType()} instead.
	 *
	 * @param <E>
	 *            the type of elements contained in the set created by the builder
	 * @param keyTypeToken
	 *            a type token for the elements contained in the set created by the builder
	 * @return a new builder
	 */
	public static <E> EqualityTransformingBuilder<E> forKeyType(Class<? super E> keyTypeToken) {
		Objects.requireNonNull(keyTypeToken, "The argument 'keyTypeToken' must not be null.");
		return new EqualityTransformingBuilder<>(keyTypeToken);
	}

	/**
	 * @param equals
	 *            a function determining equality of keys; might be called with null keys
	 * @return this builder
	 */
	public EqualityTransformingBuilder<E> withEqualsHandlingNull(BiPredicate<? super E, ? super E> equals) {
		Objects.requireNonNull(equals, "The argument 'equals' must not be null.");
		this.equals = equals;
		return this;
	}

	/**
	 * @param equals
	 *            a function determining equality of keys; will not be called with null keys
	 * @return this builder
	 */
	public EqualityTransformingBuilder<E> withEquals(BiPredicate<? super E, ? super E> equals) {
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
	 *            a function computing the hash code of a key; might be called with null keys
	 * @return this builder
	 */
	public EqualityTransformingBuilder<E> withHashHandlingNull(ToIntFunction<? super E> hash) {
		Objects.requireNonNull(hash, "The argument 'hash' must not be null.");
		this.hash = hash;
		return this;
	}

	/**
	 * @param hash
	 *            a function computing the hash code of a key; will not be called with null keys
	 * @return this builder
	 */
	public EqualityTransformingBuilder<E> withHash(ToIntFunction<? super E> hash) {
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
	public EqualityTransformingSet<E> buildSetDecorating(Set<Object> emptySet) {
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
	public <V> EqualityTransformingMap<E, V> buildMapDecorating(Map<Object, Object> emptyMap) {
		return new EqualityTransformingMap<>(emptyMap, outerKeyTypeToken, equals, hash);
	}

	// #end BUILD

}

package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Builder for {@link TransformingCollection}s, {@link TransformingSet}s and {@link TransformingList}s.
 * <p>
 * A builder can be obtained by calling {@link #forInnerAndOuterType(Class, Class) forInnerAndOuterType} or
 * {@link #forInnerAndOuterTypeUnknown()}. The building methods {@code transform...} can only be called after
 * transformations from inner to outer elements and vice versa have been set.
 *
 * @param <I>
 *            the inner type of the created transforming collection, i.e. the type of the elements contained in the
 *            wrapped/inner collection
 * @param <O>
 *            the outer type of the created transforming collection, i.e. the type of elements appearing to be in the
 *            created collection
 */
public class TransformingCollectionBuilder<I, O> {

	// #begin FIELDS

	private final Class<? super O> outerTypeToken;
	private final Class<? super I> innerTypeToken;

	private Function<? super I, ? extends O> transformToOuter;
	private Function<? super O, ? extends I> transformToInner;

	// #end FIELDS

	// #begin CONSTRUCTION

	private TransformingCollectionBuilder(Class<? super I> innerTypeToken, Class<? super O> outerTypeToken) {
		Objects.requireNonNull(innerTypeToken, "The argument 'innerTypeToken' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");

		this.outerTypeToken = outerTypeToken;
		this.innerTypeToken = innerTypeToken;
	}

	/**
	 * Returns a new builder for the specified inner and outer type.
	 *
	 * @param <I>
	 *            the inner type of the created transforming collection, i.e. the type of the elements contained in the
	 *            wrapped/inner collection
	 * @param <O>
	 *            the outer type of the created transforming collection, i.e. the type of elements appearing to be in
	 *            the created collection
	 * @param innerTypeToken
	 *            the token for the inner type
	 * @param outerTypeToken
	 *            the token for the outer type
	 * @return a new builder
	 */
	public static <I, O> TransformingCollectionBuilder<I, O> forInnerAndOuterType(
			Class<? super I> innerTypeToken, Class<? super O> outerTypeToken) {
		return new TransformingCollectionBuilder<>(innerTypeToken, outerTypeToken);
	}

	/**
	 * Returns a new builder that transforms a collection of strings into a collection of the specified outer type.
	 * <p>
	 * Besides fixing the inner type to {@link String}, the returned builder also guesses the transforming functions. It
	 * uses {@link Object#toString() toString} for outer to inner elements and the {@code valueOf(String)} methods of
	 * {@link Integer#valueOf(String) Integer}, {@link Long#valueOf(String) Long}, {@link Float#valueOf(String) Float},
	 * and {@link Double#valueOf(String) Double} for the other way if one of them is the outer type. These preselected
	 * transformations can be overriden with {@link #toInner(Function) toInner} and {@link #toOuter(Function) to Outer}.
	 *
	 * @param <O>
	 *            the outer type of the created transforming collection, i.e. the type of elements appearing to be in
	 *            the created collection
	 * @param outerTypeToken
	 *            the token for the outer type
	 * @return a new builder
	 */
	public static <O> TransformingCollectionBuilder<String, O> forInnerStringAndOuterType(
			Class<? super O> outerTypeToken) {

		TransformingCollectionBuilder<String, O> builder = new TransformingCollectionBuilder<>(
				String.class, outerTypeToken);
		builder = builder.toInner(Object::toString);
		Optional<Function<String, O>> toOuter = tryGuessToOuterFunction(outerTypeToken);
		toOuter.ifPresent(builder::toOuter);
		return builder;
	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<Function<String, T>> tryGuessToOuterFunction(Class<? super T> typeToken) {
		if (typeToken == Integer.class)
			return Optional.of(string -> (T) Integer.valueOf(string));
		if (typeToken == Long.class)
			return Optional.of(string -> (T) Long.valueOf(string));
		if (typeToken == Float.class)
			return Optional.of(string -> (T) Float.valueOf(string));
		if (typeToken == Double.class)
			return Optional.of(string -> (T) Double.valueOf(string));

		return Optional.empty();
	}

	/**
	 * Returns a new builder for unknown inner and outer types.
	 * <p>
	 * This is equivalent to calling {@link #forInnerAndOuterType(Class, Class) forInnerAndOuterType(Object.class,
	 * Object.class)}. To obtain a builder for {@code <I, O>} you will have to call
	 * {@code TransformingCollectionBuilder.<I, O> forInnerAndOuterTypeUnknown()}.
	 *
	 * @param <I>
	 *            the inner type of the created transforming collection, i.e. the type of the elements contained in the
	 *            wrapped/inner collection
	 * @param <O>
	 *            the outer type of the created transforming collection, i.e. the type of elements appearing to be in
	 *            the created collection
	 * @return a new builder
	 */
	public static <I, O> TransformingCollectionBuilder<I, O> forInnerAndOuterTypeUnknown() {
		return forInnerAndOuterType(Object.class, Object.class);
	}

	// #end CONSTRUCTION

	// #begin SET FIELDS

	/**
	 * Sets the transformation from inner to outer elements which will be used by the created collection.
	 *
	 * @param transformToOuter
	 *            transforms inner to outer elements
	 * @return this builder
	 */
	public TransformingCollectionBuilder<I, O> toOuter(Function<? super I, ? extends O> transformToOuter) {
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");

		this.transformToOuter = transformToOuter;
		return this;
	}

	/**
	 * Sets the transformation from outer to inner elements which will be used by the created collection.
	 *
	 * @param transformToInner
	 *            transforms outer to inner elements
	 * @return this builder
	 */
	public TransformingCollectionBuilder<I, O> toInner(Function<? super O, ? extends I> transformToInner) {
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.transformToInner = transformToInner;
		return this;
	}

	// #end SET FIELDS

	// #begin BUILD

	/**
	 * Creates a {@link TransformingCollection} which transforms/decorates the specified collection.
	 *
	 * @param collection
	 *            the collection to transform; will be the inner collection of the returned transformation
	 * @return a new {@link TransformingCollection}
	 */
	public TransformingCollection<I, O> transformCollection(Collection<I> collection) {
		return new TransformingCollection<>(
				collection, innerTypeToken, outerTypeToken, transformToOuter, transformToInner);
	}

	/**
	 * Creates a {@link TransformingSet} which transforms/decorates the specified set.
	 *
	 * @param set
	 *            the set to transform; will be the inner set of the returned transformation
	 * @return a new {@link TransformingSet}
	 */
	public TransformingSet<I, O> transformSet(Set<I> set) {
		return new TransformingSet<>(
				set, innerTypeToken, outerTypeToken, transformToOuter, transformToInner);
	}

	/**
	 * Creates a {@link TransformingList} which transforms/decorates the specified list.
	 *
	 * @param list
	 *            the list to transform; will be the inner list of the returned transformation
	 * @return a new {@link TransformingList}
	 */
	public TransformingList<I, O> transformList(List<I> list) {
		return new TransformingList<>(
				list, innerTypeToken, outerTypeToken, transformToOuter, transformToInner);
	}

	// #end BUILD

}

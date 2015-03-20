package org.codefx.libfx.collection.transform;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * A {@link Set} which decorates another set and transforms the element type from the inner type {@code I} to an outer
 * type {@code O}.
 * <p>
 * See the {@link org.codefx.libfx.collection.transform package} documentation for general comments.
 * <p>
 * This implementation mitigates the type safety problems by using a token of the inner and the outer type to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might occur when an element can not be transformed by the transformation functions.
 * <p>
 * Null elements are allowed. These are handled explicitly and fixed to the transformation {@code null -> null}. The
 * transforming functions specified during construction neither have to handle that case nor must they produce null
 * elements.
 * <p>
 * All method calls (of abstract and default methods existing in JDK 8) are forwarded to <b>the same method</b> on the
 * wrapped set. This implies that all all guarantees made by such methods (e.g. regarding atomicity) are upheld by the
 * transformation.
 *
 * @param <I>
 *            the inner type, i.e. the type of the elements contained in the wrapped/inner set
 * @param <O>
 *            the outer type, i.e. the type of elements appearing to be in this set
 */
public final class TransformingSet<I, O> extends AbstractTransformingSet<I, O> {

	// #region FIELDS

	private final Set<I> innerSet;

	private final Class<? super O> outerTypeToken;

	private final Class<? super I> innerTypeToken;

	private final Function<I, O> transformToOuter;

	private final Function<O, I> transformToInner;

	// #end FIELDS

	/**
	 * Creates a new transforming set.
	 *
	 * @param innerSet
	 *            the wrapped set
	 * @param innerTypeToken
	 *            the token for the inner type
	 * @param transformToOuter
	 *            transforms an element from an inner to an outer type; will never be called with null argument and must
	 *            not produce null
	 * @param outerTypeToken
	 *            the token for the outer type
	 * @param transformToInner
	 *            transforms an element from an outer to an inner type; will never be called with null argument and must
	 *            not produce null
	 */
	public TransformingSet(
			Set<I> innerSet,
			Class<? super I> innerTypeToken, Function<I, O> transformToOuter,
			Class<? super O> outerTypeToken, Function<O, I> transformToInner) {

		Objects.requireNonNull(innerSet, "The argument 'innerSet' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");
		Objects.requireNonNull(innerTypeToken, "The argument 'innerTypeToken' must not be null.");
		Objects.requireNonNull(transformToOuter, "The argument 'transformToOuter' must not be null.");
		Objects.requireNonNull(transformToInner, "The argument 'transformToInner' must not be null.");

		this.innerSet = innerSet;
		this.outerTypeToken = outerTypeToken;
		this.innerTypeToken = innerTypeToken;
		this.transformToOuter = transformToOuter;
		this.transformToInner = transformToInner;
	}

	// #region ABSTRACT METHODS FOM SUPERCLASS

	@Override
	protected Set<I> getInnerSet() {
		return innerSet;
	}

	@Override
	protected boolean isInnerElement(Object object) {
		return object == null || innerTypeToken.isInstance(object);
	}

	@Override
	protected O transformToOuter(I innerElement) {
		if (innerElement == null)
			return null;

		O outerElement = transformToOuter.apply(innerElement);
		Objects.requireNonNull(outerElement, "The transformation must not create null instances.");
		return outerElement;
	}

	@Override
	protected boolean isOuterElement(Object object) {
		return object == null || outerTypeToken.isInstance(object);
	}

	@Override
	protected I transformToInner(O outerElement) {
		if (outerElement == null)
			return null;

		I innerElement = transformToInner.apply(outerElement);
		Objects.requireNonNull(innerElement, "The transformation must not create null instances.");
		return innerElement;
	}

	// #end ABSTRACT METHODS FOM SUPERCLASS

}

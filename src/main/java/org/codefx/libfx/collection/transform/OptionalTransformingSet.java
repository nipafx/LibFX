package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A transforming {@link Set} which is a flattened view on a set of {@link Optional}s, i.e. it only presents the
 * contained values.
 * <p>
 * See the {@link org.codefx.libfx.collection.transform package} documentation for general comments on transformation.
 * <p>
 * The inner set must not contain null elements. The empty {@code Optional} is mapped to an outer element specified
 * during construction. If null is chosen for this, this set will accept null elements. Otherwise it will reject them
 * with a {@link NullPointerException}.
 * <p>
 * The transformations used by this set preserve object identity of outer elements with the exception of the default
 * element. This means if (non-default) elements are added to this set, an iteration over it will return the same
 * instances. The default value instance will be used to represent the empty {@code Optional}, so when elements equal to
 * it are added, they will be retrieved as that instance (thus loosing their identity).
 * <p>
 * This implementation mitigates the type safety problems by using type tokens. {@code Optional.class} is used as the
 * inner type token. The outer type token can be specified during construction. This solves some of the critical
 * situations but not all of them. In those other cases (e.g. if {@link #containsAll(Collection) containsAll} is called
 * with a {@code Collection<Optional<?>>}) {@link ClassCastException}s might occur.
 * <p>
 * All method calls (of abstract and default methods existing in JDK 8) are forwarded to <b>the same method</b> on the
 * wrapped set. This implies that all guarantees made by such methods (e.g. regarding atomicity) are upheld by the
 * transformation.
 *
 * @param <E>
 *            the type of elements contained in the {@code Optional}s
 */
public final class OptionalTransformingSet<E> extends AbstractTransformingSet<Optional<E>, E> {

	// #begin FIELDS

	private final Set<Optional<E>> innerSet;

	private final Class<? super E> outerTypeToken;

	/**
	 * The outer element used to represent {@link Optional#empty()}.
	 */
	private final E outerDefaultElement;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a new transforming set which uses a type token to identify the outer elements.
	 *
	 * @param innerSet
	 *            the wrapped set
	 * @param outerTypeToken
	 *            the token for the outer type
	 * @param outerDefaultElement
	 *            the element used to represent {@link Optional#empty()}; can be null; it is of crucial importance that
	 *            this element does not occur inside a non-empty optional because then the transformations from that
	 *            optional to an element and back are not inverse, which will cause unexpected behavior
	 */
	public OptionalTransformingSet(
			Set<Optional<E>> innerSet,
			Class<? super E> outerTypeToken, E outerDefaultElement) {
		Objects.requireNonNull(innerSet, "The argument 'innerSet' must not be null.");
		Objects.requireNonNull(outerTypeToken, "The argument 'outerTypeToken' must not be null.");

		this.innerSet = innerSet;
		this.outerTypeToken = outerTypeToken;
		this.outerDefaultElement = outerDefaultElement;
	}

	/**
	 * Creates a new transforming set.
	 *
	 * @param innerSet
	 *            the wrapped set
	 */
	public OptionalTransformingSet(Set<Optional<E>> innerSet) {
		this(innerSet, Object.class, null);
	}

	/**
	 * Creates a new transforming set which uses a type token to identify the outer elements.
	 *
	 * @param innerSet
	 *            the wrapped set
	 * @param outerTypeToken
	 *            the token for the outer type
	 */
	public OptionalTransformingSet(Set<Optional<E>> innerSet, Class<? super E> outerTypeToken) {
		this(innerSet, outerTypeToken, null);
	}

	/**
	 * Creates a new transforming set which uses the type of the specified default element as a token to identify the
	 * outer elements.
	 *
	 * @param innerSet
	 *            the wrapped set
	 * @param outerDefaultElement
	 *            the non-null element used to represent {@link Optional#empty()}; it is of crucial importance that this
	 *            element does not occur inside an optional because then the transformations from that optional to an
	 *            element and back are not inverse, which will cause unexpected behavior; the instance's class will be
	 *            used as the outer type token
	 */
	public OptionalTransformingSet(
			Set<Optional<E>> innerSet,
			E outerDefaultElement) {
		this(innerSet, getClassOfDefaultElement(outerDefaultElement), outerDefaultElement);
	}

	@SuppressWarnings("unchecked")
	private static <T> Class<T> getClassOfDefaultElement(T outerDefaultElement) {
		Objects.requireNonNull(outerDefaultElement, "The argument 'outerDefaultElement' must not be null.");
		return (Class<T>) outerDefaultElement.getClass();
	}

	// #end CONSTRUCTION

	// #begin IMPLEMENTATION OF 'AbstractTransformingSet'

	@Override
	protected Set<Optional<E>> getInnerSet() {
		return innerSet;
	}

	@Override
	protected boolean isInnerElement(Object object) {
		// reject nulls unless it is the outer default element
		if (outerDefaultElement != null)
			Objects.requireNonNull(object, "When the outer default element is not null, this collection rejects nulls.");

		return Optional.class.isInstance(object);
	}

	@Override
	protected E transformToOuter(Optional<E> innerElement) {
		Objects.requireNonNull(innerElement, "No element of the inner collection can be null.");
		return innerElement.orElse(outerDefaultElement);
	}

	@Override
	protected boolean isOuterElement(Object object) {
		// the second part of the check ensures
		// that 'null' is only considered an outer element if the default element is also null
		return outerTypeToken.isInstance(object) || Objects.equals(object, outerDefaultElement);
	}

	@Override
	protected Optional<E> transformToInner(E outerElement) {
		// reject nulls unless it is the outer default element
		if (outerDefaultElement != null)
			Objects.requireNonNull(outerElement, "The argument 'outerElement' must not be null.");

		return Objects.equals(outerElement, outerDefaultElement)
				? Optional.empty()
				: Optional.of(outerElement);
	}

	// #end IMPLEMENTATION OF 'AbstractTransformingSet'

}

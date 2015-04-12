package org.codefx.libfx.collection.tree;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * A straight forward implementation of {@link TreeNode}.
 *
 * @param <E>
 *            the type of the contained element
 */
public final class SimpleTreeNode<E> implements TreeNode<E> {

	private final E element;

	private final OptionalInt childIndex;

	private SimpleTreeNode(E element, OptionalInt childIndex) {
		Objects.requireNonNull(element, "The argument 'element' must not be null.");
		Objects.requireNonNull(childIndex, "The argument 'childIndex' must not be null.");
		if (childIndex.isPresent() && childIndex.getAsInt() < 0)
			throw new IllegalArgumentException("The 'childIndex' must be missing or non-negative.");

		this.element = element;
		this.childIndex = childIndex;
	}

	/**
	 * Creates a node for a node of a tree (possibly the root).
	 *
	 * @param <E>
	 *            the type of the content contained in the node
	 * @param element
	 *            the element
	 * @param childIndex
	 *            the index of the node within the list of children of its parent; as an {@link OptionalInt} because it
	 *            can be empty if the node is the root
	 * @return a node containing the element
	 */
	public static <E> SimpleTreeNode<E> node(E element, OptionalInt childIndex) {
		return new SimpleTreeNode<>(element, childIndex);
	}

	/**
	 * Creates a node for the root of a (sub-)tree.
	 *
	 * @param <E>
	 *            the type of the content contained in the node
	 * @param element
	 *            the root element
	 * @return a node containing the element
	 */
	public static <E> SimpleTreeNode<E> root(E element) {
		return new SimpleTreeNode<>(element, OptionalInt.empty());
	}

	/**
	 * Creates a node for an inner node of a tree.
	 *
	 * @param <E>
	 *            the type of the content contained in the node
	 * @param element
	 *            the element
	 * @param childIndex
	 *            the index of the node within the list of children of its parent
	 * @return a node containing the element
	 */
	public static <E> SimpleTreeNode<E> innerNode(E element, int childIndex) {
		return new SimpleTreeNode<>(element, OptionalInt.of(childIndex));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E getElement() {
		return element;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OptionalInt getChildIndex() {
		return childIndex;
	}

	@Override
	public String toString() {
		return "Node [" + element + ", " + childIndex + "]";
	}

}

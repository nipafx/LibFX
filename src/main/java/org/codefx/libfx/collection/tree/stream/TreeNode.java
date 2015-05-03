package org.codefx.libfx.collection.tree.stream;

import java.util.OptionalInt;

import org.codefx.libfx.collection.tree.navigate.TreeNavigator;

/**
 * Encapsulates an element in a tree.
 * <p>
 * A {@code TreeNode} is useful to create a {@link TreeIterationStrategy} together with a {@link TreeNavigator}. The
 * node's child index is stored in the node to reduce the number of {@link TreeNavigator#getChildIndex(Object)
 * getChildIndex} calls made to the navigator.
 *
 * @param <E>
 *            the type of the contained element
 */
interface TreeNode<E> {

	/**
	 * @return the encapsulated element
	 */
	E getElement();

	/**
	 * @return the index of the node within the list of children of its parent
	 */
	OptionalInt getChildIndex();

}

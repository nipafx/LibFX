package org.codefx.libfx.collection.tree;

import java.util.OptionalInt;

/**
 * Encapsulates the nodes returned by the {@link TreeNavigator}.
 *
 * @param <E>
 *            the type of the contained element
 */
public interface TreeNode<E> {

	/**
	 * @return the encapsulated element
	 */
	E getElement();

	/**
	 * @return the index of the node within the list of children of its parent
	 */
	OptionalInt getChildIndex();

}

package org.codefx.libfx.collection.tree.navigate;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * A tree navigator allows to walk through a tree (i.e. a connected, directed, acyclic graph).
 * <p>
 * This interface can be implemented to navigate arbitrary tree-like data structures without requiring them to implement
 * a specific interface.
 * <p>
 * The navigation relies on child nodes having some fixed order so they can be accessed via an index.
 *
 * @param <N>
 *            the type of nodes contained in the tree
 */
public interface TreeNavigator<N> {

	// PARENT

	/**
	 * @param child
	 *            an node in the tree
	 * @return the child's parent; if it is the root, it doesn't have a parent, so {@link Optional#empty() empty} is
	 *         returned
	 */
	Optional<N> getParent(N child);

	// NODE

	/**
	 * @param node
	 *            a node in the tree
	 * @return the index of the node within the list of children of its parent; if it is the root, it doesn't have a
	 *         parent, so {@link OptionalInt#empty() empty} is returned
	 */
	OptionalInt getChildIndex(N node);

	// CHILDREN

	/**
	 * @param parent
	 *            a node in the tree
	 * @return the number of children the node has
	 */
	int getChildrenCount(N parent);

	/**
	 * @param parent
	 *            a node in the tree
	 * @param childIndex
	 *            a non-negative number specifying the index of the requested child
	 * @return the child of the node with the child index; if no such child exists (e.g. because no children exist)
	 *         {@link OptionalInt#empty() empty} is returned
	 */
	Optional<N> getChild(N parent, int childIndex);

}

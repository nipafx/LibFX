package org.codefx.libfx.collection.tree;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * A path in a tree.
 *
 * @param <N>
 *            the type of nodes
 */
public interface TreePath<N> {

	/**
	 * @return whether this path is empty
	 */
	boolean isEmpty();

	/**
	 * @return the end of this path if it exists; otherwise {@link Optional#empty() empty}.
	 */
	Optional<N> getEnd();

	/**
	 * Appends the specified node to the end of this path.
	 *
	 * @param node
	 *            the node to append
	 */
	void append(N node);

	/**
	 * Removes (and returns) the end of this path.
	 *
	 * @return the former end node of this path
	 * @throws NoSuchElementException
	 *             if the path is {@link #isEmpty() isEmpty}
	 */
	N removeEnd() throws NoSuchElementException;

}

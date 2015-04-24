package org.codefx.libfx.collection.tree;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * A path in a tree from some node to an ancestor.
 * <p>
 * A {@code TreePath} is useful to create a {@link TreeIterationStrategy}. Such strategies (usually) only instrument the
 * end of the current path (e.g. when appending nodes to move down the tree or removing them to move up). This interface
 * is hence geared towards that use case and limits interaction with the path to its end, which can be {@link #getEnd()
 * retrieved}, {@link #append(Object) appended} to and {@link #removeEnd() removed}.
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
	 *             if the path {@link #isEmpty() isEmpty}
	 */
	N removeEnd() throws NoSuchElementException;

}

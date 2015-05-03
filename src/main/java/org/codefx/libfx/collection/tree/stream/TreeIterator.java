package org.codefx.libfx.collection.tree.stream;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * An {@link Iterator} which uses a {@link TreeIterationStrategy} to iterate over a tree.
 *
 * @param <E>
 *            the type of elements returned by this iterator
 */
class TreeIterator<E> implements Iterator<E> {

	/*
	 * The important task of choosing the next node in the tree is delegated to the iteration strategy. This class
	 * merely delays looking for that node as long as possible. Its state consists of a single node of the tree and
	 * whether it was already returned
	 */

	// #begin FIELDS

	private final TreeIterationStrategy<E> iterationStrategy;

	/**
	 * The next node to return; if it is {@link Optional#empty() empty}, the iteration will end.
	 */
	private Optional<E> nextNode;

	/**
	 * Indicates whether the {@link #nextNode} was already returned as an element in {@link #next()}.
	 */
	private boolean returnedNextNode;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a new tree iterator which uses the specified strategy to determine the order of nodes.
	 *
	 * @param iterationStrategy
	 *            the strategy used by this iterator
	 */
	public TreeIterator(TreeIterationStrategy<E> iterationStrategy) {
		Objects.requireNonNull(iterationStrategy, "The argument 'iterationStrategy' must not be null.");

		this.iterationStrategy = iterationStrategy;
		this.nextNode = Optional.empty();
		this.returnedNextNode = true;
	}

	// #end CONSTRUCTION

	// #begin IMPLEMENTATION OF 'Iterator'

	@Override
	public final boolean hasNext() {
		goToNextNodeIfNecessary();
		return nextNode.isPresent();
	}

	@Override
	public final E next() {
		goToNextNodeIfNecessary();
		return returnNextNode();
	}

	// #end IMPLEMENTATION OF 'Iterator'

	// #begin GO TO NEXT NODE & RETURN

	private void goToNextNodeIfNecessary() {
		if (returnedNextNode) {
			nextNode = iterationStrategy.goToNextNode();
			returnedNextNode = false;
		}
	}

	private E returnNextNode() {
		if (nextNode.isPresent()) {
			returnedNextNode = true;
			return nextNode.get();
		} else
			throw new NoSuchElementException("All nodes in the tree have been visited.");
	}

	// #end GO TO NEXT NODE & RETURN

}

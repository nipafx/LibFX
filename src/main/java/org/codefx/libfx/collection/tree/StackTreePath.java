package org.codefx.libfx.collection.tree;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link TreePath} which uses a stack to manage the path.
 *
 * @param <N>
 *            the type of nodes in this path
 */
public class StackTreePath<N> implements TreePath<N> {

	/**
	 * The path from the root to the current node as a stack with the root at the bottom and the most recently visited
	 * node at the top.
	 */
	private final Deque<N> path;

	/**
	 * Creates an empty path.
	 */
	public StackTreePath() {
		this.path = new ArrayDeque<>();
	}

	/**
	 * Creates a new path which is initialized to the specified list of nodes.
	 * <p>
	 * The list is interpreted as a path which starts in the list's first element. Hence for an initial path from
	 * {@code start} to {@code end} the list should be such that {@code initialPath.indexOf(start) == 0} and
	 * {@code initialPath.indexOf(end) == initialPath.size() - 1}.
	 *
	 * @param initialPath
	 *            the initial path
	 */
	public StackTreePath(List<? extends N> initialPath) {
		this();
		Objects.requireNonNull(initialPath, "The argument 'initialPath' must not be null.");
		initialPath.forEach(this::append);
	}

	@Override
	public boolean isEmpty() {
		return path.isEmpty();
	}

	@Override
	public Optional<N> getEnd() {
		return Optional.ofNullable(path.peek());
	}

	@Override
	public void append(N node) {
		Objects.requireNonNull(node, "The argument 'node' must not be null.");
		path.push(node);
	}

	@Override
	public N removeEnd() throws NoSuchElementException {
		return path.pop();
	}

}

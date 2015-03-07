package org.codefx.libfx.collection.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

abstract class AbstractGraphIterator<N> implements Iterator<N> {

	// #region FIELDS

	protected final GraphNavigator<N> navigator;

	/**
	 * The path from the root to the current node as a stack with the root at the bottom and the most recently visited
	 * node at the top.
	 */
	protected final Deque<Node<N>> path;

	/**
	 * Indicates whether the current path end (i.e. the topmost node in {@link #path}) was already returned as an
	 * element.
	 */
	private boolean returnedCurrentPathEnd;

	// #end FIELDS

	// #region CONSTRUCTION

	private AbstractGraphIterator(N root, GraphNavigator<N> navigator, List<Node<N>> pathFromStartToRoot) {
		requireArgumentsNonNull(root, navigator, pathFromStartToRoot);
		boolean pathEndsIsRoot = pathFromStartToRoot.size() > 0
				&& pathFromStartToRoot.get(pathFromStartToRoot.size() - 1).getElement() == root;
		assert pathEndsIsRoot : "The 'pathFromStartToRoot' must end in 'root'.";

		this.navigator = navigator;
		this.path = new ArrayDeque<>(pathFromStartToRoot);
		this.returnedCurrentPathEnd = false;
	}

	private static <N> void requireArgumentsNonNull(
			N root, GraphNavigator<N> navigator, List<Node<N>> pathFromStartToRoot) {

		Objects.requireNonNull(root, "The argument 'root' must not be null.");
		Objects.requireNonNull(navigator, "The argument 'navigator' must not be null.");
		Objects.requireNonNull(pathFromStartToRoot, "The argument 'pathFromStartToRoot' must not be null.");
	}

	protected AbstractGraphIterator(N root, GraphNavigator<N> navigator) {
		this(root, navigator, Arrays.asList(Node.root(root)));
	}

	protected AbstractGraphIterator(N root, GraphNavigator<N> navigator, N start) {
		this(root, navigator, createPathFromStartToRoot(root, navigator, start));
	}

	private static <N> List<Node<N>> createPathFromStartToRoot(N root, GraphNavigator<N> navigator, N start) {
		// handle the special case that the start node is root
		Objects.requireNonNull(start, "The argument 'start' must not be null.");
		List<Node<N>> pathFromStartToRoot = new ArrayList<>();
		if (start == root) {
			pathFromStartToRoot.add(Node.root(start));
			return pathFromStartToRoot;
		}

		requireArgumentsNonNull(root, navigator, pathFromStartToRoot);

		// handle the regular case and fill the path from start to root
		Optional<N> rootOfStartNode = fillPathFromStartToBeforeRoot(root, navigator, start, pathFromStartToRoot);
		ensurePathEndsInRoot(root, start, pathFromStartToRoot, rootOfStartNode);

		return pathFromStartToRoot;
	}

	private static <N> Optional<N> fillPathFromStartToBeforeRoot(
			N root, GraphNavigator<N> navigator, N start, List<Node<N>> pathFromStartToRoot) {

		Node<N> startNode = Node.node(start, navigator.getChildIndex(start));
		pathFromStartToRoot.add(startNode);

		Optional<N> parent = navigator.getParent(start);
		while (parent.isPresent() && !root.equals(parent.get())) {
			Node<N> parentNode = Node.node(parent.get(), navigator.getChildIndex(parent.get()));
			pathFromStartToRoot.add(parentNode);
			parent = navigator.getParent(parent.get());
		}
		return parent;
	}

	private static <N> void ensurePathEndsInRoot(
			N root, N start, List<Node<N>> pathFromStartToRoot, Optional<N> rootOfStartNode) {

		if (root.equals(rootOfStartNode.get()))
			pathFromStartToRoot.add(Node.root(root));
		else
			throw new IllegalArgumentException(
					"There must be a path from 'start' \"" + start + "\" to 'root' \"" + root + "\".");
	}

	// #end CONSTRUCTION

	// #region GO TO NEXT NODE & RETURN

	private void goToNextNodeIfNecessary() {
		if (returnedCurrentPathEnd) {
			goToNextNode();
			returnedCurrentPathEnd = false;
		}
	}

	protected abstract void goToNextNode();

	private N returnCurrentPathEnd() {
		assertGraphNotFullyTraversed();

		returnedCurrentPathEnd = true;
		return path.peek().getElement();
	}

	protected final void assertGraphNotFullyTraversed() {
		if (path.isEmpty())
			throw new NoSuchElementException("All nodes in the graph have been visited.");
	}

	// #end GO TO NEXT NODE & RETURN

	// #region IMPLEMENTATION OF ITERATOR

	@Override
	public final boolean hasNext() {
		goToNextNodeIfNecessary();
		return !path.isEmpty();
	}

	@Override
	public final N next() {
		goToNextNodeIfNecessary();
		return returnCurrentPathEnd();
	}

	// #end IMPLEMENTATION OF ITERATOR

	// #region INNER CLASSES

	protected static final class Node<E> {

		public final E element;

		public final OptionalInt childIndex;

		private Node(E element, OptionalInt childIndex) {
			Objects.requireNonNull(element, "The argument 'element' must not be null.");
			Objects.requireNonNull(childIndex, "The argument 'childIndex' must not be null.");

			this.element = element;
			this.childIndex = childIndex;
		}

		public static <N> Node<N> root(N element) {
			return new Node<>(element, OptionalInt.empty());
		}

		public static <N> Node<N> node(N element, int childIndex) {
			return new Node<>(element, OptionalInt.of(childIndex));
		}

		public static <N> Node<N> node(N element, OptionalInt childIndex) {
			return new Node<>(element, childIndex);
		}

		public E getElement() {
			return element;
		}

		public OptionalInt getChildIndex() {
			return childIndex;
		}

		@Override
		public String toString() {
			return "Node [" + element + ", " + childIndex + "]";
		}

	}

	// #end INNER CLASSES

}

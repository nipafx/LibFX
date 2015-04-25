package org.codefx.libfx.collection.tree;

import java.util.Objects;
import java.util.Optional;

/**
 * A {@link TreeIterationStrategy} which iterates a tree's nodes with a <em>backwards</em> <a
 * href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first search</a>.
 * <p>
 * This means that given an initial path from the root {@code R} to some node {@code N} , this strategy will start with
 * {@code N} and move backwards visiting parents, left siblings and their children (from right to left) until it reaches
 * {@code R}. This will enumerate the same nodes as a depth-first search which started in {@code R} and ended in
 * {@code N} but in reverse order (containing both {@code N} and {@code R}).
 * <p>
 * This implementation is only guaranteed to work on trees, i.e. a connected, directed, acyclic graph. Using it on other
 * graphs can lead to unexpected behavior including infinite loops.
 *
 * @param <E>
 *            the type of elements contained in the tree
 */
final class BackwardsDfsTreeIterationStrategy<E> implements TreeIterationStrategy<E> {

	// #region FIELDS

	private final TreeNavigator<E> navigator;

	/**
	 * The path from the root to the last node returned by {@link #goToNextNode()}
	 * <p>
	 * The path will also contain at least one node before the first call to {@link #goToNextNode()}. These are the
	 * nodes specified during construction and the one at the end of the path must be returned by the first call to
	 * {@code goToNextNode()}. Otherwise the end of the path would never be returned by the strategy. Whether
	 * {@code goToNextNode()} must return this node or find a new one is indicated by {@link #beforeFirst}.
	 * <p>
	 * If the path is empty, no more nodes will be returned.
	 */
	private final TreePath<TreeNode<E>> path;

	/**
	 * Indicates whether {@link #goToNextNode()} was not already called at least once.
	 */
	private boolean beforeFirst;

	// #end FIELDS

	// #region CONSTRUCTION

	/**
	 * Creates a new backwards depth-first search strategy starting with the specified initial path.
	 * <p>
	 * The iteration will begin with the node at the end of the initial path. It will stop when it successfully
	 * backtracked to the first node in that path, i.e. the (sub-)tree's root. (Remember, this does not happen on a
	 * straight route from nodes to their parents but via backwards depth-first search.)
	 * <p>
	 * The specified path must correspond to the navigator's view on the tree, i.e. each element in the path must be the
	 * parent (in the tree) of the next one.
	 * <p>
	 * See {@link TreePathFactory} for easy ways to create an initial path.
	 *
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param initialPath
	 *            the initial path from the root of the (sub-)tree iterated by this strategy; must contain at least one
	 *            element; the last element in the path will be returned by the first call to {@link #goToNextNode()}
	 */
	public BackwardsDfsTreeIterationStrategy(TreeNavigator<E> navigator, TreePath<TreeNode<E>> initialPath) {
		Objects.requireNonNull(navigator, "The argument 'navigator' must not be null.");
		Objects.requireNonNull(initialPath, "The argument 'initialPath' must not be null.");
		if (initialPath.isEmpty())
			throw new IllegalArgumentException("The 'initialPath' must not be empty.");

		this.navigator = navigator;
		this.path = initialPath;
		this.beforeFirst = true;
	}

	// #end CONSTRUCTION

	@Override
	public Optional<E> goToNextNode() {
		// if the path is empty, iteration ended and no more nodes will be returned
		if (path.isEmpty())
			return Optional.empty();

		// if this is the first call, do not move to the next node
		if (beforeFirst)
			beforeFirst = false;
		else
			movePathEndToNextNode();

		return path.getEnd().map(TreeNode::getElement);
	}

	private void movePathEndToNextNode() {
		Optional<TreeNode<E>> leftSibling = goToParentAndGetLeftSibling();
		if (leftSibling.isPresent())
			goToRightmostAncestor(leftSibling.get());
	}

	private Optional<TreeNode<E>> goToParentAndGetLeftSibling() {
		TreeNode<E> node = path.removeEnd();
		Optional<TreeNode<E>> parent = path.getEnd();
		if (parent.isPresent()) {
			int leftSiblingIndex = node.getChildIndex().getAsInt() - 1;
			return navigator
					.getChild(parent.get().getElement(), leftSiblingIndex)
					.map(ls -> SimpleTreeNode.innerNode(ls, leftSiblingIndex));
		} else
			return Optional.empty();
	}

	private void goToRightmostAncestor(TreeNode<E> leftSibling) {
		Optional<TreeNode<E>> rightmostChild = Optional.of(leftSibling);
		while (rightmostChild.isPresent()) {
			path.append(rightmostChild.get());
			int rightmostChildIndex = navigator.getChildrenCount(rightmostChild.get().getElement()) - 1;
			rightmostChild = navigator
					.getChild(rightmostChild.get().getElement(), rightmostChildIndex)
					.map(child -> SimpleTreeNode.innerNode(child, rightmostChildIndex));
		}
	}
}

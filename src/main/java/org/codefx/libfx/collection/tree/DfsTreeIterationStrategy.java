package org.codefx.libfx.collection.tree;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link TreeIterationStrategy} which uses <a href="https://en.wikipedia.org/wiki/Depth-first_search">depth-first
 * search</a> to iterate a tree's nodes.
 * <p>
 * This implementation is only guaranteed to work on trees, i.e. a connected, directed, acyclic graph. Using it on other
 * graphs can lead to unexpected behavior including infinite loops.
 *
 * @param <E>
 *            the type of elements contained in the tree
 */
public final class DfsTreeIterationStrategy<E> implements TreeIterationStrategy<E> {

	// #region FIELDS

	private final TreeNavigator<E> navigator;

	/**
	 * The path from the root to the last node returned by {@link #goToNextNode()}
	 * <p>
	 * The path will also contain at least one node before the first call to {@link #goToNextNode()}. These are the
	 * nodes specified during construction and the one at the end of the path must be returned by the first call to
	 * {@code goToNextNode()}. Otherwise the end of the path (e.g. the root specified during construction) would never
	 * be returned by the strategy. Whether {@code goToNextNode()} must return this node or find a new one is indicated
	 * by {@link #beforeFirst}.
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
	 * Creates a new depth-first search strategy starting with the specified initial path.
	 * <p>
	 * See {@link DfsTreeIterationStrategy#DfsTreeIterationStrategy(TreeNavigator, List) this constructor} for comments
	 * on the initial path.
	 *
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param initialPath
	 *            the initial path from the root of the (sub-)tree iterated by this strategy; must contain at least one
	 *            element; the last element in the path will be returned by the first call to {@link #goToNextNode()}
	 */
	public DfsTreeIterationStrategy(TreeNavigator<E> navigator, TreePath<TreeNode<E>> initialPath) {
		Objects.requireNonNull(navigator, "The argument 'navigator' must not be null.");
		Objects.requireNonNull(initialPath, "The argument 'initialPath' must not be null.");
		if (initialPath.isEmpty())
			throw new IllegalArgumentException("The 'initialPath' must not be empty.");

		this.navigator = navigator;
		this.path = initialPath;
		this.beforeFirst = true;
	}

	/**
	 * Creates a new depth-first search strategy starting with the specified initial path.
	 * <p>
	 * The iteration will begin with the node at the end of the initial path. Note that this does not make the node the
	 * root of the iterated (sub-)tree over which this strategy iterates. Instead it will at some point try to find
	 * right siblings or uncles of this node. It will only stop when backtracking to the first node in that path, i.e.
	 * the root.
	 * <p>
	 * The specified path must correspond to what the {@code navigator} would return for the corresponding calls. This
	 * means that each element in the list must be the parent (in the tree) of the next one.
	 *
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param initialPath
	 *            the initial path from the root of the (sub-)tree iterated by this strategy; must contain at least one
	 *            element; the last element in the path will be returned by the first call to {@link #goToNextNode()}
	 */
	public DfsTreeIterationStrategy(TreeNavigator<E> navigator, List<E> initialPath) {
		this(navigator, TreePathFactory.createFromElementList(navigator, initialPath));
	}

	/**
	 * Creates a new depth-first search strategy.
	 *
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param root
	 *            the root of the (sub-)tree iterated by this strategy; the first call to {@link #goToNextNode()} will
	 *            return this node
	 */
	public DfsTreeIterationStrategy(TreeNavigator<E> navigator, E root) {
		this(navigator, TreePathFactory.createWithSingleNode(root));
	}

	// #end CONSTRUCTION

	// #region GO TO NEXT NODE

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
		Optional<TreeNode<E>> leftmostChild = getLeftmostChild();
		if (leftmostChild.isPresent())
			goToLeftmostChild(leftmostChild.get());
		else
			goToRightSiblingOrUncle();
	}

	private Optional<TreeNode<E>> getLeftmostChild() {
		return path
				.getEnd()
				.flatMap(node -> navigator.getChild(node.getElement(), 0))
				.map(child -> SimpleTreeNode.innerNode(child, 0));
	}

	private void goToLeftmostChild(TreeNode<E> leftmostChild) {
		path.append(leftmostChild);
	}

	private void goToRightSiblingOrUncle() {
		Optional<TreeNode<E>> siblingOrUncle = Optional.empty();

		while (!siblingOrUncle.isPresent() && !path.isEmpty()) {
			TreeNode<E> currentNode = path.removeEnd();
			Optional<TreeNode<E>> parentNode = path.getEnd();
			if (parentNode.isPresent()) {
				E parentElement = parentNode.get().getElement();
				int rightSiblingIndex = currentNode.getChildIndex().getAsInt() + 1;
				siblingOrUncle = navigator
						.getChild(parentElement, rightSiblingIndex)
						.map(child -> SimpleTreeNode.innerNode(child, rightSiblingIndex));
			}
		}

		siblingOrUncle.ifPresent(path::append);
	}

	// #end GO TO NEXT NODE

}

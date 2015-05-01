package org.codefx.libfx.collection.tree.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Utility class to easily create {@link TreePath}s for recurring situations.
 */
class TreePathFactory {

	/**
	 * @param <E>
	 *            the type of elements contained in the tree
	 * @param node
	 *            the only node of the returned path
	 * @return a tree path containing the node
	 */
	public static <E> TreePath<TreeNode<E>> createWithSingleNode(E node) {
		Objects.requireNonNull(node, "The argument 'node' must not be null.");
		return new StackTreePath<>(Collections.singletonList(SimpleTreeNode.root(node)));
	}

	/**
	 * @param <E>
	 *            the type of elements contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param pathAsList
	 *            the path as a list where the path' first node is the first element in the list
	 * @return a tree path containing the nodes for the elements in the list
	 */
	public static <E> TreePath<TreeNode<E>> createFromElementList(TreeNavigator<E> navigator, List<E> pathAsList) {
		Objects.requireNonNull(navigator, "The argument 'navigator' must not be null.");
		Objects.requireNonNull(pathAsList, "The argument 'pathAsList' must not be null.");

		TreePath<TreeNode<E>> path = new StackTreePath<>();
		pathAsList.forEach(element -> {
			OptionalInt childIndex = navigator.getChildIndex(element);
			TreeNode<E> node = SimpleTreeNode.node(element, childIndex);
			path.append(node);
		});
		return path;
	}

	/**
	 * @param <E>
	 *            the type of elements contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param node
	 *            a node in the tree
	 * @param descendant
	 *            a descendant of the specified node
	 * @return a tree path leading from the node to the descendant
	 * @throws IllegalArgumentException
	 *             if the node is no ancestor of the descendant
	 */
	public static <E> TreePath<TreeNode<E>> createFromNodeToDescendant(
			TreeNavigator<E> navigator, E node, E descendant) throws IllegalArgumentException {

		Objects.requireNonNull(navigator, "The argument 'navigator' must not be null.");
		Objects.requireNonNull(node, "The argument 'node' must not be null.");
		Objects.requireNonNull(descendant, "The argument 'descendant' must not be null.");

		if (node == descendant)
			return createWithSingleNode(node);

		List<TreeNode<E>> pathFromDescendantBackToNode = createPathFromDescendantBackToNode(navigator, node, descendant);
		return createPathByInverting(pathFromDescendantBackToNode);
	}

	private static <E> List<TreeNode<E>> createPathFromDescendantBackToNode(
			TreeNavigator<E> navigator, E node, E descendant) {

		List<TreeNode<E>> pathFromDescendantToNode = createPathWithSingleNode(navigator, descendant);
		Optional<E> parent = addAllAncestorsToPathUntilReachingNode(
				navigator, node, descendant, pathFromDescendantToNode);
		addNodeToPathOrThrowException(navigator, node, parent, descendant, pathFromDescendantToNode);
		return pathFromDescendantToNode;
	}

	private static <E> List<TreeNode<E>> createPathWithSingleNode(TreeNavigator<E> navigator, E descendant) {
		List<TreeNode<E>> pathFromStartBackToNode = new ArrayList<>();
		TreeNode<E> descendantNode = SimpleTreeNode.node(descendant, navigator.getChildIndex(descendant));
		pathFromStartBackToNode.add(descendantNode);
		return pathFromStartBackToNode;
	}

	private static <E> Optional<E> addAllAncestorsToPathUntilReachingNode(
			TreeNavigator<E> navigator, E node, E descendant, List<TreeNode<E>> pathFromStartBackToNode) {

		Optional<E> parent = navigator.getParent(descendant);
		while (parent.isPresent() && parent.get() != node) {
			TreeNode<E> parentNode = SimpleTreeNode.node(parent.get(), navigator.getChildIndex(parent.get()));
			pathFromStartBackToNode.add(parentNode);
			parent = navigator.getParent(parent.get());
		}
		return parent;
	}

	private static <E> void addNodeToPathOrThrowException(
			TreeNavigator<E> navigator, E node, Optional<E> parent, E descendant,
			List<TreeNode<E>> pathFromStartBackToNode) {

		if (!parent.isPresent() || parent.get() != node)
			throw new IllegalArgumentException(
					"The specified node '" + node
							+ "' is no ancestor of the specified descendant '" + descendant + "'.");
		else {
			TreeNode<E> startNode = SimpleTreeNode.node(node, navigator.getChildIndex(node));
			pathFromStartBackToNode.add(startNode);
		}
	}

	private static <E> TreePath<TreeNode<E>> createPathByInverting(List<TreeNode<E>> pathFromDescendantBackToNode) {
		StackTreePath<TreeNode<E>> treePath = new StackTreePath<TreeNode<E>>();
		for (int i = pathFromDescendantBackToNode.size() - 1; i >= 0; i--)
			treePath.append(pathFromDescendantBackToNode.get(i));
		return treePath;
	}

	/**
	 * @param <E>
	 *            the type of elements contained in the tree
	 * @param navigator
	 *            the navigator used to navigate the tree
	 * @param node
	 *            a node in the tree
	 * @return a tree path leading from the root to the node
	 */
	public static <E> TreePath<TreeNode<E>> createFromRootToNode(TreeNavigator<E> navigator, E node) {
		Objects.requireNonNull(navigator, "The argument 'navigator' must not be null.");
		Objects.requireNonNull(node, "The argument 'node' must not be null.");

		List<TreeNode<E>> pathFromDescendantBackToNode = createPathFromNodeBackToRoot(navigator, node);
		return createPathByInverting(pathFromDescendantBackToNode);
	}

	private static <E> List<TreeNode<E>> createPathFromNodeBackToRoot(
			TreeNavigator<E> navigator, E node) {

		List<TreeNode<E>> pathFromNodeToRoot = createPathWithSingleNode(navigator, node);
		addAllAncestorNodesToPath(navigator, node, pathFromNodeToRoot);
		return pathFromNodeToRoot;
	}

	private static <E> void addAllAncestorNodesToPath(
			TreeNavigator<E> navigator, E node, List<TreeNode<E>> pathFromStartBackToNode) {

		Optional<E> parent = navigator.getParent(node);
		while (parent.isPresent()) {
			TreeNode<E> parentNode = SimpleTreeNode.node(parent.get(), navigator.getChildIndex(parent.get()));
			pathFromStartBackToNode.add(parentNode);
			parent = navigator.getParent(parent.get());
		}
	}

}

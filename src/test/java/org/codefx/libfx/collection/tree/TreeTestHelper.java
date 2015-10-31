package org.codefx.libfx.collection.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.codefx.libfx.collection.tree.navigate.TreeNavigator;
import org.codefx.libfx.collection.tree.stream.TreeIterationStrategy;

/**
 * Supports testing by providing sample trees and a {@link Navigator} for them.
 */
public class TreeTestHelper {

	/**
	 * The singleton instance of a stateless navigator which can be used to iterate over the test trees.
	 */
	public static final Navigator NAVIGATOR = new Navigator();

	// #begin TREES

	/**
	 * @return a tree with the single node [singleton].
	 */
	public static Node createSingletonTree() {
		Node tree = Node.singleton("singleton");
		return tree;
	}

	/**
	 * @return a small binary tree [root] -> { [leftLeaf], [rightLeaf] }.
	 */
	public static Node createSimpleBinaryTree() {
		Node tree = Node.root("root",
				Node.leaf("leftLeaf"),
				Node.leaf("rightLeaf")
				);
		return tree;
	}

	/**
	 * @return a perfect binary tree with depth 4 (see <a
	 *         href="https://en.wikipedia.org/wiki/Binary_tree#Types_of_binary_trees">types of trees</a>); a septh-first
	 *         search yields [1], [2], ..., [15]
	 */
	public static Node createDeepBinaryTree() {
		Node tree = Node.root("1",
				Node.node("2",
						Node.node("3",
								Node.leaf("4"),
								Node.leaf("5")
								),
						Node.node("6",
								Node.leaf("7"),
								Node.leaf("8")
								)
						),
				Node.node("9",
						Node.node("10",
								Node.leaf("11"),
								Node.leaf("12")
								),
						Node.node("13",
								Node.leaf("14"),
								Node.leaf("15")
								)
						)
				);
		return tree;
	}

	// #end TREES

	// #begin UTIL

	/**
	 * @param strategy
	 *            the strategy to iterate the nodes
	 * @return an array which contains each node the strategy returns
	 */
	public static String[] iterateTreeContent(TreeIterationStrategy<Node> strategy) {
		List<String> treeContent = new ArrayList<>();

		Optional<Node> nextNode = strategy.goToNextNode();
		while (nextNode.isPresent()) {
			treeContent.add(nextNode.get().content);
			nextNode = strategy.goToNextNode();
		}

		return treeContent.toArray(new String[0]);
	}

	// #end UTIL

	// #begin INNER CLASSES

	/**
	 * A {@link TreeNavigator} for the {@link Node}-based trees available in this class.
	 */
	public static class Navigator implements TreeNavigator<Node> {

		@Override
		public Optional<Node> getParent(Node child) {
			return child.parent;
		}

		@Override
		public OptionalInt getChildIndex(Node node) {
			Optional<Node> parent = node.parent;
			if (parent.isPresent())
				return OptionalInt.of(parent.get().children.indexOf(node));
			else
				return OptionalInt.empty();
		}

		@Override
		public int getChildrenCount(Node parent) {
			return parent.children.size();
		}

		@Override
		public Optional<Node> getChild(Node parent, int childIndex) {
			return (0 <= childIndex && childIndex < parent.children.size())
					? Optional.of(parent.children.get(childIndex))
					: Optional.empty();
		}

	}

	/**
	 * A node in the trees returned by this class.
	 */
	public static class Node {

		/**
		 * The nodes actual content.
		 */
		public final String content;

		/**
		 * The node's children.
		 */
		public final List<Node> children;

		/**
		 * The node's parent.
		 */
		public Optional<Node> parent;

		private Node(String content, Node[] children) {
			this.content = content;
			this.parent = Optional.empty();
			this.children = new ArrayList<>(Arrays.asList(children));

			Arrays.stream(children).forEach(node -> node.parent = Optional.of(this));
		}

		/**
		 * @param content
		 *            the node's content
		 * @return a singleton tree
		 */
		public static Node singleton(String content) {
			return new Node(content, new Node[0]);
		}

		/**
		 * @param content
		 *            the root node's content
		 * @param children
		 *            the child nodes
		 * @return a tree with the specified child nodes
		 */
		public static Node root(String content, Node... children) {
			return new Node(content, children);
		}

		/**
		 * @param content
		 *            the node's content
		 * @param children
		 *            the child nodes
		 * @return a node with the specified child nodes
		 */
		public static Node node(String content, Node... children) {
			return new Node(content, children);
		}

		/**
		 * @param content
		 *            the leaf's content
		 * @return a leaf node
		 */
		public static Node leaf(String content) {
			return new Node(content, new Node[0]);
		}

		@Override
		public String toString() {
			return "Node [" + content + "]";
		}

	}

	// #end INNER CLASSES

}

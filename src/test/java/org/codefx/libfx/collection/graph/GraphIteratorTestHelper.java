package org.codefx.libfx.collection.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.codefx.libfx.collection.graph.GraphNavigator;

class GraphIteratorTestHelper {

	// #region TREES

	public static Node createSingletonTree() {
		Node tree = Node.singleton("singleton");
		return tree;
	}

	public static Node createSimpleBinaryTree() {
		Node tree = Node.root("root",
				Node.leaf("leftLeaf"),
				Node.leaf("rightLeaf")
				);
		return tree;
	}

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

	// #region INNER CLASSES

	public static class Navigator implements GraphNavigator<Node> {

		public static final Navigator instance = new Navigator();

		@Override
		public Optional<Node> getParent(Node child) {
			return child.parent;
		}

		@Override
		public OptionalInt getChildIndex(Node node) {
			Optional<Node> parent = node.parent;
			if (parent.isPresent()) {
				return OptionalInt.of(parent.get().children.indexOf(node));
			} else {
				return OptionalInt.empty();
			}
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

	public static class Node {

		public final Object content;

		public final List<Node> children;

		public Optional<Node> parent;

		private Node(Object content, Node[] children) {
			this.content = content;
			this.parent = Optional.empty();
			this.children = new ArrayList(Arrays.asList(children));

			Arrays.stream(children).forEach(node -> node.parent = Optional.of(this));
		}

		public static Node singleton(Object content) {
			return new Node(content, new Node[0]);
		}

		public static Node root(Object content, Node... children) {
			return new Node(content, children);
		}

		public static Node node(Object content, Node... children) {
			return new Node(content, children);
		}

		public static Node leaf(Object content) {
			return new Node(content, new Node[0]);
		}

		@Override
		public String toString() {
			return "Node [" + content + "]";
		}

	}

	// #end INNER CLASSES

}

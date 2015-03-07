package org.codefx.libfx.collection.graph;

import static org.codefx.libfx.collection.graph.GraphIteratorTestHelper.createDeepBinaryTree;
import static org.codefx.libfx.collection.graph.GraphIteratorTestHelper.createSimpleBinaryTree;
import static org.codefx.libfx.collection.graph.GraphIteratorTestHelper.createSingletonTree;

import java.util.Iterator;

import org.codefx.libfx.collection.graph.GraphDfsIterator;
import org.codefx.libfx.collection.graph.GraphIteratorTestHelper.Navigator;
import org.codefx.libfx.collection.graph.GraphIteratorTestHelper.Node;
import org.junit.Test;

public class GraphDfsIteratorTest {

	@Test
	public void singletonTree() {
		Node tree = createSingletonTree();

		Iterator<Node> treeIterator = new GraphDfsIterator<>(tree, Navigator.instance);

		treeIterator.forEachRemaining(System.out::println);
		System.out.println();
	}

	@Test
	public void simpleBinaryTree() {
		Node tree = createSimpleBinaryTree();

		Iterator<Node> treeIterator = new GraphDfsIterator<>(tree, Navigator.instance);

		treeIterator.forEachRemaining(System.out::println);
		System.out.println();
	}

	@Test
	public void deepBinaryTree() {
		Node tree = createDeepBinaryTree();

		Iterator<Node> treeIterator = new GraphDfsIterator<>(tree, Navigator.instance);

		treeIterator.forEachRemaining(System.out::println);
		System.out.println();
	}

	@Test
	public void deepBinaryTree_startFromWithin() {
		Node fullTree = createDeepBinaryTree();
		Node partialTree = fullTree
				.children.get(0) // returns the tree rooted in "2"
				.children.get(1); // returns the tree rooted in "6"

		Iterator<Node> treeIterator = new GraphDfsIterator<>(partialTree, Navigator.instance);

		treeIterator.forEachRemaining(System.out::println);
		System.out.println();
	}

}

package org.codefx.libfx.collection.tree.stream;

import static org.codefx.libfx.collection.tree.stream.TreePathFactory.createFromNodeToDescendant;
import static org.codefx.libfx.collection.tree.stream.TreePathFactory.createWithSingleNode;
import static org.codefx.libfx.collection.tree.TreeTestHelper.NAVIGATOR;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createDeepBinaryTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createSimpleBinaryTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createSingletonTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.iterateTreeContent;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.codefx.libfx.collection.tree.navigate.TreeNavigator;
import org.codefx.libfx.collection.tree.TreeTestHelper.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link DfsTreeIterationStrategy}.
 */
public class DfsTreeIterationStrategyTest {

	private TreeNavigator<String> treeNavigator;

	@Before
	@SuppressWarnings({ "unchecked", "javadoc" })
	public void setUp() {
		treeNavigator = mock(TreeNavigator.class);
	}

	// construction

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "javadoc", "unchecked", "unused" })
	public void create_nullNavigator_throwsNullPointerException() throws Exception {
		TreePath<TreeNode<String>> initialPath = mock(TreePath.class);
		new DfsTreeIterationStrategy<>(null, initialPath);
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "javadoc", "unused" })
	public void create_nullInitialTreePath_throwsNullPointerException() throws Exception {
		new DfsTreeIterationStrategy<>(treeNavigator, null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void create_withInitialTreePath_returnPathEndOnFirstCallToGoToNext() throws Exception {
		String pathEnd = "end";
		TreePath<TreeNode<String>> treePath = new StackTreePath<>(Arrays.asList(
				SimpleTreeNode.root("root"),
				SimpleTreeNode.innerNode("inner", 0),
				SimpleTreeNode.innerNode(pathEnd, 1)
				));
		DfsTreeIterationStrategy<String> strategy = new DfsTreeIterationStrategy<>(treeNavigator, treePath);

		String firstNode = strategy.goToNextNode().get();

		assertSame(pathEnd, firstNode);
	}

	// iterate through trees

	/*
	 * These tests are not very fine grained. They create a tree (from 'TreeTestHelper') and use a navigator to iterate
	 * over all the nodes. The resulting order of nodes is compared to what is expected.
	 */

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSingletonTree_returnsCorrectElements() {
		Node singleton = createSingletonTree();
		TreePath<TreeNode<Node>> initialPath = createWithSingleNode(singleton);
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "singleton" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSimpleBinaryTree_returnsCorrectElements() {
		Node singleton = createSimpleBinaryTree();
		TreePath<TreeNode<Node>> initialPath = createWithSingleNode(singleton);
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "root", "leftLeaf", "rightLeaf" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverDeepBinaryTree_returnsCorrectElements() {
		Node root = createDeepBinaryTree();
		TreePath<TreeNode<Node>> initialPath = createWithSingleNode(root);
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" },
				treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSubTreeOfDeepBinaryTree_returnsCorrectElements() {
		Node root = createDeepBinaryTree();
		Node subtreeRoot = root
				.children.get(0) // returns the tree rooted in "2"
				.children.get(1); // returns the tree rooted in "6"
		TreePath<TreeNode<Node>> initialPath = createWithSingleNode(subtreeRoot);
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		// the strategy should recurse to the subtree root, but no further
		assertArrayEquals(
				new String[] { "6", "7", "8" },
				treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverDeepBinaryTree_startFromWithin_returnsCorrectElements() {
		Node root = createDeepBinaryTree();
		Node descendant = root
				.children.get(0) // returns the tree rooted in "2"
				.children.get(1); // returns the tree rooted in "6"
		TreePath<TreeNode<Node>> initialPath = createFromNodeToDescendant(NAVIGATOR, root, descendant);
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		// the strategy must recurse which are no descendants of "6"
		assertArrayEquals(
				new String[] { "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" },
				treeContent);
	}

}

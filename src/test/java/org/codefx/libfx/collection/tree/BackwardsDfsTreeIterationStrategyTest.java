package org.codefx.libfx.collection.tree;

import static org.codefx.libfx.collection.tree.TreePathFactory.createFromNodeToDescendant;
import static org.codefx.libfx.collection.tree.TreePathFactory.createWithSingleNode;
import static org.codefx.libfx.collection.tree.TreeTestHelper.NAVIGATOR;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createDeepBinaryTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createSimpleBinaryTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createSingletonTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.iterateTreeContent;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.codefx.libfx.collection.tree.TreeTestHelper.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link BackwardsDfsTreeIterationStrategy}.
 */
public class BackwardsDfsTreeIterationStrategyTest {

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
		new BackwardsDfsTreeIterationStrategy<>(null, initialPath);
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "javadoc", "unused" })
	public void create_nullInitialTreePath_throwsNullPointerException() throws Exception {
		new BackwardsDfsTreeIterationStrategy<>(treeNavigator, null);
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
		BackwardsDfsTreeIterationStrategy<String> strategy =
				new BackwardsDfsTreeIterationStrategy<>(treeNavigator, treePath);

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
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "singleton" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSimpleBinaryTree_startAtRoot_returnsRootOnly() {
		Node singleton = createSimpleBinaryTree();
		TreePath<TreeNode<Node>> initialPath = createWithSingleNode(singleton);
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "root" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSimpleBinaryTree_startAtLeftChild_returnsChildAndRoot() {
		Node root = createSimpleBinaryTree();
		Node leftChild = root.children.get(0);
		TreePath<TreeNode<Node>> initialPath = createFromNodeToDescendant(NAVIGATOR, root, leftChild);
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "leftLeaf", "root" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSimpleBinaryTree_startAtLastNode_returnsWholeTreeBackwards() {
		Node root = createSimpleBinaryTree();
		Node rightLeaf = root.children.get(1);
		TreePath<TreeNode<Node>> initialPath = createFromNodeToDescendant(NAVIGATOR, root, rightLeaf);
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "rightLeaf", "leftLeaf", "root", }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverDeepBinaryTree_returnsCorrectElements() {
		Node root = createDeepBinaryTree();
		Node rightmostLeaf = root
				.children.get(1) // returns the tree rooted in "9"
				.children.get(1) // returns the tree rooted in "13"
				.children.get(1); // returns the tree rooted in "15"
		TreePath<TreeNode<Node>> initialPath = createFromNodeToDescendant(NAVIGATOR, root, rightmostLeaf);
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<Node>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(
				new String[] { "15", "14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1" },
				treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSubTreeOfDeepBinaryTree_returnsCorrectElements() {
		Node root = createDeepBinaryTree();
		Node subtreeRoot = root
				.children.get(0) // returns the tree rooted in "2"
				.children.get(1); // returns the tree rooted in "6"
		Node leftChild = subtreeRoot.children.get(0); // returns the tree rooted in "7"
		TreePath<TreeNode<Node>> initialPath = createFromNodeToDescendant(NAVIGATOR, subtreeRoot, leftChild);
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		// the strategy should recurse to the subtree root, but no further
		assertArrayEquals(
				new String[] { "7", "6" },
				treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverDeepBinaryTree_startFromWithin_returnsCorrectElements() {
		Node root = createDeepBinaryTree();
		Node descendant = root
				.children.get(1) // returns the tree rooted in "9"
				.children.get(1); // returns the tree rooted in "13"
		TreePath<TreeNode<Node>> initialPath = createFromNodeToDescendant(NAVIGATOR, root, descendant);
		TreeIterationStrategy<Node> strategy = new BackwardsDfsTreeIterationStrategy<>(NAVIGATOR, initialPath);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(
				new String[] { "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2", "1" },
				treeContent);
	}

}

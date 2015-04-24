package org.codefx.libfx.collection.tree;

import static org.codefx.libfx.collection.tree.TreeTestHelper.NAVIGATOR;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createDeepBinaryTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createSimpleBinaryTree;
import static org.codefx.libfx.collection.tree.TreeTestHelper.createSingletonTree;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.codefx.libfx.collection.tree.TreeTestHelper.Node;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link DfsTreeIterationStrategy}.
 */
public class DfsTreeIteratorStrategyTest {

	private TreeNavigator<String> treeNavigator;

	@Before
	@SuppressWarnings({ "unchecked", "javadoc" })
	public void setUp() {
		treeNavigator = mock(TreeNavigator.class);
	}

	// construction

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void create_nullNavigator_throwsNullPointerException() throws Exception {
		@SuppressWarnings("unused")
		DfsTreeIterationStrategy<String> strategy =
				new DfsTreeIterationStrategy<>(null, "A");
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void create_nullRoot_throwsNullPointerException() throws Exception {
		@SuppressWarnings("unused")
		DfsTreeIterationStrategy<String> strategy =
				new DfsTreeIterationStrategy<>(treeNavigator, (String) null);
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void create_nullInitialTreePath_throwsNullPointerException() throws Exception {
		@SuppressWarnings("unused")
		DfsTreeIterationStrategy<String> strategy =
				new DfsTreeIterationStrategy<>(treeNavigator, (TreePath<TreeNode<String>>) null);
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void create_nullInitialElementList_throwsNullPointerException() throws Exception {
		@SuppressWarnings("unused")
		DfsTreeIterationStrategy<String> strategy =
				new DfsTreeIterationStrategy<>(treeNavigator, (List<String>) null);
	}

	// return last node from construction

	@Test
	@SuppressWarnings("javadoc")
	public void create_withRoot_returnRootOnFirstCallToGoToNext() throws Exception {
		String rootElement = "root";
		when(treeNavigator.getChildIndex(rootElement)).thenReturn(OptionalInt.empty());
		DfsTreeIterationStrategy<String> strategy = new DfsTreeIterationStrategy<>(treeNavigator, rootElement);

		String firstNode = strategy.goToNextNode().get();

		assertSame(rootElement, firstNode);
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

	@Test
	@SuppressWarnings("javadoc")
	public void create_withInitialElementList_returnLastElementOnFirstCallToGoToNext() throws Exception {
		String lastElement = "last";
		List<String> elementPath = Arrays.asList("first", "middle", lastElement);
		when(treeNavigator.getChildIndex("first")).thenReturn(OptionalInt.empty());
		when(treeNavigator.getChildIndex("middle")).thenReturn(OptionalInt.of(1));
		when(treeNavigator.getChildIndex(lastElement)).thenReturn(OptionalInt.of(0));
		DfsTreeIterationStrategy<String> strategy = new DfsTreeIterationStrategy<>(treeNavigator, elementPath);

		String firstNode = strategy.goToNextNode().get();

		assertSame(lastElement, firstNode);
	}

	// iterate through trees

	/*
	 * These tests are not very fine grained. They create a tree (from 'TreeTestHelper') and use a navigator to iterate
	 * over all the nodes. The resulting order of nodes is compared to what was expected.
	 */

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSingletonTree_returnsCorrectElements() {
		Node tree = createSingletonTree();
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, tree);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "singleton" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSimpleBinaryTree_returnsCorrectElements() {
		Node tree = createSimpleBinaryTree();
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, tree);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(new String[] { "root", "leftLeaf", "rightLeaf" }, treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverDeepBinaryTree_returnsCorrectElements() {
		Node tree = createDeepBinaryTree();
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, tree);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(
				new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" },
				treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverSubTreeOfDeepBinaryTree_returnsCorrectElements() {
		Node fullTree = createDeepBinaryTree();
		Node partialTree = fullTree
				.children.get(0) // returns the tree rooted in "2"
				.children.get(1); // returns the tree rooted in "6"
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, partialTree);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(
				new String[] { "6", "7", "8" },
				treeContent);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void iterateOverDeepBinaryTree_startFromWithin_returnsCorrectElements() {
		Node fullTree = createDeepBinaryTree();
		Node partialTree = fullTree
				.children.get(0) // returns the tree rooted in "2"
				.children.get(1); // returns the tree rooted in "6"
		TreeIterationStrategy<Node> strategy = new DfsTreeIterationStrategy<>(NAVIGATOR, fullTree, partialTree);

		String[] treeContent = iterateTreeContent(strategy);

		assertArrayEquals(
				new String[] { "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" },
				treeContent);
	}

	// HELPER

	private static String[] iterateTreeContent(TreeIterationStrategy<Node> strategy) {
		List<String> treeContent = new ArrayList<>();

		Optional<Node> nextNode = strategy.goToNextNode();
		while (nextNode.isPresent()) {
			treeContent.add(nextNode.get().content);
			nextNode = strategy.goToNextNode();
		}

		return treeContent.toArray(new String[0]);
	}

}

package org.codefx.libfx.collection.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Test;

/**
 * Tests {@link TreePathFactory}.
 */
public class TreePathFactoryTest {

	// createWithSingletonNode

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createWithSingletonNode_nodeNull_throwNullPointerException() throws Exception {
		TreePathFactory.createWithSingleNode(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createWithSingletonNode_someNode_pathContainsOnlyThatNode() throws Exception {
		String node = "Node";

		TreePath<TreeNode<String>> path = TreePathFactory.createWithSingleNode(node);

		assertSame(node, path.removeEnd().getElement());
		assertTrue(path.isEmpty());
	}

	// createFromElementList

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createFromElementList_listNavigator_throwNullPointerException() throws Exception {
		TreePathFactory.createFromElementList(null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createFromElementList_listNull_throwNullPointerException() throws Exception {
		TreePathFactory.createFromElementList(mockTreeNavigator(), null);
	}

	@Test
	@SuppressWarnings({ "javadoc", "unchecked" })
	public void createFromElementList_singletonList_pathOnlyContainsThatElement() throws Exception {
		String element = "node";
		List<String> singletonList = Collections.singletonList(element);
		TreeNavigator<String> navigator = mock(TreeNavigator.class);
		when(navigator.getChildIndex(element)).thenReturn(OptionalInt.empty());

		TreePath<TreeNode<String>> path = TreePathFactory.createFromElementList(navigator, singletonList);

		TreeNode<String> end = path.removeEnd();
		assertSame(element, end.getElement());
		assertFalse(end.getChildIndex().isPresent());
		assertTrue(path.isEmpty());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createFromElementList_longerList_pathContainsListElementsInCorrectOrder() throws Exception {
		String first = "first node";
		String second = "second node";
		String third = "third node";
		List<String> nodes = Arrays.asList(new String[] { first, second, third });
		TreeNavigator<String> navigator = mockTreeNavigatorWithNodesAndIndices(first, 2, second, 5, third, 8);

		TreePath<TreeNode<String>> path = TreePathFactory.createFromElementList(navigator, nodes);

		TreeNode<String> thirdNode = path.removeEnd();
		assertSame(third, thirdNode.getElement());
		assertEquals(8, thirdNode.getChildIndex().getAsInt());

		TreeNode<String> secondNode = path.removeEnd();
		assertSame(second, secondNode.getElement());
		assertEquals(5, secondNode.getChildIndex().getAsInt());

		TreeNode<String> firstNode = path.removeEnd();
		assertSame(first, firstNode.getElement());
		assertEquals(2, firstNode.getChildIndex().getAsInt());

		assertTrue(path.isEmpty());
	}

	// createFromNodeToDescendant

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createFromNodeToDescendant_nullNavigator_throwNullPointerException() throws Exception {
		TreePathFactory.createFromNodeToDescendant(null, "node", "descendant");
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createFromNodeToDescendant_nullNode_throwNullPointerException() throws Exception {
		TreePathFactory.createFromNodeToDescendant(mockTreeNavigator(), null, "descendant");
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createFromNodeToDescendant_nullDescendant_throwNullPointerException() throws Exception {
		TreePathFactory.createFromNodeToDescendant(mockTreeNavigator(), "node", null);
	}

	@Test
	@SuppressWarnings({ "javadoc", "unchecked" })
	public void createFromNodeToDescendant_nodeAndDescendantAreSame_pathOnlyContainsThatNode() throws Exception {
		String node = "node";
		String descendant = node;
		TreeNavigator<String> navigator = mock(TreeNavigator.class);
		when(navigator.getChildIndex(node)).thenReturn(OptionalInt.empty());

		TreePath<TreeNode<String>> path = TreePathFactory.createFromNodeToDescendant(navigator, node, descendant);

		TreeNode<String> end = path.removeEnd();
		assertSame(node, end.getElement());
		assertFalse(end.getChildIndex().isPresent());
		assertTrue(path.isEmpty());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createFromNodeToDescendant_longerPath_pathContainsNodesInCorrectOrder() throws Exception {
		String first = "first node";
		String second = "second node";
		String third = "third node";
		TreeNavigator<String> navigator = mockTreeNavigatorWithNodesAndIndices(first, 2, second, 5, third, 8);

		TreePath<TreeNode<String>> path = TreePathFactory.createFromNodeToDescendant(navigator, first, third);

		TreeNode<String> thirdNode = path.removeEnd();
		assertSame(third, thirdNode.getElement());
		assertEquals(8, thirdNode.getChildIndex().getAsInt());

		TreeNode<String> secondNode = path.removeEnd();
		assertSame(second, secondNode.getElement());
		assertEquals(5, secondNode.getChildIndex().getAsInt());

		TreeNode<String> firstNode = path.removeEnd();
		assertSame(first, firstNode.getElement());
		assertEquals(2, firstNode.getChildIndex().getAsInt());

		assertTrue(path.isEmpty());
	}

	// HELPER

	@SuppressWarnings("unchecked")
	private static TreeNavigator<String> mockTreeNavigator() {
		return mock(TreeNavigator.class);
	}

	@SuppressWarnings("unchecked")
	private static TreeNavigator<String> mockTreeNavigatorWithNodesAndIndices(
			String firstNode, int firstNodeIndex,
			String secondNode, int secondNodeIndex,
			String thirdNode, int thirdNodeIndex) {

		TreeNavigator<String> navigator = mock(TreeNavigator.class);

		// return child indices
		when(navigator.getChildIndex(firstNode)).thenReturn(OptionalInt.of(firstNodeIndex));
		when(navigator.getChildIndex(secondNode)).thenReturn(OptionalInt.of(secondNodeIndex));
		when(navigator.getChildIndex(thirdNode)).thenReturn(OptionalInt.of(thirdNodeIndex));

		// return parents
		when(navigator.getParent(thirdNode)).thenReturn(Optional.of(secondNode));
		when(navigator.getParent(secondNode)).thenReturn(Optional.of(firstNode));
		when(navigator.getParent(firstNode)).thenReturn(Optional.empty());

		return navigator;
	}

}

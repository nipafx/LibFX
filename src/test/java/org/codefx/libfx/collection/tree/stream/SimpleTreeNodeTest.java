package org.codefx.libfx.collection.tree.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.OptionalInt;

import org.codefx.libfx.collection.tree.stream.SimpleTreeNode;
import org.junit.Test;

/**
 * Tests the class {@link SimpleTreeNode}.
 */
public class SimpleTreeNodeTest {

	// node

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createNode_elementNull_throwsNullPointerException() throws Exception {
		SimpleTreeNode.node(null, OptionalInt.empty());
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createNode_childIndexNull_throwsNullPointerException() throws Exception {
		SimpleTreeNode.node("element", null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createNode_elementNonNull_canRetrieveElement() throws Exception {
		String element = "element";
		SimpleTreeNode<String> node = SimpleTreeNode.node(element, OptionalInt.empty());

		assertSame(element, node.getElement());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createNode_canRetrieveChildIndex() throws Exception {
		int childIndex = 4;
		SimpleTreeNode<String> node = SimpleTreeNode.node("element", OptionalInt.of(childIndex));

		assertEquals(childIndex, node.getChildIndex().getAsInt());
	}

	// root

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createRoot_elementNull_throwsNullPointerException() throws Exception {
		SimpleTreeNode.root(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createRoot_elementNonNull_canRetrieveElement() throws Exception {
		String element = "element";
		SimpleTreeNode<String> node = SimpleTreeNode.root(element);

		assertSame(element, node.getElement());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createRoot_hasNoChildIndex() throws Exception {
		SimpleTreeNode<String> node = SimpleTreeNode.root("element");

		assertFalse(node.getChildIndex().isPresent());
	}

	// inner node

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void createInnerNode_elementNull_throwsNullPointerException() throws Exception {
		SimpleTreeNode.innerNode(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("javadoc")
	public void createInnerNode_childIndexNegative_throwsIllegalArgumentException() throws Exception {
		SimpleTreeNode.innerNode("element", -1);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createInnerNode_elementNonNull_canRetrieveElement() throws Exception {
		String element = "element";
		SimpleTreeNode<String> node = SimpleTreeNode.innerNode(element, 0);

		assertSame(element, node.getElement());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void createInnerNode_canRetrieveChildIndex() throws Exception {
		int childIndex = 4;
		SimpleTreeNode<String> node = SimpleTreeNode.innerNode("element", childIndex);

		assertEquals(childIndex, node.getChildIndex().getAsInt());
	}

}

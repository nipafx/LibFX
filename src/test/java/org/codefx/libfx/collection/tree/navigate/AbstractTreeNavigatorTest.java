package org.codefx.libfx.collection.tree.navigate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Optional;
import java.util.OptionalInt;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract superclass for tests of {@link TreeNavigator} implementations.
 *
 * @param <N>
 *            the type of nodes contained in the tree that is navigated by the tested navigator
 */
public abstract class AbstractTreeNavigatorTest<N> {

	private TreeNavigator<N> navigator;

	@Before
	@SuppressWarnings("javadoc")
	public void setUp() {
		navigator = createNavigator();
	}

	// getParent

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void getParent_childNull_throwsNullPointerException() throws Exception {
		navigator.getParent(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getParent_nodeWithoutParent_returnsEmptyOptional() throws Exception {
		N parentlessNode = createSingletonNode();

		Optional<N> parent = navigator.getParent(parentlessNode);

		assertFalse(parent.isPresent());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getParent_nodeWithParent_returnsParent() throws Exception {
		N parent = createNodeWithChildren(1);
		N child = getChildOfParent(parent, 0);

		Optional<N> proclaimedParent = navigator.getParent(child);

		assertSame(parent, proclaimedParent.get());
	}

	// getChidlIndex

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void getChildIndex_nodeNull_throwsNullPointerException() throws Exception {
		navigator.getChildIndex(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChildIndex_nodeWithoutParent_emptyOptional() throws Exception {
		N parentlessNode = createSingletonNode();

		OptionalInt childIndex = navigator.getChildIndex(parentlessNode);

		assertFalse(childIndex.isPresent());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChildIndex_nodeWithParent_returnsCorrectIndex() throws Exception {
		N parent = createNodeWithChildren(5);

		for (int childIndex = 0; childIndex < 5; childIndex++) {
			N child = getChildOfParent(parent, childIndex);
			OptionalInt proclaimedChildIndex = navigator.getChildIndex(child);
			assertEquals(childIndex, proclaimedChildIndex.getAsInt());
		}
	}

	// getChildrenCount

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void getChildrenCount_parentNull_throwsNullPointerException() throws Exception {
		navigator.getChildrenCount(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChildCount_nodeWithoutChildren_returns0() throws Exception {
		N node = createSingletonNode();

		int proclaimedChildCount = navigator.getChildrenCount(node);

		assertEquals(0, proclaimedChildCount);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChildrenCount_nodeWithChildren_returnsCorrectCount() throws Exception {
		for (int childCount = 1; childCount < 5; childCount++) {
			N parent = createNodeWithChildren(childCount);
			int proclaimedChildCount = navigator.getChildrenCount(parent);
			assertEquals(childCount, proclaimedChildCount);
		}
	}

	// getChild

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void getChild_parentNull_throwsNullPointerException() throws Exception {
		navigator.getChild(null, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	@SuppressWarnings("javadoc")
	public void getChild_childIndexNegative_throwsIllegalArgumentException() throws Exception {
		navigator.getChild(createSingletonNode(), -1);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChild_noChildWithThatIndex_returnsEmptyOptional() throws Exception {
		for (int childCount = 0; childCount < 5; childCount++) {
			N parent = createNodeWithChildren(childCount);
			Optional<N> child = navigator.getChild(parent, childCount);
			assertFalse(child.isPresent());
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChild_existingChild_returnsChild() throws Exception {
		N parent = createNodeWithChildren(5);

		for (int childIndex = 0; childIndex < 5; childIndex++) {
			Optional<N> proclaimedChild = navigator.getChild(parent, childIndex);
			N child = getChildOfParent(parent, childIndex);
			assertSame(child, proclaimedChild.get());
		}
	}

	// #begin ABSTRACT METHODS

	/**
	 * @return the tested navigator
	 */
	protected abstract TreeNavigator<N> createNavigator();

	/**
	 * @return a node which as neither parents nor children
	 */
	protected abstract N createSingletonNode();

	/**
	 * @param nrOfChildren
	 *            the exact number of children the created node will have
	 * @return a node with the specified number of children
	 */
	protected abstract N createNodeWithChildren(int nrOfChildren);

	/**
	 * @param parent
	 *            a node which was created with {@link #createNodeWithChildren(int)}
	 * @param childIndex
	 *            the index of the requested child with {@code 0 < childIndex < argFor_createNodeWithChildren}
	 * @return the node which is the child with the specified index of the specified parent
	 */
	protected abstract N getChildOfParent(N parent, int childIndex);

	// #end ABSTRACT METHODS

}

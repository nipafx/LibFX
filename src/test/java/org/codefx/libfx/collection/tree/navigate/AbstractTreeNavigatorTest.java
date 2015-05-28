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
 * @param <E>
 *            the type of elements in the tree which is navigated by the tested navigator
 */
public abstract class AbstractTreeNavigatorTest<E> {

	private TreeNavigator<E> navigator;

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
		E parentlessNode = createSingletonNode();

		Optional<E> parent = navigator.getParent(parentlessNode);

		assertFalse(parent.isPresent());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getParent_nodeWithParent_returnsParent() throws Exception {
		E parent = createNodeWithChildren(1);
		E child = getChildOfParent(parent, 0);

		Optional<E> proclaimedParent = navigator.getParent(child);

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
		E parentlessNode = createSingletonNode();

		OptionalInt childIndex = navigator.getChildIndex(parentlessNode);

		assertFalse(childIndex.isPresent());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChildIndex_nodeWithParent_returnsCorrectIndex() throws Exception {
		E parent = createNodeWithChildren(5);

		for (int childIndex = 0; childIndex < 5; childIndex++) {
			E child = getChildOfParent(parent, childIndex);
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
		E node = createSingletonNode();

		int proclaimedChildCount = navigator.getChildrenCount(node);

		assertEquals(0, proclaimedChildCount);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChildrenCount_nodeWithChildren_returnsCorrectCount() throws Exception {
		for (int childCount = 1; childCount < 5; childCount++) {
			E parent = createNodeWithChildren(childCount);
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
			E parent = createNodeWithChildren(childCount);
			Optional<E> child = navigator.getChild(parent, childCount);
			assertFalse(child.isPresent());
		}
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getChild_existingChild_returnsChild() throws Exception {
		E parent = createNodeWithChildren(5);

		for (int childIndex = 0; childIndex < 5; childIndex++) {
			Optional<E> proclaimedChild = navigator.getChild(parent, childIndex);
			E child = getChildOfParent(parent, childIndex);
			assertSame(child, proclaimedChild.get());
		}
	}

	// #begin ABSTRACT METHODS

	/**
	 * @return the tested navigator
	 */
	protected abstract TreeNavigator<E> createNavigator();

	/**
	 * @return a node which as neither parents nor children
	 */
	protected abstract E createSingletonNode();

	/**
	 * @param nrOfChildren
	 *            the exact number of children the created node will have
	 * @return a node with the specified number of children
	 */
	protected abstract E createNodeWithChildren(int nrOfChildren);

	/**
	 * @param parent
	 *            a node which was created with {@link #createNodeWithChildren(int)}
	 * @param childIndex
	 *            the index of the requested child with {@code 0 < childIndex < argFor_createNodeWithChildren}
	 * @return the node which is the child with the specified index of the specified parent
	 */
	protected abstract E getChildOfParent(E parent, int childIndex);

	// #end ABSTRACT METHODS

}

package org.codefx.libfx.collection.tree;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract super class to tests of {@link TreePath} implementations.
 */
public abstract class AbstractTreePathTest {

	private TreePath<String> path;

	@Before
	@SuppressWarnings("javadoc")
	public void createPath() {
		path = createEmptyPath();
	}

	// is empty

	@Test
	@SuppressWarnings("javadoc")
	public void isEmpty_emptyPath_returnsTrue() throws Exception {
		assertTrue(path.isEmpty());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void isEmpty_nonEmptyPath_false() throws Exception {
		path = createPath("element");

		assertFalse(path.isEmpty());
	}

	// get

	@Test
	@SuppressWarnings("javadoc")
	public void getEnd_emptyPath_returnsEmptyOptional() throws Exception {
		Optional<String> end = path.getEnd();

		assertFalse(end.isPresent());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void getEnd_nonEmptyPath_returnsLastElement() throws Exception {
		String lastElement = "last";
		path = createPath("first", "some", lastElement);

		Optional<String> end = path.getEnd();

		assertSame(lastElement, end.get());
	}

	// remove

	@Test(expected = NoSuchElementException.class)
	@SuppressWarnings("javadoc")
	public void removeEnd_emptyPath_throwsNoSuchElementException() throws Exception {
		path.removeEnd();
	}

	@Test
	@SuppressWarnings("javadoc")
	public void removetEnd_nonEmptyPath_returnsElement() throws Exception {
		String lastElement = "last";
		path = createPath("first", "some", lastElement);

		String end = path.removeEnd();

		assertSame(lastElement, end);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void removetEnd_nonEmptyPath_elementIsRemoved() throws Exception {
		String onlyElement = "only";
		path = createPath(onlyElement);

		path.removeEnd();

		assertTrue(path.isEmpty());
	}

	// append

	@Test(expected = NullPointerException.class)
	@SuppressWarnings("javadoc")
	public void append_nullNode_throwsNullPointerException() throws Exception {
		path.append(null);
	}

	@Test
	@SuppressWarnings("javadoc")
	public void append_toEmptyPath_isEmptyReturnsFalse() throws Exception {
		path.append("element");

		assertFalse(path.isEmpty());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void append_toEmptyPath_getReturnsElement() throws Exception {
		String element = "element";
		path.append(element);

		assertSame(element, path.getEnd().get());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void append_toEmptyPath_removeReturnsElement() throws Exception {
		String element = "element";
		path.append(element);

		assertSame(element, path.removeEnd());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void append_toNonEmptyPath_getReturnsElement() throws Exception {
		path = createPath("first", "some", "last");

		String addedElement = "added";
		path.append(addedElement);

		assertSame(addedElement, path.getEnd().get());
	}

	@Test
	@SuppressWarnings("javadoc")
	public void append_toNonEmptyPath_removeReturnsElement() throws Exception {
		path = createPath("first", "some", "last");

		String addedElement = "added";
		path.append(addedElement);

		assertSame(addedElement, path.removeEnd());
	}

	// #region ABSTRACT FACTORY METHODS

	/**
	 * @return an empty path
	 */
	protected abstract TreePath<String> createEmptyPath();

	/**
	 * @param elements
	 *            the initial elements of the path ordered from start to end
	 * @return a path with the specified elements
	 */
	protected abstract TreePath<String> createPath(String... elements);

	// #end ABSTRACT FACTORY METHODS
}

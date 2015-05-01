package org.codefx.libfx.collection.tree.stream;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.codefx.libfx.collection.tree.stream.StackTreePath;
import org.codefx.libfx.collection.tree.stream.TreePath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Tests {@link StackTreePath}.
 */
@RunWith(Suite.class)
@SuiteClasses({
		StackTreePathTest.Construction.class,
		StackTreePathTest.TreePathContract.class,
})
public class StackTreePathTest {

	/**
	 * Tests whether the constructors work.
	 */
	public static class Construction {

		@Test
		@SuppressWarnings("javadoc")
		public void create_emptyConstructor_throwsNoException() throws Exception {
			@SuppressWarnings("unused")
			StackTreePath<String> stackTreePath = new StackTreePath<>();
		}

		@Test(expected = NullPointerException.class)
		@SuppressWarnings("javadoc")
		public void create_nullElementList_throwsNullPointerException() throws Exception {
			@SuppressWarnings("unused")
			StackTreePath<String> stackTreePath = new StackTreePath<>(null);
		}

		@Test
		@SuppressWarnings("javadoc")
		public void create_elementList_containsElementrs() throws Exception {
			List<String> elements = Arrays.asList("4", "3", "2", "1", "0");
			StackTreePath<String> stackTreePath = new StackTreePath<>(elements);

			for (int i = 0; i < 5; i++) {
				String lastElement = stackTreePath.removeEnd();
				assertEquals("" + i, lastElement);
			}
		}

	}

	/**
	 * Tests whether the class fulfills the {@link TreePath} contract.
	 */
	public static class TreePathContract extends AbstractTreePathTest {

		@Override
		protected TreePath<String> createEmptyPath() {
			return new StackTreePath<>();
		}

		@Override
		protected TreePath<String> createPath(String... elements) {
			return new StackTreePath<>(Arrays.asList(elements));
		}

	}

}

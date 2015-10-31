package org.codefx.libfx.collection.tree.build;

import static org.assertj.core.api.Assertions.assertThat;

import org.codefx.libfx.collection.tree.MutableTreeNode;
import org.codefx.libfx.collection.tree.TreeTestHelper.Node;
import org.codefx.libfx.collection.tree.build.HierarchicalTreeFactory.ToContentFunction;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Tests {@link HierarchicalTreeFactory}.
 */
public class HierarchicalTreeFactoryTest {

	private static final Function<String, List<Character>> TO_HIERARCHY =
			HierarchicalTreeFactoryTest::characters;
	private static final ToContentFunction<String, String> TO_CONTENT =
			HierarchicalTreeFactoryTest::toContent;

	private HierarchicalTreeFactory<String, Character, String> factory;

	private static List<Character> characters(String string) {
		List<Character> characters = new LinkedList<>();
		for (char c : string.toCharArray()) {
			characters.add(c);
		}
		return characters;
	}

	private static String toContent(String element, Integer index, Optional<String> existingContent) {
		String existing = existingContent.orElse("");
		Character character = element.toUpperCase().charAt(index);
		return existing + character + (index == element.length() - 1 ? character : "");
	}

	@Before
	public void setUp() {
		factory = new HierarchicalTreeFactory<>(TO_HIERARCHY, TO_CONTENT);
	}

	// #begin CONSTRUCTOR

	@Test(expected = NullPointerException.class)
	public void create_toHierarchyNull_throwsNullPointerException() {
		new HierarchicalTreeFactory<>(null, TO_CONTENT);
	}

	@Test(expected = NullPointerException.class)
	public void create_toContentNull_throwsNullPointerException() {
		new HierarchicalTreeFactory<>(TO_HIERARCHY, null);
	}

	// #end CONSTRUCTOR

	// #begin CREATE

	/*
	 * The tested factory has the following behavior:
	 *
	 * The elements are strings and the resulting hierarchies are the sequences of characters making them up.
	 * So each node corresponds to a single character in a sequence. Its content is a string consisting solely of that
	 * character (uppercased) repeated a certain number of times.
	 *
	 * This number equals the number of strings that pass through this node (including those ending there) plus
	 * the number of strings ending there (so they are counted twice).
	 *
	 * Example:
	 *  - "a" : "AA" (because "a" ends in that single node, it is counted twice)
	 *  - "ab" : "A"->"BB" (because "ab" ends in the last node, it is counted twice)
	 *  - "abc" : "A"->"B"->"CC" (because "abc" ends in the last node "C", it is counted twice)
	 */

	@Test
	public void createForest_oneCharacterElement() {
		List<MutableTreeNode<String>> forest = factory.createForest("a");

		Node tree = Node.node("AA");
		assertEquals(forest, tree);
	}

	@Test
	public void createForest_twoCharacterElement() {
		List<MutableTreeNode<String>> forest = factory.createForest("ab");

		Node tree =
				Node.node("A",
						Node.node("BB"));
		assertEquals(forest, tree);
	}

	@Test
	public void createForest_threeCharacterElement() {
		List<MutableTreeNode<String>> forest = factory.createForest("abc");

		Node tree =
				Node.node("A",
						Node.node("B",
								Node.node("CC")));
		assertEquals(forest, tree);
	}

	@Test
	public void createForest_twoElementsDifferingInLastLetter() {
		List<MutableTreeNode<String>> forest = factory.createForest("abc", "abd");

		Node tree =
				Node.node("AA",
						Node.node("BB",
								Node.node("CC"),
								Node.node("DD")));
		assertEquals(forest, tree);
	}

	@Test
	public void createForest_twoElementsDifferingInSecondLetter() {
		List<MutableTreeNode<String>> forest = factory.createForest("abc", "axc");

		Node tree =
				Node.node("AA",
						Node.node("B",
								Node.node("CC")),
						Node.node("X",
								Node.node("CC")));
		assertEquals(forest, tree);
	}

	@Test
	public void createForest_twoElementsOfDifferentLengths() {
		List<MutableTreeNode<String>> forest = factory.createForest("ab", "abc");

		Node tree =
				Node.node("AA",
						Node.node("BBB",
								Node.node("CC")));
		assertEquals(forest, tree);
	}

	@Test
	public void createForest_manyElements() {
		List<MutableTreeNode<String>> forest = factory.createForest("a", "ab", "abc", "acb", "abd");

		Node tree =
				Node.node("AAAAAA",
						Node.node("BBBB",
								Node.node("CC"),
								Node.node("DD")),
						Node.node("C",
								Node.node("BB")));
		assertEquals(forest, tree);
	}

	// #end CREATE

	static void assertEquals(List<MutableTreeNode<String>> actuals, Node... expecteds) {
		assertThat(actuals).hasSize(expecteds.length);
		for (int i = 0; i < actuals.size(); i++)
			assertEquals(actuals.get(i), expecteds[i]);
	}

	static void assertEquals(MutableTreeNode<String> actual, Node expected) {
		assertThat(actual.getContent()).isEqualTo(expected.content);

		for (int i = 0; i < actual.getChildren().size(); i++) {
			// assert that the children's parent is correctly set
			assertThat(actual.getChildren().get(i).getParent()).containsSame(actual);
			assertEquals(actual.getChildren().get(i), expected.children.get(i));
		}
	}

}
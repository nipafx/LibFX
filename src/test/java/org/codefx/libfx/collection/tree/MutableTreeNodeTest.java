package org.codefx.libfx.collection.tree;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Tests the class {@link MutableTreeNode}.
 */
public class MutableTreeNodeTest extends AbstractTreeNodeTest<MutableTreeNode<String>> {

	// #begin CONTENT

	@Test
	public void getContent_contentWasSet_returnsNewContent() {
		String content = "CONTENT";
		MutableTreeNode<String> node = new MutableTreeNode<>("");

		node.setContent(content);

		assertThat(node.getContent()).isSameAs(content);
	}

	// #end CONTENT

	// #begin ADD CHILD

	@Test
	public void addChild_parentlessNode_returnsTrue() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");

		boolean added = parent.addChild(child);

		assertThat(added).isTrue();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addChild_parentlessNode_parentGetsNewChild() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");

		parent.addChild(child);

		assertThat(parent.getChildren()).containsExactly(child);
	}

	@Test
	public void addChild_parentlessNode_childGetsNewParent() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");

		parent.addChild(child);

		assertThat(child.getParent()).containsSame(parent);
	}

	@Test
	public void addChild_existingChild_returnsFalse() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");

		parent.addChild(child);
		boolean added = parent.addChild(child);

		assertThat(added).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void addChild_existingChild_notAddedAgain() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");

		parent.addChild(child);
		parent.addChild(child);

		assertThat(parent.getChildren()).containsExactly(child);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addChild_nodeWithParent_throwsIllegalArgumentException() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");
		parent.addChild(child);

		MutableTreeNode<String> newParent = new MutableTreeNode<>("");
		newParent.addChild(child);
	}

	@Test
	public void addChild_severalChildren_containsChildrenInOrder() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		List<MutableTreeNode<String>> children =
				Arrays.asList(new MutableTreeNode<>(""), new MutableTreeNode<>(""), new MutableTreeNode<>(""));

		children.forEach(parent::addChild);

		assertThat(parent.getChildren()).containsExactlyElementsOf(children);
	}

	// #end ADD CHILD

	// #begin REMOVE CHILD

	@Test
	public void removeChild_child_returnsTrue() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");
		parent.addChild(child);

		boolean removed = parent.removeChild(child);

		assertThat(removed).isTrue();
	}

	@Test
	public void removeChild_child_childRemoved() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");
		parent.addChild(child);

		parent.removeChild(child);

		assertThat(parent.getChildren()).isEmpty();
	}

	@Test
	public void removeChild_child_childsParentRemoved() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> child = new MutableTreeNode<>("");
		parent.addChild(child);

		parent.removeChild(child);

		assertThat(child.getParent()).isEmpty();
	}

	@Test
	public void removeChild_unrelatedNode_returnsFalse() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> unrelated = new MutableTreeNode<>("");

		boolean removed = parent.removeChild(unrelated);

		assertThat(removed).isFalse();
	}

	@Test
	public void removeChild_unrelatedNode_unrelatedNodeKeepsParent() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		MutableTreeNode<String> unrelatedParent = new MutableTreeNode<>("");
		MutableTreeNode<String> unrelated = new MutableTreeNode<>("");
		unrelatedParent.addChild(unrelated);

		parent.removeChild(unrelated);

		assertThat(unrelated.getParent()).containsSame(unrelatedParent);
	}

	@Test
	public void removeChild_severalChildren_childrenAreRemoved() {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		List<MutableTreeNode<String>> childrenToStay =
				Arrays.asList(new MutableTreeNode<>(""), new MutableTreeNode<>(""));
		List<MutableTreeNode<String>> childrenToRemove =
				Arrays.asList(new MutableTreeNode<>(""), new MutableTreeNode<>(""));
		List<MutableTreeNode<String>> children = Arrays.asList(
				childrenToStay.get(0), childrenToRemove.get(0), childrenToStay.get(1), childrenToRemove.get(1));
		children.forEach(parent::addChild);

		childrenToRemove.forEach(parent::removeChild);

		assertThat(parent.getChildren()).containsExactlyElementsOf(childrenToStay);
	}

	// #end REMOVE CHILD

	@Override
	protected MutableTreeNode<String> create(String content) {
		return new MutableTreeNode<>(content);
	}

}

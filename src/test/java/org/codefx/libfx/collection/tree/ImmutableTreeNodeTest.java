package org.codefx.libfx.collection.tree;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests the class {@link ImmutableTreeNode}.
 */
public class ImmutableTreeNodeTest extends AbstractTreeNodeTest<ImmutableTreeNode<String>> {

	// #begin COPY OF

	@Test
	public void copyOf_singleNode_hasNoParent() {
		ImmutableTreeNode<String> node = ImmutableTreeNode.copyOf(new MutableTreeNode<>(""));

		assertThat(node.getParent()).isEmpty();
	}

	// #end COPY OF

	@Override
	protected ImmutableTreeNode<String> create(String content) {
		return new ImmutableTreeNode<>(content, new ArrayList<>());
	}

}

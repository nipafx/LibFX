package org.codefx.libfx.collection.tree.navigate;

import org.codefx.libfx.collection.tree.MutableTreeNode;

import java.util.stream.IntStream;

/**
 * Tests {@link TreeNodeNavigator}.
 */
public class TreeNodeNavigatorTest extends AbstractTreeNavigatorTest<MutableTreeNode<String>> {

	@Override
	protected TreeNavigator<MutableTreeNode<String>> createNavigator() {
		return new TreeNodeNavigator<>();
	}

	@Override
	protected MutableTreeNode<String> createSingletonNode() {
		return new MutableTreeNode<>("");
	}

	@Override
	protected MutableTreeNode<String> createNodeWithChildren(int nrOfChildren) {
		MutableTreeNode<String> parent = new MutableTreeNode<>("");
		IntStream.range(0, nrOfChildren)
				.mapToObj(i -> new MutableTreeNode<>(""))
				.forEach(parent::addChild);
		return parent;
	}

	@Override
	protected MutableTreeNode<String> getChildOfParent(
			MutableTreeNode<String> parent, int childIndex) {
		return parent.getChildren().get(childIndex);
	}
}

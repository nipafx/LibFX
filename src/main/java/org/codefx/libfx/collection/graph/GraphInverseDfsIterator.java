package org.codefx.libfx.collection.graph;

import java.util.Optional;

public class GraphInverseDfsIterator<N> extends AbstractGraphIterator<N> {

	public GraphInverseDfsIterator(N root, GraphNavigator<N> navigator) {
		super(root, navigator);
	}

	public GraphInverseDfsIterator(N root, GraphNavigator<N> navigator, N startNode) {
		super(root, navigator, startNode);
	}

	// #region GO TO NEXT NODE

	@Override
	protected void goToNextNode() {
		assertGraphNotFullyTraversed();

		Optional<Node<N>> leftSibling = goToParentAndGetLeftSibling();
		if (leftSibling.isPresent())
			goToRightmostAncestor(leftSibling.get());
	}

	private Optional<Node<N>> goToParentAndGetLeftSibling() {
		Node<N> currentNode = path.pop();
		if (path.isEmpty())
			return Optional.empty();

		Node<N> parent = path.peek();
		int leftSiblingIndex = currentNode.getChildIndex().getAsInt() - 1;
		return navigator
				.getChild(parent.getElement(), leftSiblingIndex)
				.map(child -> Node.node(child, leftSiblingIndex));
	}

	private void goToRightmostAncestor(Node<N> leftSibling) {
		Optional<Node<N>> rightmostChild = Optional.of(leftSibling);
		while (rightmostChild.isPresent()) {
			path.push(rightmostChild.get());
			int rightmostChildIndex = navigator.getChildrenCount(rightmostChild.get().getElement()) - 1;
			rightmostChild = navigator
					.getChild(rightmostChild.get().getElement(), rightmostChildIndex)
					.map(child -> Node.node(child, rightmostChildIndex));
		}
	}

	// #end GO TO NEXT NODE

}

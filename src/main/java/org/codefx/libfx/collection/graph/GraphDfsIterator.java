package org.codefx.libfx.collection.graph;

import java.util.Optional;

public class GraphDfsIterator<N> extends AbstractGraphIterator<N> {

	public GraphDfsIterator(N root, GraphNavigator<N> navigator) {
		super(root, navigator);
	}

	public GraphDfsIterator(N root, GraphNavigator<N> navigator, N startNode) {
		super(root, navigator, startNode);
	}

	// #region GO TO NEXT NODE

	@Override
	protected void goToNextNode() {
		assertGraphNotFullyTraversed();

		Optional<Node<N>> leftmostChild = getLeftmostChild();
		if (leftmostChild.isPresent())
			goToLeftmostChild(leftmostChild.get());
		else
			goToRightSiblingOrUncle();
	}

	private Optional<Node<N>> getLeftmostChild() {
		N currentNode = path.peek().getElement();
		return navigator
				.getChild(currentNode, 0)
				.map(child -> Node.node(child, 0));
	}

	private void goToLeftmostChild(Node<N> leftmostChild) {
		path.push(leftmostChild);
	}

	private void goToRightSiblingOrUncle() {
		Optional<Node<N>> nextNode = Optional.empty();

		while (!nextNode.isPresent() && !path.isEmpty()) {
			Node<N> currentNode = path.pop();
			boolean hasParent = !path.isEmpty();
			if (hasParent) {
				N parent = path.peek().getElement();
				int rightSiblingIndex = currentNode.getChildIndex().getAsInt() + 1;
				nextNode = navigator
						.getChild(parent, rightSiblingIndex)
						.map(child -> Node.node(child, rightSiblingIndex));
			}
		}

		nextNode.ifPresent(path::push);
	}

	// #end GO TO NEXT NODE

}

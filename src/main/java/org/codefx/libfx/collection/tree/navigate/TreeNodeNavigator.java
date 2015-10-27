package org.codefx.libfx.collection.tree.navigate;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.codefx.libfx.collection.tree.TreeNode;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * A {@link TreeNavigator} for a tree of {@link TreeNode TreeNode}s.
 *
 * @param <C>
 * 		the type of content contained in the tree
 * @param <N>
 * 		the type of nodes contained in the tree
 */
public final class TreeNodeNavigator<C, N extends TreeNode<C, N>> implements TreeNavigator<N> {

	@Override
	public Optional<N> getParent(N child) {
		return child.getParent();
	}

	@Override
	public OptionalInt getChildIndex(N node) {
		requireNonNull(node, "The argument 'node' must not be null.");
		if (!node.getParent().isPresent())
			return OptionalInt.empty();

		int childIndex = node.getParent().get().getChildren().indexOf(node);
		ensureNodeIsChildOfParent(node, childIndex);
		return OptionalInt.of(childIndex);
	}

	private void ensureNodeIsChildOfParent(N child, int childIndex) {
		if (childIndex < 0) {
			String message = format("The specified child '%s' is not a child of its parent '%s'.",
					child, child.getParent().get());
			throw new IllegalStateException(message);
		}
	}

	@Override
	public int getChildrenCount(N parent) {
		return parent.getChildren().size();
	}

	@Override
	public Optional<N> getChild(N parent, int childIndex) {
		requireNonNull(parent, "The argument 'parent' must not be null.");
		if (childIndex < 0)
			throw new IllegalArgumentException("The argument 'childIndex' must be non-negative.");

		if (childIndex < parent.getChildren().size())
			return Optional.of(parent.getChildren().get(childIndex));
		else
			return Optional.empty();
	}

}

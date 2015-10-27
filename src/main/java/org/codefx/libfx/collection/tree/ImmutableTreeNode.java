package org.codefx.libfx.collection.tree;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An immutable implementation of a {@link TreeNode}.
 * <p>
 * To create an immutable tree, first create a {@link MutableTreeNode mutable} one first and pass it to {@link
 * #copyOf(TreeNode) copyOf}.
 *
 * @param <C>
 * 		the type of content contained in the tree
 */
public class ImmutableTreeNode<C> implements TreeNode<C, ImmutableTreeNode<C>> {

	/*
	 * Each node references its parent as well as its children, which poses problems for constructing a tree with
	 * truly immutable references. One way to deal with this would be having the constructor do the copying and pass
	 * the partially constructed instance down to the children's constructor. I didn't like this.
	 *
	 * Instead I decided to make the 'parent' mutable and set it after construction.
	 */

	private final C content;
	private Optional<ImmutableTreeNode<C>> parent;
	private final List<ImmutableTreeNode<C>> childrenUnmodifiable;

	ImmutableTreeNode(C content, List<ImmutableTreeNode<C>> children) {
		this.content = requireNonNull(content, "The argument 'content' must not be null.");
		this.parent = Optional.empty();
		requireNonNull(children, "The argument 'children' must not be null.");
		this.childrenUnmodifiable = Collections.unmodifiableList(children);
	}

	/**
	 * Creates an immutable copy of the tree rooted in the specified node.
	 * <p/>
	 * The returned node's {@link TreeNode#getParent() parent} will be empty.
	 *
	 * @param node
	 * 		the node to copy
	 * @param <C>
	 * 		the type of content contained in the tree
	 * @param <N>
	 * 		the type of nodes contained in the tree
	 *
	 * @return an immutable copy of the tree rooted in the specified node
	 */
	public static <C, N extends TreeNode<C, N>> ImmutableTreeNode<C> copyOf(TreeNode<C, N> node) {
		return Factory.createImmutableCopyOfTree(node);
	}

	@Override
	public C getContent() {
		return content;
	}

	@Override
	public Optional<ImmutableTreeNode<C>> getParent() {
		return parent;
	}

	private void setParentOnce(Optional<ImmutableTreeNode<C>> parent) {
		requireNonNull(parent, "The argument 'parent' must not be null.");
		if (this.parent != null) {
			throw new IllegalStateException(format("The parent was already set to be '%s'.", this.parent));
		}
		this.parent = parent;
	}

	@Override
	public List<ImmutableTreeNode<C>> getChildren() {
		return childrenUnmodifiable;
	}

	private static class Factory {

		public static <C, N extends TreeNode<C, N>> ImmutableTreeNode<C> createImmutableCopyOfTree(
				TreeNode<C, N> node) {
			// this recursive approach might be refactored into a loop to prevent deep call stacks
			List<ImmutableTreeNode<C>> children = createOrphans(node);
			ImmutableTreeNode<C> immutableNode = new ImmutableTreeNode<>(node.getContent(), children);
			setAsParent(immutableNode, children);
			return immutableNode;
		}

		private static <C, T extends TreeNode<C, T>> List<ImmutableTreeNode<C>> createOrphans(TreeNode<C, T> node) {
			return node
					.getChildren().stream()
					.map(ImmutableTreeNode::copyOf)
					.collect(toList());
		}

		private static <C> void setAsParent(ImmutableTreeNode<C> immutableNode, List<ImmutableTreeNode<C>> children) {
			Optional<ImmutableTreeNode<C>> asParent = Optional.of(immutableNode);
			children.forEach(child -> child.setParentOnce(asParent));
		}

	}

}

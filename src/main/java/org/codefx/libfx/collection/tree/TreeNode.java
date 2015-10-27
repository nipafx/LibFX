package org.codefx.libfx.collection.tree;

import org.codefx.libfx.collection.tree.navigate.TreeNavigator;
import org.codefx.libfx.collection.tree.navigate.TreeNodeNavigator;

import java.util.List;
import java.util.Optional;

/**
 * A node in a tree, i.e. a directed, acyclic graph.
 * <p>
 * A node carries some {@link #getContent() content} and knows its {@link #getParent() parent} and {@link #getChildren()
 * children}.
 *
 * @param <C>
 * 		the type of content contained in the tree
 * @param <N>
 * 		the type of nodes contained in the tree
 */
public interface TreeNode<C, N extends TreeNode<C, N>> {

	/**
	 * @return the content carried by this node
	 */
	C getContent();

	/**
	 * @return this node's parent, which may not exist if this is the tree's root
	 */
	Optional<N> getParent();

	/**
	 * @return an unmodifiable view on the node's children
	 */
	List<N> getChildren();

	/**
	 * @return a {@link TreeNavigator} that navigates a tree of {@link TreeNode}s
	 */
	static <C, N extends TreeNode<C, N>> TreeNavigator<N> navigator() {
		return new TreeNodeNavigator<>();
	}

}

package org.codefx.libfx.collection.tree;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A fully mutable implementation of {@link TreeNode}.
 *
 * @param <C>
 * 		the type of content contained in the tree
 */
public class MutableTreeNode<C> implements TreeNode<C, MutableTreeNode<C>> {

	private C content;
	private Optional<MutableTreeNode<C>> parent;
	private final List<MutableTreeNode<C>> children;

	/**
	 * Creates a new node without parent and children.
	 *
	 * @param content
	 * 		the initial content of the new node;
	 * 		if not all nodes carry content, consider using {@code Optional<>} for {@code C}
	 */
	public MutableTreeNode(C content) {
		this.content = requireNonNull(content, "The argument 'content' must not be null.");
		children = new ArrayList<>();
		parent = Optional.empty();
	}

	@Override
	public C getContent() {
		return content;
	}

	/**
	 * @param content
	 * 		the new content to set
	 */
	public void setContent(C content) {
		this.content = requireNonNull(content, "The argument 'content' must not be null.");
	}

	@Override
	public Optional<MutableTreeNode<C>> getParent() {
		return parent;
	}

	@Override
	public List<MutableTreeNode<C>> getChildren() {
		return Collections.unmodifiableList(children);
	}

	/**
	 * Adds the specified node as a child to this node.
	 * <p>
	 * Also sets this node as the child's {@link #getParent() parent}.
	 *
	 * @param child
	 * 		the child to add; must not have a parent unless it is this node
	 *
	 * @return true if the child was added; false if it was already a child of this node
	 *
	 * @throws IllegalArgumentException
	 * 		if the {@code child} has a parent that is not this node
	 */
	public boolean addChild(MutableTreeNode<C> child) {
		requireNonNull(child, "The argument 'child' must not be null.");
		if (child.getParent().isPresent()) {
			MutableTreeNode<C> parent = child.getParent().get();
			if (parent == this)
				return false;
			else {
				String message = "Can not add the specified new child '%s' to this '%s' "
						+ "because it already has another parent '%s'.";
				throw new IllegalArgumentException(format(message, child, this, parent));
			}
		}

		addChildWithoutChecks(child);
		return true;
	}

	private void addChildWithoutChecks(MutableTreeNode<C> child) {
		child.parent = Optional.of(this);
		children.add(child);
	}

	/**
	 * Removes the specified node from the list of {@link #getChildren() children}..
	 * <p>
	 * Also sets the child's {@link #getParent() parent} to {@link Optional#empty() empty}.
	 *
	 * @param child
	 * 		the child to remove
	 *
	 * @return true if the child was removed; false if it wasn't a child of this node
	 */
	public boolean removeChild(MutableTreeNode<C> child) {
		requireNonNull(child, "The argument 'child' must not be null.");
		boolean childOfThisNode = child.getParent().filter(parent -> parent == this).isPresent();
		if (childOfThisNode) {
			removeChildWithoutChecks(child);
			return true;
		} else {
			return false;
		}
	}

	private void removeChildWithoutChecks(MutableTreeNode<C> child) {
		child.parent = Optional.empty();
		children.remove(child);
	}

}

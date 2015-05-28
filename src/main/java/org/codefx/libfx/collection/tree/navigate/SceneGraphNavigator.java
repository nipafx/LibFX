package org.codefx.libfx.collection.tree.navigate;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * A {@link TreeNavigator} for a JavaFX scene graph.
 * <p>
 * This implementation is thread-safe in the sense that individual method calls will not fail if the scene graph is
 * changed concurrently. But it can not prevent return values from getting stale so chaining calls might lead to
 * unexpected results, e.g.:
 *
 * <pre>
 * Node node = ...
 * if (getParent(node).isPresent()) {
 * 	// the node has a parent, so it should have a child index, too;
 * 	// but the scene graph may have changed, so 'indexPresent' may be false
 * 	boolean indexPresent = getChildIndex(node).isPresent();
 * }
 * </pre>
 * Similarly:
 *
 * <pre>
 * Node parent = ...
 * Optional&lt;Node&gt; child1 = getChild(parent, 0);
 * Optional&lt;Node&gt; child2 = getChild(parent, 0);
 * // if the scene graph changed between the two calls, this may be false
 * boolean sameChildren = child1.equals(child2);
 * </pre>
 */
public class SceneGraphNavigator implements TreeNavigator<Node> {

	@Override
	public Optional<Node> getParent(Node child) {
		Objects.requireNonNull(child, "The argument 'child' must not be null.");

		return Optional.ofNullable(child.getParent());
	}

	@Override
	public OptionalInt getChildIndex(Node node) {
		Objects.requireNonNull(node, "The argument 'node' must not be null.");

		Parent parent = node.getParent();
		if (parent == null)
			return OptionalInt.empty();

		int childIndex = parent.getChildrenUnmodifiable().indexOf(node);
		if (childIndex == -1)
			return OptionalInt.empty();

		return OptionalInt.of(childIndex);
	}

	@Override
	public int getChildrenCount(Node parent) {
		Objects.requireNonNull(parent, "The argument 'parent' must not be null.");

		if (!(parent instanceof Parent))
			return 0;

		return ((Parent) parent).getChildrenUnmodifiable().size();
	}

	@Override
	public Optional<Node> getChild(Node parent, int childIndex) {
		Objects.requireNonNull(parent, "The argument 'parent' must not be null.");
		if (childIndex < 0)
			throw new IllegalArgumentException("The argument 'childIndex' must be non-negative.");

		if (!(parent instanceof Parent))
			return Optional.empty();

		return getChild((Parent) parent, childIndex);
	}

	private static Optional<Node> getChild(Parent parent, int childIndex) {
		if (parent.getChildrenUnmodifiable().size() <= childIndex)
			return Optional.empty();

		try {
			// even though we checked first, due to threading this might fail
			return Optional.of(parent.getChildrenUnmodifiable().get(childIndex));
		} catch (IndexOutOfBoundsException ex) {
			return Optional.empty();
		}
	}

}

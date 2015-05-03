package org.codefx.libfx.collection.tree.navigate;

import java.awt.Component;
import java.awt.Container;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * A {@link TreeNavigator} for an AWT component hierarchy.
 * <p>
 * This implementation is thread-safe in the sense that individual method calls will not fail if the component hierarchy
 * is changed concurrently. But it can not prevent return values from getting stale so chaining calls might lead to
 * unexpected results, e.g.:
 *
 * <pre>
 * Component component = ...
 * if (getParent(component).isPresent()) {
 * 	// the component has a parent, so it should have a child index, too;
 * 	// but the component hierarchy may have changed, so 'indexPresent' may be false
 * 	boolean indexPresent = getChildIndex(component).isPresent();
 * }
 * </pre>
 * Similarly:
 *
 * <pre>
 * Component parent = ...
 * Optional&lt;Component&gt; child1 = getChild(parent, 0);
 * Optional&lt;Component&gt; child2 = getChild(parent, 0);
 * // if the component hierarchy changed between the two calls, this may be false
 * boolean sameChildren = child1.equals(child2);
 * </pre>
 */
public class ComponentHierarchyNavigator implements TreeNavigator<Component> {

	@Override
	public Optional<Component> getParent(Component child) {
		Objects.requireNonNull(child, "The argument 'child' must not be null.");

		return Optional.ofNullable(child.getParent());
	}

	@Override
	public OptionalInt getChildIndex(Component node) {
		Objects.requireNonNull(node, "The argument 'node' must not be null.");

		Component parent = node.getParent();
		if (parent == null)
			return OptionalInt.empty();

		if (!(parent instanceof Container))
			return OptionalInt.empty();

		Component[] siblings = ((Container) parent).getComponents();
		return getIndex(node, siblings);
	}

	private static OptionalInt getIndex(Component node, Component[] siblings) {
		try {
			for (int i = 0; i < siblings.length; i++)
				if (siblings[i] == node)
					return OptionalInt.of(i);
			return OptionalInt.empty();
		} catch (ArrayIndexOutOfBoundsException ex) {
			return OptionalInt.empty();
		}
	}

	@Override
	public int getChildrenCount(Component parent) {
		Objects.requireNonNull(parent, "The argument 'parent' must not be null.");

		if (!(parent instanceof Container))
			return 0;

		return ((Container) parent).getComponents().length;
	}

	@Override
	public Optional<Component> getChild(Component parent, int childIndex) {
		Objects.requireNonNull(parent, "The argument 'parent' must not be null.");
		if (childIndex < 0)
			throw new IllegalArgumentException("The argument 'childIndex' must be non-negative.");

		if (!(parent instanceof Container))
			return Optional.empty();

		return getChild((Container) parent, childIndex);
	}

	private static Optional<Component> getChild(Container parent, int childIndex) {
		if (parent.getComponents().length <= childIndex)
			return Optional.empty();

		try {
			// even though we checked first, due to threading this might fail
			return Optional.of(parent.getComponent(childIndex));
		} catch (ArrayIndexOutOfBoundsException ex) {
			return Optional.empty();
		}
	}

}
